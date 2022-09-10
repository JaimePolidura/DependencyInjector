package es.jaime.test.simple;

import es.dependencyinjector.dependencies.annotations.Service;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public final class ClassASim {
    private final ClassBSim b;
    private final ClassCSim c;
}
