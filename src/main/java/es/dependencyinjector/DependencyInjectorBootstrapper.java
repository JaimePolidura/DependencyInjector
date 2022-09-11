package es.dependencyinjector;

import es.dependencyinjector.abstractions.AbstractionsRepository;
import es.dependencyinjector.conditions.conditionalonpropery.DependencyConditionalOnPropertyTester;
import es.dependencyinjector.dependencies.DependenciesRepository;
import es.dependencyinjector.providers.ProvidersRepository;

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
        ProvidersRepository providersRepository = dependencyInjector.configuration.getProvidersRepository();

        dependenciesRepository.add(DependenciesRepository.class, dependenciesRepository);
        abstractionsRepository.add(DependenciesRepository.class, dependenciesRepository.getClass());
        dependenciesRepository.add(AbstractionsRepository.class, abstractionsRepository);
        abstractionsRepository.add(AbstractionsRepository.class, abstractionsRepository.getClass());
        dependenciesRepository.add(ProvidersRepository.class, providersRepository);
        abstractionsRepository.add(ProvidersRepository.class, providersRepository.getClass());
        dependenciesRepository.add(DependencyConditionalOnPropertyTester.class,
                new DependencyConditionalOnPropertyTester(configuration.getPropertyReader()));
        dependenciesRepository.add(dependencyInjector.getClass(), dependenciesRepository);
        dependenciesRepository.add(configuration.getClass(), configuration);

        dependencyInjector.startScanning();

        return configuration;
    }
}
