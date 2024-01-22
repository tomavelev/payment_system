package com.tomavelev.payment.service;

import com.tomavelev.payment.model.User;
import com.tomavelev.payment.model.response.BusinessCode;
import com.tomavelev.payment.model.response.LoginResponse;
import com.tomavelev.payment.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AuthService {

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UserRepository userRepository;

    public LoginResponse authenticateUser(String email, String password) {

        User user = userRepository.findByEmail(email);
        if (user == null) {
            return new LoginResponse(null, null, BusinessCode.PROFILE_NOT_FOUND);
        }

        boolean passwordMatch = passwordEncoder.matches(password, user.getPassword());

        if (!passwordMatch) {
            return new LoginResponse(null,null, BusinessCode.WRONG_PASSWORD);
        }

        String role;
        if (user.getMerchant() != null) {
            role = User.ROLE_MERCHANT;
        } else {
            role = User.ROLE_ADMIN;
        }
        String token = tokenProvider.generateToken(email, new ArrayList<>(List.of(role)));
        return new LoginResponse(token, role, BusinessCode.SUCCESS);
    }

}