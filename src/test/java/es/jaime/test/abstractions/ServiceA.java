package es.jaime.test.abstractions;

import es.dependencyinjector.annotations.Service;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Service
public final class ServiceA {
    @Getter private final RepositoryA repositoryA;
    @Getter private final ServiceB serviceB;
}
