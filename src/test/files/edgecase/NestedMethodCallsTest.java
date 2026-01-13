package test.files.edgecase;

import org.springframework.jdbc.core.simple.JdbcClient;
import test.files.shared.TestModels.User;
import java.util.stream.Stream;

/**
 * Tests detection of JDBC streams used as method arguments.
 * EXPECTED: 1 issue - stream passed as argument is not in try-with-resources
 */
class NestedMethodCallsTest {
    private JdbcClient jdbcClient;

    void testCase() {
        processStream(jdbcClient.sql("SELECT * FROM users") // Noncompliant {{This stream holds a database connection and must be used within a try-with-resources statement.}}
            .query(User.class)
            .stream());
    }

    void processStream(Stream<User> stream) {
        stream.forEach(user -> System.out.println(user.name));
    }
}
