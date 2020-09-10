package tech.yaog.flowmachine;

public class Join<T, E> extends State<T, E> {

    protected int joinCnt;
    private int currentIn = 0;
    private  T nextState;
    protected JoinCallback<T, E> callback;

    public Join(T id, int joinCnt) {
        super(id);
        this.joinCnt = joinCnt;
    }

    public Join<T, E> nextState(T state) {
        this.nextState = state;
        return this;
    }

    public void setCallback(JoinCallback<T, E> callback) {
        this.callback = callback;
    }

    @Override
    void entry() {
        super.entry();
        currentIn++;
        if (currentIn >= joinCnt) {
            if (callback != null) {
                callback.goNext(this, nextState);
            }
        }
    }

    @Override
    void exit() {
        super.exit();
        currentIn = 0;
    }

    public interface JoinCallback<T, E> {
        void goNext(State<T, E> currentState, T state);
    }
}
