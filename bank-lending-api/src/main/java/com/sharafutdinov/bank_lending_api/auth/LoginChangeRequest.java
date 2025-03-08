package com.sharafutdinov.bank_lending_api.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginChangeRequest {

    @Email(message = "Неправильный формат почты")
    @NotEmpty(message = "Введите почту")
    @NotNull(message = "Введите почту")
    private String email;

    @NotEmpty(message = "Введите старый пароль")
    @NotNull(message = "Введите старый пароль")
    private String oldPassword;

    @NotEmpty(message = "Введите новый пароль")
    @NotNull(message = "Введите новый пароль")
    private String newPassword;

    @NotEmpty(message = "Подтвердите новый пароль")
    @NotNull(message = "Подтвердите новый пароль")
    private String confirmNewPassword;
}
