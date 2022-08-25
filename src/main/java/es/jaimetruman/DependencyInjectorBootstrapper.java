package es.jaimetruman;

import es.jaimetruman.repository.AbstractionsRepository;
import es.jaimetruman.repository.DependenciesRepository;

public final class DependencyInjectorBootstrapper {
    private final DependencyInjectorConfiguration configuration;
    private final DependencyInjectorScanner scanner;

    public DependencyInjectorBootstrapper(DependencyInjectorConfiguration configuration) {
        this.configuration = configuration;
        this.scanner = new DependencyInjectorScanner(this.configuration);
    }

    public void startScanning(){
        try {
            this.scanner.start();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

    public static DependencyInjectorConfiguration init(DependencyInjectorConfiguration configuration) throws Exception{
        DependencyInjectorBootstrapper dependencyInjector = new DependencyInjectorBootstrapper(configuration);
        DependenciesRepository dependenciesRepository = dependencyInjector.configuration.getDependenciesRepository();
        AbstractionsRepository abstractionsRepository = dependencyInjector.configuration.getAbstractionsRepository();

        dependenciesRepository.add(DependenciesRepository.class, dependenciesRepository);
        abstractionsRepository.add(AbstractionsRepository.class, abstractionsRepository.getClass());

        dependencyInjector.startScanning();

        return configuration;
    }
}
