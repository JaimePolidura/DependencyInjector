package es.jaimetruman;

import es.jaimetruman.annotations.*;
import lombok.Getter;

import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.Set;

public abstract class DependencyInjectorScannerConfiguration {
    @Getter
    private final Set<Class<? extends Annotation>> usingAnnotations;

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
    }

    public abstract String packageToScan();

    public Set<Class<? extends Annotation>> extraAnnotations() {
        return new HashSet<>();
    }
}
