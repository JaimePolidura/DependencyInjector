package es.dependencyinjector.conditions;

import es.dependencyinjector.DependencyInjectorConfiguration;
import es.dependencyinjector.DependencyInjectorScanner;
import es.jaime.javaddd.application.utils.ReflectionUtils;
import lombok.AllArgsConstructor;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@SuppressWarnings("ALL")
@AllArgsConstructor
public final class DependencyConditionService {
    private final DependencyInjectorConfiguration configuration;
    private final DependencyInjectorScanner scanner;

    public boolean testAll(Class<?> dependency) throws Exception {
        List<Annotation> annotations = ReflectionUtils.findAnnotationsInClass(dependency);
        Set<Class<? extends Annotation>> allConditionAnnotations = this.getConditionAnnotations();
        List<Annotation> conditionAnnotationsToCheck = getConditionAnnotationsToCheck(annotations, allConditionAnnotations);

        for (Annotation conditionAnnotationInDependency : conditionAnnotationsToCheck) {
            SupportedConditionAnnotation supportedConditionAnnotation = getSupportedConditionAnnotation(conditionAnnotationInDependency);
            Class<? extends DependencyConditionTester> testerClass = supportedConditionAnnotation.getTester();

            DependencyConditionTester testerInstance = (DependencyConditionTester) this.scanner.instantiateClass(testerClass, testerClass);

            if(!testerInstance.test(dependency, conditionAnnotationInDependency)){
                return false;
            }
        }

        return true;
    }

    private SupportedConditionAnnotation getSupportedConditionAnnotation(Annotation conditionAnnotationInDependecy) {
        return this.configuration.getConditionAnnotations().stream()
                .filter(supportedConditionAnnotation1 -> supportedConditionAnnotation1.getAnnotation().equals(conditionAnnotationInDependecy.annotationType()))
                .findFirst()
                .get();
    }

    private List<Annotation> getConditionAnnotationsToCheck(List<Annotation> annotations, Set<Class<? extends Annotation>> allConditionAnnotations) {
        return annotations.stream()
                .filter(annotation -> allConditionAnnotations.contains(annotation.annotationType()))
                .collect(Collectors.toList());
    }

    private Set<Class<? extends Annotation>> getConditionAnnotations() {
        return configuration.getConditionAnnotations().stream()
                .map(SupportedConditionAnnotation::getAnnotation)
                .collect(Collectors.toSet());
    }
}
