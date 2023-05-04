package com.org.conceptlearning.service;

import com.org.conceptlearning.model.*;

import java.math.BigDecimal;
import java.util.List;

public interface ZakatService {

    boolean saveTransaction(String fileName);

    List<TransactionResponse> fetchAllTransactions(TransactionRequest transactionRequest);

    Amount calculateZakat(TransactionRequest transactionRequest);

    Amount calculateInterest(TransactionRequest transactionRequest);
}
