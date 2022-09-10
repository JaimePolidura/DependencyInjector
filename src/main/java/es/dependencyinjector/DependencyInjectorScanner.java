package es.dependencyinjector;

import es.dependencyinjector.abstractions.AbstractionsSaver;
import es.dependencyinjector.abstractions.AbstractionsScanner;
import es.dependencyinjector.conditions.DependencyConditionService;
import es.dependencyinjector.providers.ProvidersScanner;
import es.dependencyinjector.abstractions.AbstractionsRepository;
import es.dependencyinjector.dependencies.DependenciesRepository;
import es.dependencyinjector.providers.DependencyProvider;
import es.dependencyinjector.providers.ProvidersRepository;
import lombok.SneakyThrows;
import org.reflections.Reflections;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

import java.lang.reflect.Constructor;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

import static es.dependencyinjector.utils.ReflectionUtils.*;
import static es.dependencyinjector.utils.Utils.*;

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

    public DependencyInjectorScanner(DependencyInjectorConfiguration configuration) {
        this.configuration = configuration;
        this.dependencies = configuration.getDependenciesRepository();
        this.providersRepository = configuration.getProvidersRepository();
        this.abstractionsRepository = configuration.getAbstractionsRepository();
        this.executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        this.reflections = new Reflections(new ConfigurationBuilder()
                .setUrls(ClasspathHelper.forPackage(configuration.getPackageToScan()))
                .setScanners(new TypeAnnotationsScanner(),
                        new SubTypesScanner(), new MethodAnnotationsScanner()));
        this.providersScanner = new ProvidersScanner(this.reflections, this.configuration);
        this.abstractionsScanner = new AbstractionsScanner(this.configuration, this.reflections);
        this.dependencyConditionService = new DependencyConditionService(this.configuration, this);
        this.abstractionsSaver = new AbstractionsSaver(this.configuration, this.dependencyConditionService);
    }

    @SneakyThrows
    public CountDownLatch start() {
        CountDownLatch loadingLatch = new CountDownLatch(3);

        runCheckedOrTerminate(() -> {
            this.searchForProviders(loadingLatch);
            this.searchForAbstractions(loadingLatch);
            this.searchForClassesToInstantiate(loadingLatch);
        });

        if(this.configuration.isWaitUntilCompletion()) loadingLatch.await();

        return loadingLatch;
    }

    private void searchForProviders(CountDownLatch loadingLatch) {
        this.providersScanner.scan().forEach(this.providersRepository::save);
        loadingLatch.countDown();
    }

    private void searchForAbstractions(CountDownLatch loadingLatch) {
        this.abstractionsScanner.scan().forEach(implementation -> {
            runCheckedOrTerminate(() -> this.abstractionsSaver.save(implementation));
        });
        loadingLatch.countDown();
    }

    @SneakyThrows
    private void searchForClassesToInstantiate(CountDownLatch loadingLatch) {
        Set<Class<?>> classesAnnotated = this.getClassesAnnotated();
        CountDownLatch countDownLatch = new CountDownLatch(this.configuration.isWaitUntilCompletion() ? classesAnnotated.size() : 1);

        for (Class<?> classAnnotatedWith : classesAnnotated){
            this.executor.execute(() -> runCheckedOrTerminate(() -> {
                instantiateClass(classAnnotatedWith);
                countDownLatch.countDown();
            }));
        }

        countDownLatch.await();
        this.executor.shutdown();
        loadingLatch.countDown();;
    }

    public Object instantiateClass(Class<?> classAnnotatedWith) throws Exception {
        Optional<Constructor<?>> constructorOptional = getSmallestConstructor(classAnnotatedWith);
        boolean alreadyInstanced = this.dependencies.contains(classAnnotatedWith);
        boolean doestHaveEmptyConstructor = constructorOptional.isPresent();

        if(!this.dependencyConditionService.testAll(classAnnotatedWith)){
            return null;
        }

        if (doestHaveEmptyConstructor && !alreadyInstanced) {
            Constructor<?> constructor = constructorOptional.get();
            Class<?>[] parametersOfConstructor = constructor.getParameterTypes();
            Object[] instances = new Object[parametersOfConstructor.length];

            for (int i = 0; i < parametersOfConstructor.length; i++) {
                Class<?> parameterOfConstructor = parametersOfConstructor[i];
                boolean isAbstraction = isAbstraction(parameterOfConstructor);

                instances[i] = instantiateClass(isAbstraction ?
                        this.getImplementationFromAbstraction(parameterOfConstructor) :
                        parameterOfConstructor
                );
            }

            Object newInstance = constructor.newInstance(instances);
            saveDependency(newInstance);

            return newInstance;
        }else {
            return alreadyInstanced ?
                    this.dependencies.get(classAnnotatedWith) :
                    createInstanceAndSave(classAnnotatedWith);
        }
    }

    private void saveDependency(Object newInstance) {
        Class<?> instanceClass = newInstance.getClass();
        boolean isImplementation = isImplementation(instanceClass);

        List<Class<?>> abstractions = getAbstractions(instanceClass);
        for (Class<?> abstraction : abstractions)
            this.dependencies.add(isImplementation ? abstraction : instanceClass, newInstance);

        this.providersRepository.findByProviderClass(instanceClass).ifPresent(dependencyProviders -> {
            for (DependencyProvider provider : dependencyProviders) {
                runCheckedOrTerminate(() -> {
                    Object instanceProvided = provider.getProviderMethod().invoke(newInstance);
                    this.dependencies.add(instanceProvided.getClass(), instanceProvided);
                });
            }
        });
    }

    private Object createInstanceAndSave(Class<?> classAnnotatedWith) throws Exception {
        Object newInstance = classAnnotatedWith.newInstance();
        this.saveDependency(newInstance);

        return newInstance;
    }

    private Set<Class<?>> getClassesAnnotated() {
        return this.configuration.getAnnotations().stream()
                .map(this.reflections::getTypesAnnotatedWith)
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());
    }

    private Class<?> getImplementationFromAbstraction(Class<?> abstraction) throws Exception {
        boolean alreadyDeclaredInConfig = this.configuration.getAbstractions().containsKey(abstraction);

        return alreadyDeclaredInConfig ?
                this.configuration.getAbstractions().get(abstraction) :
                this.abstractionsRepository.get(abstraction);
    }
}
