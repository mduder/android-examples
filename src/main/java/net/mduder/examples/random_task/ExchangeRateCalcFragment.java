package net.mduder.examples.random_task;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import de.greenrobot.event.EventBus;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;

/**
 * https://openexchangerates.org/documentation
 */
public class ExchangeRateCalcFragment extends Fragment {
    private enum RequestState { NOT_SENT, PENDING, RECEIVED, FAILED }

    private RequestState requestState;
    private ArrayList<String> countryArray;
    private ArrayList<Double> rateArray;
    private String countryBase;
    private String countryTarget;
    private DecimalFormat decimalFormat;
    private ColorStateList defaultTextViewColor;

    public static ExchangeRateCalcFragment newInstance () {
        return new ExchangeRateCalcFragment();
    }

    public ExchangeRateCalcFragment () {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestState = RequestState.NOT_SENT;
        countryArray = new ArrayList<>();
        rateArray = new ArrayList<>();
        decimalFormat = new DecimalFormat("#.###");
        decimalFormat.setRoundingMode(RoundingMode.HALF_EVEN);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        String[] countryList;
        switch (requestState) {
            case NOT_SENT:
                countryList = new String[]{"INIT"};
                break;
            case PENDING:
                countryList = new String[]{"PENDING"};
                break;
            case RECEIVED:
                countryList = countryArray.toArray(new String[countryArray.size()]);
                break;
            case FAILED:
            default:
                countryList = new String[]{"UNAVAILABLE"};
                break;
        }
        View view = inflater.inflate(R.layout.fragment_exchange_rate_calc, container, false);
        /**
         * Text Fields not implicitly updated with spinner update since spinner
         * selection listeners have not yet been attached
         */
        updateSpinners(view, countryList);

        AdapterView.OnItemSelectedListener countrySelectedListener =
                new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> selected, View view,
                                       int position, long id) {
                if (requestState != RequestState.RECEIVED) {
                    return;
                }

                if (selected.getId() == R.id.spinner_exchange_rate_base) {
                    countryBase = countryArray.get(position);
                } else if (selected.getId() == R.id.spinner_exchange_rate_target) {
                    countryTarget = countryArray.get(position);
                }
                updateTextFields(getView());
            }

            @Override
            public void onNothingSelected(AdapterView<?> selected) {
                Log.d(getClass().getSimpleName(), "cleared");
            }
        };

        Spinner spinner = (Spinner)view.findViewById(R.id.spinner_exchange_rate_base);
        spinner.setOnItemSelectedListener(countrySelectedListener);
        spinner = (Spinner)view.findViewById(R.id.spinner_exchange_rate_target);
        spinner.setOnItemSelectedListener(countrySelectedListener);

        TextView.OnEditorActionListener onEditorActionListener =
                new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE &&
                        requestState == RequestState.RECEIVED) {
                    updateTextFields(getView());
                }
                return false;
            }
        };
        EditText editText = (EditText)view.findViewById(R.id.edittext_exchange_rate_base_quantity);
        editText.setText("1.000");
        editText.setOnEditorActionListener(onEditorActionListener);
        editText.addTextChangedListener(new CurrencyTextWatcher(editText));

        TextView textView = (TextView)view.findViewById(R.id.textview_exchange_rate_results);
        defaultTextViewColor = textView.getTextColors();
        updateTextFields(view);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setRetainInstance(true);
        if (requestState != RequestState.NOT_SENT) {
            return;
        }

        requestState = RequestState.PENDING;
        String[] countryList = new String[]{"PENDING"};
        /* Spinners will update text fields */
        updateSpinners(getView(), countryList);
        EventBus.getDefault().post(new EventBusMessage(
                EventBusMessage.MessageType.getDataExchangeRate));
    }

    public void setDataExchangeRate (LinkedHashMap<String, Object> dataExchangeRate) {
        if (dataExchangeRate.get("error") != null) {
            requestState = RequestState.FAILED;
            int errorCode = (int)dataExchangeRate.get("status");
            Log.d(getClass().getSimpleName(),
                    "exchange rate retrieval error: " + String.valueOf(errorCode));
            View view = getView();
            updateSpinners(view, new String[]{"UNAVAILABLE"});
            updateTextFields(view);
            return;
        }

        LinkedHashMap<String, Object> conversionRates =
                (LinkedHashMap<String, Object>)dataExchangeRate.get("rates");
        for (LinkedHashMap.Entry<String, Object> entry : conversionRates.entrySet()) {
            Object val = entry.getValue();
            String country = entry.getKey();
            Double rate;
            if (val instanceof Integer) {
                rate = ((Integer)val).doubleValue();
            } else if (val instanceof Double) {
                rate = (Double)val;
            } else {
                Log.d(getClass().getSimpleName(), "unknown type: " + val.getClass().toString());
                return;
            }
            countryArray.add(country);
            rateArray.add(rate);
        }

        countryBase = "USD";
        countryTarget = "USD";
        String[] countryList = countryArray.toArray(new String[countryArray.size()]);
        requestState = RequestState.RECEIVED;
        /* Spinners will update text fields */
        updateSpinners(getView(), countryList);
    }

    private void updateSpinners (View view, String[] countries) {
        ArrayAdapter<String> countryAdapter = new ArrayAdapter<>(
                getActivity(), android.R.layout.simple_spinner_item, countries);
        countryAdapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item);
        Spinner spinner = (Spinner)view.findViewById(R.id.spinner_exchange_rate_base);
        spinner.setAdapter(countryAdapter);
        if (requestState == RequestState.RECEIVED) {
            spinner.setSelection(countryArray.indexOf(countryBase));
        }
        spinner = (Spinner)view.findViewById(R.id.spinner_exchange_rate_target);
        spinner.setAdapter(countryAdapter);
        if (requestState == RequestState.RECEIVED) {
            spinner.setSelection(countryArray.indexOf(countryTarget));
        }
    }

    private void updateTextFields(View view) {
        TextView textView;
        String status;
        String exchangeValue;

        switch (requestState) {
            case NOT_SENT:
                status = "Initializing";
                exchangeValue = "Not Applicable";
                break;
            case PENDING:
                status = "Retrieving exchange rate data";
                exchangeValue = "Not Applicable";
                break;
            case RECEIVED:
                status = "Select base and target country codes";
                exchangeValue = "1.000";
                break;
            case FAILED:
            default:
                status = "Exchange rate data unavailable";
                exchangeValue = "Not Applicable";
                break;
        }
        textView = (TextView)view.findViewById(R.id.textview_exchange_rate_desc);
        textView.setText(status);
        if (requestState != RequestState.RECEIVED) {
            textView = (TextView)view.findViewById(R.id.textview_exchange_rate_results);
            textView.setText(exchangeValue);
            return;
        }

        textView = (TextView)view.findViewById(R.id.textview_exchange_rate_results);
        EditText editText = (EditText)view.findViewById(R.id.edittext_exchange_rate_base_quantity);
        String multiplierString = editText.getText().toString();
        if (multiplierString.equals("")) {
            textView.setTextColor(Color.RED);
            textView.setText("Invalid Base Rate field");
            return;
        }
        textView.setTextColor(defaultTextViewColor);
        textView.setText("Calculation Results");

        Double multiplier = Double.parseDouble(multiplierString);
        Double newRate = rateArray.get(countryArray.indexOf(countryTarget)) /
                rateArray.get(countryArray.indexOf(countryBase));
        textView = (TextView)view.findViewById(R.id.textview_exchange_rate_final_value);
        textView.setText(formatRateResult(multiplier * newRate));
    }

    private String formatRateResult (Double exchangeRate) {
        String resultString = decimalFormat.format(exchangeRate);
        int decimalLocation = resultString.indexOf(".");
        int stringLength = resultString.length();
        if (decimalLocation == -1) {
            return resultString + ".000";
        } else if (decimalLocation + 4 <= stringLength) {
            return resultString;
        }

        StringBuilder stringBuilder = new StringBuilder(resultString);
        for (; decimalLocation + 4 > stringLength; stringLength++) {
            stringBuilder.append("0");
        }
        return stringBuilder.toString();
    }
}
