package com.sxf.project.security;

import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.*;

@Component
@Slf4j
public class JwtTokenUtil implements Serializable {

    private String secret;
    private long jwtExpirationInMs;
    private long jwtExpirationInMsRememberMe;

    @Value("${jwt.jwtExpirationInMs}")
    private long refreshTokenExpirationMs;
    @Value("${accessTokenKey}")
    private String accessTokenKey;
    @Value("${refreshTokenKey}")
    private String refreshTokenKey;

    @Value("${jwt.secret}")
    public void setSecret(String secret) {
        this.secret = secret;
    }

    @Value("${jwt.jwtExpirationInMs}")
    public void setJwtExpirationInMs(String jwtExpirationInMs) {
        this.jwtExpirationInMs = Integer.parseInt(jwtExpirationInMs);
    }

    @Value("${jwt.jwtExpirationInMsRememberMe}")
    public void setJwtExpirationInMsRememberMe(String jwtExpirationInMsRememberME) {
        this.jwtExpirationInMsRememberMe = Integer.parseInt(jwtExpirationInMsRememberME);
    }

    public String generateToken(UserDetails userDetails, boolean rememberMe) {
        Map<String, Object> claims = new HashMap<>();
        Collection<? extends GrantedAuthority> roles = userDetails.getAuthorities();


// TODO ROLE bilan boshlanishni hal qilish zarur bu vaqtinchalik
        claims.put("roles", roles.stream().map(e -> {
            String r = e.getAuthority();
            return r.substring(r.indexOf("_") + 1);
        }).toArray());


        return doGenerateToken(claims, userDetails.getUsername(), rememberMe);
    }

    private String doGenerateToken(Map<String, Object> claims, String subject, boolean rememberMe) {
        return Jwts.builder().setClaims(claims).setSubject(subject).setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + (rememberMe ? jwtExpirationInMsRememberMe : jwtExpirationInMs))).signWith(SignatureAlgorithm.HS512, secret).compact();
    }
    // generate token for user

    public String generateAccessToken(String username) {
        Date expireDate = new Date(System.currentTimeMillis() + secret);

        return Jwts
                .builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(expireDate)
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact();
    }

    public String generateRefreshToken(String username, UUID refreshTokenId) {
        Date expireDate = new Date(System.currentTimeMillis() + refreshTokenExpirationMs);
        return Jwts
                .builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(expireDate)
                .signWith(SignatureAlgorithm.HS512, refreshTokenKey)
                .claim("tokenId", refreshTokenId.toString())
                .compact();
    }

    public boolean validateAccessToken(String token) {
        return validateToken(token, secret);
    }

    public boolean validateRefreshToken(String token) {
        return validateToken(token, refreshTokenKey);
    }

    private boolean validateToken(String token, String key) {
        try {
            Jwts.parser().setSigningKey(key).parseClaimsJws(token);
            return true;
        } catch (SignatureException ex) {
            log.error("Invalid JWT signature");
        } catch (MalformedJwtException ex) {
            log.error("Invalid JWT token");
        } catch (ExpiredJwtException ex) {
            log.error("Expired JWT token");
        } catch (UnsupportedJwtException ex) {
            log.error("Unsupported JWT token");
        } catch (IllegalArgumentException ex) {
            log.error("JWT claims string is empty.");
        }
        return false;
    }

    public String getUsernameFromAccessToken(String token) {
        return Jwts
                .parser().setSigningKey(secret).parseClaimsJws(token).getBody().getSubject();
    }

    public String getUsernameFromRefreshToken(String token) {
        return Jwts
                .parser().setSigningKey(refreshTokenKey).parseClaimsJws(token).getBody().getSubject();
    }

    public UUID getRefreshTokenIdFromRefreshToken(String token) {
        log.info(Jwts.parser().setSigningKey(refreshTokenKey).parseClaimsJws(token).getBody().get("tokenId").toString());
        return UUID.fromString(Jwts.parser().setSigningKey(refreshTokenKey).parseClaimsJws(token).getBody().get("tokenId").toString());
    }


}
