package com.sharafutdinov.bank_lending_api.user;

import lombok.Builder;

import java.time.LocalDate;

@Builder
public record UserResponse (
        Long id,
        String firstName,
        String lastname,
        String surName,
        LocalDate dateOfBirth,
        String passportSerialNumber,
        String addressRegister,
        String addressFact,
        String phoneNumber,
        String email
) {
}
