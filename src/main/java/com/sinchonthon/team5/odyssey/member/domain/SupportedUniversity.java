package com.sinchonthon.team5.odyssey.member.domain;

import java.util.Arrays;
import java.util.Optional;

public enum SupportedUniversity {

    YONSEI(1L, "yonsei.ac.kr", "연세대학교"),
    EWHA(2L, "ewha.ac.kr", "이화여자대학교"),
    SOGANG(3L, "sogang.ac.kr", "서강대학교"),
    HONGIK(4L, "hongik.ac.kr", "홍익대학교"),
    MYONGJI(5L, "mju.ac.kr", "명지대학교");

    private final Long id;
    private final String emailDomain;
    private final String name;

    SupportedUniversity(Long id, String emailDomain, String name) {
        this.id = id;
        this.emailDomain = emailDomain;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public static Optional<SupportedUniversity> fromId(Long id) {
        return Arrays.stream(values())
                .filter(university -> university.id.equals(id))
                .findFirst();
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
