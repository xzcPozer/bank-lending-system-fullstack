package com.sharafutdinov.bank_lending_api.credit_query;

import com.sharafutdinov.bank_lending_api.bank_db.entity.CreditQueryInfo;
import org.springframework.stereotype.Service;

@Service
public class CreditQueryInfoMapper {

    public CreditQueryClientResponse toResponse(CreditQueryInfo query) {
        return CreditQueryClientResponse.builder()
                .id(query.getId())
                .firstName(query.getCreditRequest().getUser().getFirstName())
                .lastName(query.getCreditRequest().getUser().getLastName())
                .surName(query.getCreditRequest().getUser().getSurName())
                .name(query.getFinancialSituation().get("наименование").toString())
                .inn(query.getFinancialSituation().get("инн").toString())
                .kpp(query.getFinancialSituation().get("кпп") == null ? "" : query.getFinancialSituation().get("кпп").toString())
                .totalIncome(query.getFinancialSituation().get("общая сумма дохода").toString())
                .taxCalculationAmount(query.getFinancialSituation().get("сумма налога исчисленная") == null ? "" : query.getFinancialSituation().get("сумма налога исчисленная").toString())
                .monthlyPayment(query.getFinancialSituation().get("средний ежемесячный доход").toString())
                .movableProperty((boolean) query.getFinancialSituation().get("движимое имущество"))
                .immovableProperty((boolean) query.getFinancialSituation().get("недвижимое имущество"))
                .build();
    }
}
