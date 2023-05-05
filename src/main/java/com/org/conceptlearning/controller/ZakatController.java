package com.org.conceptlearning.controller;

import com.org.conceptlearning.model.Amount;
import com.org.conceptlearning.model.TransactionRequest;
import com.org.conceptlearning.model.TransactionResponse;
import com.org.conceptlearning.service.ZakatService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static com.org.conceptlearning.constants.ZakatContants.*;

@RestController
@RequestMapping("/v1")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@Slf4j
public class ZakatController {

    private ZakatService zakatService;

    public ZakatController(ZakatService zakatService) {
        this.zakatService = zakatService;
    }

    public ResponseEntity<List<TransactionResponse>>
    @PostMapping(value = "/statement/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> handleFileUpload(@RequestParam("file") MultipartFile file) {
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());
        try {
            String tempDirPath = System.getProperty("java.io.tmpdir");
            File tempFile = File.createTempFile("upload-", ".tmp", new File(tempDirPath));
            file.transferTo(tempFile);
            log.info("tempDirPath:{}", tempDirPath);
            // Process the file as required
            // Delete the temporary file
//            tempFile.delete();
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to upload file");
        }
        return ResponseEntity.ok().body("File uploaded successfully");
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
