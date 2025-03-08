package com.sharafutdinov.bank_lending_api.config;

import com.sharafutdinov.bank_lending_api.security.user.BankUserDetails;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

public class ApplicationAuditAware implements AuditorAware<Long> {
    @Override
    public Optional<Long> getCurrentAuditor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null ||
                !authentication.isAuthenticated() ||
                authentication instanceof AnonymousAuthenticationToken) {
            return Optional.empty();
        }

        BankUserDetails userPrincipal = (BankUserDetails) authentication.getPrincipal();
        return Optional.ofNullable(userPrincipal.getId());
    }
}
