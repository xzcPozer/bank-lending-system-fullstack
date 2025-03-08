package com.sharafutdinov.bank_lending_api.credit_query;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record CreditQueryFinancialSituationIpRequest(

        @NotEmpty(message = "введите общую сумму дохода")
        @NotNull(message = "введите общую сумму дохода")
        String totalIncome,

        @NotEmpty(message = "введите средний ежемесячный доход")
        @NotNull(message = "введите средний ежемесячный доход")
        String monthlyPayment
) {

}
