package es.dependencyinjector.conditions;

import java.lang.annotation.Annotation;

public interface DependencyConditionTester<T extends Annotation> {
    boolean test(Class<?> test, T annotation) throws Exception;
}
