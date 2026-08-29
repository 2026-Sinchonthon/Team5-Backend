package com.sinchonthon.team5.odyssey.auth.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record StudentSignUpRequest(
        @NotBlank @Email String email,
        @NotBlank @Size(min = 8, max = 72) String password,
        @NotBlank @Size(max = 50) String name,
        @Size(max = 100) String major,
        @Size(max = 500) String introduction
) {
}
