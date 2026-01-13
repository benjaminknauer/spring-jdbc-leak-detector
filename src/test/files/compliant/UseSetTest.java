package test.files.compliant;

import org.springframework.jdbc.core.simple.JdbcClient;
import test.files.shared.TestModels.User;

/**
 * Tests that .set() usage (safe alternative) is NOT flagged.
 * EXPECTED: 0 issues
 */
class UseSetTest {
    private JdbcClient jdbcClient;

    void testCase() {
        var users = jdbcClient.sql("SELECT * FROM users")
            .query(User.class)
            .set();
        users.forEach(user -> System.out.println(user.name));
    }
}
