package test.files.falsepositive;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Tests that Set.stream() is NOT flagged (not JDBC).
 * EXPECTED: 0 issues
 */
class SetStreamTest {
    void testCase() {
        Set<String> set = new HashSet<>();
        set.add("test");
        set.stream()
            .filter(s -> s.startsWith("A"))
            .collect(Collectors.toList());
    }
}
