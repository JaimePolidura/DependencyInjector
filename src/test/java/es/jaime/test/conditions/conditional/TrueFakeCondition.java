package es.jaime.test.conditions.conditional;

import es.dependencyinjector.conditions.conditionon.Condition;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public final class TrueFakeCondition implements Condition {
    private final Service1Con service1Con;

    @Override
    public boolean test(Class<?> dependency) {
        return service1Con.get() == 1;
    }
}
