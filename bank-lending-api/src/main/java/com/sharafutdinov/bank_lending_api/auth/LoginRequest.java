package com.sharafutdinov.bank_lending_api.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginRequest {

    @Email(message = "Неправильный формат почты")
    @NotEmpty(message = "Введите почту")
    @NotNull(message = "Введите почту")
    private String email;

    @NotEmpty(message = "Введите пароль")
    @NotNull(message = "Введите пароль")
    private String password;
}
