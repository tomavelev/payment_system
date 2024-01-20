package com.tomavelev.payment.repository;

import com.tomavelev.payment.model.PaymentTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;


@Repository
public interface TransactionRepository extends JpaRepository<PaymentTransaction, String> {

    void deleteAllByCreatedAtLessThan(Date date);
}
