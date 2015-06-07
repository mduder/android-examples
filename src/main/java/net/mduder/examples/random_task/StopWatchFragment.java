package net.mduder.examples.random_task;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * Reference: http://stackoverflow.com/questions/4597690/android-timer-how
 * Start, stop and reset occur after each click, in a cycle.
 */
public class StopWatchFragment extends Fragment implements View.OnClickListener {
    private enum WatchState { STOPPED, STARTED, RESET, PAUSED}

    private static final String ARG_START_TIME = "arg_start_time";
    private long timeStarted;
    private long timeStopped;
    private WatchState watchState;
    Button button;

    private Handler timerHandler;
    private Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            button.setText(displayTime(System.currentTimeMillis() - timeStarted));
            timerHandler.postDelayed(this, 500);
        }
    };

    public static StopWatchFragment newInstance() {
        return new StopWatchFragment();
    }

    public StopWatchFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        timerHandler = new Handler();
        watchState = WatchState.RESET;
        timeStarted = 0;
        timeStopped = 0;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_stop_watch, container, false);
        button = (Button)view.findViewById(R.id.button_stop_watch);
        button.setOnClickListener(this);
        button.setText(displayTime(timeStopped - timeStarted));
        if (watchState == WatchState.PAUSED) {
            timerHandler.postDelayed(timerRunnable, 0);
            watchState = WatchState.STARTED;
        }
        return view;
    }

    @Override
    public void onDestroyView() {
        if (watchState == WatchState.STARTED) {
            timerHandler.removeCallbacks(timerRunnable);
            timeStopped = System.currentTimeMillis();
            watchState = WatchState.PAUSED;
        }
        super.onDestroyView();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onClick(View buttonView) {
        switch (watchState) {
            case RESET:
                timerHandler.postDelayed(timerRunnable, 0);
                timeStarted = System.currentTimeMillis();
                watchState = WatchState.STARTED;
                break;
            case STARTED:
                timerHandler.removeCallbacks(timerRunnable);
                timeStopped = System.currentTimeMillis();
                watchState = WatchState.STOPPED;
                break;
            case STOPPED:
                timeStarted = 0;
                timeStopped = 0;
                button.setText(displayTime(0));
                watchState = WatchState.RESET;
                break;
        }
    }

    private String displayTime (long givenTime) {
        int seconds = (int) (givenTime / 1000);
        int minutes = seconds / 60;
        seconds = seconds % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }
}
