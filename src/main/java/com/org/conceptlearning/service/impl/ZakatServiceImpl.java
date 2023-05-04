package com.org.conceptlearning.service.impl;

import com.org.conceptlearning.entity.Customer;
import com.org.conceptlearning.entity.Transaction;
import com.org.conceptlearning.model.Amount;
import com.org.conceptlearning.model.CustomerResponse;
import com.org.conceptlearning.model.TransactionRequest;
import com.org.conceptlearning.model.TransactionResponse;
import com.org.conceptlearning.repo.CustomerRepository;
import com.org.conceptlearning.repo.TransactionRepository;
import com.org.conceptlearning.service.ZakatService;
import com.org.conceptlearning.utility.ZakatUtility;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.repository.init.ResourceReader;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ZakatServiceImpl implements ZakatService {

    private static NumberFormat NUMBER_FORMATTER;

    {
        this.NUMBER_FORMATTER = NumberFormat.getInstance();
        NUMBER_FORMATTER.setMaximumFractionDigits(2);
    }

    private final TransactionRepository transactionRepository;
    private final CustomerRepository customerRepository;
    private final ZakatUtility zakatUtility;

    public ZakatServiceImpl(TransactionRepository transactionRepository, CustomerRepository customerRepository, ZakatUtility zakatUtility) {
        this.transactionRepository = transactionRepository;
        this.customerRepository = customerRepository;
        this.zakatUtility = zakatUtility;
    }

    @Override
    public boolean saveTransaction(String fileName) {
        try {
            String path = ResourceReader.class.getClassLoader().getResource("input/" + fileName).getPath();
            List<Transaction> transactionDetails = zakatUtility.readTransactionsFromExcel(path);
            Customer customerDetail = zakatUtility.readCustomerFromExcel(path);
            Customer existingCustomer = customerRepository.findByAccountNumber(customerDetail.getAccountNumber());
            if (Objects.isNull(existingCustomer)) {
                customerRepository.save(customerDetail);
            } else {
                customerDetail.setCustomerId(existingCustomer.getCustomerId());
            }
            transactionDetails.forEach(transactionDetail -> transactionDetail.setCustomerDetail(customerDetail));
            log.info("TransactionDetails:{}", transactionDetails);
            transactionRepository.saveAll(transactionDetails);
        } catch (Exception e) {
            log.error("Exception Occurred: ", e);
            return false;
        }
        return true;
    }

    @Override
    public List<TransactionResponse> fetchAllTransactions(TransactionRequest transactionRequest) {
        try {
            List<Transaction> transactionDetails = transactionRepository.findAll();
            return filterTransactionDetails(transactionDetails, transactionRequest)
                    .stream()
                    .filter(transaction -> transaction.getTransactionRemarks().toLowerCase().contains(transactionRequest.getRemarksData().toLowerCase()))
                    .sorted(Comparator.comparingDouble(i -> i.getDepositAmount().doubleValue()))
                    .map(this::mapTransactionResponse).collect(Collectors.toList());
        } catch (Exception e) {
            log.error("ERROR Occurred: ", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public Amount calculateZakat(TransactionRequest transactionRequest) {
        List<Transaction> transactionDetails = transactionRepository.findAll();
        TransactionResponse minimumBalanceTransaction = filterTransactionDetails(transactionDetails, transactionRequest)
                .stream()
                .map(this::mapTransactionResponse)
                .filter(transaction -> !transaction.getTransactionRemarks().toLowerCase().contains(transactionRequest.getRemarksData().toLowerCase()))
                .min(Comparator.comparing(TransactionResponse::getBalance)).orElseThrow();

        Set<BigDecimal> minimumBalances = filterTransactionDetails(transactionDetails, transactionRequest).stream().map(Transaction::getBalance).distinct().sorted().limit(3).collect(Collectors.toSet());
        Double averageBalance = minimumBalances.stream().mapToDouble(BigDecimal::doubleValue).average().orElse(minimumBalanceTransaction.getBalance().doubleValue());
        log.info("Average of minimum 3 balance:{}, those 3 balances are {}, minimumBalance:{}", averageBalance, minimumBalances, minimumBalanceTransaction.getBalance());
        BigDecimal zakatAmount = BigDecimal.valueOf(averageBalance).multiply(new BigDecimal("0.025"));
        log.info("ZAKAT :{}, MINIMUM BALANCE:{}, DATE:{}, ID:{}", zakatAmount, minimumBalanceTransaction.getBalance(), minimumBalanceTransaction.getTransactionDate(),
                minimumBalanceTransaction.getTransactionDetailId());

        return Amount.builder().amount(zakatAmount)
                .formattedAmount(NUMBER_FORMATTER.format(zakatAmount))
                .year(minimumBalanceTransaction.getTransactionDate())
                .isPaid(false)
                .build();
    }

    @Override
    public Amount calculateInterest(TransactionRequest transactionRequest) {
        List<Transaction> transactionDetails = transactionRepository.findAll();
        BigDecimal interestAmount = filterTransactionDetails(transactionDetails, transactionRequest)
                .stream()
                .map(this::mapTransactionResponse)
                .filter(transaction -> transaction.getTransactionRemarks().toLowerCase().contains(transactionRequest.getRemarksData().toLowerCase()))
                .map(TransactionResponse::getDepositAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return Amount.builder().amount(interestAmount)
                .formattedAmount(NUMBER_FORMATTER.format(interestAmount))
                .year(transactionRequest.getToDate())
                .build();
    }

    private List<Transaction> filterTransactionDetails(List<Transaction> transactionDetails, TransactionRequest transactionRequest) {
        return transactionDetails.stream()
                .filter(transaction -> Objects.nonNull(transactionRequest.getFromDate())
                        && Objects.nonNull(transactionRequest.getToDate())
                        && transaction.getTransactionDate().isAfter(transactionRequest.getFromDate())
                        && transaction.getTransactionDate().isBefore(transactionRequest.getToDate()))
                .toList();
    }

    private TransactionResponse mapTransactionResponse(Transaction transaction) {
        return TransactionResponse.builder()
                .transactionDetailId(transaction.getTransactionDetailId())
                .transactionRemarks(transaction.getTransactionRemarks())
                .depositAmount(transaction.getDepositAmount())
                .withdrawalAmount(transaction.getWithdrawalAmount())
                .balance(transaction.getBalance())
                .chequeNumber(transaction.getChequeNumber())
                .transactionDate(transaction.getTransactionDate())
                .valueDate(transaction.getValueDate())
                .customerDetail(mapCustomerResponse(transaction))
                .build();
    }

    private CustomerResponse mapCustomerResponse(Transaction transaction) {
        return CustomerResponse.builder()
                .firstname(transaction.getCustomerDetail().getFirstname())
                .lastname(transaction.getCustomerDetail().getLastname())
                .accountNumber(transaction.getCustomerDetail().getAccountNumber())
                .build();
    }

}
