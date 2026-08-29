package com.sinchonthon.team5.odyssey.member.domain;

import java.util.Arrays;
import java.util.Optional;

public enum SupportedUniversity {

    YONSEI(1L, "yonsei.ac.kr"),
    EWHA(2L, "ewha.ac.kr"),
    SOGANG(3L, "sogang.ac.kr");

    private final Long id;
    private final String emailDomain;

    SupportedUniversity(Long id, String emailDomain) {
        this.id = id;
        this.emailDomain = emailDomain;
    }

    public Long getId() {
        return id;
    }

    public static Optional<SupportedUniversity> fromEmail(String email) {
        int atIndex = email.lastIndexOf('@');
        if (atIndex < 0 || atIndex == email.length() - 1) {
            return Optional.empty();
        }

        String domain = email.substring(atIndex + 1);
        return Arrays.stream(values())
                .filter(university -> university.emailDomain.equals(domain))
                .findFirst();
    }
}
