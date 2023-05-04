package com.org.conceptlearning;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;
import com.org.conceptlearning.entity.Customer;
import com.org.conceptlearning.entity.Transaction;
import com.org.conceptlearning.model.TransactionRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@SpringBootTest
@Slf4j
public abstract class AbstractTest {

    protected static final String ZAKAT_API_ENDPOINT = "/v1/zakat";

    protected MockMvc mockMvc;

    public static String logPrettyPrintUsingGson(String content) {
        String prettyPrint = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create().toJson(JsonParser.parseString(content));
        log.info("getPrettyPrintUsingGson:\n{}", prettyPrint);
        return prettyPrint;
    }

    public static String getPrettyPrintUsingObjectMapper(Object content, Class<?> clazz) throws JsonProcessingException {
        String prettyPrint = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT)
                .registerModule(new JavaTimeModule())
                .writeValueAsString(new ObjectMapper().registerModule(new JavaTimeModule()).convertValue(content, clazz));
        log.info("getPrettyPrintUsingObjectMapper:\n{}", prettyPrint);
        return prettyPrint;
    }

    protected Customer createCustomer() {
        return Customer.builder()
                .accountNumber("")
                .customerId(123l)
                .firstname("Shahbaz")
                .lastname("Akhter")
                .currency("INR")
                .build();
    }

    protected List<Transaction> createTransactions(BigDecimal balance) {
        return List.of(createTransaction(balance, 1));
    }

    protected Transaction createTransaction(BigDecimal balance, Integer daysAhead) {
        LocalDate now = LocalDate.now();
        LocalDate startYear = now.minusYears(1);
        return Transaction.builder()
                .transactionDate(startYear.plusDays(daysAhead))
                .customerDetail(createCustomer())
                .balance(balance)
                .transactionRemarks("shahbaz")
                .build();
    }

    protected TransactionRequest.TransactionRequestBuilder createTransactionRequestBuilder() {
        LocalDate now = LocalDate.now();
        LocalDate startYear = now.minusYears(1);
        return TransactionRequest.builder()
                .fromDate(startYear)
                .remarksData("Shahbaz")
                .toDate(now);
    }

}
