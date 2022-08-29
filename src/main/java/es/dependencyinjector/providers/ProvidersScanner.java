package es.dependencyinjector.providers;

import es.dependencyinjector.DependencyInjectorConfiguration;
import es.dependencyinjector.annotations.Provider;
import es.dependencyinjector.exceptions.AnnotationsMissing;
import es.dependencyinjector.utils.ReflectionUtils;
import lombok.AllArgsConstructor;
import org.reflections.Reflections;

import java.util.List;
import java.util.stream.Collectors;

import static es.dependencyinjector.utils.Utils.runCheckedOrTerminate;

@AllArgsConstructor
public final class ProvidersScanner {
    private final Reflections reflections;
    private final DependencyInjectorConfiguration configuration;

    public List<DependencyProvider> scan() {
        return this.reflections.getMethodsAnnotatedWith(Provider.class).stream()
                .map(method -> DependencyProvider.of(method.getDeclaringClass(), method.getReturnType(), method))
                .peek(provider -> runCheckedOrTerminate(() -> this.ensureProviderClassAnnotated(provider)))
                .collect(Collectors.toList());
    }


    private void ensureProviderClassAnnotated(DependencyProvider dependencyProvider) throws AnnotationsMissing {
        if(!ReflectionUtils.isAnnotatedWith(dependencyProvider.getProviderClass(), this.configuration.getAnnotations()))
            throw new AnnotationsMissing("Found provider %s but its container class %s is not annotated", dependencyProvider.getDependencyClassProvided(),
                    dependencyProvider.getProviderClass());
    }
}
