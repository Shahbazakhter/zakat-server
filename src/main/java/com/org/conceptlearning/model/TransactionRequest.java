package com.org.conceptlearning.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TransactionRequest {

    private String fileName;
    private LocalDate fromDate;
    private LocalDate toDate;
    private String remarksData;
    private String sortByColumn;
    private String sortOrder;

}
