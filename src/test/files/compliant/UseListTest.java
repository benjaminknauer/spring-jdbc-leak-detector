package test.files.compliant;

import org.springframework.jdbc.core.simple.JdbcClient;
import test.files.shared.TestModels.User;
import java.util.List;

/**
 * Tests that .list() usage (safe alternative) is NOT flagged.
 * EXPECTED: 0 issues
 */
class UseListTest {
    private JdbcClient jdbcClient;

    void testCase() {
        List<User> users = jdbcClient.sql("SELECT * FROM users")
            .query(User.class)
            .list();
        users.forEach(user -> System.out.println(user.name));
    }
}
