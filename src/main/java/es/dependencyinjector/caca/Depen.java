package es.dependencyinjector.caca;

import es.dependencyinjector.DependencyInjectorBootstrapper;
import es.dependencyinjector.DependencyInjectorConfiguration;
import es.dependencyinjector.dependencies.DependenciesRepository;
import es.dependencyinjector.dependencies.InMemoryDependenciesRepository;
import es.dependencyinjector.dependencies.annotations.Component;
import es.dependencyinjector.dependencies.annotations.Service;

import java.util.List;

public final class Depen {
    public static void main(String[] args) throws Exception {
        DependenciesRepository dependencies = new InMemoryDependenciesRepository();

        DependencyInjectorBootstrapper.init(DependencyInjectorConfiguration.builder()
                .packageToScan("es.dependencyinjector.caca")
                .waitUntilCompletion()
                .dependenciesRepository(dependencies)
                .build());

        List<Object> a = dependencies.filterByAnnotatedWith(Service.class); //Will return a list of only ImplementationA

        ImplementationA b = (ImplementationA) a.get(0);

        System.out.println(a.size());
    }
}
