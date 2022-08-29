package es.dependencyinjector.abstractions;

import es.dependencyinjector.DependencyInjectorConfiguration;
import lombok.AllArgsConstructor;

import java.util.List;

import static es.dependencyinjector.utils.ReflectionUtils.getAbstractions;
import static es.dependencyinjector.utils.Utils.runCheckedOrTerminate;

@AllArgsConstructor
public final class AbstractionsSaver {
    private final DependencyInjectorConfiguration configuration;
    
    public void save(Class<?> implementationClass) {
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
