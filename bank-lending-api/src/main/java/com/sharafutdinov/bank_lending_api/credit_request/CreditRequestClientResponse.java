package com.sharafutdinov.bank_lending_api.credit_request;

import lombok.Builder;

@Builder
public record CreditRequestClientResponse (
        Long id,
        String loanPurpose,
        Integer sum,
        ProcessingStatus status,
        String descriptionStatus
) {
}
