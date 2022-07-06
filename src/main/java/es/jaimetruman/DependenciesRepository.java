package es.jaimetruman;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class DependenciesRepository {
    private final Map<Class<?>, Object> instances;

    public DependenciesRepository() {
        this.instances = new ConcurrentHashMap<>();
    }

    public void addIfNotContained(Object instance){
        this.instances.putIfAbsent(instance.getClass(), instance);
    }

    public Object get(Class<?> instance){
        return this.instances.get(instance);
    }

    public boolean contains(Class<?> classToGet){
        return this.instances.get(classToGet) != null;
    }
}
