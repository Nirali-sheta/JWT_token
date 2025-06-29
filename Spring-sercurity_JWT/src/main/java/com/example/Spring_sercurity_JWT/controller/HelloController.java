package com.example.Spring_sercurity_JWT.controller;


import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {
    @GetMapping("/")
    public String greet(HttpServletRequest req){
        return "Hello World"+req.getSession().getId();
    }

    @GetMapping("/hello")
    public String greet2(HttpServletRequest req){
        return "HEllo from new controller"+req.getSession().getId();
    }
}
