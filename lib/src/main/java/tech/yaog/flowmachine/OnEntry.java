package tech.yaog.flowmachine;

public interface OnEntry<T, E> {
    void onEntry(State<T, E> state);
}
