package es.jaime.test.simple;

import es.dependencyinjector.DependencyInjectorBootstrapper;
import es.dependencyinjector.dependencies.InMemoryDependenciesRepository;
import es.dependencyinjector.DependencyInjectorConfiguration;
import es.jaime.test.FakePropertyReader;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

public final class SimpleDependencyScannerTest {
    @Test
    @SneakyThrows
    public void shouldScan() {
        InMemoryDependenciesRepository repository = new InMemoryDependenciesRepository();
        DependencyInjectorBootstrapper.init(DependencyInjectorConfiguration.builder()
                        .packageToScan("es.jaime.test")
                        .dependenciesRepository(repository)
                        .propertyReader(new FakePropertyReader())
                        .waitUntilCompletion()
                .build());

        assertThat(repository.get(ClassASim.class)).isNotNull();
        assertThat(repository.get(ClassBSim.class)).isNotNull();
        assertThat(repository.get(ClassCSim.class)).isNotNull();
    }
}
