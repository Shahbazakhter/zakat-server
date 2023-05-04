package com.org.conceptlearning.repo;

import com.org.conceptlearning.entity.Customer;
import jakarta.persistence.NamedNativeQuery;
import jakarta.persistence.NamedQuery;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

    Customer findByAccountNumber(String accountNumber);
}

