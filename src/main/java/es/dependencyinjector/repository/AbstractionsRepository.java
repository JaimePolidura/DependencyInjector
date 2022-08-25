package es.dependencyinjector.repository;

import es.dependencyinjector.exceptions.DuplicatedImplementation;

public interface AbstractionsRepository {
    void add(Class<?> abstraction, Class<?> implementation) throws DuplicatedImplementation;
    Class<?> get(Class<?> abstraction);
    boolean contains(Class<?> abstraction);
}
