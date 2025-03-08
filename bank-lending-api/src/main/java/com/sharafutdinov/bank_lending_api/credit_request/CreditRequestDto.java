package com.sharafutdinov.bank_lending_api.credit_request;

import com.sharafutdinov.bank_lending_api.credit_query.WorkerType;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.lang.Nullable;

import java.util.Map;

@Data
public class CreditRequestDto {
    @NotEmpty(message = "ошибка передачи данных о пользователе")
    @NotNull(message = "ошибка передачи данных о пользователе")
    private Long userId;

    @NotEmpty(message = "выберите цель кредита")
    @NotNull(message = "выберите цель кредита")
    private String loanPurpose;

    @NotEmpty(message = "выберите сумму кредита")
    @NotNull(message = "выберите сумму кредита")
    @Min(value = 10000, message = "сумма должна быть больше 10 000")
    @Max(value = 10_000_000, message = "сумма должна быть не больше 10 000 000")
    private Integer sum;

    @Nullable
    private boolean immovableProperty;

    @Nullable
    private boolean movableProperty;

    @NotNull(message = "выберите кем вы работаете")
    private WorkerType type;

    @Nullable
    private Map<String, Object> currentLoans;

    public CreditRequestDto(){
        this.immovableProperty = false;
        this.movableProperty = false;
    }
}
