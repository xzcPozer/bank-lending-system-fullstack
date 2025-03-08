package com.sharafutdinov.bank_lending_api.credit_query;


import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record CreditQueryTaxAgentHiredWorkerRequest(

        @NotEmpty(message = "введите название организации")
        @NotNull(message = "введите название организации")
        String name,

        @NotEmpty(message = "введите ИНН организации")
        @NotNull(message = "введите ИНН организации")
        String inn,

        @NotEmpty(message = "введите КПП организации")
        @NotNull(message = "введите КПП организации")
        String kpp
) {
}
