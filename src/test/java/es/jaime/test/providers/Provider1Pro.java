package es.jaime.test.providers;

import es.dependencyinjector.dependencies.annotations.Component;
import es.dependencyinjector.providers.Provider;

@Component
public final class Provider1Pro {
    @Provider
    public Provided1Pro provided1() {
        return new Provided1Pro();
    }
}
