package com.tomavelev.payment.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.tomavelev.payment.util.ValidPhoneNumber;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
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
    @ValidPhoneNumber(message = "Please enter a valid phone number")
    @Column(name = "customer_phone")
    private String customerPhone;

    @NotNull
    @NotBlank
    private String uuid;

    @Column(name = "amount", precision = 20, scale = 5)
    @DecimalMin(value = "0.0", inclusive = false)
    @Digits(integer = 20, fraction = 5)
    @NotNull
    private BigDecimal amount;

    @Enumerated(EnumType.ORDINAL)
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
