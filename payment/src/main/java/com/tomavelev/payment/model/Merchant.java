package com.tomavelev.payment.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.NotBlank;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Entity(name = "merchant")
public class Merchant extends BaseEntity {

    @NotBlank
    private String name;

    @JdbcTypeCode(SqlTypes.LONGVARCHAR)
    @Column(name = "description")
    private String description;

    @Column(name = "status")
    private boolean active;

    @Column(name = "total_transaction_sum", scale = 5, precision = 20)
    private BigDecimal totalTransactionSum;

    @OneToMany(mappedBy = "merchant")
    private List<PaymentTransaction> transactions;

    @OneToOne(mappedBy = "merchant")
    private User user;

}
