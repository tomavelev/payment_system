package com.tomavelev.payment.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
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
@EqualsAndHashCode(callSuper = true, exclude = "user")
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

    @JsonIgnore
    @OneToMany(mappedBy = "merchant", cascade = CascadeType.DETACH)
    private List<PaymentTransaction> transactions;

    @PreRemove
    public void checkForTransactions(){
        if(transactions != null && !transactions.isEmpty()) {
            throw new RuntimeException("Cannot delete Merchant with Transactions");
        }
    }

    @JsonIgnore
    @OneToOne(mappedBy = "merchant")
    private User user;

}
