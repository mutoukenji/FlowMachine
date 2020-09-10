package tech.yaog.flowmachine;

public interface OnExit<T, E> {
    void onExit(State<T, E> state);
}
