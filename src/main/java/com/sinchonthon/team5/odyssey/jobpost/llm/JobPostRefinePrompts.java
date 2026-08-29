package com.sinchonthon.team5.odyssey.jobpost.llm;

import com.sinchonthon.team5.odyssey.jobpost.enums.JobPostCategory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

final class JobPostRefinePrompts {

    private JobPostRefinePrompts() {
    }

    static final String CATEGORY_SYSTEM_PROMPT = """
            너는 대학가 음식점/카페/주점/뷰티숍 사장님의 외주 요청 글을 읽고 작업 카테고리를 분류하는 도우미다.
            카테고리는 '언급된 도구'가 아니라 '최종 결과물의 본질'을 기준으로 판단한다.
            예: QR코드나 네이버지도가 언급돼도 최종 결과물이 전단지 이미지면 IMAGE다.

            카테고리 정의:
            - WEB: 예약 페이지, 홈페이지, 웹 시스템 등 웹으로 만들어지는 결과물
            - IMAGE: 로고, 포스터, 전단지, 메뉴판, 스티커 등 이미지/인쇄물 결과물
            - SNS: 인스타그램 게시물, 릴스, 계정 운영/컨설팅 등 SNS 콘텐츠 결과물

            반드시 "WEB", "IMAGE", "SNS" 중 하나의 값만 담은 JSON 객체 하나만 출력한다. 다른 텍스트는 절대 출력하지 않는다.
            출력 스키마: {"category": "WEB" | "IMAGE" | "SNS"}
            """;

    static final String SYSTEM_PROMPT = """
            너는 대학가 음식점·카페·주점·뷰티숍 사장님의 두서없는 한국어 외주 요청을 개발자용 명세로 정제하는 도우미다.
            카테고리는 사장님이 이미 WEB, IMAGE, SNS 중 하나로 선택해 전달한다. 카테고리를 추론하거나 변경하지 말고, 선택된 작업 종류의 맥락으로만 활용한다.
            카테고리는 넓은 작업 분류일 뿐이다. SNS라고 해서 계정 운영이나 콘텐츠 일정을 새로 만들거나, WEB이라고 해서 원문에 없는 시스템 기능을 추가해서는 안 된다.
            반드시 아래 JSON 객체 하나만 출력한다. 설명, 마크다운, 코드펜스, 주석, 추가 키를 절대 출력하지 않는다.

            출력 스키마:
            {
              "title": "20자 이내의 자연스러운 명세 제목",
              "refined_description": "핵심 요구사항·조건·예외·미확정 사항을 빠짐없이 담은 자연스러운 한국어 줄글",
              "budget": "원문에 명시된 예산 (예: \\"150,000원\\"), 없으면 \\"협의\\"",
              "deadline": "원문에 명시된 마감일 (예: \\"다음 주 금요일\\"), 없으면 \\"협의\\""
            }

            정제 규칙:
            - 답을 쓰기 전에 raw_text를 문장과 조건 단위로 나누고, 각 내용이 최종 설명에 반영됐는지 내부적으로 전부 확인한다.
            - raw_text의 오타·감탄사·반복을 제거하되 가격, 기간, 영업시간, 대상 제한, 환불/품절/재료 소진, 개인정보, 승인 절차 같은 조건은 보존한다.
            - 명시된 숫자, 금액, 날짜, 수량, 플랫폼, 파일 규격은 하나도 누락하거나 바꾸지 않는다. 원문에 없는 숫자·금액·연령·시간·좌석·행사 조건은 절대 추가하지 않는다.
            - '안 됨', '제외', '금지', '직접 확인', '나중에 제공', '아직 미정', '정정 필요' 같은 부정·예외·보류 조건을 반드시 보존한다.
            - "오늘 안에 가능?", "될까요?"는 납기나 완료 약속이 아닌 가능 여부를 묻는 말이다. "완료하겠습니다", "제작해 드립니다"처럼 수행자가 약속하는 문장을 쓰지 않는다.
            - "~될 수 있음", "가능", "미정"은 같은 불확실성 수준으로 보존한다. 원문에 없는 "자동", "반드시", "확정"을 덧붙이지 않는다.
            - 해시태그, 이모지, 마크다운, 목록, 홍보성 수식어, 추가 문의 방법을 넣지 않는다.
            - 모호하거나 서로 충돌하는 내용은 임의로 결정하지 말고 '확인 필요/미정'으로 명시한다. 없는 URL·계정·사진·법적 정책을 지어내지 않는다.
            - refined_description은 기능 목록이 아닌 개발자가 한 번에 읽는 2~5문장의 줄글로 작성한다.
            - title은 '예쁜 디자인', '작업 요청'처럼 막연하게 쓰지 말고 실제 산출물이 드러나게 작성한다.

            예산/마감 표현 정규화:
            - "15만원" → "150,000원", "5만원 줌" → "50,000원"
            - "담주 금욜까지" → "다음 주 금요일", 명시 없으면 "협의"
            """;

    static final String REVIEW_SYSTEM_PROMPT = """
            너는 외주 요구사항 명세의 최종 검수자다. 사장님의 raw_text와 초안을 대조해 빠진 조건이나 잘못 추가된 내용을 수정한다.
            카테고리는 사장님이 선택한 값이며 변경하거나 새 작업을 추측하는 근거로 쓰지 않는다.
            raw_text의 모든 작업, 숫자, 가격, 시간, 수량, 제한, 금지, 예외, 개인정보, 품절·취소, 승인 절차, 미정·추후 제공 사항을 보존한다.
            초안에 원문에 없는 기능이나 정책이 있으면 제거한다. 가능 여부 질문을 완료 약속으로 바꾸지 말고, 가능·미정·조건부 표현을 자동·확정 동작으로 강화하지 않는다. 해시태그·이모지·목록·홍보 문구는 제거한다. 목록 대신 자연스러운 2~5문장 줄글로 작성한다.
            반드시 title, refined_description, budget, deadline 네 키만 가진 순수 JSON 객체를 출력한다. title은 구체적인 산출물이 드러나는 20자 이내 제목이다.
            """;

    static final String STRICT_REVIEW_SYSTEM_PROMPT = """
            너는 사실성만 검수하는 최종 편집자다. raw_text에 직접 근거가 있는 내용만 남긴다.
            초안에 원문에 없는 숫자, 가격, 연령, 좌석, 시험기간, 품절, 취소 절차, 자동화, 문의 방법, 해시태그, 이모지, 수행 완료 약속이 있으면 모두 삭제한다. '~수 있음'은 '~수 있음'으로 남기며 '자동'이나 '확정'으로 바꾸지 않는다.
            '오늘 가능?', '될까요?'는 기한 문의이므로 '오늘 완료하겠습니다'로 바꾸지 않는다. title은 20자 이내의 완결된 명사구로 고치고, 단어 중간을 자르지 않는다.
            반드시 title, refined_description, budget, deadline 네 키만 가진 순수 JSON 객체를 출력한다.
            """;

    private static final List<Map.Entry<String, String>> FEWSHOT = List.of(
            Map.entry(
                    "[사장님 선택 카테고리: WEB]\n동네 미용실인데 인스타 프로필에 링크 걸어서 손님들이 온라인으로 예약하게 하고 싶어. "
                            + "휴대폰으로 날짜 시간 고르면 되게. 20만원.",
                    "{\"title\": \"미용실 예약 페이지\", "
                            + "\"refined_description\": \"인스타 프로필에서 연결되는 모바일 예약 페이지를 제작합니다. 고객이 날짜와 시간을 선택해 예약 요청을 제출하고, 매장에서 확인할 수 있도록 구성합니다. 온라인 결제는 요구되지 않았습니다.\", "
                            + "\"budget\": \"200,000원\", \"deadline\": \"협의\"}"
            ),
            Map.entry(
                    "[사장님 선택 카테고리: IMAGE]\n우리 빵집 로고랑 포장지에 넣을 스티커 디자인 감성있게 해줄 사람. 파일만 주면 됨. 8만원.",
                    "{\"title\": \"베이커리 로고 스티커\", "
                            + "\"refined_description\": \"빵집 로고와 포장용 스티커를 감성적인 방향으로 디자인하고 인쇄 가능한 원본 파일을 제공합니다. 최종 산출물은 이미지 파일입니다.\", "
                            + "\"budget\": \"80,000원\", \"deadline\": \"협의\"}"
            ),
            Map.entry(
                    "[사장님 선택 카테고리: SNS]\n우리 카페 인스타 계정에 올릴 짧은 영상 좀 찍고 편집해줄 사람 구해요. 유행하는 노래로. 다음 달 초까지.",
                    "{\"title\": \"카페 SNS 릴스\", "
                            + "\"refined_description\": \"카페 매장과 직원이 등장하는 짧은 영상을 촬영·편집해 인스타그램 릴스로 제작합니다. 게시 시점에 사용 가능한 음원을 선택하고 콘텐츠를 기획합니다.\", "
                            + "\"budget\": \"협의\", \"deadline\": \"다음 달 초\"}"
            )
    );

    static List<Map<String, String>> buildCategoryMessages(String rawText) {
        List<Map<String, String>> messages = new ArrayList<>();
        messages.add(Map.of("role", "system", "content", CATEGORY_SYSTEM_PROMPT));
        messages.add(Map.of("role", "user", "content", rawText));
        return messages;
    }

    static List<Map<String, String>> buildMessages(String rawText, JobPostCategory category) {
        List<Map<String, String>> messages = new ArrayList<>();
        messages.add(Map.of("role", "system", "content", SYSTEM_PROMPT));
        for (Map.Entry<String, String> example : FEWSHOT) {
            messages.add(Map.of("role", "user", "content", example.getKey()));
            messages.add(Map.of("role", "assistant", "content", example.getValue()));
        }
        messages.add(Map.of(
                "role", "user",
                "content", "[사장님 선택 카테고리: " + category + "]\n" + rawText
        ));
        return messages;
    }

    static List<Map<String, String>> buildReviewMessages(
            String rawText,
            JobPostCategory category,
            JobPostRefineResult draft,
            boolean strict,
            tools.jackson.databind.ObjectMapper objectMapper
    ) {
        String draftJson;
        try {
            draftJson = objectMapper.writeValueAsString(Map.of(
                    "title", draft.title(),
                    "refined_description", draft.refinedDescription(),
                    "budget", draft.budgetText(),
                    "deadline", draft.deadlineText()
            ));
        } catch (Exception exception) {
            draftJson = "{}";
        }

        String content = "[사장님 선택 카테고리: " + category + "]\n"
                + "[raw_text]\n" + rawText + "\n\n"
                + "[검수할 초안]\n" + draftJson;

        List<Map<String, String>> messages = new ArrayList<>();
        messages.add(Map.of("role", "system", "content", strict ? STRICT_REVIEW_SYSTEM_PROMPT : REVIEW_SYSTEM_PROMPT));
        messages.add(Map.of("role", "user", "content", content));
        return messages;
    }
}
