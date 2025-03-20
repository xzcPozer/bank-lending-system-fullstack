package com.sharafutdinov.bank_lending_api.client_information;

import com.sharafutdinov.bank_lending_api.bank_db.entity.ClientInformation;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class ClientInformationMapper {

    public ClientInformationResponse toResponse(ClientInformation info) {
        String previousLoans = info.getPreviousLoans() == null ? "" : getPreviousLoansToString(info.getPreviousLoans());

        return ClientInformationResponse.builder()
                .firstName(info.getUser().getFirstName())
                .lastName(info.getUser().getLastName())
                .surName(info.getUser().getSurName())
                .passportSerialNumber(info.getUser().getPassportSerialNumber())
                .balance(info.getBalance())
                .previousLoansInfo(previousLoans)
                .build();
    }

    private String getPreviousLoansToString(List<Map<String, Object>> previousLoans) {
        StringBuilder builder = new StringBuilder();

        for (var map : previousLoans) {
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                builder.append(entry.getKey()).append(" : ").append(entry.getValue());
                builder.append("\n");
            }
            builder.append("\n");
        }

        return builder.toString();
    }
}
