package com.sinchonthon.team5.odyssey.jobpost.llm;

import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.temporal.TemporalAdjusters;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
class JobPostDeadlineParser {

    private static final ZoneOffset KST = ZoneOffset.ofHours(9);
    private static final Pattern ISO_DATE = Pattern.compile("(\\d{4})[-.](\\d{1,2})[-.](\\d{1,2})");
    private static final Pattern KOREAN_DATE = Pattern.compile("(\\d{1,2})\\s*월\\s*(\\d{1,2})\\s*일");
    private static final Map<String, DayOfWeek> WEEKDAYS = Map.of(
            "월요일", DayOfWeek.MONDAY, "화요일", DayOfWeek.TUESDAY, "수요일", DayOfWeek.WEDNESDAY,
            "목요일", DayOfWeek.THURSDAY, "금요일", DayOfWeek.FRIDAY, "토요일", DayOfWeek.SATURDAY,
            "일요일", DayOfWeek.SUNDAY
    );

    OffsetDateTime parse(String text, LocalDate today) {
        if (text == null || text.isBlank() || text.equals("협의")) {
            return null;
        }

        Matcher iso = ISO_DATE.matcher(text);
        if (iso.find()) {
            return toEndOfDay(LocalDate.of(
                    Integer.parseInt(iso.group(1)), Integer.parseInt(iso.group(2)), Integer.parseInt(iso.group(3))
            ));
        }

        Matcher korean = KOREAN_DATE.matcher(text);
        if (korean.find()) {
            int month = Integer.parseInt(korean.group(1));
            int day = Integer.parseInt(korean.group(2));
            LocalDate candidate = LocalDate.of(today.getYear(), month, day);
            if (candidate.isBefore(today)) {
                candidate = candidate.plusYears(1);
            }
            return toEndOfDay(candidate);
        }

        if (text.contains("오늘")) {
            return toEndOfDay(today);
        }
        if (text.contains("내일")) {
            return toEndOfDay(today.plusDays(1));
        }
        if (text.contains("모레")) {
            return toEndOfDay(today.plusDays(2));
        }

        for (Map.Entry<String, DayOfWeek> entry : WEEKDAYS.entrySet()) {
            if (text.contains(entry.getKey())) {
                LocalDate next = today.with(TemporalAdjusters.nextOrSame(entry.getValue()));
                if (text.contains("다음") || text.contains("담주")) {
                    next = next.plusWeeks(1);
                }
                return toEndOfDay(next);
            }
        }

        return null;
    }

    private OffsetDateTime toEndOfDay(LocalDate date) {
        return date.atTime(23, 59, 59).atOffset(KST);
    }
}
