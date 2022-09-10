package es.jaime.test.conditions.simple;

import es.dependencyinjector.DependencyInjectorBootstrapper;
import es.dependencyinjector.DependencyInjectorConfiguration;
import es.dependencyinjector.conditions.conditionalonpropery.PropertyReader;
import es.dependencyinjector.dependencies.InMemoryDependenciesRepository;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public final class SimpleConditionsDependecyInjectorTest {
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

        assertThat(dependenciesRepository.get(ServiceCond1.class)).isNull();
        assertThat(dependenciesRepository.get(ServiceCond2.class)).isNotNull();
    }

    private static class FakePropertyReader implements PropertyReader {
        @Override
        public String get(String key) {
            if(key.equalsIgnoreCase("1")) return "a";
            if(key.equalsIgnoreCase("2")) return "b";
            return "c";
        }
    }
}
