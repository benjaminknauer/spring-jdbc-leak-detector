# JDBC Leak Example

A Spring Boot application demonstrating JDBC stream leak issues and how the **Spring JDBC Leak Detector** SonarQube plugin catches them.

**This code contains intentional bugs for demonstration purposes!**

## Running the Application

### Prerequisites

- Java 17+
- Docker and Docker Compose

### Start the Database

```bash
docker-compose up -d
```

This starts a PostgreSQL database with sample data.

### Run the Application

```bash
mvn spring-boot:run
```

The application starts with a **very small connection pool (3 connections)** to make leaks visible quickly.

### Observe Connection Leaks

Call the leaky endpoints multiple times and watch the application hang:

```bash
# Each call leaks one connection
curl http://localhost:8080/users/print
curl http://localhost:8080/users/print
curl http://localhost:8080/users/print

# 4th call will timeout - pool exhausted!
curl http://localhost:8080/users/print
```

Compare with the safe endpoints that never leak:

```bash
# Can call unlimited times - connections are properly returned
curl http://localhost:8080/users/print-safe
curl http://localhost:8080/orders/revenue-safe
```

### Stop

```bash
docker-compose down -v
```

## Integration Tests

The project includes two test classes that prove both the problem and the solution.

### Run the Tests

```bash
mvn test
```

No Docker required - tests use H2 in-memory database.

### ConnectionLeakProofTest - Proving Leaks Exist

These tests use an isolated connection pool (2 connections) to prove that unclosed streams leak:

| Test | What it Proves |
|------|----------------|
| `jdbcClientStream_withoutClose_exhaustsPool` | Unclosed `JdbcClient.stream()` exhausts pool after 2 calls |
| `jdbcTemplateQueryForStream_withoutClose_exhaustsPool` | Unclosed `queryForStream()` exhausts pool |
| `partialStreamConsumption_stillLeaks` | Even reading just one element leaks the connection |
| `streamGoingOutOfScope_doesNotClose` | Variables going out of scope does NOT close streams |
| `tryWithResources_returnsConnections` | try-with-resources properly returns connections |
| `explicitClose_returnsConnection` | Explicit `stream.close()` also works |

### ConnectionLeakIntegrationTest - Verifying Compliant Code

These tests verify that properly written code works with Spring's context:

| Test | Description |
|------|-------------|
| `jdbcClientStream_withTryWithResources_noLeak` | Verifies `JdbcClient.stream()` with try-with-resources returns connections |
| `jdbcTemplateQueryForStream_withTryWithResources_noLeak` | Verifies `JdbcTemplate.queryForStream()` with try-with-resources works |
| `callerClosingReturnedStream_noLeak` | Verifies caller can safely close returned streams |
| `manualStreamHandling_withTryWithResources_noLeak` | Verifies manual stream handling works correctly |

## SonarQube Analysis

### Quick Start

```bash
# 1. Start SonarQube with the plugin (from parent directory)
cd ..
./scripts/start-sonarqube.sh

# 2. In SonarQube (http://localhost:9000):
#    - Login (admin/admin), change password
#    - Quality Profiles -> Java -> Create (parent: Sonar way) -> Set as Default
#    - Activate More Rules -> Search "SpringJdbcStreamLeak" -> Activate
#    - My Account -> Security -> Generate Token -> Copy it

# 3. Export the token and analyze
export SONAR_TOKEN=<your-token>
cd example
mvn compile
cd ..
./scripts/analyze-project.sh ./example jdbc-leak-example
```

**Important:** You must create a custom Quality Profile and generate a token before analysis!

### Expected SonarQube Issues

After analysis, you should see 4 issues:

| Location | Issue |
|----------|-------|
| `UserService.java:42` | Stream assigned without try-with-resources |
| `UserService.java:58` | Direct stream usage without closing |
| `OrderService.java:42` | queryForStream() result not closed |
| `OrderService.java:62` | Returning unclosed stream |

## Code Examples

### Noncompliant Code (UserService.java)

```java
// BAD: Stream is never closed - leaks connection!
Stream<User> users = jdbcClient
    .sql("SELECT * FROM users")
    .query(USER_MAPPER)
    .stream();

users.forEach(user -> System.out.println(user.getName()));
// Connection leaked here!
```

### Noncompliant Code (OrderService.java)

```java
// BAD: queryForStream() returns an unclosed stream
Stream<Order> orders = jdbcTemplate.queryForStream(
    "SELECT * FROM orders WHERE status = ?",
    ORDER_MAPPER,
    "completed"
);

return orders.map(Order::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
// Connection leaked!
```

### Compliant Code

```java
// GOOD: Stream is closed automatically with try-with-resources
try (Stream<User> users = jdbcClient
        .sql("SELECT * FROM users")
        .query(USER_MAPPER)
        .stream()) {
    users.forEach(user -> System.out.println(user.getName()));
}
// Connection properly returned to pool
```

## Connection Pool Configuration

The application uses intentionally small pool settings (`application.properties`):

```properties
spring.datasource.hikari.maximum-pool-size=3
spring.datasource.hikari.minimum-idle=1
spring.datasource.hikari.connection-timeout=2000
spring.datasource.hikari.leak-detection-threshold=5000
```

This makes connection leaks visible after just 3 calls instead of the default 10.
