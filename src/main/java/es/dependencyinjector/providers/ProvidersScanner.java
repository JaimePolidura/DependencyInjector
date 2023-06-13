package es.dependencyinjector.providers;

import es.dependencyinjector.DependencyInjectorConfiguration;
import es.dependencyinjector.DependencyInjectorLogger;
import es.dependencyinjector.utils.exceptions.AnnotationsMissing;
import lombok.AllArgsConstructor;
import org.reflections.Reflections;

import java.util.List;
import java.util.stream.Collectors;

import static es.jaime.javaddd.application.utils.ExceptionUtils.runCheckedOrTerminate;
import static es.jaime.javaddd.application.utils.ReflectionUtils.isAnnotatedWith;

@AllArgsConstructor
public final class ProvidersScanner {
    private final Reflections reflections;
    private final DependencyInjectorConfiguration configuration;
    private final DependencyInjectorLogger logger;

    public List<DependencyProvider> scan() {
        return reflections.getMethodsAnnotatedWith(Provider.class).stream()
                .map(method -> DependencyProvider.of(method.getDeclaringClass(), method.getReturnType(), method))
                .peek(provider -> runCheckedOrTerminate(() -> this.ensureProviderClassAnnotated(provider)))
                .peek(provider -> logger.info("Found provided class %s in provider class %s",
                        provider.getDependencyClassProvided().getName(), provider.getProviderClass().getName()))
                .collect(Collectors.toList());
    }

    private void ensureProviderClassAnnotated(DependencyProvider dependencyProvider) throws AnnotationsMissing {
        if(!isAnnotatedWith(dependencyProvider.getProviderClass(), this.configuration.getAnnotations()))
            throw new AnnotationsMissing("Found provider %s but its container class %s is not annotated", dependencyProvider.getDependencyClassProvided(),
                    dependencyProvider.getProviderClass());
    }
}
