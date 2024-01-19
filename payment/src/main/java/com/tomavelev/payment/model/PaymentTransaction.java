package com.tomavelev.payment.model;


import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Entity(name = "transaction")
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class PaymentTransaction extends BaseEntity {

    @Email
    @Column(name = "customer_email")
    private String customerEmail;

    @Column(name = "customer_phone")
    private String customerPhone;

    @NotNull
    private String uuid;

    @Column(name = "amount",  precision = 20, scale = 5)
    @Min(value = 0)
    private BigDecimal amount;


    private TransactionStatus status;

    @OneToMany(mappedBy = "paymentTransaction", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PaymentTransaction> paymentTransactions;


    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "reference_id")
    private PaymentTransaction paymentTransaction;

    @ManyToOne
    private Merchant merchant;
}
