package com.sharafutdinov.bank_lending_api.user;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.springframework.lang.Nullable;

import java.time.LocalDate;

public record UserRequest (

        @NotEmpty(message = "введите имя")
        @NotNull(message = "введите имя")
        String firstName,

        @NotEmpty(message = "введите фамилию")
        @NotNull(message = "введите фамилию")
        String lastname,

        @Nullable
        String surName,

        @NotNull(message = "выберите дату рождения")
        LocalDate dateOfBirth,

        @NotEmpty(message = "введите серийный номер паспорта")
        @NotNull(message = "введите серийный номер паспорта")
        String passportSerialNumber,

        @NotEmpty(message = "введите адрес регистрации")
        @NotNull(message = "введите адрес регистрации")
        String addressRegister,

        @NotEmpty(message = "введите адрес проживания")
        @NotNull(message = "введите адрес проживания")
        String addressFact,

        @NotEmpty(message = "введите телефон")
        @NotNull(message = "введите телефон")
        String phoneNumber,

        @Email(message = "неверный формат почты")
        @NotEmpty(message = "введите почту")
        @NotNull(message = "введите почту")
        String email
        ) {
}
