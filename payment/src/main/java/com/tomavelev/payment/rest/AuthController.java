package com.tomavelev.payment.rest;

import com.tomavelev.payment.model.LoginResponse;
import com.tomavelev.payment.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/public/login")
    public LoginResponse authenticateUser(@RequestParam("email") String email,
                                          @RequestParam("password") String password) {
        return authService.authenticateUser(email, password);
    }
}