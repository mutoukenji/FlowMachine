package tech.yaog.flowmachine;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 流式控制器.
 *
 * 基本设计
 * <ol>
 *    <li>一个起始态和一个（或多个）终止态</li>
 *    <li>可以从1个状态激发分叉处理，同时进入多个并行状态</li>
 *    <li>可以从多个状态汇合到1个状态</li>
 *    <li>状态、事件配置完成后，调用 start 方法开启控制器。此时会根据配置的状态及事件计算活动流，此后除非调用 interrupt 方法，否则不应修改状态与事件配置</li>
 *    <li>由于存在汇合、分叉处理，控制器中状态变化时不传递数据</li>
 * </ol>
 */
public class FlowMachine<T, E> implements Branch.BranchCallback<T, E>, Join.JoinCallback<T, E> {
    private T initStateId;
    private Map<T, State<T, E>> stateMap = new HashMap<>();
    private Logger logger = new LogcatLogger();

    public FlowMachine<T, E> logger(Logger logger) {
        this.logger = logger;
        return this;
    }

    public FlowMachine<T, E> initState(State<T, E> state) {
        initStateId = state.getId();
        stateMap.put(state.getId(), state);
        return this;
    }

    public FlowMachine<T, E> state(State<T, E> state) {
        stateMap.put(state.getId(), state);
        if (state instanceof Branch) {
            ((Branch<T, E>) state).setCallback(this);
        }
        if (state instanceof Join) {
            ((Join<T, E>) state).setCallback(this);
        }
        return this;
    }

    public void event(Event<E> event) {
        for (State<T, E> state : stateMap.values()) {
            if (state.isActive()) {
                state.event(event);
                logger.v("FM", state.id+"下触发"+event.id+"事件");
                T nextState = state.getForward(event.getId());
                if (nextState != null) {
                    goNext(state, nextState);
                }
            }
        }
    }

    public void start() {
        State<T, E> st = stateMap.get(initStateId);
        if (st != null) {
            st.entry();
            logger.v("FM", "进入"+st.id);
        }
    }

    public void interrupt() {
        for (State<T, E> state : stateMap.values()) {
            if (state.isActive()) {
                state.exit();
                logger.v("FM", "离开"+state.id);
            }
        }
    }

    @Override
    public void goNext(State<T, E> currentState, List<T> states) {
        currentState.exit();
        logger.v("FM", "离开"+currentState.id);
        for (T state : states) {
            State<T, E> st = stateMap.get(state);
            if (st != null) {
                st.entry();
                logger.v("FM", "进入"+state);
            }
        }
    }

    @Override
    public void goNext(State<T, E> currentState, T state) {
        currentState.exit();
        logger.v("FM", "离开"+currentState.id);
        State<T, E> st = stateMap.get(state);
        if (st != null) {
            st.entry();
            logger.v("FM", "进入"+state);
        }
    }
}
