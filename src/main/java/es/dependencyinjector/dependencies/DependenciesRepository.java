package es.dependencyinjector.dependencies;

public interface DependenciesRepository {
    void add(Class<?> instanceClass, Object instance);

    Object get(Class<?> instance);

    boolean contains(Class<?> classToGet);
}
