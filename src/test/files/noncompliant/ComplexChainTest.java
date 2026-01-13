package test.files.noncompliant;

import org.springframework.jdbc.core.simple.JdbcClient;
import test.files.shared.TestModels.User;
import java.util.stream.Stream;

/**
 * Tests detection of complex chain with multiple params and .stream() assigned to variable.
 * EXPECTED: 1 issue on the line with jdbcClient
 */
class ComplexChainTest {
    private JdbcClient jdbcClient;

    void testCase() {
        Stream<User> users = jdbcClient // Noncompliant {{This stream holds a database connection and must be used within a try-with-resources statement.}}
            .sql("SELECT * FROM users WHERE age > ?")
            .param(18)
            .param(100)
            .query(User.class)
            .stream();
        users.forEach(System.out::println);
    }
}
