package com.tomavelev.payment.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity(name = "transaction")
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class PaymentTransaction extends BaseEntity {

    @Email
    @NotBlank
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

    @Column(name = "reference_id")
    private String referenceId;
//    @OneToMany(mappedBy = "paymentTransaction", cascade = CascadeType.ALL, orphanRemoval = true)
//    private List<PaymentTransaction> paymentTransactions;
//    @ManyToOne(fetch = FetchType.LAZY, optional = true)
//    @Nullable
//    @JoinColumn(name = "reference_id", nullable = true)
//    private PaymentTransaction paymentTransaction;



    @JsonIgnore
    @ManyToOne
    private Merchant merchant;
}
