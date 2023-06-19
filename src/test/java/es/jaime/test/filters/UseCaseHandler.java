package es.jaime.test.filters;

public interface UseCaseHandler<T extends Parametros> {
    void handle(T params);
}
