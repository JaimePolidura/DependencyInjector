package es.jaime.test.providers;

import es.dependencyinjector.dependencies.annotations.Service;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Service
@AllArgsConstructor
public final class Service2Pro {
    @Getter private final Provided1Pro provided1;
}
