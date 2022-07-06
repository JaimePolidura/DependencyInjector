package es.jaimetruman.utils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.util.*;

public final class ReflectionUtils {
    private ReflectionUtils () {}

    public static Optional<Constructor<?>> getSmallerConstructor(Class<?> classToGetConstructor){
        return Arrays.stream(classToGetConstructor.getConstructors())
                .min(Comparator.comparingInt(Constructor::getParameterCount));
    }

    public static boolean isAnnotatedWith(Class<?> classToCheck, Set<Class<? extends Annotation>> annotations){
        for (Annotation declaredAnnotation : classToCheck.getDeclaredAnnotations())
            for (Class<? extends Annotation> annotation : annotations)
                if(declaredAnnotation.annotationType().equals(annotation))
                    return true;

        return false;
    }
}
