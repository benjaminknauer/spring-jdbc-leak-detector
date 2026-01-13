# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

**Spring JDBC Leak Detector** is a SonarQube plugin that detects resource leaks when using Spring Framework's `JdbcClient.stream()` and `JdbcTemplate.queryForStream()`. These methods return streams that hold active database connections; forgetting to close them exhausts the connection pool.

**Requirements:** Java 17+, Maven 3.6+. The project requires Java 17 due to SonarQube plugin API dependencies.

## Build Commands

```bash
# Build the plugin JAR
mvn clean package

# Run tests
mvn test

# Run a single test class
mvn test -Dtest=NoncompliantCasesTest

# Run a single test method
mvn test -Dtest=NoncompliantCasesTest#testNoncompliant_streamNotClosed
```

## Local Testing with SonarQube

```bash
# Start SonarQube with the plugin (runs in foreground)
./scripts/start-sonarqube.sh

# Analyze a project against local SonarQube
./scripts/analyze-project.sh /path/to/project project-key

# Stop SonarQube
docker-compose down
```

After starting SonarQube, create a custom profile (the built-in "Sonar way" cannot be modified): Quality Profiles → Java → Create (parent: Sonar way) → Set as Default → Activate More Rules → filter by "Spring JDBC Leak Detector" → activate `SpringJdbcStreamLeak`.

## Architecture

### Plugin Structure

- `JdbcClientRulesPlugin` - Entry point, registers extensions with SonarQube
- `JdbcClientRulesDefinition` - Defines the rules repository (`spring-jdbc-leak-detector`), loads rule metadata from resources
- `JdbcClientRulesDefinition.JdbcClientCheckRegistrar` - Registers checks for SonarLint (Connected Mode only)
- `RulesList` - Central registry of all check classes; add new checks here

### Rule Implementation

The main check is `SpringJdbcStreamLeakCheck` which extends `IssuableSubscriptionVisitor`. It uses a dual detection strategy:

1. **Semantic analysis**: Uses type information when Spring JDBC is on the classpath
2. **Name-based heuristics**: Falls back to method chain pattern matching when types are unavailable

The check visits `METHOD_INVOCATION` nodes and looks for:
- `stream()` calls on `JdbcClient.MappedQuerySpec`
- `queryForStream()` calls on `JdbcTemplate`

Issues are reported only when the stream is NOT declared within a try-with-resources statement.

### Adding a New Rule

1. Create a check class in `src/main/java/com/example/sonar/jdbc/checks/` extending `IssuableSubscriptionVisitor`
2. Annotate with `@Rule(key = "YourRuleKey")`
3. Create metadata files in `src/main/resources/org/sonar/l10n/java/rules/jdbc/`:
   - `YourRuleKey.json` (rule metadata)
   - `YourRuleKey.html` (documentation)
4. Add the class to `RulesList.getChecks()`

### Test Structure

Tests use SonarQube's `CheckVerifier` with test files organized by expected behavior:

- `src/test/files/noncompliant/` - Code that SHOULD trigger issues (use `// Noncompliant` comments)
- `src/test/files/compliant/` - Code that should NOT trigger issues
- `src/test/files/falsepositive/` - Edge cases ensuring no false positives (e.g., collection streams)
- `src/test/files/edgecase/` - Boundary conditions and unusual patterns

Test classes are organized by category: `CompliantCasesTest`, `NoncompliantCasesTest`, `FalsePositiveCasesTest`, `EdgeCaseTest`.
