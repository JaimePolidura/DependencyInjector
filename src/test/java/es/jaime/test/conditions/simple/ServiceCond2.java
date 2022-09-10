package es.jaime.test.conditions.simple;

import es.dependencyinjector.conditions.conditionalonpropery.ConditionalOnProperty;
import es.dependencyinjector.dependencies.annotations.Service;

@Service
@ConditionalOnProperty(key = "1", havingValue = "a")
public final class ServiceCond2 {
}
