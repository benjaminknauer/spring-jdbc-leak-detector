package test.files.falsepositive;

import java.util.*;

/**
 * Tests that List.stream() is NOT flagged (not JDBC).
 * EXPECTED: 0 issues
 */
class ListStreamTest {
    void testCase() {
        List<String> list = Arrays.asList("a", "b", "c");
        list.stream().forEach(System.out::println);
    }
}
