package com.tomavelev.payment.rest;

import com.tomavelev.payment.model.PaymentTransaction;
import com.tomavelev.payment.model.response.PaymentResponse;
import com.tomavelev.payment.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class TransactionRestController {

    @Autowired
    private TransactionService transactionService;

    @PostMapping("/user/acceptPayment")
    public PaymentResponse acceptPayment(@RequestBody PaymentTransaction transaction) {
        return transactionService.save(transaction);
    }

}
