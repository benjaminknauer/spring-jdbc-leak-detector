package com.example.sonar.jdbc.checks.edgecase;

import com.example.sonar.jdbc.checks.SpringJdbcStreamLeakCheck;
import org.junit.jupiter.api.Test;
import org.sonar.java.checks.verifier.CheckVerifier;

/**
 * Tests for edge cases and boundary conditions in the SpringJdbcStreamLeakCheck.
 *
 * <p>These tests verify behavior in unusual but valid scenarios such as:</p>
 * <ul>
 *   <li>JDBC streams within lambda expressions</li>
 *   <li>Streams passed as method arguments</li>
 *   <li>Name-based heuristic fallback for JdbcTemplate detection</li>
 * </ul>
 *
 * @author SonarQube JDBC Client Plugin Team
 * @since 1.0.0
 * @see SpringJdbcStreamLeakCheck
 */
class EdgeCaseTest {

    @Test
    void testEdgeCase_lambdaWithJdbcClient() {
        CheckVerifier.newVerifier()
            .onFile("src/test/files/edgecase/LambdaWithJdbcClientTest.java")
            .withCheck(new SpringJdbcStreamLeakCheck())
            .verifyIssues();
    }

    @Test
    void testEdgeCase_nestedMethodCalls() {
        CheckVerifier.newVerifier()
            .onFile("src/test/files/edgecase/NestedMethodCallsTest.java")
            .withCheck(new SpringJdbcStreamLeakCheck())
            .verifyIssues();
    }

    @Test
    void testEdgeCase_jdbcTemplateDirectUsage() {
        CheckVerifier.newVerifier()
            .onFile("src/test/files/edgecase/JdbcTemplateSubclassTest.java")
            .withCheck(new SpringJdbcStreamLeakCheck())
            .verifyIssues();
    }

    @Test
    void testEdgeCase_customQueryForStreamNotFlagged() {
        CheckVerifier.newVerifier()
            .onFile("src/test/files/edgecase/CustomQueryForStreamTest.java")
            .withCheck(new SpringJdbcStreamLeakCheck())
            .verifyNoIssues();
    }

    @Test
    void testEdgeCase_semanticAnalysisTakesPrecedence() {
        // Even if variable name suggests JdbcTemplate, semantic analysis takes precedence
        CheckVerifier.newVerifier()
            .onFile("src/test/files/edgecase/JdbcTemplateNamedVariableTest.java")
            .withCheck(new SpringJdbcStreamLeakCheck())
            .verifyNoIssues();
    }
}
