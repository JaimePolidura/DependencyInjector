package es.dependencyinjector.utils;

@FunctionalInterface
public interface CheckedRunnable {
    void run() throws Exception;
}
