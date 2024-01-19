package com.tomavelev.payment.service;

import com.tomavelev.payment.model.response.BusinessCode;
import com.tomavelev.payment.model.response.LoginResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AuthService {


    @Autowired
    private JwtTokenProvider tokenProvider;

    public LoginResponse authenticateUser(String email, String password) {
//TODO this will be replaced with db authentication
        String role = "";
        if ("admin".equals(email) && "password".equals(password)) {
            role = "ADMIN";
        }
        if ("merchant".equals(email) && "password".equals(password)) {
            role = "MERCHANT";
        }
        if (role.isBlank()) {
            return new LoginResponse("", BusinessCode.ERROR);
        }
        String token = tokenProvider.generateToken(email, new ArrayList<>(List.of(role)));
        return new LoginResponse(token, BusinessCode.SUCCESS);
    }

}