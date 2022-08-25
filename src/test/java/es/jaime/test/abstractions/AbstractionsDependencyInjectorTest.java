package es.jaime.test.abstractions;

import es.jaimetruman.DependencyInjectorBootstrapper;
import es.jaimetruman.repository.InMemoryAbstractionsRepository;
import es.jaimetruman.repository.InMemoryDependenciesRepository;
import es.jaimetruman.DependencyInjectorConfiguration;
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
                .build());

        assertThat(dependenciesRepository.get(ServiceA.class)).isNotNull();
        assertThat(dependenciesRepository.get(ServiceB.class)).isNotNull();
        assertThat(dependenciesRepository.get(RepositoryA.class)).isNotNull().isInstanceOf(RepositoryAImpl.class);
        assertThat(abstractionsRepository.get(RepositoryA.class)).isNotNull().matches(impl -> impl == RepositoryAImpl.class);
    }
}
