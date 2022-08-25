package es.jaimetruman;

import lombok.Getter;

public final class DependencyInjector {
    @Getter private final InMemoryDependenciesRepository dependencies;
    private final DependencyInjectorScannerConfiguration configuration;
    private final DependencyInjectorScanner scanner;

    public DependencyInjector(DependencyInjectorScannerConfiguration configuration) {
        this.dependencies = new InMemoryDependenciesRepository();
        this.configuration = configuration;
        this.scanner = new DependencyInjectorScanner(this.dependencies, new InMemoryAbstractionsRepository(), this.configuration);
    }

    public void startScanning(){
        try {
            this.scanner.start();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }
}
