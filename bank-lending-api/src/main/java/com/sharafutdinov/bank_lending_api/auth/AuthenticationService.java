package com.sharafutdinov.bank_lending_api.auth;

import com.sharafutdinov.bank_lending_api.bank_db.repository.UserRepository;
import com.sharafutdinov.bank_lending_api.email.EmailService;
import com.sharafutdinov.bank_lending_api.security.jwt.JwtUtils;
import com.sharafutdinov.bank_lending_api.security.user.BankUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.naming.AuthenticationException;
import java.security.SecureRandom;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    public String login(LoginRequest request) {
        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(
                        request.getEmail(), request.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        return jwtUtils.generateTokenForUser(authentication);
    }

    public void changePassword(LoginChangeRequest request) throws AuthenticationException {
        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(
                        request.getEmail(), request.getOldPassword()));

        Long userId = ((BankUserDetails) authentication.getPrincipal()).getId();

        if (request.getNewPassword().equals(request.getConfirmNewPassword()))
            throw new AuthenticationException("Пароли не совпадают");

        userRepository.findById(userId)
                .map(user -> {
                    user.setPassword(passwordEncoder.encode(request.getNewPassword()));
                    return userRepository.save(user);
                });
    }

    public String getRandomPassword() {
        String upperCaseLetters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String lowerCaseLetters = "abcdefghijklmnopqrstuvwxyz";
        String digits = "0123456789";

        String allCharacters = upperCaseLetters + lowerCaseLetters + digits;

        int passwordLength = 8;

        SecureRandom random = new SecureRandom();

        StringBuilder password = new StringBuilder();
        for (int i = 0; i < passwordLength; i++) {
            int randomIndex = random.nextInt(allCharacters.length());
            password.append(allCharacters.charAt(randomIndex));
        }

        return password.toString();
    }
}

