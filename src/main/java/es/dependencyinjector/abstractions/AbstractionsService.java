package es.dependencyinjector.abstractions;

import es.dependencyinjector.DependencyInjectorConfiguration;
import es.jaime.javaddd.application.utils.ReflectionUtils;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public final class AbstractionsService {
    private final DependencyInjectorConfiguration configuration;

    public List<Class<?>> getAbstractions(Class<?> clazz) {
        return ReflectionUtils.getAbstractions(clazz).stream()
                .filter(abstraction -> !configuration.getExcludedAbstractions().contains(abstraction) &&
                        abstraction != Object.class)
                .collect(Collectors.toList());
    }

    public boolean isAbstraction(Class<?> clazz) {
        return ReflectionUtils.isAbstraction(clazz) &&
                !configuration.getExcludedAbstractions().contains(clazz) &&
                clazz != Object.class;
    }

    public boolean isImplementation(Class<?> clazz) {
        return getAbstractions(clazz).size() > 0;
    }
}
