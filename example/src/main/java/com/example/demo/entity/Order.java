package com.example.demo.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Order {
    private Long id;
    private Long userId;
    private BigDecimal amount;
    private String status;
    private LocalDateTime createdAt;

    public Order() {}

    public Order(Long id, Long userId, BigDecimal amount, String status, LocalDateTime createdAt) {
        this.id = id;
        this.userId = userId;
        this.amount = amount;
        this.status = status;
        this.createdAt = createdAt;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    @Override
    public String toString() {
        return "Order{id=" + id + ", userId=" + userId + ", amount=" + amount + ", status='" + status + "'}";
    }
}
