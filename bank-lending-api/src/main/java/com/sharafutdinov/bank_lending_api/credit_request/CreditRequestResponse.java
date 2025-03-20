package com.sharafutdinov.bank_lending_api.credit_request;

import com.sharafutdinov.bank_lending_api.credit_query.WorkerType;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record CreditRequestResponse(
        Long id,
        Long userId,
        String firstName,
        String lastName,
        String surName,
        LocalDateTime date,
        ProcessingStatus status,
        WorkerType type,
        Integer amount,
        String descriptionStatus
) {
}
