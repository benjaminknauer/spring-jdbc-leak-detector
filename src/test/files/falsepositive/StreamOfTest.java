package test.files.falsepositive;

import java.util.stream.Stream;

/**
 * Tests that Stream.of() is NOT flagged (not JDBC).
 * EXPECTED: 0 issues
 */
class StreamOfTest {
    void testCase() {
        Stream.of("a", "b", "c").forEach(System.out::println);
    }
}
