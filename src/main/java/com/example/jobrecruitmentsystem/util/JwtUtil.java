package com.example.jobrecruitmentsystem.util;

import com.example.jobrecruitmentsystem.entity.User;
import com.example.jobrecruitmentsystem.security.CustomUserDetails;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private long jwtExpirationMs;

    private SecretKey getSigningKey() {
        if (jwtSecret == null || jwtSecret.length() < 32) {
            throw new IllegalArgumentException("JWT secret must be at least 256 bits (32 chars)");
        }
        return Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(UserDetails userDetails) {
        CustomUserDetails user = (CustomUserDetails) userDetails;

        Map<String, Object> claims = new HashMap<>();
        claims.put("id", user.getId()); // ✅ Thêm id
        claims.put("roles", user.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList()));

        return Jwts.builder()
                .setClaims(claims) // ✅ Duy nhất dùng setClaims
                .setSubject(user.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }


    public UserDetails getUserDetails(String token) {
        Claims claims = extractAllClaims(token);
        String username = claims.getSubject();
        List<String> roles = claims.get("roles", List.class);

        return new org.springframework.security.core.userdetails.User(
                username,
                "",
                roles.stream()
                        .map(role -> (GrantedAuthority) () -> role)
                        .collect(Collectors.toList())
        );
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
}