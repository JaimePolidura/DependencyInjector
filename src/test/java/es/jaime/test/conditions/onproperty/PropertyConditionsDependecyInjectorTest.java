package es.jaime.test.conditions.onproperty;

import es.dependencyinjector.DependencyInjectorBootstrapper;
import es.dependencyinjector.DependencyInjectorConfiguration;
import es.dependencyinjector.abstractions.InMemoryAbstractionsRepository;
import es.dependencyinjector.dependencies.InMemoryDependenciesRepository;
import es.jaime.test.FakePropertyReader;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public final class PropertyConditionsDependecyInjectorTest {
    @Test
    @SneakyThrows
    public void shouldScan() {
        InMemoryDependenciesRepository dependenciesRepository = new InMemoryDependenciesRepository();
        InMemoryAbstractionsRepository abstractionsRepository = new InMemoryAbstractionsRepository();
        DependencyInjectorBootstrapper.init(DependencyInjectorConfiguration.builder()
                .packageToScan("es.jaime.test")
                .abstractionsRepository(abstractionsRepository)
                .dependenciesRepository(dependenciesRepository)
                .propertyReader(new FakePropertyReader())
                .waitUntilCompletion()
                .build());

        assertThat(dependenciesRepository.get(ServiceCond1.class)).isNull();
        assertThat(dependenciesRepository.get(ServiceCond2.class)).isNotNull();
        assertThat(dependenciesRepository.get(AbsCond1.class)).isNotNull().matches(instance -> instance.getClass() == Impl1Cond1.class);
        assertThat(abstractionsRepository.get(AbsCond1.class)).matches(impl -> impl == Impl1Cond1.class);
    }
}
