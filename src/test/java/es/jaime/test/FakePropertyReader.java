package es.jaime.test;

import es.dependencyinjector.conditions.conditionalonpropery.PropertyReader;

public final class FakePropertyReader implements PropertyReader {
    @Override
    public String get(String key) {
        if(key.equalsIgnoreCase("1")) return "a";
        if(key.equalsIgnoreCase("2")) return "b";
        return "c";
    }
}
