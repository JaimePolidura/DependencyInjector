package es.dependencyinjector.utils.exceptions;

public final class AnnotationsMissing extends Exception{
    public AnnotationsMissing(String message, Object... args) {
        this (String.format(message, args));
    }

    public AnnotationsMissing(String message) {
        super(message);
    }
}
