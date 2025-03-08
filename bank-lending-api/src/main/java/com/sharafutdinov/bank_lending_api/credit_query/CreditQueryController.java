package com.sharafutdinov.bank_lending_api.credit_query;

import com.sharafutdinov.bank_lending_api.common.PageResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.rmi.server.ExportException;

@RestController
@RequestMapping("credit-query")
@RequiredArgsConstructor
@Tag(name = "CreditQuery")
@PreAuthorize("hasRole('ROLE_CREDIT_OFFICER')")
public class CreditQueryController {

    private final CreditQueryService service;

    @PostMapping("/confirmation-solvency/{userId}")
    public ResponseEntity<Long> confirmationOfSolvencyPdf(
            @PathVariable Long userId,
            Authentication authentication
    ) throws ExportException {
        return ResponseEntity.ok(service.confirmationOfSolvency(userId, authentication));
    }

    @PostMapping("/confirmation-employment/{userId}")
    public ResponseEntity<Long> confirmationOfEmploymentPdf(
            @PathVariable Long userId,
            Authentication authentication
    ) {
        return ResponseEntity.ok(service.confirmationOfAccountStatus(userId, authentication));
    }

    @PostMapping("/generate-query/{userId}")
    public ResponseEntity<Long> createCreditQueryPdf(
            @PathVariable Long userId,
            Authentication authentication
    ) {
        return ResponseEntity.ok(service.generateCreditQuery(userId, authentication));
    }

    @PutMapping("/edit/solvency-verify/{userId}")
    public ResponseEntity<Long> solvencyVerifyPdf(
            @PathVariable Long userId,
            Authentication authentication
    ) {
        return ResponseEntity.ok(service.solvencyVerify(userId, authentication));
    }

    @PutMapping("/edit/employment-verify/{userId}")
    public ResponseEntity<Long> employmentVerifyPdf(
            @PathVariable Long userId,
            Authentication authentication
    ) {
        return ResponseEntity.ok(service.accountStatusVerify(userId, authentication));
    }

    @PutMapping("/edit/query-verify/{userId}")
    public ResponseEntity<Long> creditQueryVerifyPdf(
            @PathVariable Long userId,
            Authentication authentication
    ) {
        return ResponseEntity.ok(service.creditQueryVerify(userId, authentication));
    }

    @GetMapping("/get/solvency/{userId}")
    public ResponseEntity<InputStreamResource> getSolvencyInformation(
            @PathVariable Long userId
    ) throws FileNotFoundException {
        File file = service.getSolvencyPdfInfoByUserId(userId);

        FileInputStream fileInputStream = new FileInputStream(file);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + file.getName() + "\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(fileInputStream));
    }

    @GetMapping("/get/payment/{userId}")
    public ResponseEntity<InputStreamResource> getPaymentInformation(
            @PathVariable Long userId
    ) throws FileNotFoundException {
        File file = service.getPaymentPdfInfoByUserId(userId);

        FileInputStream fileInputStream = new FileInputStream(file);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + file.getName() + "\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(fileInputStream));
    }

    @GetMapping("/get/credit-request/{userId}")
    public ResponseEntity<InputStreamResource> getCreditRequestInformation(
            @PathVariable Long userId
    ) throws FileNotFoundException {
        File file = service.getCreditRequestPdfInfoByUserId(userId);

        FileInputStream fileInputStream = new FileInputStream(file);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + file.getName() + "\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(fileInputStream));
    }

    @GetMapping("/get/solvency/upload/{userId}")
    public ResponseEntity<InputStreamResource> getSolvencyUploadInformation(
            @PathVariable Long userId
    ) throws FileNotFoundException {
        File file = service.getSolvencyUploadPdfInfoByUserId(userId);

        FileInputStream fileInputStream = new FileInputStream(file);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + file.getName() + "\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(fileInputStream));
    }

    @GetMapping("/get/payment/upload/{userId}")
    public ResponseEntity<InputStreamResource> getPaymentUploadInformation(
            @PathVariable Long userId
    ) throws FileNotFoundException {
        File file = service.getPaymentUploadPdfInfoByUserId(userId);

        FileInputStream fileInputStream = new FileInputStream(file);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + file.getName() + "\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(fileInputStream));
    }

    @GetMapping("/all-client")
    public ResponseEntity<PageResponse<CreditQueryClientResponse>> getPaymentInformation(
            @RequestParam(name = "page", defaultValue = "0", required = false) int page,
            @RequestParam(name = "size", defaultValue = "10", required = false) int size
    ) {
        return ResponseEntity.ok(service.getAllClientCreditQueryInfo(page, size));
    }

    @PutMapping("/change/solvency/hired-worker/tax-agent-info/{userId}")
    public ResponseEntity<Long> changeSolvencyHiredWorkerTaxAgent(
            @PathVariable Long userId,
            @RequestBody @Valid CreditQueryTaxAgentHiredWorkerRequest request,
            Authentication authentication
    ) {
        return ResponseEntity.ok(service.changeSolvencyHiredWorkerTaxAgentByUserId(userId, request, authentication));
    }

    @PutMapping("/change/solvency/hired-worker/financial-situation/{userId}")
    public ResponseEntity<Long> changeSolvencyHiredWorkerFinancialSituation(
            @PathVariable Long userId,
            @RequestBody @Valid CreditQueryFinancialSituationHiredWorkerRequest request,
            Authentication authentication
    ) {
        return ResponseEntity.ok(service.changeSolvencyHiredWorkerFinancialSituationByUserId(userId, request, authentication));
    }

    @PutMapping("/change/solvency/ip/tax-agent-info/{userId}")
    public ResponseEntity<Long> changeSolvencyIpTaxAgent(
            @PathVariable Long userId,
            @RequestBody @Valid CreditQueryTaxAgentIpRequest request,
            Authentication authentication
    ) {
        return ResponseEntity.ok(service.changeSolvencyIpTaxAgentByUserId(userId, request, authentication));
    }

    @PutMapping("/change/solvency/ip/financial-situation/{userId}")
    public ResponseEntity<Long> changeSolvencyIpFinancialSituation(
            @PathVariable Long userId,
            @RequestBody @Valid CreditQueryFinancialSituationIpRequest request,
            Authentication authentication
    ) {
        return ResponseEntity.ok(service.changeSolvencyIpFinancialSituationByUserId(userId, request, authentication));
    }

    @PostMapping("/send/refuse/{userId}")
    public ResponseEntity<Long> sendRefuseRequest(
            @PathVariable Long userId,
            @RequestPart String description
    ) throws MessagingException {
        return ResponseEntity.ok(service.sendRefuseData(userId, description));
    }
}
