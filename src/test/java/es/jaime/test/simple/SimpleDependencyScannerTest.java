package es.jaime.test.simple;

import es.jaimetruman.InMemoryAbstractionsRepository;
import es.jaimetruman.InMemoryDependenciesRepository;
import es.jaimetruman.DependencyInjectorScanner;
import es.jaimetruman.DependencyInjectorScannerConfiguration;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

public final class SimpleDependencyScannerTest {
    @Test
    public void shouldScan() {
        InMemoryDependenciesRepository repository = new InMemoryDependenciesRepository();
        DependencyInjectorScanner scanner = new DependencyInjectorScanner(
                repository,
                new InMemoryAbstractionsRepository(),
                new Configuration()
        );
        scanner.start();

        Assertions.assertThat(repository.get(ClassA.class)).isNotNull();
        Assertions.assertThat(repository.get(ClassB.class)).isNotNull();
        Assertions.assertThat(repository.get(ClassC.class)).isNotNull();
    }

    private static class Configuration extends DependencyInjectorScannerConfiguration {
        @Override
        public String packageToScan() {
            return "es.jaime.test.simple";
        }
    }
}
