package test.files.edgecase;

import org.springframework.jdbc.core.simple.JdbcClient;
import test.files.shared.TestModels.User;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * Tests detection of JDBC streams within lambda expressions.
 * EXPECTED: 1 issue - lambda does not provide try-with-resources
 */
class LambdaWithJdbcClientTest {
    private JdbcClient jdbcClient;

    void testCase() {
        Supplier<Stream<User>> supplier = () -> jdbcClient.sql("SELECT * FROM users") // Noncompliant {{This stream holds a database connection and must be used within a try-with-resources statement.}}
            .query(User.class)
            .stream();

        // Stream from supplier is not in try-with-resources
        supplier.get().forEach(user -> System.out.println(user.name));
    }
}
