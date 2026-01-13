package test.files.falsepositive;

import java.util.*;

/**
 * Tests that custom query builder with .query() method is NOT flagged (not JdbcClient).
 * EXPECTED: 0 issues
 */
class CustomQueryBuilderQueryTest {

    static class CustomQueryBuilder {
        public List<String> query(String sql) {
            return new ArrayList<>();
        }
    }

    void testCase() {
        CustomQueryBuilder builder = new CustomQueryBuilder();
        builder.query("SELECT * FROM users").stream().forEach(System.out::println);
    }
}
