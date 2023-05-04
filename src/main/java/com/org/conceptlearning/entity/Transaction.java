package com.org.conceptlearning.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.context.annotation.Lazy;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "transaction")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "transaction_Id")
    private Long transactionDetailId;

    @Column(name = "value_date")
    private LocalDate valueDate;

    @Column(name = "transaction_date")
    private LocalDate transactionDate;

    @Column(name = "cheque_number")
    private String chequeNumber;

    @Column(name = "remarks")
    private String transactionRemarks;

    @Column(name = "withdraw_amount")
    private BigDecimal withdrawalAmount;

    @Column(name = "deposit_amount")
    private BigDecimal depositAmount;

    @Column(name = "balance")
    private BigDecimal balance;

    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    @Lazy(value = false)
    private Customer customerDetail;

}
