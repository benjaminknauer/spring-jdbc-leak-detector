package test.files.compliant;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import test.files.shared.TestModels.User;
import java.util.stream.Stream;

/**
 * Tests that try-with-resources with JdbcTemplate is NOT flagged.
 * EXPECTED: 0 issues
 */
class TryWithResourcesJdbcTemplateTest {
    private JdbcTemplate jdbcTemplate;

    void testCase() {
        RowMapper<User> rowMapper = (rs, rowNum) -> {
            User user = new User();
            user.name = rs.getString("name");
            user.age = rs.getInt("age");
            return user;
        };

        try (Stream<User> users = jdbcTemplate.queryForStream("SELECT * FROM users", rowMapper)) {
            users.forEach(user -> System.out.println(user.name));
        }
    }
}
