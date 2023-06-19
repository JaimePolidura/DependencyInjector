package es.dependencyinjector;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public final class DependencyInjectorLogger {
    private final DependencyInjectorConfiguration configuration;

    public void info(String mensaje, Object ...args) {
        if(configuration.isUseDebugLogging()){
            System.out.println(String.format(mensaje, args));
        }
    }
}
