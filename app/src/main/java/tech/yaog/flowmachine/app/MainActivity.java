package tech.yaog.flowmachine.app;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;

import tech.yaog.flowmachine.Branch;
import tech.yaog.flowmachine.Event;
import tech.yaog.flowmachine.FlowMachine;
import tech.yaog.flowmachine.Join;
import tech.yaog.flowmachine.OnEntry;
import tech.yaog.flowmachine.OnExit;
import tech.yaog.flowmachine.State;

public class MainActivity extends AppCompatActivity {

    enum ST {
        Start,
        St1,
        Branch1,
        St21,
        St22,
        Join1,
        St3
    }

    enum EV {
        Go1,
        Go2,
        Go21,
        Go22
    }

    private FlowMachine<ST, EV> fm;

    private OnEntry<ST, EV> entryHandler = new OnEntry<ST, EV>() {
        @Override
        public void onEntry(State<ST, EV> state) {
            Log.d("Flow", "Entry " + state.getId().name());
            final TextView view = stViewMap.get(state.getId());
            if (view != null) {
                view.post(new Runnable() {
                    @Override
                    public void run() {
                        view.setBackgroundColor(Color.GREEN);
                    }
                });
            }
        }
    };

    private OnExit<ST, EV> exitHandler = new OnExit<ST, EV>() {
        @Override
        public void onExit(State<ST, EV> state) {
            Log.d("Flow", "Exit " + state.getId().name());
            final TextView view = stViewMap.get(state.getId());
            if (view != null) {
                view.post(new Runnable() {
                    @Override
                    public void run() {
                        view.setBackgroundColor(Color.TRANSPARENT);
                    }
                });
            }
        }
    };

    TextView st1, st21, st22, st3;

    Map<ST, TextView> stViewMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        st1 = findViewById(R.id.st1);
        st21 = findViewById(R.id.st21);
        st22 = findViewById(R.id.st22);
        st3 = findViewById(R.id.st3);

        stViewMap.put(ST.St1, st1);
        stViewMap.put(ST.St21, st21);
        stViewMap.put(ST.St22, st22);
        stViewMap.put(ST.St3, st3);

        fm = new FlowMachine<ST, EV>()
                .initState(
                        new State<ST, EV>(ST.Start)
                                .onEntry(entryHandler)
                                .onExit(exitHandler)
                                .onEvent(EV.Go1, ST.St1)
                )
                .state(
                        new State<ST, EV>(ST.St1)
                                .onEntry(entryHandler)
                                .onExit(exitHandler)
                                .onEvent(EV.Go2, ST.Branch1)
                )
                .state(
                        new Branch<ST, EV>(ST.Branch1)
                                .branch(ST.St21)
                                .branch(ST.St22)
                )
                .state(
                        new State<ST, EV>(ST.St21)
                                .onEntry(entryHandler)
                                .onExit(exitHandler)
                                .onEvent(EV.Go21, ST.Join1)
                )
                .state(
                        new State<ST, EV>(ST.St22)
                                .onEntry(entryHandler)
                                .onExit(exitHandler)
                                .onEvent(EV.Go22, ST.Join1)
                )
                .state(
                        new Join<ST, EV>(ST.Join1, 2)
                                .nextState(ST.St3)
                )
                .state(
                        new State<ST, EV>(ST.St3)
                                .onEntry(entryHandler)
                                .onExit(exitHandler)
                )
        ;

        fm.start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                EV[] evs = {EV.Go1, EV.Go2, EV.Go21, EV.Go22};
                for (int i = 0; i < 4; i++) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        return;
                    }
                    fm.event(new Event<EV>(evs[i]));
                }
            }
        }).start();
    }
}