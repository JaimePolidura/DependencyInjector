package es.jaimetruman;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class DependenciesRepository {
    private final Map<Class<?>, Object> instances;

    public DependenciesRepository() {
        this.instances = new ConcurrentHashMap<>();
    }

    public void add(Class<?> instanceClass, Object instance){
        this.instances.putIfAbsent(instanceClass, instance);
    }

    public Object get(Class<?> instance){
        return this.instances.get(instance);
    }

    public boolean contains(Class<?> classToGet){
        return this.instances.get(classToGet) != null;
    }
}
