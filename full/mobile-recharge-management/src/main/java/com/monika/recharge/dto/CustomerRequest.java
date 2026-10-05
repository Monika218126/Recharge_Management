package com.monika.recharge.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record CustomerRequest(
        @NotBlank String fullName,
        @NotBlank @Email String email,
        String address,
        @NotBlank String password
) {}