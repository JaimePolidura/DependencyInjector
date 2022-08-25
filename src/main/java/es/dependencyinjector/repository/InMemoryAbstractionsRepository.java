package es.dependencyinjector.repository;

import es.dependencyinjector.exceptions.DuplicatedImplementation;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class InMemoryAbstractionsRepository implements AbstractionsRepository {
    private final Map<Class<?>, Class<?>> abstractions;

    public InMemoryAbstractionsRepository() {
        this.abstractions = new ConcurrentHashMap<>();
    }

    @Override
    public void add(Class<?> abstraction, Class<?> implementation) throws DuplicatedImplementation {
       Class<?> alreadySavedImplementation = this.abstractions.get(abstraction);

        if(alreadySavedImplementation != null)
           throw new DuplicatedImplementation("Duplicated implementation for %s found %s when already created %s",
                   abstraction, implementation, alreadySavedImplementation);

        this.abstractions.put(abstraction, implementation);
    }

    @Override
    public Class<?> get(Class<?> abstraction){
        return this.abstractions.get(abstraction);
    }

    @Override
    public boolean contains(Class<?> abstraction){
        return this.abstractions.get(abstraction) != null;
    }
}
