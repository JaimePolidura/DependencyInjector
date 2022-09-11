package es.jaime.test.conditions.onproperty;

import es.dependencyinjector.conditions.conditionalonpropery.ConditionalOnProperty;
import es.dependencyinjector.dependencies.annotations.Service;

@Service
@ConditionalOnProperty(key = "1", havingValue = "a")
public final class Impl1Cond1 implements AbsCond1{
}
