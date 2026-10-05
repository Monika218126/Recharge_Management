package com.monika.recharge.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record RechargeRequest(
        @NotNull Long mobileId,
        @NotNull Long planId,
        @NotBlank String paymentMethod
) {}
