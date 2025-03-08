package com.sharafutdinov.bank_lending_api.credit_request;

import com.sharafutdinov.bank_lending_api.common.PageResponse;
import org.springframework.security.core.Authentication;
import org.springframework.web.multipart.MultipartFile;

import java.rmi.server.ExportException;

public interface CreditRequestService {

    Long addCreditRequest(CreditRequestDto request, MultipartFile solvency, MultipartFile employment) throws ExportException;

    Long addCreditRequest(CreditRequestDto request, MultipartFile solvency) throws ExportException;

    PageResponse<CreditRequestResponse> getAllCreditRequest(int page, int size);

    PageResponse<CreditRequestResponse> getAllCreditRequest(int page, int size, boolean isProcessed);

    PageResponse<CreditRequestResponse> getAllCreditByUserIdRequest(int page, int size, Authentication connectedUser);

    Long changeCreditRequest(MultipartFile solvency, MultipartFile employment, Authentication connectedUser, Long creditRequestId) throws ExportException;

    Long changeCreditRequest(MultipartFile solvency, Authentication connectedUser, Long creditRequestId) throws ExportException;

    PageResponse<CreditRequestResponseForDirector> getAllCreditRequestWithLendingOfficer(int page, int size);
}
