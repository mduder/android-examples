package net.mduder.examples.random_task;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import de.greenrobot.event.EventBus;

/**
 * Display the main UI, and respond to button clicks by passing requests
 * to the main activity via EventBus messages.
 */
public class MainFragment extends Fragment implements View.OnClickListener {
    private CubeButton cube;

    /**
     * http://stackoverflow.com/questions/11421368/android-fragment-oncreateview-with-gestures
     * Gesture detection transforms the button into a cube surface which will display
     * the adjacent cube side relative to the swipe direction.
     */
    private final GestureDetector cubeGestureDetector = new GestureDetector(getActivity(),
            new GestureDetector.SimpleOnGestureListener() {

                @Override
                public boolean onFling(MotionEvent me1, MotionEvent me2,
                                       float velocityX, float velocityY) {
                    final int SWIPE_MIN_DISTANCE = 120;
                    final int SWIPE_THRESHOLD_VELOCITY = 200;
                    float distanceX = Math.abs(me1.getX() - me2.getX());
                    float distanceY = Math.abs(me1.getY() - me2.getY());

                    View view = getView();
                    if (view == null) {
                        return super.onFling(me1, me2, velocityX, velocityY);
                    }
                    Button button = (Button)view.findViewById(R.id.button_main_cube);

                    if (distanceX > distanceY) {
                        if (me1.getX() - me2.getX() > SWIPE_MIN_DISTANCE
                                && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                            cube.rotate(CubeButton.Direction.LEFT);
                        } else if (me2.getX() - me1.getX() > SWIPE_MIN_DISTANCE
                                && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                            cube.rotate(CubeButton.Direction.RIGHT);
                        }
                    } else {
                        if (me1.getY() - me2.getY() > SWIPE_MIN_DISTANCE
                                && Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY) {
                            cube.rotate(CubeButton.Direction.UP);
                        } else if (me2.getY() - me1.getY() > SWIPE_MIN_DISTANCE
                                && Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY) {
                            cube.rotate(CubeButton.Direction.DOWN);
                        }
                    }

                    button.setText(Integer.toString(cube.getFaceValue()));
                    return super.onFling(me1, me2, velocityX, velocityY);
                }

                @Override
                public boolean onSingleTapConfirmed(MotionEvent event) {
                    EventBus.getDefault().post(new EventBusMessage(
                            EventBusMessage.MessageType.launchFragmentNumberDetails));
                    return super.onSingleTapConfirmed(event);
                }
            });


    public static MainFragment newInstance() {
        return new MainFragment();
    }

    public MainFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        cube = new CubeButton();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        view.findViewById(R.id.button_main_stop_watch).setOnClickListener(this);
        view.findViewById(R.id.button_main_mortgage_loan_calc).setOnClickListener(this);
        view.findViewById(R.id.button_main_exchange_rate_calc).setOnClickListener(this);
        Button button = (Button)view.findViewById(R.id.button_main_cube);
        button.setText(String.valueOf(cube.getFaceValue()));
        button.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return cubeGestureDetector.onTouchEvent(event);
            }
        });
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public void onClick(View buttonView) {
        switch (buttonView.getId()) {
            case R.id.button_main_stop_watch:
                EventBus.getDefault().post(new EventBusMessage(
                        EventBusMessage.MessageType.launchFragmentStopWatch));
                break;
            case R.id.button_main_mortgage_loan_calc:
                EventBus.getDefault().post(new EventBusMessage(
                        EventBusMessage.MessageType.launchFragmentMortgageLoanCalc));
                break;
            case R.id.button_main_exchange_rate_calc:
                EventBus.getDefault().post(new EventBusMessage(
                        EventBusMessage.MessageType.launchFragmentExchangeRateCalc));
                break;
            default:
                Log.d(getClass().getSimpleName(), "onClick() - Unknown event ID");
                break;
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public int getCubeFaceValue () {
        return cube.getFaceValue();
    }
}
