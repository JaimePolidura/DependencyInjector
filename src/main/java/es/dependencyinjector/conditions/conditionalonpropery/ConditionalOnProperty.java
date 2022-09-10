package es.dependencyinjector.conditions.conditionalonpropery;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface ConditionalOnProperty {
    String key();
    String havingValue();
}
