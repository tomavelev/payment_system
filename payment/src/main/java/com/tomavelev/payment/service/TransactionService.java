package com.tomavelev.payment.service;

import com.tomavelev.payment.model.PaymentTransaction;
import com.tomavelev.payment.model.response.BusinessCode;
import com.tomavelev.payment.model.response.PaymentResponse;
import com.tomavelev.payment.model.response.RestResponse;
import com.tomavelev.payment.repository.TransactionRepository;
import com.tomavelev.payment.repository.UserRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Set;


@Service
public class TransactionService {

    @Autowired
    private Validator validator;

    @Autowired
    private TransactionRepository transactionRepository;
    @Autowired
    private UserRepository userRepository;

    @Transactional
    public PaymentResponse save(PaymentTransaction transaction) {
        Set<ConstraintViolation<PaymentTransaction>> violations = validator.validate(transaction);
        if (!violations.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            violations.forEach(userConstraintViolation -> sb.append(userConstraintViolation.getPropertyPath()).append(" value: '").append(userConstraintViolation.getInvalidValue()).append("' ").append(userConstraintViolation.getMessage()).append("\n"));
            return new PaymentResponse(BusinessCode.ERROR, sb.toString());
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();

        com.tomavelev.payment.model.User dbUser = userRepository.findByEmail(user.getUsername());
        if (dbUser != null) {
            if (!dbUser.getMerchant().isActive()) {
                return new PaymentResponse(BusinessCode.MERCHANT_NOT_ACTIVE, null);
            }

            transaction.setMerchant(dbUser.getMerchant());
            transactionRepository.save(transaction);
        }

        return new PaymentResponse(BusinessCode.SUCCESS, null);
    }

    @Transactional
    public void cleanTransactionsOlderThan(Date date) {
        transactionRepository.deleteAllByCreatedAtLessThan(date);
    }

    public RestResponse<PaymentTransaction> getTransactions(long offset, int limit) {
        if (limit <= 0) {
            limit = 10;
        }
        Page<PaymentTransaction> page = transactionRepository.findAll(PageRequest.ofSize(limit).withPage((int) (offset / limit)));
        return new RestResponse<>(page.get().toList(), page.getTotalElements(), null, BusinessCode.SUCCESS);

    }
}
