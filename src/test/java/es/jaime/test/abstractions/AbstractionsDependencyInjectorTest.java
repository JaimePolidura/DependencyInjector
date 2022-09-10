package es.jaime.test.abstractions;

import es.dependencyinjector.DependencyInjectorBootstrapper;
import es.dependencyinjector.abstractions.InMemoryAbstractionsRepository;
import es.dependencyinjector.DependencyInjectorConfiguration;
import es.dependencyinjector.dependencies.InMemoryDependenciesRepository;
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

        assertThat(dependenciesRepository.get(ServiceAAbs.class)).isNotNull();
        assertThat(dependenciesRepository.get(ServiceBAbs.class)).isNotNull();
        assertThat(dependenciesRepository.get(RepositoryAAbs.class)).isNotNull().isInstanceOf(RepositoryAImplAbs.class);
        assertThat(abstractionsRepository.get(RepositoryAAbs.class)).isNotNull().matches(impl -> impl == RepositoryAImplAbs.class);
    }
}
