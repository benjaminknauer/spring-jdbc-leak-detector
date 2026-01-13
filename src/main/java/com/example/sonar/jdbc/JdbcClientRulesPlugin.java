package com.example.sonar.jdbc;

import org.sonar.api.Plugin;

/**
 * Entry point for the Spring JDBC Leak Detector SonarQube plugin.
 *
 * <p>This plugin provides custom static analysis rules to detect resource leaks
 * when using Spring Framework's {@code JdbcClient} and {@code JdbcTemplate}
 * stream-returning methods.</p>
 *
 * <p>The plugin registers:</p>
 * <ul>
 *   <li>{@link JdbcClientRulesDefinition} - Rule definitions for SonarQube</li>
 *   <li>{@link JdbcClientRulesDefinition.JdbcClientCheckRegistrar} - Check registrar for SonarLint</li>
 * </ul>
 *
 * @author SonarQube JDBC Client Plugin Team
 * @since 1.0.0
 * @see JdbcClientRulesDefinition
 * @see org.sonar.api.Plugin
 */
public class JdbcClientRulesPlugin implements Plugin {

    /**
     * {@inheritDoc}
     *
     * <p>Registers all extensions provided by this plugin with the SonarQube platform.</p>
     *
     * @param context the plugin context for registering extensions
     */
    @Override
    public void define(Context context) {
        // Register the rules definition
        context.addExtension(JdbcClientRulesDefinition.class);

        // Register the check registrar for SonarLint support (provides checks via CheckRegistrar)
        context.addExtension(JdbcClientRulesDefinition.JdbcClientCheckRegistrar.class);
    }
}
