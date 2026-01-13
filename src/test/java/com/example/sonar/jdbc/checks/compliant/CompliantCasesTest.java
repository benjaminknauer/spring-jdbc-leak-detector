package com.example.sonar.jdbc.checks.compliant;

import com.example.sonar.jdbc.checks.SpringJdbcStreamLeakCheck;
import org.junit.jupiter.api.Test;
import org.sonar.java.checks.verifier.CheckVerifier;

/**
 * Tests for COMPLIANT cases - code that should NOT be flagged by the rule.
 *
 * <p>Each test method validates exactly ONE compliant pattern to ensure
 * proper isolation and clear failure diagnosis. All tests in this class
 * verify that the rule does NOT produce any issues.</p>
 *
 * <p>Compliant patterns include:</p>
 * <ul>
 *   <li>Proper try-with-resources usage</li>
 *   <li>Using {@code .list()} or {@code .set()} instead of {@code .stream()}</li>
 *   <li>Nested and multiple resource declarations</li>
 * </ul>
 *
 * @since 1.0.0
 * @see SpringJdbcStreamLeakCheck
 */
class CompliantCasesTest {

    @Test
    void testCompliant_tryWithResources() {
        CheckVerifier.newVerifier()
            .onFile("src/test/files/compliant/TryWithResourcesTest.java")
            .withCheck(new SpringJdbcStreamLeakCheck())
            .verifyNoIssues();
    }

    @Test
    void testCompliant_tryWithResourcesJdbcTemplate() {
        CheckVerifier.newVerifier()
            .onFile("src/test/files/compliant/TryWithResourcesJdbcTemplateTest.java")
            .withCheck(new SpringJdbcStreamLeakCheck())
            .verifyNoIssues();
    }

    @Test
    void testCompliant_useList() {
        CheckVerifier.newVerifier()
            .onFile("src/test/files/compliant/UseListTest.java")
            .withCheck(new SpringJdbcStreamLeakCheck())
            .verifyNoIssues();
    }

    @Test
    void testCompliant_useSet() {
        CheckVerifier.newVerifier()
            .onFile("src/test/files/compliant/UseSetTest.java")
            .withCheck(new SpringJdbcStreamLeakCheck())
            .verifyNoIssues();
    }

    @Test
    void testCompliant_tryWithResourcesMultipleOperations() {
        CheckVerifier.newVerifier()
            .onFile("src/test/files/compliant/TryWithResourcesMultipleOperationsTest.java")
            .withCheck(new SpringJdbcStreamLeakCheck())
            .verifyNoIssues();
    }

    @Test
    void testCompliant_nestedTryWithResources() {
        CheckVerifier.newVerifier()
            .onFile("src/test/files/compliant/NestedTryWithResourcesTest.java")
            .withCheck(new SpringJdbcStreamLeakCheck())
            .verifyNoIssues();
    }

    @Test
    void testCompliant_multipleResources() {
        CheckVerifier.newVerifier()
            .onFile("src/test/files/compliant/MultipleResourcesTest.java")
            .withCheck(new SpringJdbcStreamLeakCheck())
            .verifyNoIssues();
    }

    @Test
    void testCompliant_multipleResourcesWithParams() {
        CheckVerifier.newVerifier()
            .onFile("src/test/files/compliant/MultipleResourcesWithParamsTest.java")
            .withCheck(new SpringJdbcStreamLeakCheck())
            .verifyNoIssues();
    }
}
