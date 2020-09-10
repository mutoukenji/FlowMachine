package tech.yaog.flowmachine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class State<T,E> {
    protected T id;
    protected boolean isActive;

    private Map<E, T> forwards = new HashMap<>();
    private Map<E, Handler<T,E>> handlers = new HashMap<>();
    private OnEntry<T, E> onEntryHandler = null;
    private OnExit<T, E> onExitHandler = null;

    public T getId() {
        return id;
    }

    public void setId(T id) {
        this.id = id;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public State(T id) {
        this.id = id;
    }

    public State<T, E> onEvent(E event, T nextState) {
        forwards.put(event, nextState);
        return this;
    }

    public State<T, E> onEvent(E event, T nextState, Handler<T, E> handler) {
        forwards.put(event, nextState);
        handlers.put(event, handler);
        return this;
    }

    public State<T, E> onEvent(E event, Handler<T, E> handler) {
        handlers.put(event, handler);
        return this;
    }

    public State<T, E> onEntry(OnEntry<T, E> onEntryHandler) {
        this.onEntryHandler = onEntryHandler;
        return this;
    }

    public State<T, E> onExit(OnExit<T, E> onExitHandler) {
        this.onExitHandler = onExitHandler;
        return this;
    }

    void event(Event<E> event) {
        Handler<T, E> handler = handlers.get(event.getId());
        if (handler != null) {
            handler.handle(this, event);
        }
    }

    List<E> getForwardEvent(T nextState) {
        List<E> events = new ArrayList<>();
        for (Map.Entry<E, T> forward : forwards.entrySet()) {
            if (forward.getValue().getClass() == nextState) {
                events.add(forward.getKey());
            }
        }
        return events;
    }

    T getForward(E event) {
        return forwards.get(event);
    }

    void entry() {
        isActive = true;
        if (onEntryHandler != null) {
            onEntryHandler.onEntry(this);
        }
    }

    void exit() {
        isActive = false;
        if (onExitHandler != null) {
            onExitHandler.onExit(this);
        }
    }
}
