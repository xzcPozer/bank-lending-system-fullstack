package com.sharafutdinov.bank_lending_api.credit_condition;

import com.sharafutdinov.bank_lending_api.bank_db.entity.CreditCondition;
import org.springframework.stereotype.Service;

@Service
public class CreditConditionMapper {

    public CreditConditionResponse toResponse(CreditCondition condition){
        return CreditConditionResponse.builder()
                .id(condition.getId())
                .productName(condition.getProductName())
                .interestRate(condition.getInterestRate())
                .term(condition.getTermMax())
                .build();
    }
}
