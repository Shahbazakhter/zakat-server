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
public class Amount {

    private BigDecimal amount;
    private String formattedAmount;
    private LocalDate year;
    private boolean isPaid;

}
