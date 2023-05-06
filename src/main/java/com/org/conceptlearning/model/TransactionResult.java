package com.org.conceptlearning.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TransactionResult {

    private String successMessage;
    private String errorCode;
    private List<String> errorMessage;

}
