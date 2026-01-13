package com.example.sonar.jdbc;

import com.example.sonar.jdbc.checks.SpringJdbcStreamLeakCheck;

import java.util.List;

/**
 * Central registry for all custom Java rules provided by this plugin.
 *
 * <p>This utility class maintains the list of all check classes that are
 * registered with SonarQube. To add a new rule, simply add its class
 * to the list returned by {@link #getChecks()}.</p>
 *
 * <h2>Adding a New Rule</h2>
 * <ol>
 *   <li>Create a new check class extending {@code IssuableSubscriptionVisitor}</li>
 *   <li>Add the {@code @Rule(key = "YourRuleKey")} annotation</li>
 *   <li>Create metadata files: {@code YourRuleKey.json} and {@code YourRuleKey.html}</li>
 *   <li>Add the class to the list in {@link #getChecks()}</li>
 * </ol>
 *
 * @since 1.0.0
 * @see com.example.sonar.jdbc.checks.SpringJdbcStreamLeakCheck
 */
public final class RulesList {

    /**
     * Private constructor to prevent instantiation of this utility class.
     */
    private RulesList() {
        // Utility class - private constructor
    }

    /**
     * Returns the list of all check classes provided by this plugin.
     *
     * <p>Each class in this list must:</p>
     * <ul>
     *   <li>Extend {@code IssuableSubscriptionVisitor} or implement {@code JavaCheck}</li>
     *   <li>Have a {@code @Rule} annotation with a unique key</li>
     *   <li>Have corresponding metadata files in the resources directory</li>
     * </ul>
     *
     * @return an immutable list of check classes; never {@code null}
     */
    public static List<Class<?>> getChecks() {
        return List.of(
            SpringJdbcStreamLeakCheck.class
            // Add more custom checks here as needed
        );
    }
}
