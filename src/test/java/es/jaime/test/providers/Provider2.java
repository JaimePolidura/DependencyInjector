package es.jaime.test.providers;

import es.dependencyinjector.annotations.Component;
import es.dependencyinjector.annotations.Provider;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@Component
public final class Provider2 {
    @Provider
    public Provided2 provided2(){
        return new Provided2();
    }

    @Provider
    public Provided3 provided3() {
        return new Provided3();
    }
}
