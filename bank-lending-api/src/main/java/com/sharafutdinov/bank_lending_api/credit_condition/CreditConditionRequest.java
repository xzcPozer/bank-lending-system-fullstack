package com.sharafutdinov.bank_lending_api.credit_condition;

import lombok.Builder;

@Builder
public record CreditConditionRequest(
        Long id,
        Integer amount,
        Integer term,
        Double interestRate,
        Double monthlyPayment
) {
}
