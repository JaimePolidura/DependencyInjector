package es.jaime.test.conditions.conditional;

import es.dependencyinjector.DependencyInjectorBootstrapper;
import es.dependencyinjector.DependencyInjectorConfiguration;
import es.dependencyinjector.dependencies.InMemoryDependenciesRepository;
import es.jaime.test.FakePropertyReader;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

public final class ConditionalOnConditionsDependecyInjectorTest {
    @Test
    @SneakyThrows
    public void shouldScan() {
        InMemoryDependenciesRepository dependenciesRepository = new InMemoryDependenciesRepository();
        DependencyInjectorBootstrapper.init(DependencyInjectorConfiguration.builder()
                .packageToScan("es.jaime.test")
                .dependenciesRepository(dependenciesRepository)
                .propertyReader(new FakePropertyReader())
                .waitUntilCompletion()
                .build());

        assertThat(dependenciesRepository.get(Service2Con.class)).isNotNull();
        assertThat(dependenciesRepository.get(Service3Con.class)).isNull();
    }
}

