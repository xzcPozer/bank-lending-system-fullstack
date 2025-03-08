package com.sharafutdinov.bank_lending_api.credit_request;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record CreditRequestResponse(
        Long id,
        String firstName,
        String lastName,
        String surName,
        LocalDateTime date,
        ProcessingStatus status,
        String descriptionStatus
) {
}
