package es.dependencyinjector.dependencies;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static es.jaime.javaddd.application.utils.CollectionUtils.*;
import static es.jaime.javaddd.application.utils.ReflectionUtils.*;

public final class InMemoryDependenciesRepository implements DependenciesRepository {
    private final Map<Class<?>, Object> instances = new ConcurrentHashMap<>();

    private final Map<Class<?>, List<Object>> implementsInterfaceIndex = new ConcurrentHashMap<>();

    @Override
    public void add(Class<?> instanceClass, Object instance){
        this.instances.putIfAbsent(instanceClass, instance);

        findInterfacesInClass(instanceClass).forEach(interfaceInClass -> {
            incrementMapList(implementsInterfaceIndex, interfaceInClass, instance, LinkedList::new);
        });
    }

    @Override
    public <T> T get(Class<T> instance) {
        return (T) this.instances.get(instance);
    }

    @Override
    public boolean contains(Class<?> classToGet){
        return this.instances.get(classToGet) != null;
    }

    @Override
    public <T> Optional<T> filterByImplementsInterfaceWithGeneric(Class<T> interfaceToCheck, Class<?> genericType) {
        return filterByImplementsInterface(interfaceToCheck).stream()
                .filter(implementation -> hasInterfaceWithGenericType(implementation.getClass(), genericType))
                .findFirst();
    }

    @Override
    public <T> List<T> filterByImplementsInterface(Class<T> interfaceToCheck) {
        return implementsInterfaceIndex.containsKey(interfaceToCheck) ?
                new LinkedList<T>((Collection<? extends T>) implementsInterfaceIndex.get(interfaceToCheck)) :
                Collections.EMPTY_LIST;
    }
}
