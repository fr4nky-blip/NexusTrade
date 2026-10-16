# NexusTrade – Stock Trading Backend API

NexusTrade is a production-grade, full-stack stock trading simulation API built with **Spring Boot 3**, **Spring Security**, and **MySQL**. It provides secure authentication, real-time portfolio tracking, precise financial transaction handling, and transaction history auditing.

---

## 🛠 Tech Stack & Tools
* **Language:** Java 17+
* **Framework:** Spring Boot 3
* **Security:** Spring Security (Stateless JWT Authentication)
* **Database:** MySQL & Hibernate / JPA
* **Math & Financial Integrity:** `BigDecimal`
* **Build Tool:** Maven

---

## ✨ Key Features
* 🔐 **Stateless Security:** User registration and authentication powered by JWT tokens.
* 📈 **Trading Engine:** Instant `BUY` and `SELL` transaction handling wrapped in `@Transactional` boundaries.
* 💰 **Financial Accuracy:** Utilizes `BigDecimal` across all cash and price operations to eliminate floating-point precision errors.
* 📊 **Portfolio Management:** Real-time user cash balance updates and stock holding synchronization.
* 📜 **Transaction Auditing:** Full historical audit log accessible via dedicated endpoints.
* 🚨 **Global Error Handling:** Integrated `@ControllerAdvice` providing clean, structured JSON error responses.

---

## 🔌 API Endpoints Summary

### 🔑 Authentication
* `POST /api/auth/register` – Register a new trading account
* `POST /api/auth/login` – Authenticate and receive a JWT token

### 💼 Portfolio & Trading
* `GET /api/portfolio` – Fetch current cash balance and holdings
* `POST /api/trade/buy?ticker=AAPL&quantity=5` – Buy shares of a stock
* `POST /api/trade/sell?ticker=AAPL&quantity=5` – Sell shares of a stock
* `GET /api/trade/history` – Retrieve transaction history log