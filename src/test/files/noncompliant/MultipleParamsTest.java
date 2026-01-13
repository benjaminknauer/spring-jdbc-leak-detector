package test.files.noncompliant;

import org.springframework.jdbc.core.simple.JdbcClient;
import test.files.shared.TestModels.User;

/**
 * Tests detection of stream with multiple .param() calls without try-with-resources.
 * EXPECTED: 1 issue on the line with jdbcClient.sql()
 */
class MultipleParamsTest {
    private JdbcClient jdbcClient;

    void testCase() {
        jdbcClient.sql("SELECT * FROM users WHERE name = ? AND age = ?") // Noncompliant {{This stream holds a database connection and must be used within a try-with-resources statement.}}
            .param("John")
            .param(25)
            .query(User.class)
            .stream()
            .forEach(System.out::println);
    }
}
