package tech.yaog.flowmachine;

public interface Handler<T,E> {
    void handle(State<T, E> state, Event<E> event);
}
