package test.files.noncompliant;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import test.files.shared.TestModels.User;
import java.util.stream.Stream;

/**
 * Tests detection of JdbcTemplate.queryForStream() without try-with-resources.
 * EXPECTED: 1 issue on the line with queryForStream()
 */
class JdbcTemplateQueryForStreamTest {
    private JdbcTemplate jdbcTemplate;

    void testCase() {
        RowMapper<User> rowMapper = (rs, rowNum) -> {
            User user = new User();
            user.name = rs.getString("name");
            user.age = rs.getInt("age");
            return user;
        };

        Stream<User> users = jdbcTemplate.queryForStream("SELECT * FROM users", rowMapper); // Noncompliant {{This stream holds a database connection and must be used within a try-with-resources statement.}}
        users.forEach(user -> System.out.println(user.name));
    }
}
