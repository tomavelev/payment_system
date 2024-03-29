package com.tomavelev.payment.rest;

import com.tomavelev.payment.model.PaymentTransaction;
import com.tomavelev.payment.model.response.PaymentResponse;
import com.tomavelev.payment.model.response.RestResponse;
import com.tomavelev.payment.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;


@RestController
public class TransactionRestController {

    @Autowired
    private TransactionService transactionService;

    @PostMapping("/transactions")
    public PaymentResponse acceptPayment(@RequestBody PaymentTransaction transaction) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();

        return transactionService.save(transaction, user.getUsername());
    }

    @GetMapping(value = "/transactions")
    public RestResponse<PaymentTransaction> transactions(@RequestParam(value = "offset", defaultValue = "0") long offset,
                                                         @RequestParam(value = "limit", defaultValue = "10") int limit) {
        return transactionService.getTransactions(offset, limit);
    }
}
