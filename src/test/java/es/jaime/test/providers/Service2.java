package es.jaime.test.providers;

import es.dependencyinjector.annotations.Service;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Service
@AllArgsConstructor
public final class Service2 {
    @Getter private final Provided1 provided1;
}
