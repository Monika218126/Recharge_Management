package com.monika.recharge.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record MobileRequest(
        @NotNull Long customerId,
        @NotBlank @Pattern(regexp = "\\d{10,15}", message = "Mobile number must contain 10 to 15 digits")
        String mobileNumber,
        @NotBlank String operatorName,
        String circleName
) {}
