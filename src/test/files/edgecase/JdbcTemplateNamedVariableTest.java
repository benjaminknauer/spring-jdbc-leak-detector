package test.files.edgecase;

import java.util.stream.Stream;

/**
 * Tests that queryForStream is NOT flagged when semantic analysis shows it's not JdbcTemplate.
 * Even though the variable name suggests JdbcTemplate, semantic analysis takes precedence.
 * EXPECTED: 0 issues - semantic analysis confirms it's not JdbcTemplate
 */
class JdbcTemplateNamedVariableTest {

    private MockTemplate jdbcTemplate;

    void testCase() {
        // This should NOT be flagged because semantic analysis knows MockTemplate is not JdbcTemplate
        Stream<String> items = jdbcTemplate.queryForStream("query");
        items.forEach(System.out::println);
    }

    // Mock class that mimics JdbcTemplate but is not the actual class
    static class MockTemplate {
        Stream<String> queryForStream(String query) {
            return Stream.of("item1", "item2");
        }
    }
}
