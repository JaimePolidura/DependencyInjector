package es.jaime.test.abstractions;

import es.dependencyinjector.DependencyInjectorBootstrapper;
import es.dependencyinjector.abstractions.InMemoryAbstractionsRepository;
import es.dependencyinjector.repository.InMemoryDependenciesRepository;
import es.dependencyinjector.DependencyInjectorConfiguration;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

public final class AbstractionsDependencyInjectorTest {
    @Test
    @SneakyThrows
    public void shouldScan() {
        InMemoryDependenciesRepository dependenciesRepository = new InMemoryDependenciesRepository();
        InMemoryAbstractionsRepository abstractionsRepository = new InMemoryAbstractionsRepository();
        DependencyInjectorBootstrapper.init(DependencyInjectorConfiguration.builder()
                .packageToScan("es.jaime.test")
                .dependenciesRepository(dependenciesRepository)
                .abstractionsRepository(abstractionsRepository)
                .waitUntilCompletion()
                .build());

        assertThat(dependenciesRepository.get(ServiceA.class)).isNotNull();
        assertThat(dependenciesRepository.get(ServiceB.class)).isNotNull();
        assertThat(dependenciesRepository.get(RepositoryA.class)).isNotNull().isInstanceOf(RepositoryAImpl.class);
        assertThat(abstractionsRepository.get(RepositoryA.class)).isNotNull().matches(impl -> impl == RepositoryAImpl.class);
    }
}
