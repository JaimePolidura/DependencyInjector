package es.jaime.test.simple;

import es.dependencyinjector.DependencyInjectorBootstrapper;
import es.dependencyinjector.repository.InMemoryDependenciesRepository;
import es.dependencyinjector.DependencyInjectorConfiguration;
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
                        .waitUntilCompletion()
                .build());

        assertThat(repository.get(ClassA.class)).isNotNull();
        assertThat(repository.get(ClassB.class)).isNotNull();
        assertThat(repository.get(ClassC.class)).isNotNull();
    }
}
