package es.dependencyinjector.abstractions;

import es.dependencyinjector.utils.exceptions.DuplicatedImplementation;
import es.dependencyinjector.utils.exceptions.UnknownDependency;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public final class InMemoryAbstractionsRepository implements AbstractionsRepository {
    private final Map<Class<?>, List<Class<?>>> abstractions; //Abstraction -> Implementation

    public InMemoryAbstractionsRepository() {
        this.abstractions = new ConcurrentHashMap<>();
    }

    @Override
    public void add(Class<?> abstraction, Class<?> implementation) {
       this.abstractions.putIfAbsent(abstraction, new LinkedList<>());
       this.abstractions.get(abstraction).add(implementation);
    }

    @Override
    public Class<?> get(Class<?> abstraction) throws Exception{
        List<Class<?>> implementations = this.abstractions.get(abstraction);
        if(implementations == null)
            throw new UnknownDependency("Implementation not found for %s, it may not be annotated", abstraction.getName());
        if(implementations.size() > 1)
            throw new DuplicatedImplementation("Duplicated implementation for %s found %s",
                    abstraction, implementations);

        return implementations.get(0);
    }

    @Override
    public boolean contains(Class<?> abstraction){
        return this.abstractions.get(abstraction) != null;
    }
}
