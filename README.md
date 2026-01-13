# Spring JDBC Leak Detector (SonarQube Plugin)

[![Java](https://img.shields.io/badge/Java-11%2B-blue)](https://openjdk.java.net/)
[![SonarQube](https://img.shields.io/badge/SonarQube-25.9.0.112764--community%2B-4E9BCD)](https://www.sonarqube.org/)
[![License](https://img.shields.io/badge/License-MIT-green.svg)](LICENSE)

Detects resource leaks when using Spring Framework's `JdbcClient.stream()` and `JdbcTemplate.queryForStream()`.

## The Problem

Spring's stream methods return lazy-evaluated streams that hold active database connections. Forgetting to close them exhausts your connection pool and crashes your application:

```java
// BAD - Connection leaked forever
Stream<User> users = jdbcClient.sql("SELECT * FROM users")
    .query(User.class)
    .stream();
users.forEach(user -> process(user));
```

## The Solution

Use try-with-resources to ensure connections are closed automatically:

```java
// GOOD - Connection automatically closed
try (Stream<User> users = jdbcClient.sql("SELECT * FROM users")
        .query(User.class)
        .stream()) {
    users.forEach(user -> process(user));
}
```

This plugin provides a SonarQube rule to catch these dangerous patterns during code analysis.

## Quick Start

```bash
# 1. Build the plugin
mvn clean package

# 2. Install to SonarQube
cp target/spring-jdbc-leak-detector-1.0.0.jar $SONARQUBE_HOME/extensions/plugins/

# 3. Restart SonarQube
$SONARQUBE_HOME/bin/[OS]/sonar.sh restart

# 4. Activate rule in Quality Profile → "SpringJdbcStreamLeak"
```

## Local Development with Docker

The easiest way to test the plugin locally is using Docker Compose.

### Prerequisites

- Docker and Docker Compose
- Maven 3.6+
- Java 11+

### Step 1: Start SonarQube with Plugin

```bash
# Build plugin and start SonarQube (runs in foreground, shows logs)
./scripts/start-sonarqube.sh
```

Wait until you see `SonarQube is operational` in the logs.

Open http://localhost:9000 (login: `admin` / `admin`, then change password to `MyTestPw123!`).

### Step 2: Activate the Rule (Required!)

**Important:** Custom rules are NOT activated by default. The built-in "Sonar way" profile cannot be modified, so you must create a custom profile:

1. Login with `admin` / `admin` and change the password to `test`
2. Go to **Quality Profiles** → **Java**
3. Click **Create** (top right) → Name: `Custom Java`, Parent: `Sonar way` → **Create**
4. Select your new profile → **Set as Default**
5. Click **Activate More Rules**
6. Filter by Repository: **Spring JDBC Leak Detector**
7. Click **Activate** on `SpringJdbcStreamLeak`

To verify the plugin is loaded, search for `spring-jdbc-leak-detector` under **Rules**.

### Step 3: Generate a Token

SonarQube requires a token for analysis:

1. Click your profile (top right) → **My Account** → **Security**
2. Generate a token (e.g., name: `local-analysis`)
3. Copy the token and export it:

```bash
export SONAR_TOKEN=<your-token>
```

### Step 4: Analyze a Project

```bash
# First, compile the project you want to analyze
cd /path/to/your/project
mvn compile

# Then analyze (from this plugin's directory)
./scripts/analyze-project.sh /path/to/your/project my-project-key
```

### Stop SonarQube

Press `Ctrl+C` in the terminal running `start-sonarqube.sh`, or:

```bash
docker-compose down

# To also remove data volumes:
docker-compose down -v
```

## Example Project

The `example/` directory contains a Spring Boot application with **intentional JDBC stream leaks** for testing the plugin.

### Features

- **Runnable application**: Start the app and observe connection pool exhaustion in real-time
- **Integration tests**: Prove that leaky code exhausts the pool using Testcontainers
- **Small connection pool**: Configured with only 3 connections to make leaks visible quickly

See [`example/README.md`](example/README.md) for detailed instructions.

### Quick Demo

```bash
# Run integration tests that prove the leak behavior
cd example
mvn test
```

### SonarQube Analysis

```bash
# 1. Start SonarQube (from project root)
./scripts/start-sonarqube.sh

# 2. Analyze the example project
./scripts/analyze-project.sh ./example jdbc-leak-example
```

Open http://localhost:9000/dashboard?id=jdbc-leak-example to see the 4 detected issues.

## Usage

Run your normal SonarQube analysis:

```bash
mvn sonar:sonar -Dsonar.host.url=http://localhost:9000 -Dsonar.token=YOUR_TOKEN
```

The rule automatically flags unclosed JDBC streams in your code.

## Requirements

- Java 11+
- Maven 3.6+
- SonarQube 9.9+

## SonarLint Support

This plugin works with **SonarLint in Connected Mode only**. SonarLint standalone (without a SonarQube connection) does not load custom plugins.

To use with SonarLint:
1. Install the plugin on your SonarQube server
2. Connect SonarLint to your SonarQube instance ([Connected Mode setup](https://docs.sonarsource.com/sonarlint/vs-code/team-features/connected-mode/))
3. Sync your project bindings

The rule will then appear in your IDE alongside the built-in SonarLint rules.

## Development

```bash
# Build
mvn clean package

# Run tests
mvn test
```

## Troubleshooting

### Rule not applied to code

If SonarQube shows the rule exists but doesn't flag any issues:

1. **Rule not activated**: Custom rules must be manually activated in Quality Profiles (see Step 2 above)
2. **Project not compiled**: SonarQube needs `.class` files. Run `mvn compile` before analysis
3. **Wrong project analyzed**: Ensure you're analyzing the target project, not the plugin itself

### Plugin not loaded

If the rule doesn't appear under Rules → search "spring-jdbc-leak-detector":

1. Check SonarQube logs for plugin loading errors
2. Verify the JAR is in `extensions/plugins/` directory
3. Restart SonarQube after adding the plugin

### ClassNotFoundException for RuleMetadataLoader

The plugin uses `maven-shade-plugin` to bundle `sonar-analyzer-commons`. If you see this error:

```
java.lang.NoClassDefFoundError: org/sonarsource/analyzer/commons/RuleMetadataLoader
```

Rebuild with: `mvn clean package`

## License

MIT License - see [LICENSE](LICENSE) file for details.
