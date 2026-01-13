package test.files.falsepositive;

import java.util.*;

/**
 * Tests that Map.entrySet().stream() is NOT flagged (not JDBC).
 * EXPECTED: 0 issues
 */
class MapStreamTest {
    void testCase() {
        Map<String, Integer> map = new HashMap<>();
        map.put("key", 1);
        map.entrySet().stream()
            .forEach(e -> System.out.println(e.getKey()));
    }
}
