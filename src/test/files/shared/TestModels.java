package test.files.shared;

/**
 * Shared test model classes used across all test scenarios.
 * These classes are imported by test files to avoid code duplication.
 */
public class TestModels {

    public static class User {
        public String name;
        public int age;
    }

    public static class Order {
        public String id;
        public double amount;
    }
}
