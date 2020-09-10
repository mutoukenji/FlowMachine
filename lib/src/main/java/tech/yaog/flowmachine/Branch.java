package tech.yaog.flowmachine;

import java.util.ArrayList;
import java.util.List;

public class Branch<T, E> extends State<T, E>{

    private List<T> nextStates = new ArrayList<>();
    protected BranchCallback<T, E> callback;

    public Branch(T id) {
        super(id);
    }

    public Branch<T, E> branch(T nextState) {
        nextStates.add(nextState);
        return this;
    }

    public void setCallback(BranchCallback<T, E> callback) {
        this.callback = callback;
    }

    @Override
    void entry() {
        super.entry();
        if (callback != null) {
            callback.goNext(this, nextStates);
        }
    }

    public interface BranchCallback<T, E> {
        void goNext(State<T, E> currentState, List<T> states);
    }
}
