package test.files.noncompliant;

import org.springframework.jdbc.core.simple.JdbcClient;
import test.files.shared.TestModels.User;

/**
 * Tests detection of direct stream().forEach() without try-with-resources.
 * EXPECTED: 1 issue on the line with jdbcClient.sql()
 */
class DirectUsageTest {
    private JdbcClient jdbcClient;

    void testCase() {
        jdbcClient.sql("SELECT * FROM users") // Noncompliant {{This stream holds a database connection and must be used within a try-with-resources statement.}}
            .query(User.class)
            .stream()
            .forEach(user -> System.out.println(user.name));
    }
}
