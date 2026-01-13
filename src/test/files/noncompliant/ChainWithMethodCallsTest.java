package test.files.noncompliant;

import org.springframework.jdbc.core.simple.JdbcClient;
import test.files.shared.TestModels.User;
import java.util.stream.Stream;

/**
 * Tests detection of chain with method calls as parameters.
 * EXPECTED: 1 issue on the line with jdbcClient
 */
class ChainWithMethodCallsTest {
    private JdbcClient jdbcClient;

    String buildQuery() {
        return "SELECT * FROM users";
    }

    Object getParam1() {
        return "param1";
    }

    Object getParam2() {
        return "param2";
    }

    void testCase() {
        Stream<User> users = jdbcClient // Noncompliant {{This stream holds a database connection and must be used within a try-with-resources statement.}}
            .sql(buildQuery())
            .param(getParam1())
            .param(getParam2())
            .query(User.class)
            .stream();
        users.forEach(System.out::println);
    }
}
