package com.org.conceptlearning.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CustomerResponse {

    private Long customerId;

    private String accountNumber;

    private String firstname;

    private String lastname;

    private String currency;

    private String bankName;

    private String iFSCCode;

}
