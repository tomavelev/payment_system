package com.tomavelev.payment.service;

import com.tomavelev.payment.model.PaymentTransaction;
import com.tomavelev.payment.model.response.BusinessCode;
import com.tomavelev.payment.model.response.PaymentResponse;
import com.tomavelev.payment.repository.TransactionRepository;
import com.tomavelev.payment.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;


@Service
public class TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;
    @Autowired
    private UserRepository userRepository;

    @Transactional
    public PaymentResponse save(PaymentTransaction transaction) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();

        com.tomavelev.payment.model.User dbUser = userRepository.findByEmail(user.getUsername());
        if (dbUser != null) {
            if (!dbUser.getMerchant().isActive()) {
                return new PaymentResponse(BusinessCode.MERCHANT_NOT_ACTIVE);
            }

            transaction.setMerchant(dbUser.getMerchant());
            transactionRepository.save(transaction);
        }

        return new PaymentResponse(BusinessCode.SUCCESS);
    }
}
