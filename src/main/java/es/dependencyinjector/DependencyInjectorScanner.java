package es.dependencyinjector;

import es.dependencyinjector.annotations.Provider;
import es.dependencyinjector.exceptions.AnnotationsMissing;
import es.dependencyinjector.exceptions.UnknownDependency;
import es.dependencyinjector.repository.AbstractionsRepository;
import es.dependencyinjector.repository.DependenciesRepository;
import es.dependencyinjector.repository.DependencyProvider;
import es.dependencyinjector.repository.ProvidersRepository;
import es.dependencyinjector.utils.ReflectionUtils;
import org.reflections.Reflections;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

import static es.dependencyinjector.utils.ReflectionUtils.*;
import static es.dependencyinjector.utils.Utils.*;

public final class DependencyInjectorScanner {
    private final DependenciesRepository dependencies;
    private final AbstractionsRepository abstractionsRepository;
    private final ProvidersRepository providersRepository;
    private final DependencyInjectorConfiguration configuration;
    private final Reflections reflections;

    public DependencyInjectorScanner(DependencyInjectorConfiguration configuration) {
        this.configuration = configuration;
        this.dependencies = configuration.getDependenciesRepository();
        this.providersRepository = configuration.getProvidersRepository();
        this.abstractionsRepository = configuration.getAbstractionsRepository();
        this.reflections = new Reflections(new ConfigurationBuilder()
                .setUrls(ClasspathHelper.forPackage(configuration.getPackageToScan()))
                .setScanners(new TypeAnnotationsScanner(),
                        new SubTypesScanner(), new MethodAnnotationsScanner()));
    }

    public void start() {
        runCheckedOrTerminate(() -> {
            this.searchForProviders();
            this.searchForAbstractions();
            this.searchForClassesToInstantiate();
        });
    }

    private void searchForProviders() {
        this.reflections.getMethodsAnnotatedWith(Provider.class).stream()
                .map(method -> DependencyProvider.of(method.getDeclaringClass(), method.getReturnType(), method))
                .peek(provider -> runCheckedOrTerminate(() -> this.ensureProviderClassAnnotated(provider)))
                .forEach(this.providersRepository::save);
    }

    private void ensureProviderClassAnnotated(DependencyProvider dependencyProvider) throws AnnotationsMissing{
        if(!ReflectionUtils.isAnnotatedWith(dependencyProvider.getProviderClass(), this.configuration.getAnnotations()))
            throw new AnnotationsMissing("Found provider %s but its container class %s is not annotated", dependencyProvider.getDependencyClassProvided(),
                    dependencyProvider.getProviderClass());
    }

    private void searchForAbstractions() {
        this.getClassesAnnotated().stream()
                .filter(ReflectionUtils::isImplementation)
                .forEach(this::saveImplementation);
    }

    private void saveImplementation(Class<?> implementationClass) {
        List<Class<?>> abstractions = getAbstractions(implementationClass);

        for (Class<?> abstraction : abstractions) {
            boolean alreadyDeclaredInConfig = this.configuration.getAbstractions().containsKey(abstraction);

            runCheckedOrTerminate(() -> this.abstractionsRepository.add(
                    abstraction,
                    alreadyDeclaredInConfig ? this.configuration.getAbstractions().get(abstraction) : implementationClass)
            );
        }
    }

    private void searchForClassesToInstantiate() throws Exception {
        for (Class<?> classAnnotatedWith : this.getClassesAnnotated())
            instantiateClass(classAnnotatedWith);
    }

    private Object instantiateClass(Class<?> classAnnotatedWith) throws Exception {
        Optional<Constructor<?>> constructorOptional = getSmallestConstructor(classAnnotatedWith);
        boolean alreadyInstanced = this.dependencies.contains(classAnnotatedWith);
        boolean doestHaveEmptyConstructor = constructorOptional.isPresent();

        if (doestHaveEmptyConstructor && !alreadyInstanced) {
            Constructor<?> constructor = constructorOptional.get();
            this.ensureAllParametersAreFound(constructor.getParameterTypes());
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

    private void ensureAllParametersAreFound(Class<?>[] parameterTypes) throws UnknownDependency {
        for (Class<?> parameterType : parameterTypes) {
            boolean notAnnotated = !isAnnotatedWith(parameterType, this.configuration.getAnnotations());
            boolean isAbstraction = isAbstraction(parameterType);
            boolean implementationNotFound = !this.abstractionsRepository.contains(parameterType);
            boolean notProvided = !this.providersRepository.findByDependencyClassProvided(parameterType).isPresent();

            if(isAbstraction && implementationNotFound)
                throw new UnknownDependency("Implementation not found for %s, it may not be annotated", parameterType.getName());
            if((!isAbstraction && notAnnotated) && notProvided)
                throw new UnknownDependency("Unknown dependency type %s. Make sure it is annotated", parameterType.getName());
        }
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
