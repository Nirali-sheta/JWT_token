package com.example.Spring_sercurity_JWT.service;

import com.example.Spring_sercurity_JWT.model.UserPrincipal;
import com.example.Spring_sercurity_JWT.model.Users;
import com.example.Spring_sercurity_JWT.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;


@Service
public class UserService {

    @Autowired
    private UserRepo repo;

    @Autowired
    private MyUserDetailService myUserDetailService;


    @Autowired
    private JWTService jwtService;

    @Autowired
    AuthenticationManager authmanager;

    private BCryptPasswordEncoder encoder=new BCryptPasswordEncoder(12);
    public  Map<String, String> register(Users user){
//        bcrypt the password before register
        user.setPassword(encoder.encode(user.getPassword()));
        repo.save(user);

        UserDetails userDetails = myUserDetailService.loadUserByUsername(user.getUsername());
        // Generate access and refresh tokens
        String accessToken = jwtService.generateAccessToken(userDetails);
        String refreshToken = jwtService.generateRefreshToken(userDetails);

        // Return tokens
        Map<String, String> tokens = new HashMap<>();
        tokens.put("accessToken", accessToken);
        tokens.put("refreshToken", refreshToken);
        return tokens;
    }

    public Map<String,String> verify(Users user){
        Authentication authentication=
                authmanager.authenticate(new UsernamePasswordAuthenticationToken(user.getUsername(),user.getPassword()));


        if(authentication.isAuthenticated()){
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String accessToken = jwtService.generateAccessToken(userDetails);
            String refreshToken = jwtService.generateRefreshToken(userDetails);

            Map<String, String> tokens = new HashMap<>();
            tokens.put("accessToken", accessToken);
            tokens.put("refreshToken", refreshToken);
            return tokens;

        }
        throw new RuntimeException("Invalid username or password");
    }



    public String refreshAccessToken(String refreshToken) {
        // Extract username from the refresh token
        String username = jwtService.extractUserName(refreshToken);
        UserDetails userDetails = myUserDetailService.loadUserByUsername(username);

        // Validate refresh token
        if (jwtService.validateRefreshToken(refreshToken, userDetails)) {
            // Generate new access token
            return jwtService.generateAccessToken(userDetails);
        } else {
            throw new RuntimeException("Invalid or expired refresh token");
        }
    }
}
