package es.jaime.test.conditions.simple;

import es.dependencyinjector.conditions.conditionalonpropery.ConditionalOnProperty;
import es.dependencyinjector.dependencies.annotations.Service;

@Service
@ConditionalOnProperty(key = "2", havingValue = "h")
public final class ServiceCond1 {
}
