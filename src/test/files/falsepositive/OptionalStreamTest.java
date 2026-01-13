package test.files.falsepositive;

import java.util.Optional;

/**
 * Tests that Optional.stream() is NOT flagged (not JDBC).
 * EXPECTED: 0 issues
 */
class OptionalStreamTest {
    void testCase() {
        Optional<String> opt = Optional.of("test");
        opt.stream().forEach(System.out::println);
    }
}
