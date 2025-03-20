package com.sharafutdinov.bank_lending_api.credit_condition;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("credit-condition")
@RequiredArgsConstructor
@Tag(name = "CreditCondition")
@PreAuthorize("hasRole('ROLE_CREDIT_OFFICER')")
public class CreditConditionController {

    private final CreditConditionService service;

    @GetMapping("/best-condition/{userId}")
    public ResponseEntity<CreditConditionResponse> getBestConditionForUser(
            @PathVariable Long userId
    ) throws MessagingException {
        return ResponseEntity.ok(service.selectBestConditions(userId));
    }

    @PostMapping("/send/final-condition/{userId}")
    public ResponseEntity<Long> sendBestConditionForClient(
            @PathVariable Long userId,
            @RequestBody CreditConditionRequest request
    ) throws MessagingException {
        return ResponseEntity.ok(service.sendBestCondition(request, userId));
    }

    @PostMapping("/recalculation-monthly-payment")
    public ResponseEntity<Double> recalculationOfMonthlyPayment(
            @RequestBody PaymentCalculationRequest request
    ) {
        return ResponseEntity.ok(service.recalculationOfMonthlyPayment(request));
    }

    @GetMapping("/condition/by/condition-name")
    public ResponseEntity<CreditConditionResponse> getConditionByName(
            @RequestParam String name
    ) {
        return ResponseEntity.ok(service.getConditionByName(name));
    }

    @GetMapping("/all-conditions/names")
    public ResponseEntity<List<String>> getAllConditionNames() {
        return ResponseEntity.ok(service.getAllConditionNames());
    }

}
