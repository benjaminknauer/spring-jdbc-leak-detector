package test.files.falsepositive;

import java.util.*;

/**
 * Tests that custom query builder chain with JDBC-like methods is NOT flagged.
 * EXPECTED: 0 issues
 */
class CustomQueryBuilderChainTest {

    static class CustomQueryBuilder {
        public List<String> query(String sql) {
            return new ArrayList<>();
        }

        public List<String> sql(String s) {
            return new ArrayList<>();
        }

        public CustomQueryBuilder param(Object p) {
            return this;
        }
    }

    void testCase() {
        CustomQueryBuilder builder = new CustomQueryBuilder();
        builder.param("test").query("SELECT *").stream().forEach(System.out::println);
    }
}
