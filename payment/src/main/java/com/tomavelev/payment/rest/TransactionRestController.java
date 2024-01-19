package com.tomavelev.payment.rest;

import com.tomavelev.payment.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class TransactionRestController {

    @Autowired
    private TransactionService transactionService;
}
