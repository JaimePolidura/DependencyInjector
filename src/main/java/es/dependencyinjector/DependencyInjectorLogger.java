package es.dependencyinjector;

import lombok.AllArgsConstructor;

import java.util.logging.Level;

@AllArgsConstructor
public final class DependencyInjectorLogger {
    private final DependencyInjectorConfiguration configuration;

    public void info(String mensaje, Object ...args) {
        if(configuration.isLogging()){
            configuration.getLogger().log(Level.INFO, String.format(mensaje, args));
        }
    }
}
