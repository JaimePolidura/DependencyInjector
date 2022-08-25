package es.dependencyinjector.exceptions;

public final class UnknownDependency extends Exception{
    public UnknownDependency(String message, Object... args) {
        this (String.format(message, args));
    }

    public UnknownDependency(String message) {
        super(message);
    }
}
