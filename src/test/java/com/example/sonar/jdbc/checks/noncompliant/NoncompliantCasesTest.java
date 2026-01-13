package com.example.sonar.jdbc.checks.noncompliant;

import com.example.sonar.jdbc.checks.SpringJdbcStreamLeakCheck;
import org.junit.jupiter.api.Test;
import org.sonar.java.checks.verifier.CheckVerifier;

/**
 * Tests for NONCOMPLIANT cases - code that SHOULD be flagged by the rule.
 *
 * <p>Each test method validates exactly ONE noncompliant pattern to ensure
 * proper isolation and clear failure diagnosis. All tests in this class
 * verify that the rule correctly identifies issues.</p>
 *
 * <p>Noncompliant patterns include:</p>
 * <ul>
 *   <li>Streams assigned to variables without try-with-resources</li>
 *   <li>Direct stream usage without closing</li>
 *   <li>Streams returned from methods</li>
 *   <li>Complex method chains without proper resource management</li>
 * </ul>
 *
 * @since 1.0.0
 * @see SpringJdbcStreamLeakCheck
 */
class NoncompliantCasesTest {

    @Test
    void testNoncompliant_streamNotClosed() {
        CheckVerifier.newVerifier()
            .onFile("src/test/files/noncompliant/StreamNotClosedTest.java")
            .withCheck(new SpringJdbcStreamLeakCheck())
            .verifyIssues();
    }

    @Test
    void testNoncompliant_directUsage() {
        CheckVerifier.newVerifier()
            .onFile("src/test/files/noncompliant/DirectUsageTest.java")
            .withCheck(new SpringJdbcStreamLeakCheck())
            .verifyIssues();
    }

    @Test
    void testNoncompliant_jdbcTemplateQueryForStream() {
        CheckVerifier.newVerifier()
            .onFile("src/test/files/noncompliant/JdbcTemplateQueryForStreamTest.java")
            .withCheck(new SpringJdbcStreamLeakCheck())
            .verifyIssues();
    }

    @Test
    void testNoncompliant_returnStream() {
        CheckVerifier.newVerifier()
            .onFile("src/test/files/noncompliant/ReturnStreamTest.java")
            .withCheck(new SpringJdbcStreamLeakCheck())
            .verifyIssues();
    }

    @Test
    void testNoncompliant_variableOutsideTry() {
        CheckVerifier.newVerifier()
            .onFile("src/test/files/noncompliant/VariableOutsideTryTest.java")
            .withCheck(new SpringJdbcStreamLeakCheck())
            .verifyIssues();
    }

    @Test
    void testNoncompliant_multipleParams() {
        CheckVerifier.newVerifier()
            .onFile("src/test/files/noncompliant/MultipleParamsTest.java")
            .withCheck(new SpringJdbcStreamLeakCheck())
            .verifyIssues();
    }

    @Test
    void testNoncompliant_complexChain() {
        CheckVerifier.newVerifier()
            .onFile("src/test/files/noncompliant/ComplexChainTest.java")
            .withCheck(new SpringJdbcStreamLeakCheck())
            .verifyIssues();
    }

    @Test
    void testNoncompliant_chainWithMethodCalls() {
        CheckVerifier.newVerifier()
            .onFile("src/test/files/noncompliant/ChainWithMethodCallsTest.java")
            .withCheck(new SpringJdbcStreamLeakCheck())
            .verifyIssues();
    }
}
