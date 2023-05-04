package com.org.conceptlearning.controller;

import com.org.conceptlearning.model.Amount;
import com.org.conceptlearning.model.TransactionRequest;
import com.org.conceptlearning.model.TransactionResponse;
import com.org.conceptlearning.service.ZakatService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.org.conceptlearning.constants.ZakatContants.*;

@RestController
@RequestMapping("/v1")
@Slf4j
public class ZakatController {

    private ZakatService zakatService;

    public ZakatController(ZakatService zakatService) {
        this.zakatService = zakatService;
    }

    @PostMapping(TRANSACTION)
    public ResponseEntity<String> saveTransactionDetails(@RequestBody TransactionRequest transactionRequest) {
        try {
            return ResponseEntity.ok().body(String.valueOf(zakatService.saveTransaction(transactionRequest.getFileName())));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("FAILED, e:" + e.getMessage());
        }
    }

    @PostMapping(TRANSACTIONS_FILTER)
    public ResponseEntity<List<TransactionResponse>> getTransactionDetails(@RequestBody TransactionRequest transactionRequest) {
        try {
            return ResponseEntity.ok().body(zakatService.fetchAllTransactions(transactionRequest));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(null);
        }
    }

    @PostMapping(ZAKAT)
    public ResponseEntity<Amount> calculateZakat(@RequestBody TransactionRequest transactionRequest) {
        try {
            transactionRequest.setRemarksData(":Int.Pd:");
            return ResponseEntity.ok().body(zakatService.calculateZakat(transactionRequest));
        } catch (Exception e) {
            log.error("Exception Occurred:", e);
            return ResponseEntity.internalServerError().body(null);
        }
    }

    @PostMapping(INTEREST)
    public ResponseEntity<Amount> calculateInterest(@RequestBody TransactionRequest transactionRequest) {
        try {
            transactionRequest.setRemarksData(":Int.Pd:");
            return ResponseEntity.ok().body(zakatService.calculateInterest(transactionRequest));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(null);
        }
    }

}
