package com.example.Spring_sercurity_JWT.controller;

import com.example.Spring_sercurity_JWT.model.Users;
import com.example.Spring_sercurity_JWT.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserService service;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Users user){
        Map<String, String> tokens=service.register(user);
        return ResponseEntity.ok(tokens);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Users user){
        Map<String, String> tokens = service.verify(user);
        return ResponseEntity.ok(tokens);
    }

    @PostMapping("/refresh-token")
    public Map<String, String> refreshAccessToken(@RequestParam("refreshToken") String refreshToken) {
        String newAccessToken = service.refreshAccessToken(refreshToken);
        return Map.of("accessToken", newAccessToken);
    }



}
