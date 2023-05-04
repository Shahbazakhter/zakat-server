package com.org.conceptlearning.utility;

import com.org.conceptlearning.entity.Customer;
import com.org.conceptlearning.entity.Transaction;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.stereotype.Component;

import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
@Slf4j
public class ZakatUtility {

    private static final SimpleDateFormat COMMA_SEPERATED_DATE_FORMAT = new SimpleDateFormat("dd,MM,yyyy");

    public List<Transaction> readTransactionsFromExcel(String filename) throws IOException, ParseException {
        List<Transaction> transactionDetails = new ArrayList<>();
        try (Workbook workbook = WorkbookFactory.create(new FileInputStream(filename))) {
            Sheet sheet = workbook.getSheetAt(0);
            for (Row row : sheet) {
                if (row.getRowNum() < 13 || String.valueOf(row.getCell(1)).isBlank() || String.valueOf(row.getCell(1)).trim().matches(".*\\D.*")) {
                    continue;
                }
                log.info(String.valueOf(row.getCell(1)));
                LocalDate valueDate = COMMA_SEPERATED_DATE_FORMAT.parse(String.valueOf(row.getCell(2))).toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                LocalDate transactionDate = COMMA_SEPERATED_DATE_FORMAT.parse(String.valueOf(row.getCell(3))).toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                String chequeNumber = String.valueOf(row.getCell(4));
                String remarks = String.valueOf(row.getCell(5));
                BigDecimal withdrawalAmount = BigDecimal.valueOf(Double.parseDouble(String.valueOf(row.getCell(6))));
                BigDecimal depositAmount = BigDecimal.valueOf(Double.parseDouble(String.valueOf(row.getCell(7))));
                BigDecimal balance = BigDecimal.valueOf(Double.parseDouble(String.valueOf(row.getCell(8))));
                Transaction transactionDetail = Transaction.builder().valueDate(valueDate).transactionDate(transactionDate).chequeNumber(chequeNumber)
                        .transactionRemarks(remarks).withdrawalAmount(withdrawalAmount).depositAmount(depositAmount).balance(balance).build();
                transactionDetails.add(transactionDetail);
            }
        }
        return transactionDetails;
    }

    public Customer readCustomerFromExcel(String filename) {
        Customer.CustomerBuilder customerBuilder = Customer.builder();
        try (Workbook workbook = WorkbookFactory.create(new FileInputStream(filename))) {
            Sheet sheet = workbook.getSheetAt(0);
            String[] cellText = String.valueOf(sheet.getRow(11).getCell(1)).split(" - ");
            String firstname = cellText[1].trim().split(" {2}")[0];
            String lastname = cellText[1].trim().split(" {2}")[1];
            String accountNumber = cellText[2].trim();
            customerBuilder.firstname(firstname).lastname(lastname).accountNumber(accountNumber).currency("INR");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return customerBuilder.build();
    }
}
