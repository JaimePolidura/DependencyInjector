package es.dependencyinjector;

import es.dependencyinjector.abstractions.AbstractionsRepository;
import es.dependencyinjector.abstractions.InMemoryAbstractionsRepository;
import es.dependencyinjector.conditions.*;
import es.dependencyinjector.conditions.conditionalonpropery.ConditionalOnProperty;
import es.dependencyinjector.conditions.conditionalonpropery.DependencyConditionalOnPropertyTester;
import es.dependencyinjector.conditions.conditionalonpropery.PropertyReader;
import es.dependencyinjector.conditions.conditionon.ConditionalOn;
import es.dependencyinjector.conditions.conditionon.DependencyConditionalOnTester;
import es.dependencyinjector.dependencies.annotations.*;
import es.dependencyinjector.providers.InMemoryProvidersRepository;
import es.dependencyinjector.providers.ProvidersRepository;
import es.dependencyinjector.dependencies.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import org.reflections.Reflections;

import java.lang.annotation.Annotation;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@AllArgsConstructor
public class DependencyInjectorConfiguration {
    @Getter private final Set<Class<? extends Annotation>> annotations;
    @Getter private final Map<Class<?>, Class<?>> abstractions;
    @Getter private final Set<Class<?>> excludedDependencies;
    @Getter private final DependenciesRepository dependenciesRepository;
    @Getter private final AbstractionsRepository abstractionsRepository;
    @Getter private final ProvidersRepository providersRepository;
    @Getter private final String packageToScan;
    @Getter private final boolean waitUntilCompletion;
    @Getter private final PropertyReader propertyReader;
    @Getter private final List<SupportedConditionAnnotation> conditionAnnotations;
    @Getter private final boolean logging;
    @Getter private final Logger logger;
    @Getter private final boolean multiThreadedScan;
    @Getter private final Set<Class<?>> excludedAbstractions;
    @Getter private Reflections reflections;

    public static DependencyInjectorConfigurationBuilder builder() {
        return new DependencyInjectorConfigurationBuilder();
    }

    public static class DependencyInjectorConfigurationBuilder {
        private final List<SupportedConditionAnnotation> conditionAnnotations;
        private final Set<Class<? extends Annotation>> annotations;
        private final Map<Class<?>, Class<?>> abstractions;
        private final Set<Class<?>> excludedAbstractions;
        private DependenciesRepository dependenciesRepository;
        private AbstractionsRepository abstractionsRepository;
        private ProvidersRepository providersRepository;
        private Set<Class<?>> excludedDependencies;
        private String packageToScan;
        private boolean waitUntilCompletion;
        private PropertyReader propertyReader;
        private boolean logging;
        private Logger logger;
        private boolean multiThreadedScan;
        private Reflections reflections;

        public DependencyInjectorConfigurationBuilder() {
            this.logging = false;
            this.multiThreadedScan = true;
            this.excludedAbstractions = new HashSet<>();
            this.dependenciesRepository = new InMemoryDependenciesRepository();
            this.abstractionsRepository = new InMemoryAbstractionsRepository();
            this.excludedDependencies = new HashSet<>();
            this.providersRepository = new InMemoryProvidersRepository();
            this.abstractions = new ConcurrentHashMap<>();
            this.conditionAnnotations = new ArrayList<>(Arrays.asList(
                    SupportedConditionAnnotation.from(ConditionalOnProperty.class, DependencyConditionalOnPropertyTester.class),
                    SupportedConditionAnnotation.from(ConditionalOn.class, DependencyConditionalOnTester.class)
            ));
            this.annotations = new HashSet<>(Arrays.asList(CommandHandler.class, Component.class, Configuration.class,
                    EventHandler.class, QueryHandler.class, Repository.class, Service.class, UseCase.class, Controller.class
            ));
        }

        public DependencyInjectorConfiguration build() {
            return new DependencyInjectorConfiguration(annotations, abstractions, excludedDependencies, dependenciesRepository,
                    abstractionsRepository, providersRepository, packageToScan, waitUntilCompletion,
                    propertyReader, conditionAnnotations, logging, logger, multiThreadedScan, excludedAbstractions, reflections);
        }

        public DependencyInjectorConfigurationBuilder reflections(Reflections reflections) {
            this.reflections = reflections;
            return this;
        }

        public DependencyInjectorConfigurationBuilder excludedAbstractions(Class<?> ...excludedAbstractions) {
            this.excludedAbstractions.addAll(Arrays.stream(excludedAbstractions).collect(Collectors.toSet()));
            return this;
        }

        public DependencyInjectorConfigurationBuilder excludedDependencies(Class<?> ...excludedDependencies) {
            this.excludedDependencies.addAll(Arrays.stream(excludedDependencies).collect(Collectors.toSet()));
            return this;
        }

        public DependencyInjectorConfigurationBuilder singleThreadedScan() {
            this.multiThreadedScan = false;
            return this;
        }

        public DependencyInjectorConfigurationBuilder multiThreadedScan() {
            this.multiThreadedScan = true;
            return this;
        }

        public DependencyInjectorConfigurationBuilder logging(Logger logger) {
            this.logging = true;
            this.logger = logger;
            return this;
        }

        public DependencyInjectorConfigurationBuilder propertyReader(PropertyReader reader) {
            this.propertyReader = reader;
            return this;
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
