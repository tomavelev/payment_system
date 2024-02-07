package com.tomavelev.payment.model;

public enum TransactionStatus {
    APPROVED,
    CHARGE,// not in the initial list in the requirements, but, needed to complete the logic
    REVERSED,
    REFUNDED,
    ERROR
}
