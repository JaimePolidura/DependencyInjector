package es.dependencyinjector.exceptions;

public final class DuplicatedImplementation extends Exception{
    public DuplicatedImplementation(String message, Object... args) {
        this (String.format(message, args));
    }

    public DuplicatedImplementation(String message) {
        super(message);
    }

}
