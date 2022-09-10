package es.dependencyinjector.abstractions;

import es.dependencyinjector.utils.exceptions.DuplicatedImplementation;

public interface AbstractionsRepository {
    void add(Class<?> abstraction, Class<?> implementation) throws DuplicatedImplementation;

    Class<?> get(Class<?> abstraction) throws Exception;

    boolean contains(Class<?> abstraction);
}
