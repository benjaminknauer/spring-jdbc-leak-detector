package test.files.edgecase;

import java.util.stream.Stream;

/**
 * Tests that a custom class with queryForStream method is NOT flagged
 * when the variable name doesn't suggest JdbcTemplate.
 * EXPECTED: 0 issues - receiver name 'customService' doesn't match JdbcTemplate patterns
 */
class CustomQueryForStreamTest {

    private CustomService customService;

    void testCase() {
        // This should NOT be flagged because:
        // 1. No semantic info available (not actual JdbcTemplate)
        // 2. Receiver name 'customService' doesn't contain 'template' or 'jdbc'
        Stream<String> items = customService.queryForStream("query");
        items.forEach(System.out::println);
    }

    // Mock class - not actual JdbcTemplate
    static class CustomService {
        Stream<String> queryForStream(String query) {
            return Stream.of("item1", "item2");
        }
    }
}
