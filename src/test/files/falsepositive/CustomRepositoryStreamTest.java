package test.files.falsepositive;

import java.util.*;

/**
 * Tests that custom repository stream is NOT flagged (not JDBC).
 * EXPECTED: 0 issues
 */
class CustomRepositoryStreamTest {

    static class CustomRepository {
        List<String> data = new ArrayList<>();

        public List<String> findAll() {
            return data;
        }
    }

    void testCase() {
        CustomRepository repo = new CustomRepository();
        repo.findAll().stream().forEach(System.out::println);
    }
}
