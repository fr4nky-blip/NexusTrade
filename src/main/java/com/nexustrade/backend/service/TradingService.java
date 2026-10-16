package com.nexustrade.backend.service;

import com.nexustrade.backend.model.Portfolio;
import com.nexustrade.backend.model.Stock;
import com.nexustrade.backend.model.User;
import com.nexustrade.backend.model.Transaction;
import com.nexustrade.backend.repository.PortfolioRepository;
import com.nexustrade.backend.repository.StockRepository;
import com.nexustrade.backend.repository.UserRepository;
import com.nexustrade.backend.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.math.BigDecimal;
import java.util.Optional;

@Service
public class TradingService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StockRepository stockRepository;

    @Autowired
    private PortfolioRepository portfolioRepository;

    @Autowired
    private TransactionRepository transactionRepository; // 👈 1. Autowire the TransactionRepository

    @Transactional
    public String buyStock(String email, String ticker, Integer quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero.");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Stock stock = stockRepository.findByTicker(ticker.toUpperCase())
                .orElseThrow(() -> new RuntimeException("Stock ticker not found: " + ticker));

        BigDecimal totalCost = stock.getCurrentPrice().multiply(BigDecimal.valueOf(quantity));

        // Check if user has enough virtual cash
        if (user.getCashBalance().compareTo(totalCost) < 0) {
            throw new RuntimeException("Insufficient funds! Required: $" + totalCost + ", Available: $" + user.getCashBalance());
        }

        // Deduct cash and update user
        user.setCashBalance(user.getCashBalance().subtract(totalCost));
        userRepository.save(user);

        // Update or create portfolio holding
        Optional<Portfolio> portfolioOpt = portfolioRepository.findByUserAndStock(user, stock);
        Portfolio portfolio;
        if (portfolioOpt.isPresent()) {
            portfolio = portfolioOpt.get();
            portfolio.setQuantity(portfolio.getQuantity() + quantity);
        } else {
            portfolio = new Portfolio(user, stock, quantity);
        }
        portfolioRepository.save(portfolio);

        // 👇 2. Save BUY transaction log
        Transaction transaction = new Transaction(
                user,
                stock.getTicker(),
                "BUY",
                quantity,
                stock.getCurrentPrice().doubleValue(),
                LocalDateTime.now()
        );
        transactionRepository.save(transaction);

        return "Successfully bought " + quantity + " shares of " + stock.getTicker() + " for $" + totalCost;
    }

    @Transactional
    public String sellStock(String email, String ticker, Integer quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero.");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Stock stock = stockRepository.findByTicker(ticker.toUpperCase())
                .orElseThrow(() -> new RuntimeException("Stock ticker not found: " + ticker));

        Portfolio portfolio = portfolioRepository.findByUserAndStock(user, stock)
                .orElseThrow(() -> new RuntimeException("You do not own any shares of " + stock.getTicker()));

        if (portfolio.getQuantity() < quantity) {
            throw new RuntimeException("You only own " + portfolio.getQuantity() + " shares, cannot sell " + quantity);
        }

        BigDecimal totalRevenue = stock.getCurrentPrice().multiply(BigDecimal.valueOf(quantity));

        // Add cash back to user
        user.setCashBalance(user.getCashBalance().add(totalRevenue));
        userRepository.save(user);

        // Update portfolio quantity
        int remainingShares = portfolio.getQuantity() - quantity;
        if (remainingShares == 0) {
            portfolioRepository.delete(portfolio);
        } else {
            portfolio.setQuantity(remainingShares);
            portfolioRepository.save(portfolio);
        }

        // 👇 3. Save SELL transaction log
        Transaction transaction = new Transaction(
                user,
                stock.getTicker(),
                "SELL",
                quantity,
                stock.getCurrentPrice().doubleValue(),
                LocalDateTime.now()
        );
        transactionRepository.save(transaction);

        return "Successfully sold " + quantity + " shares of " + stock.getTicker() + " for $" + totalRevenue;
    }
}