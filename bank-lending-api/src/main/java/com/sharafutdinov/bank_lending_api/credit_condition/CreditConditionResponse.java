package com.sharafutdinov.bank_lending_api.credit_condition;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreditConditionResponse {
    private Long id;
    private Double interestRate;
    private Integer term;
    private Double monthlyPayment;
    private String productName;
}
