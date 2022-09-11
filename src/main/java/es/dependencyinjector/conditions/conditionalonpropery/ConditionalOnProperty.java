package es.dependencyinjector.conditions.conditionalonpropery;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface ConditionalOnProperty {
    String key();
    String havingValue();
}
