package com.example.sonar.jdbc;

import org.sonar.api.SonarRuntime;
import org.sonar.api.server.rule.RulesDefinition;
import org.sonar.plugins.java.api.CheckRegistrar;
import org.sonar.plugins.java.api.JavaCheck;
import org.sonarsource.analyzer.commons.RuleMetadataLoader;

import java.util.ArrayList;
import java.util.List;

/**
 * Defines the custom rules repository for the JDBC Client plugin.
 *
 * <p>This class is responsible for:</p>
 * <ul>
 *   <li>Creating the custom rules repository with key {@value #REPOSITORY_KEY}</li>
 *   <li>Loading rule metadata from JSON and HTML resource files</li>
 *   <li>Registering all checks from {@link RulesList}</li>
 * </ul>
 *
 * <p>Rule metadata is loaded from {@code org/sonar/l10n/java/rules/jdbc/} resources.
 * Each rule requires a {@code <RuleKey>.json} file for metadata and an optional
 * {@code <RuleKey>.html} file for documentation.</p>
 *
 * @author SonarQube JDBC Client Plugin Team
 * @since 1.0.0
 * @see RulesList
 * @see org.sonar.api.server.rule.RulesDefinition
 */
public class JdbcClientRulesDefinition implements RulesDefinition {

    private static final String REPOSITORY_KEY = "spring-jdbc-leak-detector";
    private static final String REPOSITORY_NAME = "Spring JDBC Leak Detector";
    private static final String LANGUAGE = "java";
    private static final String RESOURCE_BASE_PATH = "org/sonar/l10n/java/rules/jdbc";

    private final SonarRuntime sonarRuntime;

    /**
     * Constructs a new rules definition with the given SonarQube runtime.
     *
     * @param sonarRuntime the SonarQube runtime environment, used for version-specific behavior
     */
    public JdbcClientRulesDefinition(SonarRuntime sonarRuntime) {
        this.sonarRuntime = sonarRuntime;
    }

    /**
     * {@inheritDoc}
     *
     * <p>Creates the custom rules repository and loads all rule definitions from
     * the resource files. Each rule's metadata is loaded from JSON files, and
     * documentation is loaded from HTML files.</p>
     *
     * @param context the rules definition context for creating repositories
     */
    @Override
    public void define(Context context) {
        NewRepository repository = context.createRepository(REPOSITORY_KEY, LANGUAGE)
            .setName(REPOSITORY_NAME);

        RuleMetadataLoader ruleMetadataLoader = new RuleMetadataLoader(RESOURCE_BASE_PATH, sonarRuntime);

        // Load rule metadata from JSON and HTML files
        ruleMetadataLoader.addRulesByAnnotatedClass(repository, RulesList.getChecks());

        repository.done();
    }

    /**
     * Registration point for SonarLint and SonarQube scanner to discover custom checks.
     *
     * <p>This registrar enables the custom JDBC rules to work with both SonarQube
     * server analysis and SonarLint IDE integration. It registers all check classes
     * from {@link RulesList} with the repository.</p>
     *
     * @author SonarQube JDBC Client Plugin Team
     * @since 1.0.0
     * @see CheckRegistrar
     */
    public static class JdbcClientCheckRegistrar implements CheckRegistrar {

        /**
         * {@inheritDoc}
         *
         * <p>Registers all JDBC client check classes with the repository for
         * discovery by the SonarQube analysis engine.</p>
         *
         * @param registrarContext the context for registering check classes
         */
        @Override
        public void register(RegistrarContext registrarContext) {
            List<Class<? extends JavaCheck>> checkClasses = new ArrayList<>();
            for (Class<?> check : RulesList.getChecks()) {
                @SuppressWarnings("unchecked")
                Class<? extends JavaCheck> javaCheck = (Class<? extends JavaCheck>) check;
                checkClasses.add(javaCheck);
            }
            registrarContext.registerClassesForRepository(
                REPOSITORY_KEY,
                checkClasses,
                null
            );
        }
    }
}
