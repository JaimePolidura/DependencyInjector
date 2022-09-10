package es.dependencyinjector.abstractions;

import es.dependencyinjector.DependencyInjectorConfiguration;
import es.dependencyinjector.conditions.DependencyConditionService;
import lombok.AllArgsConstructor;

import java.util.List;

import static es.dependencyinjector.utils.ReflectionUtils.getAbstractions;
import static es.dependencyinjector.utils.Utils.runCheckedOrTerminate;

@AllArgsConstructor
public final class AbstractionsSaver {
    private final DependencyInjectorConfiguration configuration;
    private final DependencyConditionService dependencyConditionService;

    public void save(Class<?> implementationClass) throws Exception {
        if(!this.dependencyConditionService.testAll(implementationClass))
            return;

        List<Class<?>> abstractions = getAbstractions(implementationClass);

        for (Class<?> abstraction : abstractions) {
            boolean alreadyDeclaredInConfig = this.configuration.getAbstractions().containsKey(abstraction);

            runCheckedOrTerminate(() -> this.configuration.getAbstractionsRepository().add(
                    abstraction,
                    alreadyDeclaredInConfig ? this.configuration.getAbstractions().get(abstraction) : implementationClass)
            );
        }
    }

}
