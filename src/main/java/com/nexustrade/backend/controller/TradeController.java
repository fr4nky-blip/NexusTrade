package com.nexustrade.backend.controller;

import com.nexustrade.backend.model.Transaction;
import com.nexustrade.backend.model.User;
import com.nexustrade.backend.repository.TransactionRepository;
import com.nexustrade.backend.repository.UserRepository;
import com.nexustrade.backend.service.TradingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/trade")
public class TradeController {

    @Autowired
    private TradingService tradingService;

    @Autowired
    private TransactionRepository transactionRepository; // 👈 1. Autowire TransactionRepository

    @Autowired
    private UserRepository userRepository; // 👈 2. Autowire UserRepository to fetch the user

    @PostMapping("/buy")
    public ResponseEntity<String> buyStock(@RequestParam String ticker, @RequestParam Integer quantity, Authentication authentication) {
        String email = authentication.getName(); // Extracted securely from JWT token!
        try {
            String result = tradingService.buyStock(email, ticker, quantity);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/sell")
    public ResponseEntity<String> sellStock(@RequestParam String ticker, @RequestParam Integer quantity, Authentication authentication) {
        String email = authentication.getName();
        try {
            String result = tradingService.sellStock(email, ticker, quantity);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 👇 3. Add the Transaction History endpoint
    @GetMapping("/history")
    public ResponseEntity<?> getTransactionHistory(Authentication authentication) {
        String email = authentication.getName();
        try {
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            List<Transaction> history = transactionRepository.findByUserOrderByTimestampDesc(user);
            return ResponseEntity.ok(history);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}