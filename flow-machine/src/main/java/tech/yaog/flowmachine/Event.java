package tech.yaog.flowmachine;

public class Event<E> {
    protected E id;
    protected Object[] data;

    public Event(E id, Object... data) {
        this.id = id;
        this.data = data;
    }

    public Object[] getData() {
        return data;
    }

    public E getId() {
        return id;
    }
}
