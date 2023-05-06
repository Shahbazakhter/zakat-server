package com.org.conceptlearning.service;

import com.org.conceptlearning.model.*;

import java.util.List;

public interface ZakatService {

    TransactionResult saveTransaction(String fileName);

    List<TransactionResponse> fetchAllTransactions(TransactionRequest transactionRequest);

    Amount calculateZakat(TransactionRequest transactionRequest);

    Amount calculateInterest(TransactionRequest transactionRequest);
}
