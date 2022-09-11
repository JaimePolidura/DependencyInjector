package es.jaime.test.conditions.conditional;

import es.dependencyinjector.conditions.conditionon.Condition;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public final class FalseFakeCondition implements Condition {
    private final Service1Con service1Con;

    @Override
    public boolean test(Class<?> dependency) {
        return this.service1Con.get() != 1;
    }
}
