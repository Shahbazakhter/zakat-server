package com.org.conceptlearning.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TransactionResponse {

    private Long transactionDetailId;

    private Long serialNumber;

    private LocalDate valueDate;

    private LocalDate transactionDate;

    private String chequeNumber;

    private String transactionRemarks;

    private BigDecimal withdrawalAmount;

    private BigDecimal depositAmount;

    private BigDecimal balance;

    private CustomerResponse customerDetail;
}
