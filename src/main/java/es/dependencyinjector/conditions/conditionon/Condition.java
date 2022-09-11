package es.dependencyinjector.conditions.conditionon;

public interface Condition {
    boolean test(Class<?> dependency);
}
