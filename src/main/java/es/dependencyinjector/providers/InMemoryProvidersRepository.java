package es.dependencyinjector.providers;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public final class InMemoryProvidersRepository implements ProvidersRepository{
    private final Map<Class<?>, DependencyProvider> providersByDependencyClassProvided = new ConcurrentHashMap<>();
    private final Map<Class<?>, List<DependencyProvider>> providersByProviderClass = new ConcurrentHashMap<>();

    @Override
    public void save(DependencyProvider dependencyProvider) {
        this.providersByDependencyClassProvided.put(dependencyProvider.getDependencyClassProvided(), dependencyProvider);
        this.providersByProviderClass.putIfAbsent(dependencyProvider.getProviderClass(), new LinkedList<>());
        this.providersByProviderClass.get(dependencyProvider.getProviderClass()).add(dependencyProvider);
    }

    @Override
    public Optional<DependencyProvider> findByDependencyClassProvided(Class<?> dependencyClassProvided) {
        return Optional.ofNullable(this.providersByDependencyClassProvided.get(dependencyClassProvided));
    }

    @Override
    public Optional<List<DependencyProvider>> findByProviderClass(Class<?> providerClass) {
        return Optional.ofNullable(this.providersByProviderClass.get(providerClass));
    }

    @Override
    public List<Class<?>> findAllProviderClasses() {
        return new ArrayList<>(providersByProviderClass.keySet());
    }
}
