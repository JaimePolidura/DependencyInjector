package es.dependencyinjector.dependencies;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Optional;

public interface DependenciesRepository {
    void add(Class<?> instanceClass, Object instance);

    <T> T get(Class<T> instance);

    boolean contains(Class<?> classToGet);

    <T> List<T> filterByImplementsInterface(Class<T> interfaceToCheck);

    <T> Optional<T> filterByImplementsInterfaceWithGeneric(Class<T> interfaceToCheck, Class<?> genericType);

    List<Object> filterByAnnotatedWith(Class<? extends Annotation> annotationToCheck);
}
