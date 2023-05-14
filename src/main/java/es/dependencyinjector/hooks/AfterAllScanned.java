package es.dependencyinjector.hooks;

import es.dependencyinjector.dependencies.DependenciesRepository;

public interface AfterAllScanned {
    void afterAllScanned(DependenciesRepository dependencies);
}
