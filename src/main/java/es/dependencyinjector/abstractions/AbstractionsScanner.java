package es.dependencyinjector.abstractions;

import es.dependencyinjector.DependencyInjectorConfiguration;
import es.dependencyinjector.DependencyInjectorLogger;
import org.reflections.Reflections;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

public final class AbstractionsScanner {
    private final DependencyInjectorLogger dependencyInjectorLogger;
    private final DependencyInjectorConfiguration configuration;
    private final AbstractionsService abstractionsService;
    private final Reflections reflections;

    public AbstractionsScanner(DependencyInjectorLogger dependencyInjectorLogger, DependencyInjectorConfiguration configuration,
                               Reflections reflections) {
        this.abstractionsService = new AbstractionsService(configuration);
        this.dependencyInjectorLogger = dependencyInjectorLogger;
        this.configuration = configuration;
        this.reflections = reflections;
    }

    public Set<Class<?>> scan() {
        return this.configuration.getAnnotations().stream()
                .map(this.reflections::getTypesAnnotatedWith)
                .flatMap(Collection::stream)
                .filter(abstractionsService::isImplementation)
                .filter(implementation -> !configuration.getExcludedDependencies().contains(implementation))
                .collect(Collectors.toSet());
    }
}
