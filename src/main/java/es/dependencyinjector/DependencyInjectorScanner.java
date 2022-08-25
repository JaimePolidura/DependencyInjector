package es.dependencyinjector;

import es.dependencyinjector.exceptions.UnknownDependency;
import es.dependencyinjector.repository.AbstractionsRepository;
import es.dependencyinjector.repository.DependenciesRepository;
import es.dependencyinjector.utils.ReflectionUtils;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

import java.lang.reflect.Constructor;
import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static es.dependencyinjector.utils.ReflectionUtils.*;
import static es.dependencyinjector.utils.Utils.*;

public final class DependencyInjectorScanner {
    private final DependenciesRepository dependencies;
    private final AbstractionsRepository abstractionsRepository;
    private final DependencyInjectorConfiguration configuration;
    private final Reflections reflections;

    public DependencyInjectorScanner(DependencyInjectorConfiguration configuration) {
        this.configuration = configuration;
        this.dependencies = configuration.getDependenciesRepository();
        this.abstractionsRepository = configuration.getAbstractionsRepository();
        this.reflections = new Reflections(new ConfigurationBuilder()
                .setUrls(ClasspathHelper.forPackage(configuration.getPackageToScan()))
                .setScanners(new TypeAnnotationsScanner(),
                        new SubTypesScanner()));
    }

    public void start() {
        runCheckedOrTerminate(() -> {
            this.searchForAbstractions();
            this.searchForClassesToInstantiate();
        });
    }

    private void searchForAbstractions() {
        this.getClassesAnnotated().stream()
                .filter(ReflectionUtils::isImplementation)
                .forEach(this::saveImplementation);
    }

    private void saveImplementation(Class<?> implementationClass) {
        Class<?> abstractionClass = getAbstraction(implementationClass);
        boolean alreadyDeclaredInConfig = this.configuration.getAbstractions().containsKey(abstractionClass);

        runCheckedOrTerminate(() -> this.abstractionsRepository.add(
                abstractionClass,
                alreadyDeclaredInConfig ? this.configuration.getAbstractions().get(abstractionClass) : implementationClass)
        );
    }

    private void searchForClassesToInstantiate() throws Exception {
        for (Class<?> classAnnotatedWith : this.getClassesAnnotated()) {
            instantiateClass(classAnnotatedWith);
        }
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
        }else{
            return alreadyInstanced ?
                    this.dependencies.get(classAnnotatedWith) :
                    createInstanceAndSave(classAnnotatedWith);
        }
    }

    private void saveDependency(Object newInstance) {
        Class<?> instanceClass = newInstance.getClass();
        boolean isImplementation = isImplementation(instanceClass);

        this.dependencies.add(isImplementation ? getAbstraction(instanceClass) : instanceClass, newInstance);
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

            if(isAbstraction && implementationNotFound)
                throw new UnknownDependency("Implementation not found for %s, it may not be annotated", parameterType.getName());
            if(!isAbstraction && notAnnotated)
                throw new UnknownDependency("Unknown dependency type %s. Make sure it is annotated", parameterType.getName());
        }
    }

    private Set<Class<?>> getClassesAnnotated() {
        return this.configuration.getAnnotations().stream()
                .map(this.reflections::getTypesAnnotatedWith)
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());
    }

    private Class<?> getImplementationFromAbstraction(Class<?> abstraction) {
        boolean alreadyDeclaredInConfig = this.configuration.getAbstractions().containsKey(abstraction);

        return alreadyDeclaredInConfig ?
                this.configuration.getAbstractions().get(abstraction) :
                this.abstractionsRepository.get(abstraction);
    }
}
