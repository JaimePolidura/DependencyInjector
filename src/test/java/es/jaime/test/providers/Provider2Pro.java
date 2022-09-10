package es.jaime.test.providers;

import es.dependencyinjector.dependencies.annotations.Component;
import es.dependencyinjector.providers.Provider;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@Component
public final class Provider2Pro {
    @Provider
    public Provided2Pro provided2(){
        return new Provided2Pro();
    }

    @Provider
    public Provided3Pro provided3() {
        return new Provided3Pro();
    }
}
