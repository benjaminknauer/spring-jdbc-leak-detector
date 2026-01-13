package test.files.noncompliant;

import org.springframework.jdbc.core.simple.JdbcClient;
import test.files.shared.TestModels.User;
import java.util.stream.Stream;

/**
 * Tests detection of stream assigned to variable without try-with-resources.
 * EXPECTED: 1 issue on the line with .stream()
 */
class StreamNotClosedTest {
    private JdbcClient jdbcClient;

    void testCase() {
        Stream<User> users = jdbcClient.sql("SELECT * FROM users") // Noncompliant {{This stream holds a database connection and must be used within a try-with-resources statement.}}
            .query(User.class)
            .stream();
        users.forEach(user -> System.out.println(user.name));
    }
}
