package es.dependencyinjector;

import es.dependencyinjector.abstractions.AbstractionsRepository;
import es.dependencyinjector.conditions.conditionalonpropery.DependencyConditionalOnPropertyTester;
import es.dependencyinjector.dependencies.DependenciesRepository;
import es.dependencyinjector.dependencies.InMemoryDependenciesRepository;
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
        
        abstractionsRepository.add(DependenciesRepository.class, dependenciesRepository.getClass());
        abstractionsRepository.add(AbstractionsRepository.class, abstractionsRepository.getClass());
        abstractionsRepository.add(ProvidersRepository.class, providersRepository.getClass());

        dependenciesRepository.add(dependenciesRepository.getClass(), dependenciesRepository);
        dependenciesRepository.add(abstractionsRepository.getClass(), abstractionsRepository);
        dependenciesRepository.add(providersRepository.getClass(), providersRepository);
        dependenciesRepository.add(DependencyConditionalOnPropertyTester.class, new DependencyConditionalOnPropertyTester(configuration.getPropertyReader()));
        dependenciesRepository.add(dependencyInjector.getClass(), dependenciesRepository);
        dependenciesRepository.add(configuration.getClass(), configuration);
        dependenciesRepository.add(DependenciesExecutor.class, new DependenciesExecutor(dependenciesRepository));
        dependenciesRepository.add(DependenciesRepository.class, dependenciesRepository);

        dependencyInjector.startScanning();

        return configuration;
    }
}
