package test.files.noncompliant;

import org.springframework.jdbc.core.simple.JdbcClient;
import test.files.shared.TestModels.User;
import java.util.stream.Stream;

/**
 * Tests detection of stream returned from method without try-with-resources.
 * EXPECTED: 1 issue on the line with .stream()
 */
class ReturnStreamTest {
    private JdbcClient jdbcClient;

    Stream<User> testCase() {
        return jdbcClient.sql("SELECT * FROM users") // Noncompliant {{This stream holds a database connection and must be used within a try-with-resources statement.}}
            .query(User.class)
            .stream();
    }
}
