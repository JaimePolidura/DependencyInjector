package es.jaime.test.conditions.conditional;

import es.dependencyinjector.conditions.conditionon.ConditionalOn;
import es.dependencyinjector.dependencies.annotations.Service;

@Service
@ConditionalOn(TrueFakeCondition.class)
public final class Service2Con {
}
