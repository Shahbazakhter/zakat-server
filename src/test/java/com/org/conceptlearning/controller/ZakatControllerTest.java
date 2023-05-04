package com.org.conceptlearning.controller;

import com.org.conceptlearning.AbstractTest;
import com.org.conceptlearning.entity.Transaction;
import com.org.conceptlearning.model.TransactionRequest;
import com.org.conceptlearning.repo.CustomerRepository;
import com.org.conceptlearning.repo.TransactionRepository;
import com.org.conceptlearning.service.ZakatService;
import com.org.conceptlearning.utility.ZakatUtility;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
public class ZakatControllerTest extends AbstractTest {

    @MockBean
    private TransactionRepository transactionRepository;

    @MockBean
    private CustomerRepository customerRepository;

    @MockBean
    private ZakatUtility zakatUtility;

    @Autowired
    private ZakatService zakatService;

    @BeforeEach
    public void setup() throws IOException, ParseException {
        when(zakatUtility.readCustomerFromExcel(anyString())).thenReturn(createCustomer());
        mockMvc = MockMvcBuilders.standaloneSetup(new ZakatController(zakatService)).build();
    }

    @ParameterizedTest
    @CsvSource({"100, 2.5", "1000, 25.0", "10000, 250.0", "100000, 2500.0", "1000000, 25000.0"})
    public void calculateZakat_ShouldReturnCorrectZakatAmount_WhenOneTransactionsExist(BigDecimal actualBalance, BigDecimal expectedZakat) throws Exception {
        when(zakatUtility.readTransactionsFromExcel(anyString())).thenReturn(createTransactions(actualBalance));
        when(transactionRepository.findAll()).thenReturn(createTransactions(actualBalance));

        ResultActions apiResponse = mockMvc
                .perform(post(ZAKAT_API_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(getPrettyPrintUsingObjectMapper(createTransactionRequestBuilder().build(), TransactionRequest.class)));
        logPrettyPrintUsingGson(apiResponse.andReturn().getResponse().getContentAsString());
        apiResponse.andExpect(status().isOk())
                .andExpect(jsonPath("$.amount", is(expectedZakat.doubleValue())));
    }

    @Test
    public void calculateZakat_ShouldReturnCorrectZakatAmount_WhenMultipleTransactionsExist() throws Exception {
        List<Transaction> transactions = List.of(createTransaction(BigDecimal.valueOf(1000), 1),
                createTransaction(BigDecimal.valueOf(30000), 30),
                createTransaction(BigDecimal.valueOf(60000), 60),
                createTransaction(BigDecimal.valueOf(90000), 90),
                createTransaction(BigDecimal.valueOf(100000), 120),
                createTransaction(BigDecimal.valueOf(200000), 150),
                createTransaction(BigDecimal.valueOf(300000), 180),
                createTransaction(BigDecimal.valueOf(400000), 210),
                createTransaction(BigDecimal.valueOf(500000), 240),
                createTransaction(BigDecimal.valueOf(600000), 270),
                createTransaction(BigDecimal.valueOf(700000), 300),
                createTransaction(BigDecimal.valueOf(800000), 330),
                createTransaction(BigDecimal.valueOf(900000), 360),
                createTransaction(BigDecimal.valueOf(20000), 370));
        when(zakatUtility.readTransactionsFromExcel(anyString())).thenReturn(transactions);
        when(transactionRepository.findAll()).thenReturn(transactions);

        ResultActions apiResponse = mockMvc
                .perform(post(ZAKAT_API_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(getPrettyPrintUsingObjectMapper(createTransactionRequestBuilder().build(), TransactionRequest.class)));

        logPrettyPrintUsingGson(apiResponse.andReturn().getResponse().getContentAsString());
        apiResponse.andExpect(status().isOk())
                .andExpect(jsonPath("$.formattedAmount", is("758.33")));
    }

}