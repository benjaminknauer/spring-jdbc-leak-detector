package test.files.falsepositive;

import java.util.*;
import java.util.stream.Stream;

/**
 * Tests that stream variable from collection is NOT flagged (not JDBC).
 * EXPECTED: 0 issues
 */
class StreamVariableFromCollectionTest {
    void testCase() {
        List<String> list = Arrays.asList("a", "b", "c");
        Stream<String> stream = list.stream();
        stream.forEach(System.out::println);
    }
}
