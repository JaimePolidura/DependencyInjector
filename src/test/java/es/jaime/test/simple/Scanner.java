package es.jaime.test.simple;

import es.jaimetruman.AbstractionsRepository;
import es.jaimetruman.DependenciesRepository;
import es.jaimetruman.DependencyInjectorScanner;
import es.jaimetruman.DependencyInjectorScannerConfiguration;

public final class Scanner {
    public static void main(String[] args) throws Exception {
        DependenciesRepository repostitory = new DependenciesRepository();
        DependencyInjectorScanner scanner = new DependencyInjectorScanner(
                repostitory,
                new AbstractionsRepository(),
                new Configuration()
        );
        scanner.start();

        System.out.println(repostitory.get(ClassA.class));
        System.out.println(repostitory.get(ClassB.class));
        System.out.println(repostitory.get(ClassC.class));
    }

    private static class Configuration extends DependencyInjectorScannerConfiguration {
        @Override
        public String packageToScan() {
            return "es.jaime.test.simple";
        }
    }
}
