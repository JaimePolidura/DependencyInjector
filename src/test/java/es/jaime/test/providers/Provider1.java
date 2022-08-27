package es.jaime.test.providers;

import es.dependencyinjector.annotations.Component;
import es.dependencyinjector.annotations.Provider;

@Component
public final class Provider1 {
    @Provider
    public Provided1 provided1() {
        return new Provided1();
    }
}
