package es.dependencyinjector.abstractions;

import es.dependencyinjector.DependencyInjectorConfiguration;
import es.dependencyinjector.DependencyInjectorLogger;
import es.dependencyinjector.conditions.DependencyConditionService;

import java.util.List;

import static es.jaime.javaddd.application.utils.ExceptionUtils.runCheckedOrTerminate;

public final class AbstractionsSaver {
    private final DependencyConditionService dependencyConditionService;
    private final DependencyInjectorLogger dependencyInjectorLogger;
    private final DependencyInjectorConfiguration configuration;
    private final AbstractionsService abstractionsService;

    public AbstractionsSaver(DependencyInjectorConfiguration configuration, DependencyConditionService dependencyConditionService,
                             DependencyInjectorLogger dependencyInjectorLogger) {
        this.configuration = configuration;
        this.dependencyConditionService = dependencyConditionService;
        this.dependencyInjectorLogger = dependencyInjectorLogger;
        this.abstractionsService = new AbstractionsService(configuration);
    }

    public void save(Class<?> implementationClass) throws Exception {
        if(!this.dependencyConditionService.testAll(implementationClass))
            return;

        List<Class<?>> abstractions = abstractionsService.getAbstractions(implementationClass);

        for (Class<?> abstraction : abstractions) {
            boolean alreadyDeclaredInConfig = this.configuration.getAbstractions().containsKey(abstraction);
            Class<?> implementation = alreadyDeclaredInConfig ? this.configuration.getAbstractions().get(abstraction) : implementationClass;

            dependencyInjectorLogger.info("Found implementation %s for abstraction %s " + (alreadyDeclaredInConfig ? "in config": ""),
                    implementation.getName(), abstraction.getName());

            runCheckedOrTerminate(() -> this.configuration.getAbstractionsRepository().add(
                    abstraction,
                    implementation
            ));
        }
    }

}
