package com.sharafutdinov.bank_lending_api.credit_request;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record CreditRequestResponseForDirector(
        Long id,
        String clientFirstName,
        String clientLastName,
        String clientSurName,
        LocalDateTime date,
        ProcessingStatus status,
        String lendingOfficerFirstName,
        String lendingOfficerLastName,
        String lendingOfficerSurName
) {
}
