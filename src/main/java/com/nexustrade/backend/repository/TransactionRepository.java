package com.nexustrade.backend.repository;

import com.nexustrade.backend.model.Transaction;
import com.nexustrade.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByUserOrderByTimestampDesc(User user);
}