package com.sharafutdinov.bank_lending_api.email;

import com.sharafutdinov.bank_lending_api.credit_condition.CreditConditionRequest;
import lombok.Builder;

@Builder
public record SendApprovedMailRequest(
        String firstName,
        String surName,
        String to,
        String from,
        CreditConditionRequest creditConditionRequest
) {
}
