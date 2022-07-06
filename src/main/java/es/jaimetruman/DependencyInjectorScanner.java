package es.jaimetruman;

import es.jaimetruman.exceptions.UnknownDependency;
import es.jaimetruman.utils.ReflectionUtils;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.util.Optional;
import java.util.Set;

public final class DependencyInjectorScanner {
    private final DependenciesRepository dependencies;
    private final DependencyInjectorScannerConfiguration configuration;
    private final Reflections reflections;

    public DependencyInjectorScanner(DependenciesRepository dependencies, DependencyInjectorScannerConfiguration configuration) {
        this.dependencies = dependencies;
        this.configuration = configuration;
        this.reflections = new Reflections(new ConfigurationBuilder()
                .setUrls(ClasspathHelper.forPackage(configuration.packageToScan()))
                .setScanners(new TypeAnnotationsScanner(),
                        new SubTypesScanner()));
    }

    public void start() throws Exception {
        Set<Class<? extends Annotation>> annotationsToScan = this.configuration.getUsingAnnotations();

        for (Class<? extends Annotation> annotationToScan : annotationsToScan) {
            Set<Class<?>> classesAnnotatedWith = this.reflections.getTypesAnnotatedWith(annotationToScan);

            for (Class<?> classAnnotatedWith : classesAnnotatedWith) {
                instantiateClass(classAnnotatedWith);
            }
        }
    }

    private Object instantiateClass(Class<?> classAnnotatedWith) throws Exception {
        Optional<Constructor<?>> constructorOptional = ReflectionUtils.getSmallerConstructor(classAnnotatedWith);
        boolean alreadyInstanced = this.dependencies.contains(classAnnotatedWith);
        boolean doestHaveEmptyConstructor = constructorOptional.isPresent();

        if (doestHaveEmptyConstructor && !alreadyInstanced) {
            Constructor<?> constructor = constructorOptional.get();
            this.ensureAllParametersAreAnnotated(constructor.getParameterTypes());
            Class<?>[] parametersOfConstructor = constructor.getParameterTypes();
            Object[] instances = new Object[parametersOfConstructor.length];

            for (int i = 0; i < parametersOfConstructor.length; i++) {
                Class<?> parameterOfConstructor = parametersOfConstructor[i];
                instances[i] = instantiateClass(parameterOfConstructor);
            }

            Object newInstance = constructor.newInstance(instances);
            this.dependencies.addIfNotContained(newInstance);

            return newInstance;

        } else {
            return alreadyInstanced ?
                    this.dependencies.get(classAnnotatedWith) :
                    createInstanceAndSave(classAnnotatedWith);
        }
    }

    private Object createInstanceAndSave(Class<?> classAnnotatedWith) throws Exception {
        Object instance = classAnnotatedWith.newInstance();
        this.dependencies.addIfNotContained(instance);
        return instance;
    }

    private void ensureAllParametersAreAnnotated(Class<?>[] parameterTypes) throws UnknownDependency {
        for (Class<?> parameterType : parameterTypes) {
            boolean annotated = ReflectionUtils.isAnnotatedWith(parameterType, this.configuration.getUsingAnnotations());

            if(!annotated)
                throw new UnknownDependency("Unknown dependency type %s. Make sure it is annotated", parameterType.getName());
        }
    }
}
