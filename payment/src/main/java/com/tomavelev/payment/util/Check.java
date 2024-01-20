package com.tomavelev.payment.util;

import com.tomavelev.payment.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class Check {


    private static final long HOUR = 60  * 60 * 1000;
    @Autowired
    private TransactionService transactionService;

    @Scheduled(fixedRate = HOUR)
    public void clearOldTransactions() {
        transactionService.cleanTransactionsOlderThan(new Date(System.currentTimeMillis()-HOUR));
    }
}