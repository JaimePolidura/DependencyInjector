package es.jaime.test.abstractions;

import es.jaimetruman.AbstractionsRepository;
import es.jaimetruman.DependenciesRepository;
import es.jaimetruman.DependencyInjectorScanner;
import es.jaimetruman.DependencyInjectorScannerConfiguration;

public final class Scanner {
    public static void main(String[] args) throws Exception {
        DependenciesRepository repostitory = new DependenciesRepository();
        AbstractionsRepository abstractionsRepository = new AbstractionsRepository();
        DependencyInjectorScanner scanner = new DependencyInjectorScanner(
                repostitory,
                abstractionsRepository,
                new Configuration()
        );
        scanner.start();

        System.out.println(repostitory.get(ServiceA.class));
        System.out.println(repostitory.get(ServiceB.class));
        System.out.println(repostitory.get(RepositoryA.class));
        System.out.println(abstractionsRepository.get(RepositoryA.class));
    }

    private static class Configuration extends DependencyInjectorScannerConfiguration {
        @Override
        public String packageToScan() {
            return "es.jaime.test.abstractions";
        }
    }
}
