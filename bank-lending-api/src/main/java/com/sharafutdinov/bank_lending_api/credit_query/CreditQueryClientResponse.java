package com.sharafutdinov.bank_lending_api.credit_query;


import lombok.Builder;

@Builder
public record CreditQueryClientResponse(
        Long id,
        String firstName,
        String lastName,
        String surName,
        String name,
        String inn,
        String kpp,
        String totalIncome,
        String taxCalculationAmount,
        String monthlyPayment,
        boolean immovableProperty,
        boolean movableProperty
) {
}
