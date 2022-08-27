package es.jaime.test.providers;

import es.dependencyinjector.DependencyInjectorBootstrapper;
import es.dependencyinjector.DependencyInjectorConfiguration;
import es.dependencyinjector.repository.InMemoryAbstractionsRepository;
import es.dependencyinjector.repository.InMemoryDependenciesRepository;
import es.dependencyinjector.repository.InMemoryProvidersRepository;
import lombok.SneakyThrows;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

public final class DependencyInjectorScannerProvidersTest {
    @Test
    @SneakyThrows
    public void shouldScan() {
        InMemoryDependenciesRepository dependenciesRepository = new InMemoryDependenciesRepository();
        InMemoryAbstractionsRepository abstractionsRepository = new InMemoryAbstractionsRepository();
        InMemoryProvidersRepository providersRepository = new InMemoryProvidersRepository();
        DependencyInjectorBootstrapper.init(DependencyInjectorConfiguration.builder()
                .packageToScan("es.jaime.test")
                .dependenciesRepository(dependenciesRepository)
                .abstractionsRepository(abstractionsRepository)
                .providers(providersRepository)
                .build());

        assertThat(dependenciesRepository.get(Provider1.class)).isNotNull();
        assertThat(dependenciesRepository.get(Provider2.class)).isNotNull();
        assertThat(dependenciesRepository.get(Provided1.class)).isNotNull();
        assertThat(dependenciesRepository.get(Provided2.class)).isNotNull();
        assertThat(dependenciesRepository.get(Service1.class)).isNotNull();
        assertThat(dependenciesRepository.get(Service2.class)).isNotNull().matches(service -> ((Service2) service).getProvided1() != null);
    }
}
