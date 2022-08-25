package es.jaime.test.abstractions;

import es.jaimetruman.InMemoryAbstractionsRepository;
import es.jaimetruman.InMemoryDependenciesRepository;
import es.jaimetruman.DependencyInjectorScanner;
import es.jaimetruman.DependencyInjectorScannerConfiguration;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

public final class AbstractionsDependencyInjectorTest {
    @Test
    public void shouldScan() {
        InMemoryDependenciesRepository repostitory = new InMemoryDependenciesRepository();
        InMemoryAbstractionsRepository abstractionsRepository = new InMemoryAbstractionsRepository();
        DependencyInjectorScanner scanner = new DependencyInjectorScanner(
                repostitory,
                abstractionsRepository,
                new Configuration()
        );
        scanner.start();

        assertThat(repostitory.get(ServiceA.class)).isNotNull();
        assertThat(repostitory.get(ServiceB.class)).isNotNull();
        assertThat(repostitory.get(RepositoryA.class)).isNotNull().isInstanceOf(RepositoryAImpl.class);
        assertThat(abstractionsRepository.get(RepositoryA.class)).isNotNull().matches(impl -> impl == RepositoryAImpl.class);
    }

    private static class Configuration extends DependencyInjectorScannerConfiguration {
        @Override
        public String packageToScan() {
            return "es.jaime.test.abstractions";
        }
    }
}
