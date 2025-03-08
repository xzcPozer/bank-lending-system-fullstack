package com.sharafutdinov.bank_lending_api.credit_condition;

import lombok.Builder;

@Builder
public record PaymentCalculationRequest (
        Integer amount,
        Integer term,
        Double interestRate
) {
}
