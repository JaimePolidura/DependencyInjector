package es.dependencyinjector;

import es.dependencyinjector.abstractions.AbstractionsRepository;
import es.dependencyinjector.abstractions.InMemoryAbstractionsRepository;
import es.dependencyinjector.annotations.*;
import es.dependencyinjector.providers.InMemoryProvidersRepository;
import es.dependencyinjector.providers.ProvidersRepository;
import es.dependencyinjector.repository.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;

import java.lang.annotation.Annotation;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@AllArgsConstructor
public class DependencyInjectorConfiguration {
    @Getter private final Set<Class<? extends Annotation>> annotations;
    @Getter private final Map<Class<?>, Class<?>> abstractions;
    @Getter private final DependenciesRepository dependenciesRepository;
    @Getter private final AbstractionsRepository abstractionsRepository;
    @Getter private final ProvidersRepository providersRepository;
    @Getter private final String packageToScan;
    @Getter private final boolean waitUntilCompletion;

    public static DependencyInjectorConfigurationBuilder builder() {
        return new DependencyInjectorConfigurationBuilder();
    }

    public static class DependencyInjectorConfigurationBuilder {
        private final Set<Class<? extends Annotation>> annotations;
        private final Map<Class<?>, Class<?>> abstractions;
        private DependenciesRepository dependenciesRepository;
        private AbstractionsRepository abstractionsRepository;
        private ProvidersRepository providersRepository;
        private String packageToScan;
        private boolean waitUntilCompletion;

        public DependencyInjectorConfigurationBuilder() {
            this.dependenciesRepository = new InMemoryDependenciesRepository();
            this.abstractionsRepository = new InMemoryAbstractionsRepository();
            this.providersRepository = new InMemoryProvidersRepository();
            this.abstractions = new ConcurrentHashMap<>();
            this.annotations = new HashSet<>(Arrays.asList(CommandHandler.class, Component.class, Configuration.class,
                    EventHandler.class, QueryHandler.class, Repository.class, Service.class, UseCase.class, Controller.class
            ));
        }

        public DependencyInjectorConfiguration build() {
            return new DependencyInjectorConfiguration(this.annotations, this.abstractions, this.dependenciesRepository,
                    this.abstractionsRepository, this.providersRepository, this.packageToScan, this.waitUntilCompletion);
        }

        public DependencyInjectorConfigurationBuilder waitUntilCompletion() {
            this.waitUntilCompletion = true;
            return this;
        }

        public DependencyInjectorConfigurationBuilder providers(ProvidersRepository providersRepository) {
            this.providersRepository = providersRepository;
            return this;
        }

        public DependencyInjectorConfigurationBuilder abstractions(@NonNull Map<Class<?>, Class<?>> abstractions) {
            this.abstractions.putAll(abstractions);
            return this;
        }

        public DependencyInjectorConfigurationBuilder abstractionsRepository(@NonNull AbstractionsRepository abstractionsRepository) {
            this.abstractionsRepository = abstractionsRepository;
            return this;
        }

        public DependencyInjectorConfigurationBuilder dependenciesRepository(@NonNull DependenciesRepository dependenciesRepository) {
            this.dependenciesRepository = dependenciesRepository;
            return this;
        }

        public DependencyInjectorConfigurationBuilder packageToScan(@NonNull String packageToScan) {
            this.packageToScan = packageToScan;
            return this;
        }

        @SafeVarargs
        public final DependencyInjectorConfigurationBuilder customAnnotations(@NonNull Class<? extends Annotation>... annotations) {
            this.annotations.addAll(Arrays.asList(annotations));
            return this;
        }

        public final DependencyInjectorConfigurationBuilder customAnnotations(@NonNull List<Class<? extends Annotation>> annotations) {
            this.annotations.addAll(annotations);
            return this;
        }
    }
}
