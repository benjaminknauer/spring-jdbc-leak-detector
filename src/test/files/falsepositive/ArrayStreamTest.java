package test.files.falsepositive;

import java.util.Arrays;

/**
 * Tests that Arrays.stream() is NOT flagged (not JDBC).
 * EXPECTED: 0 issues
 */
class ArrayStreamTest {
    void testCase() {
        String[] array = {"a", "b", "c"};
        Arrays.stream(array).forEach(System.out::println);
    }
}
