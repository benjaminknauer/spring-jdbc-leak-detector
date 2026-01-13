package com.example.sonar.jdbc.checks.falsepositive;

import com.example.sonar.jdbc.checks.SpringJdbcStreamLeakCheck;
import org.junit.jupiter.api.Test;
import org.sonar.java.checks.verifier.CheckVerifier;

/**
 * Tests to verify that the rule does NOT produce false positives
 * for Collection.stream() and other non-JDBC streams.
 *
 * <p>Each test method validates exactly ONE false positive prevention scenario
 * to ensure the rule correctly distinguishes JDBC streams from other stream types.</p>
 *
 * <p>False positive prevention scenarios include:</p>
 * <ul>
 *   <li>Collection streams (List, Set, Map)</li>
 *   <li>Array streams and Stream.of()</li>
 *   <li>Custom repository patterns with stream() methods</li>
 *   <li>Custom query builders that mimic JDBC API patterns</li>
 *   <li>Optional.stream() and parallelStream()</li>
 * </ul>
 *
 * @since 1.0.0
 * @see SpringJdbcStreamLeakCheck
 */
class FalsePositiveCasesTest {

    @Test
    void testFalsePositive_listStream() {
        CheckVerifier.newVerifier()
            .onFile("src/test/files/falsepositive/ListStreamTest.java")
            .withCheck(new SpringJdbcStreamLeakCheck())
            .verifyNoIssues();
    }

    @Test
    void testFalsePositive_setStream() {
        CheckVerifier.newVerifier()
            .onFile("src/test/files/falsepositive/SetStreamTest.java")
            .withCheck(new SpringJdbcStreamLeakCheck())
            .verifyNoIssues();
    }

    @Test
    void testFalsePositive_mapStream() {
        CheckVerifier.newVerifier()
            .onFile("src/test/files/falsepositive/MapStreamTest.java")
            .withCheck(new SpringJdbcStreamLeakCheck())
            .verifyNoIssues();
    }

    @Test
    void testFalsePositive_arrayStream() {
        CheckVerifier.newVerifier()
            .onFile("src/test/files/falsepositive/ArrayStreamTest.java")
            .withCheck(new SpringJdbcStreamLeakCheck())
            .verifyNoIssues();
    }

    @Test
    void testFalsePositive_streamOf() {
        CheckVerifier.newVerifier()
            .onFile("src/test/files/falsepositive/StreamOfTest.java")
            .withCheck(new SpringJdbcStreamLeakCheck())
            .verifyNoIssues();
    }

    @Test
    void testFalsePositive_collectionStreamWithOperations() {
        CheckVerifier.newVerifier()
            .onFile("src/test/files/falsepositive/CollectionStreamWithOperationsTest.java")
            .withCheck(new SpringJdbcStreamLeakCheck())
            .verifyNoIssues();
    }

    @Test
    void testFalsePositive_customRepositoryStream() {
        CheckVerifier.newVerifier()
            .onFile("src/test/files/falsepositive/CustomRepositoryStreamTest.java")
            .withCheck(new SpringJdbcStreamLeakCheck())
            .verifyNoIssues();
    }

    @Test
    void testFalsePositive_customQueryBuilderQuery() {
        CheckVerifier.newVerifier()
            .onFile("src/test/files/falsepositive/CustomQueryBuilderQueryTest.java")
            .withCheck(new SpringJdbcStreamLeakCheck())
            .verifyNoIssues();
    }

    @Test
    void testFalsePositive_customQueryBuilderSql() {
        CheckVerifier.newVerifier()
            .onFile("src/test/files/falsepositive/CustomQueryBuilderSqlTest.java")
            .withCheck(new SpringJdbcStreamLeakCheck())
            .verifyNoIssues();
    }

    @Test
    void testFalsePositive_customQueryBuilderChain() {
        CheckVerifier.newVerifier()
            .onFile("src/test/files/falsepositive/CustomQueryBuilderChainTest.java")
            .withCheck(new SpringJdbcStreamLeakCheck())
            .verifyNoIssues();
    }

    @Test
    void testFalsePositive_streamVariableFromCollection() {
        CheckVerifier.newVerifier()
            .onFile("src/test/files/falsepositive/StreamVariableFromCollectionTest.java")
            .withCheck(new SpringJdbcStreamLeakCheck())
            .verifyNoIssues();
    }

    @Test
    void testFalsePositive_parallelStream() {
        CheckVerifier.newVerifier()
            .onFile("src/test/files/falsepositive/ParallelStreamTest.java")
            .withCheck(new SpringJdbcStreamLeakCheck())
            .verifyNoIssues();
    }

    @Test
    void testFalsePositive_optionalStream() {
        CheckVerifier.newVerifier()
            .onFile("src/test/files/falsepositive/OptionalStreamTest.java")
            .withCheck(new SpringJdbcStreamLeakCheck())
            .verifyNoIssues();
    }
}
