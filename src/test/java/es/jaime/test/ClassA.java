package es.jaime.test;

import es.jaimetruman.annotations.Service;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public final class ClassA {
    private final ClassB b;
    private final ClassC c;
}
