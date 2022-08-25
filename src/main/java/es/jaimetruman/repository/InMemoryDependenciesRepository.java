package es.jaimetruman.repository;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class InMemoryDependenciesRepository implements DependenciesRepository {
    private final Map<Class<?>, Object> instances = new ConcurrentHashMap<>();

    @Override
    public void add(Class<?> instanceClass, Object instance){
        this.instances.putIfAbsent(instanceClass, instance);
    }

    @Override
    public Object get(Class<?> instance){
        return this.instances.get(instance);
    }

    @Override
    public boolean contains(Class<?> classToGet){
        return this.instances.get(classToGet) != null;
    }
}
