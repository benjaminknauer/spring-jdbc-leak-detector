package com.example.sonar.jdbc.checks;

import org.sonar.check.Rule;
import org.sonar.plugins.java.api.IssuableSubscriptionVisitor;
import org.sonar.plugins.java.api.semantic.Symbol;
import org.sonar.plugins.java.api.semantic.Type;
import org.sonar.plugins.java.api.tree.*;

import java.util.Collections;
import java.util.List;

/**
 * SonarQube check to detect potential resource leaks when using Spring Framework's
 * {@code JdbcClient.stream()} or {@code JdbcTemplate.queryForStream()} methods
 * without proper try-with-resources handling.
 *
 * <p>These stream methods return a {@link java.util.stream.Stream} that holds an active
 * database connection. If not explicitly closed, the connection will not be returned
 * to the connection pool, leading to connection leaks and eventual pool exhaustion.</p>
 *
 * <h2>Detection Strategy</h2>
 * <p>This check uses a dual detection strategy:</p>
 * <ul>
 *   <li><strong>Semantic analysis:</strong> Uses type information when Spring JDBC is on the classpath</li>
 *   <li><strong>Name-based heuristics:</strong> Falls back to method chain analysis when types are unavailable</li>
 * </ul>
 *
 * <h2>Example of Noncompliant Code</h2>
 * <pre>{@code
 * Stream<User> users = jdbcClient.sql("SELECT * FROM users")
 *     .query(User.class)
 *     .stream();  // Noncompliant - connection leak!
 * users.forEach(user -> process(user));
 * }</pre>
 *
 * <h2>Example of Compliant Code</h2>
 * <pre>{@code
 * try (Stream<User> users = jdbcClient.sql("SELECT * FROM users")
 *         .query(User.class)
 *         .stream()) {
 *     users.forEach(user -> process(user));
 * }  // Connection automatically closed
 * }</pre>
 *
 * @since 1.0.0
 * @see org.springframework.jdbc.core.simple.JdbcClient
 * @see org.springframework.jdbc.core.JdbcTemplate#queryForStream
 */
@Rule(key = "SpringJdbcStreamLeak")
public class SpringJdbcStreamLeakCheck extends IssuableSubscriptionVisitor {

    private static final String MESSAGE = "This stream holds a database connection and must be used within a try-with-resources statement.";
    private static final int MAX_CHAIN_DEPTH = 50;

    // Method names to detect
    private static final String METHOD_STREAM = "stream";
    private static final String METHOD_QUERY_FOR_STREAM = "queryForStream";
    private static final String METHOD_QUERY = "query";
    private static final String METHOD_SQL = "sql";
    private static final String METHOD_PARAM = "param";

    // Fully qualified class names for Spring JDBC types
    private static final String FQN_JDBC_CLIENT_MAPPED_QUERY_SPEC =
        "org.springframework.jdbc.core.simple.JdbcClient$MappedQuerySpec";
    private static final String FQN_JDBC_TEMPLATE =
        "org.springframework.jdbc.core.JdbcTemplate";

    /**
     * {@inheritDoc}
     *
     * <p>This check only visits method invocation nodes to detect calls to
     * {@code stream()} and {@code queryForStream()} methods.</p>
     *
     * @return a singleton list containing {@link Tree.Kind#METHOD_INVOCATION}
     */
    @Override
    public List<Tree.Kind> nodesToVisit() {
        return Collections.singletonList(Tree.Kind.METHOD_INVOCATION);
    }

    /**
     * {@inheritDoc}
     *
     * <p>Analyzes each method invocation to determine if it's a dangerous JDBC stream
     * method that is not properly wrapped in a try-with-resources statement.</p>
     *
     * @param tree the AST node to visit, guaranteed to be a {@link MethodInvocationTree}
     */
    @Override
    public void visitNode(Tree tree) {
        MethodInvocationTree mit = (MethodInvocationTree) tree;

        // Check if this is a dangerous JDBC stream method (semantic + fallback)
        if (!isDangerousJdbcStream(mit)) {
            return;
        }

        // Check if it's properly managed with try-with-resources
        if (!isInTryWithResources(mit)) {
            reportIssue(mit, MESSAGE);
        }
    }

    /**
     * Extracts the method name from a method invocation tree.
     *
     * <p>Handles both simple method calls ({@code foo()}) and member select
     * expressions ({@code obj.foo()}).</p>
     *
     * @param mit the method invocation tree to extract the name from
     * @return the method name as a string, or {@code null} if the name cannot be determined
     */
    private String getMethodName(MethodInvocationTree mit) {
        ExpressionTree methodSelect = mit.methodSelect();
        if (methodSelect.is(Tree.Kind.IDENTIFIER)) {
            return ((IdentifierTree) methodSelect).name();
        } else if (methodSelect.is(Tree.Kind.MEMBER_SELECT)) {
            return ((MemberSelectExpressionTree) methodSelect).identifier().name();
        }
        return null;
    }

    /**
     * Determines if a method invocation is a dangerous JDBC stream method
     * that requires try-with-resources handling.
     *
     * <p>Checks for two specific method patterns:</p>
     * <ul>
     *   <li>{@code JdbcClient.MappedQuerySpec.stream()} - Spring 6.1+ JdbcClient</li>
     *   <li>{@code JdbcTemplate.queryForStream()} - Traditional JdbcTemplate</li>
     * </ul>
     *
     * <p>Uses semantic type checking when available, falls back to name-based heuristics
     * when Spring JDBC is not on the analysis classpath.</p>
     *
     * @param mit the method invocation to analyze
     * @return {@code true} if this is a dangerous JDBC stream method, {@code false} otherwise
     */
    private boolean isDangerousJdbcStream(MethodInvocationTree mit) {
        String methodName = getMethodName(mit);
        if (methodName == null) {
            return false;
        }

        if (METHOD_STREAM.equals(methodName)) {
            return isJdbcClientStream(mit);
        } else if (METHOD_QUERY_FOR_STREAM.equals(methodName)) {
            return isJdbcTemplateQueryForStream(mit);
        }

        return false;
    }

    /**
     * Performs heuristic-based detection to determine if a {@code stream()} call
     * looks like it originates from a JdbcClient method chain.
     *
     * <p>This fallback method is used when semantic type information is unavailable
     * (e.g., Spring JDBC not on the classpath). It traverses the method chain
     * looking for characteristic JdbcClient methods like {@code query()}, {@code sql()},
     * or {@code param()}.</p>
     *
     * @param mit the method invocation to analyze
     * @return {@code true} if the method chain contains JdbcClient-characteristic methods
     */
    private boolean looksLikeJdbcClientStream(MethodInvocationTree mit) {
        return looksLikeJdbcClientStream(mit, 0);
    }

    /**
     * Recursive helper for heuristic-based JdbcClient detection with depth limiting.
     *
     * <p>Traverses the method chain up to {@link #MAX_CHAIN_DEPTH} levels to prevent
     * stack overflow on pathological ASTs.</p>
     *
     * @param mit   the method invocation to analyze
     * @param depth the current recursion depth
     * @return {@code true} if JdbcClient-characteristic methods are found in the chain
     */
    private boolean looksLikeJdbcClientStream(MethodInvocationTree mit, int depth) {
        if (depth > MAX_CHAIN_DEPTH) {
            return false;
        }

        ExpressionTree methodSelect = mit.methodSelect();
        if (methodSelect.is(Tree.Kind.MEMBER_SELECT)) {
            MemberSelectExpressionTree memberSelect = (MemberSelectExpressionTree) methodSelect;
            ExpressionTree expression = memberSelect.expression();

            // Check if the expression is another method invocation
            if (expression.is(Tree.Kind.METHOD_INVOCATION)) {
                MethodInvocationTree chainedCall = (MethodInvocationTree) expression;
                String chainedMethodName = getMethodName(chainedCall);

                // Common JdbcClient methods: query, sql, param
                if (chainedMethodName != null &&
                    (METHOD_QUERY.equals(chainedMethodName) ||
                     METHOD_SQL.equals(chainedMethodName) ||
                     METHOD_PARAM.equals(chainedMethodName))) {
                    return true;
                }

                // Recursively check the chain with incremented depth
                return looksLikeJdbcClientStream(chainedCall, depth + 1);
            }
        }
        return false;
    }

    /**
     * Checks if a {@code stream()} method call is from {@code JdbcClient.MappedQuerySpec}.
     *
     * <p>This method uses a two-phase detection strategy:</p>
     * <ol>
     *   <li><strong>Semantic check:</strong> If type information is available,
     *       verifies the method owner is exactly {@code JdbcClient$MappedQuerySpec}</li>
     *   <li><strong>Heuristic fallback:</strong> If types are unknown, uses
     *       {@link #looksLikeJdbcClientStream(MethodInvocationTree)} to analyze the method chain</li>
     * </ol>
     *
     * @param mit the method invocation to check
     * @return {@code true} if this is a JdbcClient stream method, {@code false} otherwise
     */
    private boolean isJdbcClientStream(MethodInvocationTree mit) {
        // Try semantic type checking first
        Symbol.MethodSymbol methodSymbol = mit.methodSymbol();

        // Fall back to heuristics if semantic info unavailable
        if (methodSymbol == null || methodSymbol.isUnknown()) {
            return looksLikeJdbcClientStream(mit);
        }

        Symbol owner = methodSymbol.owner();
        if (owner == null) {
            return looksLikeJdbcClientStream(mit);
        }

        Type ownerType = owner.type();
        if (ownerType.isUnknown()) {
            return looksLikeJdbcClientStream(mit);
        }

        // Check exact match for JdbcClient.MappedQuerySpec
        // Note: Inner class uses $ separator
        return FQN_JDBC_CLIENT_MAPPED_QUERY_SPEC.equals(ownerType.fullyQualifiedName());
    }

    /**
     * Checks if a {@code queryForStream()} method call is from {@code JdbcTemplate}.
     *
     * <p>This method uses semantic type analysis to verify the method owner is
     * {@code JdbcTemplate} or a subclass thereof. If type information is unavailable,
     * it uses a name-based heuristic to check if the receiver object name suggests
     * it might be a JdbcTemplate instance.</p>
     *
     * @param mit the method invocation to check
     * @return {@code true} if this appears to be a JdbcTemplate queryForStream call
     */
    private boolean isJdbcTemplateQueryForStream(MethodInvocationTree mit) {
        // Try semantic type checking
        Symbol.MethodSymbol methodSymbol = mit.methodSymbol();

        if (methodSymbol == null || methodSymbol.isUnknown()) {
            return looksLikeJdbcTemplateCall(mit);
        }

        Symbol owner = methodSymbol.owner();
        if (owner == null) {
            return looksLikeJdbcTemplateCall(mit);
        }

        Type ownerType = owner.type();
        if (ownerType.isUnknown()) {
            return looksLikeJdbcTemplateCall(mit);
        }

        // Check for JdbcTemplate or subclasses
        return ownerType.is(FQN_JDBC_TEMPLATE) ||
               ownerType.isSubtypeOf(FQN_JDBC_TEMPLATE);
    }

    /**
     * Performs heuristic-based detection to determine if a method call looks like
     * it's being invoked on a JdbcTemplate instance based on the receiver variable name.
     *
     * <p>This fallback method is used when semantic type information is unavailable.
     * It checks if the receiver object's name contains common JdbcTemplate naming patterns.</p>
     *
     * @param mit the method invocation to analyze
     * @return {@code true} if the receiver name suggests a JdbcTemplate instance
     */
    private boolean looksLikeJdbcTemplateCall(MethodInvocationTree mit) {
        ExpressionTree methodSelect = mit.methodSelect();

        if (!methodSelect.is(Tree.Kind.MEMBER_SELECT)) {
            return false;
        }

        MemberSelectExpressionTree memberSelect = (MemberSelectExpressionTree) methodSelect;
        ExpressionTree expression = memberSelect.expression();

        // Check if receiver is a simple identifier with a JdbcTemplate-like name
        if (expression.is(Tree.Kind.IDENTIFIER)) {
            String receiverName = ((IdentifierTree) expression).name().toLowerCase();
            return receiverName.contains("jdbctemplate") ||
                   receiverName.contains("template") ||
                   receiverName.equals("jdbc");
        }

        return false;
    }

    /**
     * Determines if a method invocation is properly managed within a try-with-resources statement.
     *
     * <p>This method traverses the AST upward from the method invocation to find if it's
     * declared as a resource in a try-with-resources statement. The check is strict:
     * only try-with-resources is accepted; manual {@code close()} calls in finally blocks
     * are not considered safe by this rule.</p>
     *
     * <p>Handles two AST patterns:</p>
     * <ul>
     *   <li>Direct method invocation in resource list: The {@code stream()} call
     *       is directly part of the try-with-resources declaration</li>
     *   <li>Nested in variable initializer: The {@code stream()} call is within
     *       a variable declaration that is itself in the resource list</li>
     * </ul>
     *
     * @param mit the method invocation to check
     * @return {@code true} if the stream is declared in a try-with-resources, {@code false} otherwise
     */
    private boolean isInTryWithResources(MethodInvocationTree mit) {
        Tree current = mit;

        while (current != null) {
            Tree parent = current.parent();

            if (parent == null) {
                break;
            }

            // Check if we're in a try-with-resources statement
            if (parent.is(Tree.Kind.TRY_STATEMENT)) {
                TryStatementTree tryStatement = (TryStatementTree) parent;
                List<Tree> resources = tryStatement.resourceList();

                if (!resources.isEmpty()) {
                    // Check if our method invocation is part of the resource declaration
                    for (Tree resource : resources) {
                        if (containsMethodInvocation(resource, mit)) {
                            return true;
                        }
                    }
                }

                // If we're in a try block but not in the resources, it's not safe
                return false;
            }

            // Check if we're inside a variable declaration that's in try-with-resources
            if (parent.is(Tree.Kind.VARIABLE)) {
                VariableTree variable = (VariableTree) parent;
                if (variable.initializer() == current || containsMethodInvocation(variable.initializer(), mit)) {
                    // Check if this variable is in a try-with-resources
                    Tree grandParent = parent.parent();
                    if (grandParent != null && grandParent.parent() != null &&
                        grandParent.parent().is(Tree.Kind.TRY_STATEMENT)) {
                        TryStatementTree tryStatement = (TryStatementTree) grandParent.parent();
                        if (!tryStatement.resourceList().isEmpty()) {
                            return true;
                        }
                    }
                }
            }

            current = parent;
        }

        return false;
    }

    /**
     * Recursively checks if an AST subtree contains a specific method invocation.
     *
     * <p>This method is used to determine if a method invocation appears within
     * a resource declaration in a try-with-resources statement. It traverses:</p>
     * <ul>
     *   <li>Variable initializers</li>
     *   <li>Method invocation chains (method select and arguments)</li>
     *   <li>Member select expressions</li>
     * </ul>
     *
     * @param tree   the AST subtree to search in
     * @param target the specific method invocation to find
     * @return {@code true} if the target is found within the tree, {@code false} otherwise
     */
    private boolean containsMethodInvocation(Tree tree, MethodInvocationTree target) {
        return containsMethodInvocation(tree, target, 0);
    }

    /**
     * Recursive helper with depth limiting to prevent stack overflow on pathological ASTs.
     *
     * @param tree   the AST subtree to search in
     * @param target the specific method invocation to find
     * @param depth  the current recursion depth
     * @return {@code true} if the target is found within the tree, {@code false} otherwise
     */
    private boolean containsMethodInvocation(Tree tree, MethodInvocationTree target, int depth) {
        if (depth > MAX_CHAIN_DEPTH) {
            return false;
        }

        if (tree == target) {
            return true;
        }

        if (tree instanceof VariableTree) {
            VariableTree variable = (VariableTree) tree;
            ExpressionTree init = variable.initializer();
            // Null-check: initializer can be null for uninitialized variables
            return init != null && containsMethodInvocation(init, target, depth + 1);
        }

        if (tree instanceof MethodInvocationTree) {
            MethodInvocationTree mit = (MethodInvocationTree) tree;

            // Check method select
            if (containsMethodInvocation(mit.methodSelect(), target, depth + 1)) {
                return true;
            }

            // Check arguments
            for (ExpressionTree arg : mit.arguments()) {
                if (containsMethodInvocation(arg, target, depth + 1)) {
                    return true;
                }
            }
        }

        if (tree instanceof MemberSelectExpressionTree) {
            MemberSelectExpressionTree memberSelect = (MemberSelectExpressionTree) tree;
            return containsMethodInvocation(memberSelect.expression(), target, depth + 1);
        }

        return false;
    }
}
