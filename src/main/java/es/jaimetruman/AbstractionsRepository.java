package es.jaimetruman;

import es.jaimetruman.exceptions.DuplicatedImplementation;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class AbstractionsRepository {
    private final Map<Class<?>, Class<?>> abstractions;

    public AbstractionsRepository() {
        this.abstractions = new ConcurrentHashMap<>();
    }

    public void add(Class<?> abstraction, Class<?> implementation) throws DuplicatedImplementation {
       Class<?> alreadySavedImplementation = this.abstractions.get(abstraction);

        if(alreadySavedImplementation != null)
           throw new DuplicatedImplementation("Duplicated implementation for %s found %s when already created %s",
                   abstraction, implementation, alreadySavedImplementation);

        this.abstractions.put(abstraction, implementation);
    }

    public Class<?> get(Class<?> abstraction){
        return this.abstractions.get(abstraction);
    }

    public boolean contains(Class<?> abstraction){
        return this.abstractions.get(abstraction) != null;
    }
}
