package test.files.edgecase;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import test.files.shared.TestModels.User;
import java.util.stream.Stream;

/**
 * Tests detection with JdbcTemplate used directly (not subclass).
 * EXPECTED: 1 issue - queryForStream without try-with-resources
 */
class JdbcTemplateSubclassTest {
    private JdbcTemplate jdbcTemplate;

    void testCase() {
        RowMapper<User> mapper = (rs, rowNum) -> {
            User u = new User();
            u.name = rs.getString("name");
            return u;
        };

        Stream<User> users = jdbcTemplate.queryForStream("SELECT * FROM users", mapper); // Noncompliant {{This stream holds a database connection and must be used within a try-with-resources statement.}}
        users.forEach(user -> System.out.println(user.name));
    }
}
