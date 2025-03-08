package com.sharafutdinov.bank_lending_api.security.jwt;

import com.sharafutdinov.bank_lending_api.security.user.BankUserDetails;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.List;
import java.util.function.Function;

@Service
public class JwtUtils {

    @Value("${auth.token.jwt-secret}")
    private String jwtSecret; // ключ для подписи JWT-токенов.

    @Value("${auth.token.expiration}")
    private int expirationTime; // время жизни JWT-токена в миллисекундах

    public String generateTokenForUser(Authentication authentication) {
        BankUserDetails userPrincipal = (BankUserDetails) authentication.getPrincipal();

        // преобразование коллекции в список строк
        List<String> roles = userPrincipal.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                //.map(role -> "ROLE_" + role)
                .toList();

        // создание JWT токена
        return Jwts.builder()
                .setSubject(userPrincipal.getUsername())
                .claim("roles", roles)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(key(), SignatureAlgorithm.HS256)
                .compact();
    }

    // декодирует секретный ключ и возвращает ключ для подписки
    private Key key() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
    }

    // получение claim в зависимости от задачи
    private <T> T extractClaim(String token, Function<Claims, T> claimResolver) {
        final Claims claims = extractAllClaims(token);
        return claimResolver.apply(claims);
    }

    // получение всех claim
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // получение логина пользователя
    public String getUsernameFromToken(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // валидация пользователя
    public boolean validateToken(String token, UserDetails userDetails) {
        try {
            final String userName = getUsernameFromToken(token);
            return (userName.equals(userDetails.getUsername()) && !isTokenExpired(token));
        } catch (ExpiredJwtException | UnsupportedJwtException | MalformedJwtException
                 | SignatureException | IllegalArgumentException e) {
            throw new JwtException(e.getMessage());
        }
    }

    // проверка истечения срока jwt
    private boolean isTokenExpired(String token) {
        return extractClaim(token, Claims::getExpiration).before(new Date());
    }

}
