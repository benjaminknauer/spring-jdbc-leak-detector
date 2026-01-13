package test.files.falsepositive;

import java.util.*;

/**
 * Tests that Collection stream with operations is NOT flagged even with JDBC-like method names.
 * EXPECTED: 0 issues
 */
class CollectionStreamWithOperationsTest {
    void testCase() {
        List<String> list = Arrays.asList("query", "sql", "param");
        list.stream()
            .filter(s -> s.length() > 3)
            .map(String::toUpperCase)
            .forEach(System.out::println);
    }
}
