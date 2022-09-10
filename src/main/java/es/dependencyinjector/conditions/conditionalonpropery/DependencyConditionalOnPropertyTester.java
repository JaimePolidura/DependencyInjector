package es.dependencyinjector.conditions.conditionalonpropery;

import es.dependencyinjector.conditions.DependencyConditionTester;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public final class DependencyConditionalOnPropertyTester implements DependencyConditionTester<ConditionalOnProperty> {
    private final PropertyReader reader;

    @Override
    public boolean test(Class<?> test, ConditionalOnProperty annotation) {
        String keyProperty = annotation.key();
        String havingValue = annotation.havingValue();
        String valueInProperties = reader.get(keyProperty);

        return valueInProperties.equalsIgnoreCase(havingValue);
    }
}
