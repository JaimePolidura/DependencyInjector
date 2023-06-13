package es.dependencyinjector.conditions.conditionon;

import es.dependencyinjector.DependencyInjectorScanner;
import es.dependencyinjector.conditions.DependencyConditionTester;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public final class DependencyConditionalOnTester implements DependencyConditionTester<ConditionalOn> {
    private final DependencyInjectorScanner dependencyInjectorScanner;

    @Override
    public boolean test(Class<?> dependencyToTest, ConditionalOn annotation) throws Exception {
        Class<?> conditionClass = annotation.value();
        Condition conditionInstance = (Condition) this.dependencyInjectorScanner.instantiateClass(conditionClass, conditionClass);

        return conditionInstance.test(dependencyToTest);
    }
}
