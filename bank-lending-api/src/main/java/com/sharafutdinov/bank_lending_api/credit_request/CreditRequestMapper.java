package com.sharafutdinov.bank_lending_api.credit_request;

import com.sharafutdinov.bank_lending_api.bank_db.entity.CreditRequest;
import org.springframework.stereotype.Service;

@Service
public class CreditRequestMapper {

    public CreditRequestResponse toResponse(CreditRequest request) {
        return CreditRequestResponse.builder()
                .id(request.getId())
                .userId(request.getUser().getId())
                .firstName(request.getUser().getFirstName())
                .lastName(request.getUser().getLastName())
                .surName(request.getUser().getSurName())
                .date(request.getLastModifiedDate() != null ? request.getLastModifiedDate() : request.getCreatedDate())
                .status(request.getStatus())
                .type(request.getType())
                .amount(request.getSum())
                .descriptionStatus(request.getDescriptionStatus())
                .build();
    }

    public CreditRequestClientResponse toClientResponse(CreditRequest request) {
        return CreditRequestClientResponse.builder()
                .id(request.getId())
                .loanPurpose(request.getLoanPurpose())
                .sum(request.getSum())
                .status(request.getStatus())
                .descriptionStatus(request.getDescriptionStatus())
                .build();
    }



    public CreditRequestResponseForDirector toDirectorResponse(CreditRequest request) {
        return CreditRequestResponseForDirector.builder()
                .id(request.getId())
                .clientFirstName(request.getUser().getFirstName())
                .clientLastName(request.getUser().getLastName())
                .clientSurName(request.getUser().getSurName())
                .date(request.getLastModifiedDate() != null ? request.getLastModifiedDate() : request.getCreatedDate())
                .status(request.getStatus())
                .lendingOfficerFirstName(request.getLendingOfficer().getFirstName())
                .lendingOfficerLastName(request.getLendingOfficer().getLastName())
                .lendingOfficerSurName(request.getLendingOfficer().getSurName())
                .build();
    }
}
