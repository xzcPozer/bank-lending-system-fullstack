package com.sharafutdinov.bank_lending_api.credit_request;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sharafutdinov.bank_lending_api.common.PageResponse;
import com.sharafutdinov.bank_lending_api.security.user.BankUserDetails;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.*;
import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.lang.reflect.Method;
import java.rmi.server.ExportException;
import java.util.Set;

@RestController
@RequestMapping("credit-request")
@RequiredArgsConstructor
@Tag(name = "CreditRequest")
public class CreditRequestController {

    private final CreditRequestService service;

    @PostMapping("/add/by/hired-worker")
    public ResponseEntity<Long> addCreditRequest(
            @RequestPart String requestStr,
            @RequestPart MultipartFile solvency,
            @RequestPart MultipartFile employment
    ) throws ExportException, JsonProcessingException, MethodArgumentNotValidException, NoSuchMethodException {
        ObjectMapper objectMapper = new ObjectMapper();
        CreditRequestDto request = objectMapper.readValue(requestStr, CreditRequestDto.class);
        creditRequestDtoValidation(request);

        return ResponseEntity.ok(service.addCreditRequest(request, solvency, employment));
    }

    @PostMapping("/add")
    public ResponseEntity<Long> addCreditRequest(
            @RequestPart String requestStr,
            @RequestPart MultipartFile solvency
    ) throws ExportException, JsonProcessingException, MethodArgumentNotValidException, NoSuchMethodException {
        ObjectMapper objectMapper = new ObjectMapper();
        CreditRequestDto request = objectMapper.readValue(requestStr, CreditRequestDto.class);
        creditRequestDtoValidation(request);

        return ResponseEntity.ok(service.addCreditRequest(request, solvency));

    }

    @GetMapping("/request/by/{creditRequestId}")
    @PreAuthorize("hasRole('ROLE_CLIENT') or hasRole('ROLE_CREDIT_OFFICER')")
    public ResponseEntity<CreditRequestResponse> getCreditRequestById(
            @PathVariable Long creditRequestId
    ){
        return ResponseEntity.ok(service.getCreditRequestById(creditRequestId));
    }

    @GetMapping("/all-requests/with/lending-officer")
    @PreAuthorize("hasRole('ROLE_DIRECTOR')")
    public ResponseEntity<PageResponse<CreditRequestResponseForDirector>> getAllWithLendingOfficer(
            @RequestParam(name = "page", defaultValue = "0", required = false) int page,
            @RequestParam(name = "size", defaultValue = "10", required = false) int size
    ) {
        return ResponseEntity.ok(service.getAllCreditRequestWithLendingOfficer(page, size));
    }

    @GetMapping("/all-requests")
    @PreAuthorize("hasRole('ROLE_CREDIT_OFFICER')")
    public ResponseEntity<PageResponse<CreditRequestResponse>> getAllF(
            @RequestParam(name = "page", defaultValue = "0", required = false) int page,
            @RequestParam(name = "size", defaultValue = "10", required = false) int size
    ) {
        return ResponseEntity.ok(service.getAllCreditRequest(page, size));
    }

    @GetMapping("/all-requests/by/processed")
    @PreAuthorize("hasRole('ROLE_CREDIT_OFFICER')")
    public ResponseEntity<PageResponse<CreditRequestResponse>> getAll(
            @RequestParam(name = "page", defaultValue = "0", required = false) int page,
            @RequestParam(name = "size", defaultValue = "10", required = false) int size,
            @RequestParam(name = "isProcessed") boolean isProcessed
    ) {
        return ResponseEntity.ok(service.getAllCreditRequest(page, size, isProcessed));
    }

    @GetMapping("/my-requests")
    @PreAuthorize("hasRole('ROLE_CLIENT')")
    public ResponseEntity<PageResponse<CreditRequestClientResponse>> getAll(
            @RequestParam(name = "page", defaultValue = "0", required = false) int page,
            @RequestParam(name = "size", defaultValue = "10", required = false) int size,
            Authentication connectedUser
    ) {
        return ResponseEntity.ok(service.getAllCreditByUserIdRequest(page, size, connectedUser));
    }

    @PutMapping("/change/by/solvency/and/employment/{creditRequestId}")
    @PreAuthorize("hasRole('ROLE_CLIENT')")
    public ResponseEntity<Long> changeCreditRequest(
            @PathVariable Long creditRequestId,
            @RequestPart MultipartFile solvency,
            @RequestPart MultipartFile employment,
            Authentication connectedUser
    ) throws ExportException {
        return ResponseEntity.ok(service.changeCreditRequest(solvency, employment, connectedUser, creditRequestId));
    }

    @PutMapping("/change/by/solvency/{creditRequestId}")
    @PreAuthorize("hasRole('ROLE_CLIENT')")
    public ResponseEntity<Long> changeCreditRequest(
            @PathVariable Long creditRequestId,
            @RequestPart MultipartFile solvency,
            Authentication connectedUser
    ) throws ExportException {
        return ResponseEntity.ok(service.changeCreditRequest(solvency, connectedUser, creditRequestId));
    }

    private void creditRequestDtoValidation(CreditRequestDto request) throws NoSuchMethodException, MethodArgumentNotValidException {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        Set<ConstraintViolation<CreditRequestDto>> violations = validator.validate(request);

        if (!violations.isEmpty()) {
            Method method = CreditRequestController.class.getMethod(
                    "addCreditRequest",
                    String.class,
                    MultipartFile.class,
                    MultipartFile.class,
                    Authentication.class
            );
            MethodParameter methodParameter = new MethodParameter(method, 0);

            BindingResult bindingResult = new BeanPropertyBindingResult(request, "creditRequestDto");
            violations.forEach(violation -> {
                String field = violation.getPropertyPath().toString();
                bindingResult.rejectValue(field, "", violation.getMessage());
            });
            throw new MethodArgumentNotValidException(methodParameter, bindingResult);
        }
    }
}
