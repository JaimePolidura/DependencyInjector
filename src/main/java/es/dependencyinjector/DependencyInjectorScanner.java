package es.dependencyinjector;

import es.dependencyinjector.abstractions.AbstractionsSaver;
import es.dependencyinjector.abstractions.AbstractionsScanner;
import es.dependencyinjector.abstractions.AbstractionsService;
import es.dependencyinjector.conditions.DependencyConditionService;
import es.dependencyinjector.hooks.AfterAllScanned;
import es.dependencyinjector.providers.ProvidersScanner;
import es.dependencyinjector.abstractions.AbstractionsRepository;
import es.dependencyinjector.dependencies.DependenciesRepository;
import es.dependencyinjector.providers.DependencyProvider;
import es.dependencyinjector.providers.ProvidersRepository;
import es.dependencyinjector.utils.FakeExecutorService;
import lombok.SneakyThrows;
import org.reflections.Reflections;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

import static es.jaime.javaddd.application.utils.ExceptionUtils.*;
import static es.jaime.javaddd.application.utils.ReflectionUtils.*;

public final class DependencyInjectorScanner {
    private final DependenciesRepository dependencies;
    private final AbstractionsRepository abstractionsRepository;
    private final ProvidersRepository providersRepository;
    private final DependencyInjectorConfiguration configuration;
    private final Reflections reflections;
    private final ExecutorService executor;
    private final ProvidersScanner providersScanner;
    private final AbstractionsSaver abstractionsSaver;
    private final AbstractionsScanner abstractionsScanner;
    private final DependencyConditionService dependencyConditionService;
    private final DependencyInjectorLogger dependencyInjectorLogger;
    private final AbstractionsService abstractionsService;

    public DependencyInjectorScanner(DependencyInjectorConfiguration configuration) {
        this.configuration = configuration;
        this.dependencyInjectorLogger = new DependencyInjectorLogger(configuration);
        this.dependencies = configuration.getDependenciesRepository();
        this.providersRepository = configuration.getProvidersRepository();
        this.abstractionsRepository = configuration.getAbstractionsRepository();
        this.executor = configuration.isMultiThreadedScan() ?
                Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors()) :
                new FakeExecutorService();
        this.reflections = configuration.getReflections() == null ? new Reflections(new ConfigurationBuilder()
                .setUrls(ClasspathHelper.forPackage(configuration.getPackageToScan()))
                .setScanners(new TypeAnnotationsScanner(),
                        new SubTypesScanner(), new MethodAnnotationsScanner())) : configuration.getReflections();
        this.providersScanner = new ProvidersScanner(reflections, configuration, dependencyInjectorLogger);
        this.abstractionsScanner = new AbstractionsScanner(dependencyInjectorLogger, configuration, reflections);
        this.dependencyConditionService = new DependencyConditionService(configuration, this);
        this.abstractionsSaver = new AbstractionsSaver(configuration, dependencyConditionService, dependencyInjectorLogger);
        this.abstractionsService = new AbstractionsService(configuration);
    }

    @SneakyThrows
    public CountDownLatch start() {
        CountDownLatch loadingLatch = new CountDownLatch(4);

        runCheckedOrTerminate(() -> {
            searchForProviders(loadingLatch);
            searchForAbstractions(loadingLatch);

            instantiateProvidedClasses(loadingLatch);
            instantiateClasses(loadingLatch);
        });

        if(this.configuration.isWaitUntilCompletion()) {
            loadingLatch.await();
            this.callDependenciesAfterAllScannedHook();
        }else{
            this.executor.execute(() -> {
                awaitFor(loadingLatch);
                callDependenciesAfterAllScannedHook();
            });
        }

        return loadingLatch;
    }

    private void instantiateProvidedClasses(CountDownLatch loadingLatch) throws Exception {
        for (Class<?> providerClass : this.providersRepository.findAllProviderClasses()) {
            Object providerInstance = instantiateClass(providerClass, providerClass);
            dependencies.add(providerClass, providerInstance);

            for (DependencyProvider dependencyProvider : this.providersRepository.findByProviderClass(providerClass)
                    .get()) {
                Class<?> providedClass = dependencyProvider.getDependencyClassProvided();
                Object providedInstance = dependencyProvider.getProviderMethod().invoke(providerInstance);

                dependencies.add(providedClass, providedInstance);
            }
        }

        loadingLatch.countDown();
    }

    private void callDependenciesAfterAllScannedHook() {
        this.dependencies.filterByImplementsInterface(AfterAllScanned.class).forEach(hookListener -> {
            hookListener.afterAllScanned(dependencies);
        });
    }

    private void searchForProviders(CountDownLatch loadingLatch) {
        dependencyInjectorLogger.info("STARTING WITH PROVIDERS\n");

        this.providersScanner.scan().forEach(this.providersRepository::save);
        loadingLatch.countDown();
    }

    private void searchForAbstractions(CountDownLatch loadingLatch) {
        dependencyInjectorLogger.info("STARTING WITH ABSTRACTIONS\n");

        this.abstractionsScanner.scan().forEach(implementation -> {
            runCheckedOrTerminate(() -> this.abstractionsSaver.save(implementation));
        });
        loadingLatch.countDown();
    }

    @SneakyThrows
    private void instantiateClasses(CountDownLatch loadingLatch) {
        dependencyInjectorLogger.info("STARTING WITH CLASSES\n");

        Set<Class<?>> classesAnnotated = this.getClassesAnnotated();
        CountDownLatch countDownLatch = new CountDownLatch(this.configuration.isWaitUntilCompletion() ? classesAnnotated.size() : 1);

        dependencyInjectorLogger.info("%s classes to be instantiated", classesAnnotated.size());

        for (Class<?> classAnnotatedWith : classesAnnotated){
            this.executor.execute(() -> runCheckedOrTerminate(() -> {
                instantiateClass(classAnnotatedWith, classAnnotatedWith);
                countDownLatch.countDown();
            }));
        }

        countDownLatch.await();
        this.executor.shutdown();
        loadingLatch.countDown();;
    }

    public Object instantiateClass(Class<?> containerClass, Class<?> classAnnotatedWith) throws Exception {
        if(!dependencyConditionService.testAll(classAnnotatedWith)){
            return null;
        }

        dependencyInjectorLogger.info("Found class %s contained from %s class", classAnnotatedWith.getName(), containerClass.getName());

        Optional<Constructor<?>> constructorOptional = getSmallestConstructor(classAnnotatedWith);
        boolean alreadyInstanced = this.dependencies.contains(classAnnotatedWith);
        boolean doesntHaveEmptyConstructor = constructorOptional.isPresent();

        if (doesntHaveEmptyConstructor && !alreadyInstanced) {
            Constructor<?> constructor = constructorOptional.get();
            Class<?>[] parametersOfConstructor = constructor.getParameterTypes();
            Object[] instances = new Object[parametersOfConstructor.length];

            for (int i = 0; i < parametersOfConstructor.length; i++) {
                Class<?> parameterOfConstructor = parametersOfConstructor[i];
                boolean isAbstraction = abstractionsService.isAbstraction(parameterOfConstructor);

                //TODO Provided class might depend on another provided class
                instances[i] = instantiateClass(classAnnotatedWith, isAbstraction ?
                        getImplementationFromAbstraction(parameterOfConstructor) :
                        parameterOfConstructor
                );
            }

            Object newInstance = constructor.newInstance(instances);
            saveDependency(newInstance);

            return newInstance;
        }else if (doesntHaveEmptyConstructor){
            return dependencies.get(classAnnotatedWith);
        }else{ //Has an empty constructor
            return createInstanceAndSave(classAnnotatedWith);
        }
    }

    private Object createInstanceAndSave(Class<?> classAnnotatedWith) throws Exception {
        Object newInstance = classAnnotatedWith.newInstance();
        saveDependency(newInstance);

        return newInstance;
    }

    private void saveDependency(Object newInstance) {
        Class<?> instanceClass = newInstance.getClass();

        dependencies.add(newInstance.getClass(), newInstance);

        if(abstractionsService.isImplementation(instanceClass)){
            saveAbstractions(newInstance, instanceClass);
        }
    }

    private void saveAbstractions(Object newInstanceImplementation, Class<?> instanceClass) {
        for (Class<?> abstraction : abstractionsService.getAbstractions(instanceClass))
            this.dependencies.add(abstraction, newInstanceImplementation);
    }

    private Set<Class<?>> getClassesAnnotated() {
        return this.configuration.getAnnotations().stream()
                .map(this.reflections::getTypesAnnotatedWith)
                .flatMap(Collection::stream)
                .filter(dependency -> !configuration.getExcludedDependencies().contains(dependency))
                .collect(Collectors.toSet());
    }

    private Class<?> getImplementationFromAbstraction(Class<?> abstraction) throws Exception {
        boolean alreadyDeclaredInConfig = this.configuration.getAbstractions().containsKey(abstraction);

        return alreadyDeclaredInConfig ?
                this.configuration.getAbstractions().get(abstraction) :
                this.abstractionsRepository.get(abstraction);
    }

    @SneakyThrows
    private void awaitFor(CountDownLatch latch) {
        latch.await();
    }
}
