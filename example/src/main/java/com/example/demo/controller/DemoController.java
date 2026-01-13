package com.example.demo.controller;

import com.example.demo.service.OrderService;
import com.example.demo.service.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

/**
 * REST controller exposing the leaky service methods for demonstration.
 *
 * <p>Use these endpoints to observe connection pool exhaustion in action:</p>
 * <ol>
 *   <li>Start the application with {@code mvn spring-boot:run}</li>
 *   <li>Call the leaky endpoints 3+ times</li>
 *   <li>Watch the 4th call timeout due to pool exhaustion</li>
 * </ol>
 */
@RestController
public class DemoController {

    private final UserService userService;
    private final OrderService orderService;

    public DemoController(UserService userService, OrderService orderService) {
        this.userService = userService;
        this.orderService = orderService;
    }

    // ==================== LEAKY ENDPOINTS ====================

    @GetMapping("/users/print")
    public String printUsers() {
        userService.printAllUsers();
        return "Printed users (connection leaked!)";
    }

    @GetMapping("/users/count")
    public String countUsers() {
        long count = userService.countActiveUsers();
        return "Active users: " + count + " (connection leaked!)";
    }

    @GetMapping("/orders/revenue")
    public String calculateRevenue() {
        BigDecimal revenue = orderService.calculateTotalRevenue();
        return "Total revenue: " + revenue + " (connection leaked!)";
    }

    // ==================== SAFE ENDPOINTS ====================

    @GetMapping("/users/print-safe")
    public String printUsersSafe() {
        userService.printAllUsersCorrectly();
        return "Printed users (connection returned to pool)";
    }

    @GetMapping("/orders/revenue-safe")
    public String calculateRevenueSafe() {
        BigDecimal revenue = orderService.calculateTotalRevenueCorrectly();
        return "Total revenue: " + revenue + " (connection returned to pool)";
    }
}
