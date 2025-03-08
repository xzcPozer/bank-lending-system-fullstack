package com.sharafutdinov.bank_lending_api.user;

import com.sharafutdinov.bank_lending_api.common.PageResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("user")
@RequiredArgsConstructor
@Tag(name = "User")
public class UserController {

    private final UserService service;

    @PostMapping("/check")
    public ResponseEntity<Long> checkUserData(
            @RequestBody @Valid UserRequest request
    ) {
        return ResponseEntity.ok(service.checkPassportData(request));
    }

    @GetMapping("/{userId}")
    @PreAuthorize("hasRole('ROLE_CREDIT_OFFICER')")
    public ResponseEntity<UserResponse> getSelectedUser(
            @PathVariable Long userId
    ) {
        return ResponseEntity.ok(service.getUserById(userId));
    }

    @GetMapping("/all-client")
    @PreAuthorize("hasRole('ROLE_CREDIT_OFFICER')")
    public ResponseEntity<PageResponse<UserResponse>> getAllClient(
            @RequestParam(name = "page", defaultValue = "0", required = false) int page,
            @RequestParam(name = "size", defaultValue = "10", required = false) int size
    ){
        return ResponseEntity.ok(service.getAllBankClient(page, size));
    }
}
