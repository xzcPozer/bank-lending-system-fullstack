package com.sharafutdinov.bank_lending_api.email;

import lombok.Builder;

@Builder
public record SendRefuseMailRequest (
        String firstName,
        String surName,
        String to,
        String from,
        Integer amount
) {
}
