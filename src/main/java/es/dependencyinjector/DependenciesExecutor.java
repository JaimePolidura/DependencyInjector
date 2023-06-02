package es.dependencyinjector;

import es.dependencyinjector.dependencies.DependenciesRepository;
import lombok.AllArgsConstructor;

import java.util.function.Consumer;
import java.util.function.Function;

@AllArgsConstructor
public final class DependenciesExecutor {
    private final DependenciesRepository dependenciesRepository;

    public <I, O> O execute(Class<I> clazz, Function<I, O> toExecute) {
        return toExecute.apply(dependenciesRepository.get(clazz));
    }

    public <I> void execute(Class<I> clazz, Consumer<I> toExecute) {
        toExecute.accept(dependenciesRepository.get(clazz));
    }
}
