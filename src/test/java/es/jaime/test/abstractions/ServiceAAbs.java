package es.jaime.test.abstractions;

import es.dependencyinjector.dependencies.annotations.Service;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Service
public final class ServiceAAbs {
    @Getter private final RepositoryAAbs repositoryA;
    @Getter private final ServiceBAbs serviceB;
}
