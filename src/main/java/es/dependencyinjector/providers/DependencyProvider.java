package es.dependencyinjector.providers;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.lang.reflect.Method;

@AllArgsConstructor
@ToString
public final class DependencyProvider {
    @Getter private final Class<?> providerClass;
    @Getter private final Class<?> dependencyClassProvided;
    @Getter private final Method providerMethod;

    public static DependencyProvider of(Class<?> providerClass, Class<?> dependencyClassProvided, Method providerMethod) {
        return new DependencyProvider(providerClass, dependencyClassProvided, providerMethod);
    }
}
