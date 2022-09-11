package es.jaime.test.providers;

import es.dependencyinjector.DependencyInjectorBootstrapper;
import es.dependencyinjector.DependencyInjectorConfiguration;
import es.dependencyinjector.abstractions.InMemoryAbstractionsRepository;
import es.dependencyinjector.dependencies.InMemoryDependenciesRepository;
import es.dependencyinjector.providers.InMemoryProvidersRepository;
import es.jaime.test.FakePropertyReader;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

import java.util.concurrent.Exchanger;

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
                .propertyReader(new FakePropertyReader())
                .waitUntilCompletion()
                .build());

        Exchanger<Integer> exchanger = new Exchanger<>();

        assertThat(dependenciesRepository.get(Provider1Pro.class)).isNotNull();
        assertThat(dependenciesRepository.get(Provider2Pro.class)).isNotNull();
        assertThat(dependenciesRepository.get(Provided1Pro.class)).isNotNull();
        assertThat(dependenciesRepository.get(Provided2Pro.class)).isNotNull();
        assertThat(dependenciesRepository.get(Provided3Pro.class)).isNotNull();

        assertThat(dependenciesRepository.get(Service1Pro.class)).isNotNull();
        assertThat(dependenciesRepository.get(Service2Pro.class)).isNotNull().matches(service -> ((Service2Pro) service).getProvided1() != null);
    }
}
