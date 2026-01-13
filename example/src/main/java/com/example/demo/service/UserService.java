package com.example.demo.service;

import com.example.demo.entity.User;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Service;

import java.util.stream.Stream;

/**
 * UserService demonstrating NONCOMPLIANT usage of JdbcClient.stream().
 *
 * WARNING: This code contains intentional bugs for demonstration purposes!
 * The stream() method returns a Stream that holds an open database connection.
 * Without try-with-resources, the connection is never returned to the pool,
 * causing connection leaks that will eventually crash the application.
 */
@Service
public class UserService {

    private final JdbcClient jdbcClient;

    private static final RowMapper<User> USER_MAPPER = (rs, rowNum) -> new User(
        rs.getLong("id"),
        rs.getString("name"),
        rs.getString("email"),
        rs.getTimestamp("created_at").toLocalDateTime()
    );

    public UserService(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    /**
     * NONCOMPLIANT: Stream is not closed, causing connection leak!
     *
     * Each call to this method leaks one database connection.
     * After ~10 calls (default pool size), the application will hang.
     */
    public void printAllUsers() {
        // BAD: Stream is assigned to variable but never closed
        Stream<User> users = jdbcClient
            .sql("SELECT * FROM users")
            .query(USER_MAPPER)
            .stream();

        users.forEach(user -> System.out.println("User: " + user.getName()));
        // Connection is leaked here - never returned to pool!
    }

    /**
     * NONCOMPLIANT: Direct stream usage without closing!
     *
     * This pattern is even more dangerous because it's less obvious.
     */
    public long countActiveUsers() {
        // BAD: Stream is used directly but never closed
        return jdbcClient
            .sql("SELECT * FROM users WHERE email LIKE :pattern")
            .param("pattern", "%@example.com")
            .query(USER_MAPPER)
            .stream()
            .filter(user -> user.getName().length() > 3)
            .count();
        // Connection leaked after count() returns!
    }

    /**
     * COMPLIANT: Proper usage with try-with-resources.
     *
     * This is the correct way to use JdbcClient.stream().
     */
    public void printAllUsersCorrectly() {
        try (Stream<User> users = jdbcClient
                .sql("SELECT * FROM users")
                .query(USER_MAPPER)
                .stream()) {
            users.forEach(user -> System.out.println("User: " + user.getName()));
        }
        // Connection is automatically closed here
    }
}
