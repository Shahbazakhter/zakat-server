package com.org.conceptlearning.service.impl;

import com.org.conceptlearning.entity.Customer;
import com.org.conceptlearning.entity.Transaction;
import com.org.conceptlearning.model.*;
import com.org.conceptlearning.repo.CustomerRepository;
import com.org.conceptlearning.repo.TransactionRepository;
import com.org.conceptlearning.service.ZakatService;
import com.org.conceptlearning.utility.ZakatUtility;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.*;
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
    public TransactionResult saveTransaction(String fileName) {
        TransactionResult.TransactionResultBuilder transactionBuilder = TransactionResult.builder();
        try {
            List<Transaction> transactionDetails = zakatUtility.readTransactionsFromExcel(fileName);
            Customer customerDetail = zakatUtility.readCustomerFromExcel(fileName);
            List<Transaction> duplicateTransactions = transactionRepository.findByTransactionDateAndRemarksIn(transactionDetails.stream().map(Transaction::getSerialNumber).collect(Collectors.toList()),
                    transactionDetails.stream().map(Transaction::getTransactionDate).collect(Collectors.toList()),
                    transactionDetails.stream().map(Transaction::getTransactionRemarks).collect(Collectors.toList()));
            if (!duplicateTransactions.isEmpty()) {
                transactionBuilder.errorCode("DUPLICATE_ENTRIES").errorMessage(duplicateTransactions.stream()
                        .map(transaction -> "TransactionDate:" + transaction.getTransactionDate() + ",Remarks:" + transaction.getTransactionRemarks()).collect(Collectors.toList()));
            }
            if(duplicateTransactions.size()!=transactionDetails.size()){
                transactionBuilder.successMessage("File Uploaded Successfully");
            }
            Map<String, LocalDate> duplicateEntries = duplicateTransactions.stream().collect(Collectors.toMap(Transaction::getTransactionRemarks, Transaction::getTransactionDate));
            List<Transaction> updatedTransactionDetails = transactionDetails.stream().filter(transaction -> !duplicateEntries.containsKey(transaction.getTransactionRemarks())).collect(Collectors.toList());

            Customer existingCustomer = customerRepository.findByAccountNumber(customerDetail.getAccountNumber());
            if (Objects.isNull(existingCustomer)) {
                customerRepository.save(customerDetail);
            } else {
                customerDetail.setCustomerId(existingCustomer.getCustomerId());
            }
            updatedTransactionDetails.forEach(transactionDetail -> transactionDetail.setCustomerDetail(customerDetail));
            log.info("TransactionDetails:{}", updatedTransactionDetails);
            transactionRepository.saveAll(updatedTransactionDetails);
        } catch (Exception e) {
            log.error("Exception Occurred: ", e);
            throw new RuntimeException(e);
        }
        return transactionBuilder.build();
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
                .serialNumber(transaction.getSerialNumber())
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
