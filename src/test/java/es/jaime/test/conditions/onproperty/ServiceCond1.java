package es.jaime.test.conditions.onproperty;

import es.dependencyinjector.conditions.conditionalonpropery.ConditionalOnProperty;
import es.dependencyinjector.dependencies.annotations.Service;

@Service
@ConditionalOnProperty(key = "2", havingValue = "h")
public final class ServiceCond1 {
}
