package com.example.Spring_sercurity_JWT.service;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

import java.util.function.Function;

import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static io.jsonwebtoken.Jwts.claims;

@Service
public class JWTService {

    String secretKey;
    private static final long ACCESS_TOKEN_VALIDITY = 1000 * 60 * 15;      // 15 minutes
    private static final long REFRESH_TOKEN_VALIDITY = 1000 * 60 * 60 * 24 * 7; // 7 days

//    generate a key
    public JWTService()  {
        try {
            KeyGenerator keyGen=KeyGenerator.getInstance("HmacSHA256");
            SecretKey sk=keyGen.generateKey();
            secretKey=Base64.getEncoder().encodeToString(sk.getEncoded());
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public String generateAccessToken(UserDetails userDetails) {
        return createToken(new HashMap<>(), userDetails.getUsername(), ACCESS_TOKEN_VALIDITY);
    }

    public String generateRefreshToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("type", "refresh");
        return createToken(claims, userDetails.getUsername(), REFRESH_TOKEN_VALIDITY);
    }

    private String createToken(Map<String, Object> claims, String username, long validity) {
        return Jwts.builder()
                .claims()
                .add(claims)
                .subject(username)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + validity))
                .and()
                .signWith(getKey())
                .compact();
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUserName(token);
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    public boolean validateRefreshToken(String refreshToken, UserDetails userDetails) {
        // Check if the token is of type "refresh"
        if (isRefreshToken(refreshToken)) {
            // Validate the refresh token
            return validateToken(refreshToken, userDetails);
        }
        return false;
    }

    public boolean isRefreshToken(String token) {
        return "refresh".equals(extractAllClaims(token).get("type"));
    }

    public String extractUserName(String token) {
        return extractClaim(token,Claims::getSubject);
    }

    private <T> T extractClaim(String token, Function<Claims,T> claimResolver){
        final Claims claims=extractAllClaims(token);
        return claimResolver.apply(claims);
    }

    private Claims extractAllClaims(String token){
        return Jwts.parser()
                .verifyWith(getKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }


    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private SecretKey getKey() {
        byte[] decodedKey = Base64.getDecoder().decode(secretKey);
        return Keys.hmacShaKeyFor(decodedKey);
    }
//
//    public String generateToken(String username) {
//        Map<String,Object> claims=new HashMap<>();
//        return Jwts.builder()
//                .claims()
//                .add(claims)
//                .subject(username)
//                .issuedAt(new Date(System.currentTimeMillis()))
//                .expiration((new Date(System.currentTimeMillis()+60*60*10 )))
//                .and()
//                .signWith(getKey())
//                .compact();
//
//    }













}
