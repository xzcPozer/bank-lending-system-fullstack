package com.sharafutdinov.bank_lending_api.credit_query;

import com.sharafutdinov.bank_lending_api.common.PageResponse;
import jakarta.mail.MessagingException;
import org.springframework.security.core.Authentication;

import java.io.File;
import java.rmi.server.ExportException;

public interface CreditQueryService {
    Long confirmationOfSolvency(Long userId, Authentication authentication) throws ExportException;
    Long confirmationOfAccountStatus(Long userId, Authentication authentication);
    Long generateCreditQuery(Long userId, Authentication authentication);
    Long accountStatusVerify(Long userId, Authentication authentication);
    Long solvencyVerify(Long userId, Authentication authentication);
    Long creditQueryVerify(Long userId, Authentication authentication);

    File getSolvencyPdfInfoByUserId(Long userId);

    Long sendRefuseData(Long userId, String description) throws MessagingException;

    File getPaymentPdfInfoByUserId(Long userId);

    PageResponse<CreditQueryClientResponse> getAllClientCreditQueryInfo(int page, int size);

    Long changeSolvencyHiredWorkerTaxAgentByUserId(Long userId, CreditQueryTaxAgentHiredWorkerRequest request, Authentication authentication);

    Long changeSolvencyHiredWorkerFinancialSituationByUserId(Long userId, CreditQueryFinancialSituationHiredWorkerRequest request, Authentication authentication);

    Long changeSolvencyIpTaxAgentByUserId(Long userId, CreditQueryTaxAgentIpRequest request, Authentication authentication);

    Long changeSolvencyIpFinancialSituationByUserId(Long userId, CreditQueryFinancialSituationIpRequest request, Authentication authentication);

    File getSolvencyUploadPdfInfoByUserId(Long userId);

    File getPaymentUploadPdfInfoByUserId(Long userId);

    File getCreditRequestPdfInfoByUserId(Long userId);
}
