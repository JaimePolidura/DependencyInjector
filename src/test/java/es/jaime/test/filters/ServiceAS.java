package es.jaime.test.filters;

import es.dependencyinjector.dependencies.DependenciesRepository;
import es.dependencyinjector.dependencies.annotations.Service;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public final class ServiceAS {
    private final DependenciesRepository dependencies;

    public void hola() {
        dependencies.filterByImplementsInterface(UseCaseHandler.class)
                .forEach(a -> System.out.println(a.getClass().getName()));
    }
}
