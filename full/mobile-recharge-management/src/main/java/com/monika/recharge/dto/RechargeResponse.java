package com.monika.recharge.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record RechargeResponse(
        Long rechargeId,
        String customerName,
        String mobileNumber,
        String operatorName,
        String planName,
        BigDecimal amount,
        Integer validityDays,
        LocalDateTime rechargeDate,
        String paymentMethod,
        String transactionRef,
        String rechargeStatus,
        String mobileStatus,
        java.time.LocalDate validityEndDate
) {}
