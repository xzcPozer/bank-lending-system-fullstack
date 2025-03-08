package com.sharafutdinov.bank_lending_api.client_information;

import lombok.Builder;

@Builder
public record ClientInformationResponse(

        String firstName,
        String lastName,
        String surName,
        String passportSerialNumber,
        Double balance,
        String previousLoansInfo
) {
}
