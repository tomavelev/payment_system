package com.tomavelev.payment.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.List;

@Service
public class JwtTokenProvider {
    public static final String ROLES = "roles";
    private static final SecretKey SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256); // Replace with a secure key
    private static final long EXPIRATION_TIME = 864_000_000; // 10 days
    //This is for demo purposes. Should be 10 minutes for access token - with refresh token - some bigger time

    public String generateToken(String username, List<String> roles) {
        Claims claims = Jwts.claims().setSubject(username);
        claims.put(ROLES, roles);

        Date now = new Date();
        Date expirationDate = new Date(now.getTime() + EXPIRATION_TIME);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(expirationDate)
                .signWith(SECRET_KEY)
                .compact();
    }

    public Authentication getAuthentication(String token) {
        UserDetails userDetails = getUsername(token);
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    public UserDetails getUsername(String token) {
        Claims claims = (Claims) Jwts.parserBuilder().setSigningKey(SECRET_KEY).build().parse(token).getBody();
        @SuppressWarnings("unchecked")
        Class<List<String>> stringListClass = (Class<List<String>>) (Class<?>) List.class;
        List<String> roles = claims.get(ROLES, stringListClass);
        return User.builder().username(claims.getSubject()).authorities(roles.toArray(new String[]{})).password("").build();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(SECRET_KEY).build().parse(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
}
