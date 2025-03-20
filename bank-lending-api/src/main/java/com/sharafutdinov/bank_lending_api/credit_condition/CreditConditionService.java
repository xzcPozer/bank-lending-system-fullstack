package com.sharafutdinov.bank_lending_api.credit_condition;

import jakarta.mail.MessagingException;

import java.util.List;

public interface CreditConditionService {
    CreditConditionResponse selectBestConditions(Long request) throws MessagingException;
    Long sendBestCondition(CreditConditionRequest request, Long userId) throws MessagingException;
    CreditConditionResponse getConditionByName(String name);
    Double recalculationOfMonthlyPayment(PaymentCalculationRequest request);

    List<String> getAllConditionNames();
}
