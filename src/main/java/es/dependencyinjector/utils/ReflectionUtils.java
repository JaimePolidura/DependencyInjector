package es.dependencyinjector.utils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.*;

public final class ReflectionUtils {
    private ReflectionUtils () {}

    public static Optional<Constructor<?>> getSmallestConstructor(Class<?> classToGetConstructor){
        return Arrays.stream(classToGetConstructor.getConstructors())
                .min(Comparator.comparingInt(Constructor::getParameterCount));
    }

    public static List<Class<?>> getAbstractions(Class<?> implementation) {
        Class<?> superClass = implementation.getSuperclass();
        Class<?>[] interfaces = implementation.getInterfaces();
        List<Class<?>> abstractions = new ArrayList<>(interfaces.length + 1);
        abstractions.addAll(Arrays.asList(interfaces));
        if(superClass != null) abstractions.add(superClass);

        return abstractions;
    }

    public static boolean isImplementation(Class<?> classToCheck) {
        Class<?> classExtends = classToCheck.getSuperclass();
        boolean extendsAbstraction = classExtends != null && isAbstraction(classExtends);
        boolean implementsInterface = classToCheck.getInterfaces().length > 0;

        return implementsInterface || extendsAbstraction;
    }

    public static boolean isAbstraction(Class<?> classToCheck) {
        return classToCheck.isInterface() || Modifier.isAbstract(classToCheck.getModifiers());
    }

    public static List<Annotation> findAnnotationsInClass(Class<?> toCheck) {
        return new ArrayList<>(Arrays.asList(toCheck.getDeclaredAnnotations()));
    }

    public static Optional<Annotation> findAnnotationIn(Class<?> classToCheck, Class<? extends Annotation> annotation) {
        return Arrays.stream(classToCheck.getDeclaredAnnotations())
                .filter(a -> a.annotationType().equals(annotation))
                .findFirst();
    }

    public static boolean isAnnotatedWith(Class<?> classToCheck, Set<Class<? extends Annotation>> annotations){
        for (Annotation declaredAnnotation : classToCheck.getDeclaredAnnotations())
            for (Class<? extends Annotation> annotation : annotations)
                if(declaredAnnotation.annotationType().equals(annotation))
                    return true;

        return false;
    }
}
