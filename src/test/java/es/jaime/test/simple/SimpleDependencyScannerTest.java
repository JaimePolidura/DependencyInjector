package es.jaime.test.simple;

import es.jaimetruman.DependencyInjectorBootstrapper;
import es.jaimetruman.repository.InMemoryDependenciesRepository;
import es.jaimetruman.DependencyInjectorConfiguration;
import lombok.SneakyThrows;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

public final class SimpleDependencyScannerTest {
    @Test
    @SneakyThrows
    public void shouldScan() {
        InMemoryDependenciesRepository repository = new InMemoryDependenciesRepository();
        DependencyInjectorBootstrapper.init(DependencyInjectorConfiguration.builder()
                        .packageToScan("es.jaime.test")
                        .dependenciesRepository(repository)
                .build());

        Assertions.assertThat(repository.get(ClassA.class)).isNotNull();
        Assertions.assertThat(repository.get(ClassB.class)).isNotNull();
        Assertions.assertThat(repository.get(ClassC.class)).isNotNull();
    }
}
