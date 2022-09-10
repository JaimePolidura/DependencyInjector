package es.dependencyinjector.conditions;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.lang.annotation.Annotation;

@AllArgsConstructor
public final class SupportedConditionAnnotation {
    @Getter private final Class<? extends Annotation> annotation;
    @Getter private final Class<? extends DependencyConditionTester> tester;
        
    public static SupportedConditionAnnotation from(Class<? extends Annotation> annotation, Class<? extends DependencyConditionTester> tester) {
        return new SupportedConditionAnnotation(annotation, tester);
    }
}
