package com.sinchonthon.team5.odyssey.jobpost.llm;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import com.sinchonthon.team5.odyssey.jobpost.enums.JobPostCategory;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
class JobPostRefineParser {

    private static final Set<String> RISK_TERMS = Set.of(
            "자동", "19세", "4인석", "시험", "품절", "해시태그", "이모지", "추가 문의", "예산", "마감일", "결제 기능"
    );
    private static final Pattern PROMISE_PATTERN =
            Pattern.compile("(오늘|당장|이번\\s*주).{0,20}(완료|제작|구현|작성|업데이트).{0,12}(하겠습니다|해드|제공)");
    private static final Pattern URGENT_QUESTION_PATTERN = Pattern.compile("오늘.{0,12}(가능|될까|될수|해줄|할수)");
    private static final Pattern SAME_DAY_WORK_PATTERN =
            Pattern.compile("오늘.{0,80}(완료|완성|제작|구현|작성|업데이트|제공)");
    private static final Pattern NUMBER_PATTERN = Pattern.compile("\\d[\\d,]*");
    private static final Pattern MAN_WON_PATTERN = Pattern.compile("(\\d+)\\s*만\\s*원");
    private static final Pattern WON_PATTERN = Pattern.compile("(\\d[\\d,]*)\\s*원");

    private final OllamaClient ollamaClient;
    private final ObjectMapper objectMapper;
    private final String reviewModel;

    JobPostRefineParser(
            OllamaClient ollamaClient,
            ObjectMapper objectMapper,
            @org.springframework.beans.factory.annotation.Value("${ollama.review-model}") String reviewModel
    ) {
        this.ollamaClient = ollamaClient;
        this.objectMapper = objectMapper;
        this.reviewModel = reviewModel;
    }

    JobPostCategory classifyCategory(String rawText) {
        try {
            String raw = ollamaClient.chatJson(JobPostRefinePrompts.buildCategoryMessages(rawText));
            JsonNode node = extractJson(raw);
            String value = node.path("category").asText("");
            return JobPostCategory.valueOf(value.trim().toUpperCase());
        } catch (Exception exception) {
            return inferCategoryByKeyword(rawText);
        }
    }

    private JobPostCategory inferCategoryByKeyword(String rawText) {
        String text = rawText.toLowerCase();
        if (text.contains("인스타") || text.contains("릴스") || text.contains("스토리") || text.contains("sns")) {
            return JobPostCategory.SNS;
        }
        if (text.contains("로고") || text.contains("포스터") || text.contains("메뉴판")
                || text.contains("배너") || text.contains("전단") || text.contains("디자인")) {
            return JobPostCategory.IMAGE;
        }
        return JobPostCategory.WEB;
    }

    JobPostRefineResult parse(String rawText, JobPostCategory category) {
        try {
            String raw = ollamaClient.chatJson(JobPostRefinePrompts.buildMessages(rawText, category));
            JobPostRefineResult result = normalize(extractJson(raw), category);

            if (result.title().isBlank()) {
                result = new JobPostRefineResult(
                        category, fallbackParse(rawText, category).title(),
                        result.refinedDescription(), result.budgetText(), result.deadlineText(), true
                );
            }
            if (result.refinedDescription().isBlank()) {
                result = new JobPostRefineResult(
                        category, result.title(), rawText.strip(),
                        result.budgetText(), result.deadlineText(), true
                );
            }
            if (result.budgetText().isBlank() || result.budgetText().equals("협의")) {
                result = result.withBudgetDeadline(extractBudgetText(rawText), result.deadlineText());
            }

            List<JobPostRefineResult> candidates = new ArrayList<>();
            candidates.add(result);

            try {
                String reviewedRaw = ollamaClient.chatJson(
                        JobPostRefinePrompts.buildReviewMessages(rawText, category, result, false, objectMapper)
                );
                JobPostRefineResult reviewed = normalize(extractJson(reviewedRaw), category);
                if (!reviewed.title().isBlank() && !reviewed.refinedDescription().isBlank()) {
                    candidates.add(reviewed.withBudgetDeadline(result.budgetText(), result.deadlineText()));
                }
            } catch (Exception ignored) {
                // 검수 실패 시 1차 결과를 그대로 사용
            }

            JobPostRefineResult safest = safest(rawText, candidates);
            if (needsStrictReview(rawText, safest)) {
                try {
                    String strictRaw = ollamaClient.chatJson(
                            JobPostRefinePrompts.buildReviewMessages(rawText, category, safest, true, objectMapper),
                            reviewModel
                    );
                    JobPostRefineResult strict = normalize(extractJson(strictRaw), category);
                    if (!strict.title().isBlank() && !strict.refinedDescription().isBlank()) {
                        candidates.add(strict.withBudgetDeadline(safest.budgetText(), safest.deadlineText()));
                    }
                } catch (Exception ignored) {
                    // 엄격 검수 실패 시 이전 후보 유지
                }
            }

            safest = safest(rawText, candidates);
            if (unsafeScore(rawText, safest) > 0) {
                return rawPreservingResult(rawText, category, safest);
            }
            return safest;
        } catch (Exception exception) {
            return fallbackParse(rawText, category);
        }
    }

    private JobPostRefineResult safest(String rawText, List<JobPostRefineResult> candidates) {
        JobPostRefineResult best = candidates.get(0);
        int bestScore = Integer.MAX_VALUE;
        for (JobPostRefineResult candidate : candidates) {
            int unsafe = unsafeScore(rawText, candidate);
            int coverage = coverageScore(rawText, candidate);
            int score = unsafe * 1000 - coverage;
            if (score < bestScore) {
                bestScore = score;
                best = candidate;
            }
        }
        return best;
    }

    private JobPostRefineResult normalize(JsonNode node, JobPostCategory category) {
        String title = limitTitle(firstNonBlank(node, "title", "summary"));
        String refined = firstNonBlank(node, "refined_description", "description");
        String budget = textOrDefault(node.path("budget"), "협의");
        String deadline = textOrDefault(node.path("deadline"), "협의");
        return new JobPostRefineResult(category, title, refined, budget, deadline, true);
    }

    private String firstNonBlank(JsonNode node, String... fields) {
        for (String field : fields) {
            String value = node.path(field).asText("").strip();
            if (!value.isBlank()) {
                return value;
            }
        }
        return "";
    }

    private String textOrDefault(JsonNode node, String defaultValue) {
        String value = node.asText("").strip();
        return value.isBlank() ? defaultValue : value;
    }

    private String limitTitle(String value) {
        String title = value.strip();
        String candidate = title.length() <= 20 ? title : title.substring(0, 20).stripTrailing();
        for (String suffix : new String[] {" 디자", " 시스", " 업데이", " 시", " 및", " 와", " &"}) {
            if (candidate.endsWith(suffix)) {
                candidate = candidate.substring(0, candidate.length() - suffix.length()).stripTrailing();
                break;
            }
        }
        int lastSpace = candidate.lastIndexOf(' ');
        if (lastSpace >= 0 && candidate.length() - lastSpace - 1 <= 1) {
            candidate = candidate.substring(0, lastSpace).stripTrailing();
        }
        return candidate.isBlank() ? title.substring(0, Math.min(20, title.length())).stripTrailing() : candidate;
    }

    private int unsafeScore(String rawText, JobPostRefineResult result) {
        String raw = rawText.replace(" ", "");
        String output = (result.title() + " " + result.refinedDescription()).replace(" ", "");
        int score = 0;

        if (output.contains("#") || result.refinedDescription().contains("-")
                || output.contains("📚") || output.contains("✨") || output.contains("✅")) {
            score++;
        }
        for (String term : RISK_TERMS) {
            String stripped = term.replace(" ", "");
            if (output.contains(stripped) && !raw.contains(stripped)) {
                score++;
                break;
            }
        }
        if (output.contains("자동") && !raw.contains("자동")) {
            score++;
        }
        if ((rawText.contains("가능?") || rawText.contains("될까요") || rawText.contains("해줄수"))
                && PROMISE_PATTERN.matcher(result.refinedDescription()).find()) {
            score++;
        }
        if (URGENT_QUESTION_PATTERN.matcher(rawText).find() && SAME_DAY_WORK_PATTERN.matcher(result.refinedDescription()).find()) {
            score++;
        }

        Set<String> rawNumbers = extractNumbers(rawText);
        Set<String> outputNumbers = extractNumbers(result.refinedDescription());
        if (!rawNumbers.containsAll(outputNumbers)) {
            score++;
        }

        if (raw.contains("화목") && !(output.contains("화요일") && output.contains("목요일"))) {
            score++;
        }
        if (raw.contains("화목") && (output.contains("월요일") || output.contains("수요일")
                || output.contains("금요일") || output.contains("토요일") || output.contains("일요일"))) {
            score++;
        }
        if (rawText.contains("평일") && output.contains("오후") && !rawText.contains("오후")) {
            score++;
        }
        if (result.title().endsWith("디자") || result.title().endsWith("시스") || result.title().endsWith("업데이")) {
            score++;
        }
        return score;
    }

    private boolean needsStrictReview(String rawText, JobPostRefineResult result) {
        return unsafeScore(rawText, result) > 0;
    }

    private int coverageScore(String rawText, JobPostRefineResult result) {
        String output = (result.title() + " " + result.refinedDescription()).replace(" ", "");
        int score = 0;
        for (String number : extractNumbers(rawText)) {
            if (output.replace(",", "").contains(number)) {
                score++;
            }
        }
        for (String term : new String[] {
                "재료", "품절", "취소", "결제", "현장", "주소", "개인정보", "전화", "화목", "평일", "주말", "미정", "제외", "금지", "19세"
        }) {
            String stripped = term.replace(" ", "");
            if (rawText.replace(" ", "").contains(stripped) && output.contains(stripped)) {
                score++;
            }
        }
        if (rawText.contains("화목") && output.contains("화요일") && output.contains("목요일")) {
            score++;
        }
        return score;
    }

    private Set<String> extractNumbers(String text) {
        Set<String> numbers = new HashSet<>();
        Matcher matcher = NUMBER_PATTERN.matcher(text);
        while (matcher.find()) {
            numbers.add(matcher.group().replace(",", ""));
        }
        return numbers;
    }

    private JobPostRefineResult rawPreservingResult(String rawText, JobPostCategory category, JobPostRefineResult candidate) {
        String title = limitTitle(candidate.title());
        return new JobPostRefineResult(
                category,
                title.isBlank() ? "요청 내용 확인" : title,
                "요청 내용은 다음과 같습니다. " + rawText.strip(),
                candidate.budgetText(),
                candidate.deadlineText(),
                true
        );
    }

    private JsonNode extractJson(String text) {
        String trimmed = text.strip().replaceAll("(?m)^```(?:json)?|```$", "").strip();
        try {
            return objectMapper.readTree(trimmed);
        } catch (Exception exception) {
            Matcher matcher = Pattern.compile("\\{.*}", Pattern.DOTALL).matcher(trimmed);
            if (matcher.find()) {
                try {
                    return objectMapper.readTree(matcher.group());
                } catch (Exception nested) {
                    throw new OllamaException("LLM 응답 JSON 파싱 실패", nested);
                }
            }
            throw new OllamaException("LLM 응답 JSON 파싱 실패", exception);
        }
    }

    private String extractBudgetText(String text) {
        Matcher manWon = MAN_WON_PATTERN.matcher(text);
        if (manWon.find()) {
            long amount = Long.parseLong(manWon.group(1)) * 10000;
            return "%,d원".formatted(amount);
        }
        Matcher won = WON_PATTERN.matcher(text);
        if (won.find()) {
            long amount = Long.parseLong(won.group(1).replace(",", ""));
            return "%,d원".formatted(amount);
        }
        return "협의";
    }

    JobPostRefineResult fallbackParse(String rawText, JobPostCategory category) {
        String stripped = rawText.strip();
        String title = stripped.isBlank()
                ? "제목 미정 공고"
                : stripped.lines().findFirst().orElse("제목 미정 공고");
        title = title.length() > 40 ? title.substring(0, 40) : title;

        String description = "원본 요청을 확인해 필요한 산출물과 조건을 정리해야 합니다. "
                + (stripped.isBlank() ? "구체적인 요구사항이 제공되지 않았습니다." : stripped);

        return new JobPostRefineResult(category, title, description, extractBudgetText(rawText), "협의", false);
    }

    Integer parseBudgetAmount(String budgetText) {
        Matcher matcher = Pattern.compile("\\d[\\d,]*").matcher(budgetText);
        if (matcher.find()) {
            try {
                return Integer.parseInt(matcher.group().replace(",", ""));
            } catch (NumberFormatException exception) {
                return null;
            }
        }
        return null;
    }
}
