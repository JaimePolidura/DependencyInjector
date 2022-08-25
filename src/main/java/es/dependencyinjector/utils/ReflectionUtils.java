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

    public static Class<?> getAbstraction(Class<?> implementation) {
        Class<?> classExtends = implementation.getSuperclass();
        boolean superClassAbstraction = classExtends != null && isAbstraction(classExtends);

        return superClassAbstraction ? classExtends : implementation.getInterfaces()[0];
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

    public static boolean isAnnotatedWith(Class<?> classToCheck, Set<Class<? extends Annotation>> annotations){
        for (Annotation declaredAnnotation : classToCheck.getDeclaredAnnotations())
            for (Class<? extends Annotation> annotation : annotations)
                if(declaredAnnotation.annotationType().equals(annotation))
                    return true;

        return false;
    }
}
