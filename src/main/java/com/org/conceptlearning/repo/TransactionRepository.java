package com.org.conceptlearning.repo;

import com.org.conceptlearning.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    @Query(""" 
            SELECT t FROM Transaction t  
            WHERE t.serialNumber IN :serialNumber  
            AND t.transactionDate IN :transactionDates  
            AND t.transactionRemarks IN :remarks """)
    List<Transaction> findByTransactionDateAndRemarksIn(@Param("serialNumber") List<Long> serialNumber,
                                                        @Param("transactionDates") List<LocalDate> transactionDates,
                                                        @Param("remarks") List<String> remarks);

}
