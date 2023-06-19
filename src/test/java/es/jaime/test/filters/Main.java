package es.jaime.test.filters;

import es.dependencyinjector.DependencyInjectorBootstrapper;
import es.dependencyinjector.DependencyInjectorConfiguration;
import es.dependencyinjector.dependencies.DependenciesRepository;
import es.dependencyinjector.dependencies.InMemoryDependenciesRepository;
import es.jaime.test.FakePropertyReader;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Optional;

public final class Main {
    @Test
    public void shouldFilter() throws Exception {
        DependenciesRepository dependencies = new InMemoryDependenciesRepository();

        DependencyInjectorBootstrapper.init(DependencyInjectorConfiguration.builder()
                .packageToScan("es.jaime.test.filters")
                .singleThreadedScan()
                .waitUntilCompletion()
//                .useDebugLogging()
                .excludedAbstractions(UseCaseHandler.class)
                .dependenciesRepository(dependencies)
                .propertyReader(new FakePropertyReader())
                .build());

        Optional<UseCaseHandler> optionalHandler = dependencies.filterByImplementsInterfaceWithGeneric(UseCaseHandler.class, ParametrosA.class);

        Assertions.assertThat(optionalHandler)
                .isPresent();

        Assertions.assertThat(optionalHandler.get())
                .isOfAnyClassIn(AUseCaseHandler.class);
    }
}
