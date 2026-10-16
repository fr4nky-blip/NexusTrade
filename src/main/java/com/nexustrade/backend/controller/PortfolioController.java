package com.nexustrade.backend.controller;

import com.nexustrade.backend.model.Portfolio;
import com.nexustrade.backend.model.User;
import com.nexustrade.backend.repository.PortfolioRepository;
import com.nexustrade.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/portfolio")
public class PortfolioController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PortfolioRepository portfolioRepository;

    @GetMapping
    public ResponseEntity<?> getPortfolio(Authentication authentication) {
        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Portfolio> portfolios = portfolioRepository.findByUser(user);

        // Format the holdings for clean JSON output
        List<Map<String, Object>> holdings = portfolios.stream().map(p -> {
            Map<String, Object> item = new HashMap<>();
            item.put("ticker", p.getStock().getTicker());
            item.put("companyName", p.getStock().getCompanyName());
            item.put("quantity", p.getQuantity());
            item.put("currentPrice", p.getStock().getCurrentPrice());

            BigDecimal totalValue = p.getStock().getCurrentPrice().multiply(BigDecimal.valueOf(p.getQuantity()));
            item.put("totalValue", totalValue);
            return item;
        }).collect(Collectors.toList());

        // Build summary response
        Map<String, Object> response = new HashMap<>();
        response.put("email", user.getEmail());
        response.put("cashBalance", user.getCashBalance());
        response.put("holdings", holdings);

        return ResponseEntity.ok(response);
    }
}