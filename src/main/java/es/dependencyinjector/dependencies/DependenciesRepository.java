package es.dependencyinjector.dependencies;

import java.lang.annotation.Annotation;
import java.util.List;

public interface DependenciesRepository {
    void add(Class<?> instanceClass, Object instance);

    Object get(Class<?> instance);

    boolean contains(Class<?> classToGet);

    <T> List<T> queryByImplementsInterface(Class<T> interfaceToCheck);

    List<Object> queryByAnnotatedWith(Class<? extends Annotation> annotationToCheck);

    <T> List<T> queryByAnnotatedWithAndImplementsInterface(Class<? extends Annotation> annotationToCheck, Class<T> interfaceToCheck);
}
