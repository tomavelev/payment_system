package com.tomavelev.payment.service;

import com.tomavelev.payment.model.Merchant;
import com.tomavelev.payment.model.PaymentTransaction;
import com.tomavelev.payment.model.TransactionStatus;
import com.tomavelev.payment.model.User;
import com.tomavelev.payment.model.response.BusinessCode;
import com.tomavelev.payment.model.response.PaymentResponse;
import com.tomavelev.payment.model.response.RestResponse;
import com.tomavelev.payment.repository.MerchantRepository;
import com.tomavelev.payment.repository.TransactionRepository;
import com.tomavelev.payment.repository.UserRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;


@Service
public class TransactionService {

    @Autowired
    private Validator validator;

    @Autowired
    private TransactionRepository transactionRepository;
    @Autowired
    private MerchantRepository merchantRepository;
    @Autowired
    private UserRepository userRepository;

    @Transactional
    public PaymentResponse save(PaymentTransaction transaction, String merchantUserEmail) {

        User dbMerchant = userRepository.findByEmail(merchantUserEmail);
        if (dbMerchant != null) {
            if (!dbMerchant.getMerchant().isActive()) {
                return new PaymentResponse(BusinessCode.MERCHANT_NOT_ACTIVE, null);
            }
        } else {
            return new PaymentResponse(BusinessCode.PROFILE_NOT_FOUND, null);
        }

        if (transaction.getStatus() == null) {
            return new PaymentResponse(BusinessCode.ERROR, "Transaction Status is not set");
        }

        switch (transaction.getStatus()) {
            case APPROVED -> {
                User customer = userRepository.findByEmail(transaction.getCustomerEmail());

                Set<ConstraintViolation<PaymentTransaction>> violations = validator.validate(transaction);
                if (!violations.isEmpty()) {
                    StringBuilder sb = new StringBuilder();
                    collectViolations(violations, sb);
                    return new PaymentResponse(BusinessCode.ERROR, sb.toString());
                }
                if (customer == null || customer.getMerchant() == null) {
                    return new PaymentResponse(BusinessCode.CUSTOMER_NOT_FOUND, null);
                }

                if (customer.getMerchant().getTotalTransactionSum().compareTo(transaction.getAmount()) < 0) {
                    return new PaymentResponse(BusinessCode.NOT_ENOUGH_CUSTOMER_FUNDS, null);
                }
                BigDecimal amount = customer.getMerchant().getTotalTransactionSum().subtract(transaction.getAmount());
                customer.getMerchant().setTotalTransactionSum(amount);
                merchantRepository.save(customer.getMerchant());
            }
            case REVERSED -> {
                transaction.setAmount(new BigDecimal("0.00001"));
                transaction.setCustomerEmail(dbMerchant.getEmail());
                transaction.setCustomerPhone("+359882626454");

                Optional<PaymentTransaction> approvedTransaction = transactionRepository.findById(transaction.getReferenceId());
                User customer = userRepository.findByEmail(approvedTransaction.get().getCustomerEmail());


                customer.getMerchant().setTotalTransactionSum(
                        customer.getMerchant().getTotalTransactionSum().add(approvedTransaction.get().getAmount())
                );

                merchantRepository.save(customer.getMerchant());

            }
            case REFUNDED -> {
                transaction.setAmount(new BigDecimal("0.00001"));
                transaction.setCustomerEmail(dbMerchant.getEmail());
                transaction.setCustomerPhone("+359882626454");
                String referenceId = transaction.getReferenceId();

                Optional<PaymentTransaction> chargedTransactions = transactionRepository.findById(referenceId);

                if (chargedTransactions.isPresent()) {
                    if (chargedTransactions.get().getStatus() == TransactionStatus.CHARGE) {
                        Optional<PaymentTransaction> approvedTransaction = transactionRepository.findById(chargedTransactions.get().getReferenceId());

                        if (approvedTransaction.isEmpty()) {
                            return new PaymentResponse(BusinessCode.REFERENCE_TRANSACTION_NOT_FOUND, null);

                        }
                        User customer = userRepository.findByEmail(approvedTransaction.get().getCustomerEmail());

                        Merchant merchant = approvedTransaction.get().getMerchant();
                        merchant.setTotalTransactionSum(merchant.getTotalTransactionSum().subtract(approvedTransaction.get().getAmount()));

                        customer.getMerchant().setTotalTransactionSum(
                                customer.getMerchant().getTotalTransactionSum().add(approvedTransaction.get().getAmount())
                        );

                        merchantRepository.saveAll(Arrays.asList(merchant, customer.getMerchant()));
                    } else {
                        setAsError(transaction, dbMerchant);
                    }
                } else {
                    return new PaymentResponse(BusinessCode.REFERENCE_TRANSACTION_NOT_FOUND, null);
                }

            }
            case CHARGE -> {
                //find referenced transaction
                String referenceId = transaction.getReferenceId();
                Optional<PaymentTransaction> approvedTransaction = transactionRepository.findById(referenceId);

                if (approvedTransaction.isPresent()) {
                    if (approvedTransaction.get().getStatus() == TransactionStatus.APPROVED) {
                        //increase amount
                        Merchant merchant = approvedTransaction.get().getMerchant();
                        merchant.setTotalTransactionSum(merchant.getTotalTransactionSum().add(approvedTransaction.get().getAmount()));
                        merchantRepository.save(merchant);

                        transaction.setCustomerEmail(approvedTransaction.get().getCustomerEmail());
                        transaction.setAmount(new BigDecimal("0.00001"));
                        transaction.setCustomerPhone(approvedTransaction.get().getCustomerPhone());
                    } else {
                        setAsError(transaction, dbMerchant);
                    }
                } else {
                    return new PaymentResponse(BusinessCode.REFERENCE_TRANSACTION_NOT_FOUND, null);
                }
            }
            case ERROR -> {
                Set<ConstraintViolation<PaymentTransaction>> violations = validator.validate(transaction);
                if (!violations.isEmpty()) {
                    StringBuilder sb = new StringBuilder();
                    collectViolations(violations, sb);
                    return new PaymentResponse(BusinessCode.ERROR, sb.toString());
                }
            }
        }


        transaction.setMerchant(dbMerchant.getMerchant());
        transactionRepository.save(transaction);

        return new PaymentResponse(BusinessCode.SUCCESS, null);
    }

    private static void setAsError(PaymentTransaction transaction, User dbMerchant) {
        transaction.setAmount(new BigDecimal("0.00001"));
        transaction.setStatus(TransactionStatus.ERROR);

        if (transaction.getUuid() == null || transaction.getUuid().isEmpty()) {
            transaction.setUuid(UUID.randomUUID().toString());
        }
        transaction.setCustomerEmail(dbMerchant.getEmail());
        transaction.setCustomerPhone("+359882626454");
    }

    private static void collectViolations(Set<ConstraintViolation<PaymentTransaction>> violations, StringBuilder sb) {
        violations.forEach(userConstraintViolation -> sb.append(userConstraintViolation.getPropertyPath()).append(" value: '").append(userConstraintViolation.getInvalidValue()).append("' ").append(userConstraintViolation.getMessage()).append("\n"));
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
