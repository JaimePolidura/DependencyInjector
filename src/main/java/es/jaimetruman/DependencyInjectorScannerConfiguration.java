package es.jaimetruman;

import es.jaimetruman.annotations.*;
import lombok.Getter;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public abstract class DependencyInjectorScannerConfiguration {
    @Getter
    private final Set<Class<? extends Annotation>> usingAnnotations;
    @Getter
    private final Map<Class<?>, Class<?>> abstractions;

    public DependencyInjectorScannerConfiguration() {
        this.usingAnnotations = new HashSet<Class<? extends Annotation>>() {{
            add(CommandHandler.class);
            add(Component.class);
            add(Configuration.class);
            add(EventHandler.class);
            add(QueryHandler.class);
            add(Repository.class);
            add(Service.class);
            add(UseCase.class);
            add(Controller.class);
        }};
        this.usingAnnotations.addAll(extraAnnotations());
        this.abstractions = this.abstractions();
    }

    public abstract String packageToScan();

    public Set<Class<? extends Annotation>> extraAnnotations() {
        return new HashSet<>();
    }

    public Map<Class<?>, Class<?>> abstractions() {
        return new HashMap<>();
    }
}
