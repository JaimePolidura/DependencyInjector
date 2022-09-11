package es.jaime.test.conditions.onproperty;

import es.dependencyinjector.conditions.conditionalonpropery.ConditionalOnProperty;
import es.dependencyinjector.dependencies.annotations.Service;

@Service
@ConditionalOnProperty(key = "1", havingValue = "abc")
public final class Impl2Cond1 implements AbsCond1{
}
