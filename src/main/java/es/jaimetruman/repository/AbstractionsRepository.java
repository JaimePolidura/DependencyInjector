package es.jaimetruman.repository;

import es.jaimetruman.exceptions.DuplicatedImplementation;

public interface AbstractionsRepository {
    void add(Class<?> abstraction, Class<?> implementation) throws DuplicatedImplementation;
    Class<?> get(Class<?> abstraction);
    boolean contains(Class<?> abstraction);
}
