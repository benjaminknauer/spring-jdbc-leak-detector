package test.files.falsepositive;

import java.util.*;

/**
 * Tests that parallelStream() is NOT flagged (not JDBC).
 * EXPECTED: 0 issues
 */
class ParallelStreamTest {
    void testCase() {
        List<String> list = Arrays.asList("a", "b", "c");
        list.parallelStream().forEach(System.out::println);
    }
}
