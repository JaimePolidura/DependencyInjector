package es.jaime.test.providers;

import es.dependencyinjector.annotations.Component;
import es.dependencyinjector.annotations.Provider;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@Component
public final class Provider2 {
    private final Provider1 provider1;
    private final Service1 service1;

    @Provider
    public Provided2 provided2(){
        return new Provided2();
    }
}
