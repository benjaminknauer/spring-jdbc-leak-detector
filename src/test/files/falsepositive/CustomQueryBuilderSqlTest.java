package test.files.falsepositive;

import java.util.*;

/**
 * Tests that custom query builder with .sql() method is NOT flagged (not JdbcClient).
 * EXPECTED: 0 issues
 */
class CustomQueryBuilderSqlTest {

    static class CustomQueryBuilder {
        public List<String> sql(String s) {
            return new ArrayList<>();
        }
    }

    void testCase() {
        CustomQueryBuilder builder = new CustomQueryBuilder();
        builder.sql("SELECT *").stream().forEach(System.out::println);
    }
}
