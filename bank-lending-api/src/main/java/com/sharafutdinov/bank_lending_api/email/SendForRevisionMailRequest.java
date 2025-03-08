package com.sharafutdinov.bank_lending_api.email;

import lombok.Builder;

@Builder
public record SendForRevisionMailRequest(
        String firstName,
        String surName,
        String description,
        String to,
        String from
) {
}
