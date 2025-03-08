package com.sharafutdinov.bank_lending_api.client_information;

import com.sharafutdinov.bank_lending_api.common.PageResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("client-information")
@RequiredArgsConstructor
@Tag(name = "ClientInformation")
public class ClientInformationController {

    private final ClientInformationService service;

    @GetMapping("/all-client")
    @PreAuthorize("hasRole('ROLE_CREDIT_OFFICER')")
    public ResponseEntity<PageResponse<ClientInformationResponse>> getAllClientInfo(
            @RequestParam(name = "page", defaultValue = "0", required = false) int page,
            @RequestParam(name = "size", defaultValue = "10", required = false) int size
    ) {
        return ResponseEntity.ok(service.getAllClientInfo(page, size));
    }
}
