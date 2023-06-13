package es.dependencyinjector.providers;

import java.util.List;
import java.util.Optional;

public interface ProvidersRepository {
    void save(DependencyProvider dependencyProvider);

    Optional<DependencyProvider> findByDependencyClassProvided(Class<?> dependencyClassProvided);

    Optional<List<DependencyProvider>> findByProviderClass(Class<?> providerClass);

    List<Class<?>> findAllProviderClasses();
}
