package es.dependencyinjector.abstractions;

import es.dependencyinjector.DependencyInjectorConfiguration;
import es.dependencyinjector.DependencyInjectorLogger;
import es.jaime.javaddd.application.utils.ReflectionUtils;
import lombok.AllArgsConstructor;
import org.reflections.Reflections;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

@AllArgsConstructor
public final class AbstractionsScanner {
    private final DependencyInjectorLogger dependencyInjectorLogger;
    private final DependencyInjectorConfiguration configuration;
    private final Reflections reflections;

    public Set<Class<?>> scan() {
        return this.configuration.getAnnotations().stream()
                .map(this.reflections::getTypesAnnotatedWith)
                .flatMap(Collection::stream)
                .filter(ReflectionUtils::isImplementation)
                .collect(Collectors.toSet());
    }
}
