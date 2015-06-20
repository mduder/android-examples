package net.mduder.examples.random_task;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.HashSet;

/**
 * This fragment represents the UI for the mortgage loan calculator.
 * Reference: http://www.intmath.com/money-math/3-math-of-house-buying.php
 */
public class MortgageLoanCalcFragment extends Fragment {
    enum TextField { PRINCIPAL, APR, PMT_REQ, PMT_MADE }

    private int principal;
    private double APR;
    private int paymentsRequired;
    private int paymentsMade;
    private HashSet<TextField> invalidFields;
    private ColorStateList defaultTextViewColor;
    private DecimalFormat decimalFormat;

    public static MortgageLoanCalcFragment newInstance() {
        return new MortgageLoanCalcFragment();
    }

    public MortgageLoanCalcFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            return;
        }

        decimalFormat = new DecimalFormat("#.##");
        decimalFormat.setRoundingMode(RoundingMode.HALF_EVEN);
        invalidFields = new HashSet<>();
        for (TextField field : TextField.values()) {
            invalidFields.add(field);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mortgage_loan_calc, container, false);

        /**
         * Since the user can modify any number of input fields before sending Done
         * to the app, all fields must be validated after every pass.  For this reason,
         * the textView and actionID specific to this event is not accessed via
         * the method arguments.
         */
        TextView.OnEditorActionListener onEditorActionListener =
                new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent event) {
                if (actionId != EditorInfo.IME_ACTION_DONE) {
                    return false;
                } else if (textView == null) {
                    return false;
                }

                TextField badField = null;
                String fieldString = "";
                View globalView = getView();
                if (globalView == null) {
                    return false;
                }

                for (TextField field : TextField.values()) {
                    if (field == TextField.PRINCIPAL) {
                        fieldString = ((EditText)globalView
                                .findViewById(R.id.edittext_mortgage_loan_principal))
                                .getText().toString();
                        if (!(fieldString.equals(""))) {
                            principal = Integer.parseInt(fieldString.replaceAll("[$,.]", ""));
                        }
                    } else if (field == TextField.APR) {
                        fieldString = ((EditText)globalView
                                .findViewById(R.id.edittext_mortgage_loan_interest_rate))
                                .getText().toString();
                        if (!(fieldString.equals(""))) {
                            APR = Double.parseDouble(fieldString);
                        }
                    } else if (field == TextField.PMT_REQ) {
                        fieldString = ((EditText)globalView
                                .findViewById(R.id.edittext_mortgage_loan_payments_required))
                                .getText().toString();
                        if (!(fieldString.equals(""))) {
                            paymentsRequired = Integer.parseInt(fieldString);
                        }
                    } else if (field == TextField.PMT_MADE) {
                        fieldString = ((EditText)globalView
                                .findViewById(R.id.edittext_mortgage_loan_payments_made))
                                .getText().toString();
                        if (!(fieldString.equals(""))) {
                            paymentsMade = Integer.parseInt(fieldString);
                        }
                    }
                    if (!(fieldString.equals(""))) {
                        invalidFields.remove(field);
                    } else {
                        invalidFields.add(field);
                        badField = field;
                    }
                }

                if (badField == null) {
                    recalculateValues(globalView);
                } else {
                    if (invalidFields.size() > 1) {
                        setLabelsInvalid(globalView, fieldErrorString(null));
                    } else {
                        setLabelsInvalid(globalView, fieldErrorString(badField));
                    }
                }
                return false;
            }
        };

        EditText editText = (EditText)view.findViewById(R.id.edittext_mortgage_loan_principal);
        if (!(invalidFields.contains(TextField.PRINCIPAL))) {
            editText.setText(NumberFormat.getCurrencyInstance()
                    .format(principal).replace(".00", ""));
        }
        editText.setOnEditorActionListener(onEditorActionListener);
        editText.addTextChangedListener(new DollarTextWatcher(editText));

        editText = (EditText)view.findViewById(R.id.edittext_mortgage_loan_interest_rate);
        if (!(invalidFields.contains(TextField.APR))) {
            editText.setText(String.valueOf(APR));
        }
        editText.setOnEditorActionListener(onEditorActionListener);
        editText.addTextChangedListener(new PercentageTextWatcher(editText));

        editText = (EditText)view.findViewById(R.id.edittext_mortgage_loan_payments_required);
        if (!(invalidFields.contains(TextField.PMT_REQ))) {
            editText.setText(String.valueOf(paymentsRequired));
        }
        editText.setOnEditorActionListener(onEditorActionListener);
        editText.addTextChangedListener(new NumericTextWatcher(editText));

        editText = (EditText)view.findViewById(R.id.edittext_mortgage_loan_payments_made);
        if (!(invalidFields.contains(TextField.PMT_MADE))) {
            editText.setText(String.valueOf(paymentsMade));
        }
        editText.setOnEditorActionListener(onEditorActionListener);
        editText.addTextChangedListener(new NumericTextWatcher(editText));

        TextView textView = (TextView)view.findViewById(R.id.textview_mortgage_loan_results);
        defaultTextViewColor = textView.getTextColors();
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setRetainInstance(true);
        recalculateValues(getView());
    }

    /**
     * Update output fields and display appropriate results
     * Any errors arising from a bad field combination should be caught here,
     * as the onEditorActionListener shall only validate individual fields.
     */
    private void recalculateValues(View view) {
        /**
         * Conversion value will divide into the APR display value
         * to generate the monthly interest rate value for computation
         */
        final int APR_CONVERSION = 1200;
        if (view == null) {
            return;
        } else if (invalidFields.size() > 0) {
            setLabelsInvalid(view, "Please provide input for calculation");
            return;
        } else if (paymentsMade > paymentsRequired) {
            setLabelsInvalid(view, "Too many payments made");
            return;
        }

        TextView textView = (TextView) view.findViewById(R.id.textview_mortgage_loan_results);
        textView.setText("Calculation Results");
        textView.setTextColor(defaultTextViewColor);

        Double num = principal * (APR / APR_CONVERSION);
        Double den = 1.0 - Math.pow(1.0 + (APR / APR_CONVERSION), paymentsRequired * -1);
        String resultString = decimalFormat.format(num / den);
        Double monthlyCost = Double.valueOf(resultString);
        resultString = decimalFormat.format(monthlyCost * paymentsRequired);
        Double totalCost = Double.valueOf(resultString);

        textView = (TextView)view.findViewById(R.id.textview_mortgage_loan_monthly_cost);
        textView.setText(cashValueStringRepr(monthlyCost));
        textView = (TextView)view.findViewById(R.id.textview_mortgage_loan_total_cost);
        textView.setText(cashValueStringRepr(totalCost));
        textView = (TextView)view.findViewById(R.id.textview_mortgage_loan_payments_remaining);
        textView.setText(String.valueOf(paymentsRequired - paymentsMade));

        num = (1.0 - Math.pow(1.0 + (APR / APR_CONVERSION), paymentsMade - paymentsRequired))
                * principal;
        den = 1.0 - Math.pow(1.0 + (APR / APR_CONVERSION), paymentsRequired * -1);
        resultString = decimalFormat.format(num / den);
        Double balanceRemaining = Double.valueOf(resultString);
        resultString = decimalFormat.format((double)principal - balanceRemaining);
        Double balancePaid = Double.valueOf(resultString);

        textView = (TextView)view.findViewById(R.id.textview_mortgage_loan_balance_remaining);
        textView.setText(cashValueStringRepr(balanceRemaining));
        textView = (TextView)view.findViewById(R.id.textview_mortgage_loan_balance_paid);
        textView.setText(cashValueStringRepr(balancePaid));

        Double interestPaid = ((totalCost / paymentsRequired) * paymentsMade)
                - (principal - balanceRemaining);
        textView = (TextView)view.findViewById(R.id.textview_mortgage_loan_interest_paid);
        textView.setText(cashValueStringRepr(interestPaid));
    }

    /* recalculateValues helper method */
    private String cashValueStringRepr(Double rawValue) {
        BigDecimal parsed = new BigDecimal(rawValue)
                .setScale(2, BigDecimal.ROUND_FLOOR);
        return NumberFormat.getCurrencyInstance().format(parsed);
    }

    /* Called when user provides invalid input */
    private void setLabelsInvalid (View view, String errorString) {
        TextView textView = (TextView)view.findViewById(R.id.textview_mortgage_loan_results);
        textView.setText(errorString);
        textView.setTextColor(Color.RED);
        textView = (TextView)view.findViewById(R.id.textview_mortgage_loan_total_cost);
        textView.setText("");
        textView = (TextView)view.findViewById(R.id.textview_mortgage_loan_monthly_cost);
        textView.setText("");
        textView = (TextView)view.findViewById(R.id.textview_mortgage_loan_payments_remaining);
        textView.setText("");
        textView = (TextView)view.findViewById(R.id.textview_mortgage_loan_balance_remaining);
        textView.setText("");
        textView = (TextView)view.findViewById(R.id.textview_mortgage_loan_balance_paid);
        textView.setText("");
        textView = (TextView)view.findViewById(R.id.textview_mortgage_loan_interest_paid);
        textView.setText("");
    }

    private String fieldErrorString(TextField missingField) {
        String errorString = "";

        if (missingField == null) {
            errorString = "Please provide input for calculation";
        } else {
            switch (missingField) {
                case PRINCIPAL:
                    errorString = "Missing Principal Amount";
                    break;
                case APR:
                    errorString = "Missing Percentage Rate";
                    break;
                case PMT_REQ:
                    errorString = "Missing Payments Required Count";
                    break;
                case PMT_MADE:
                    errorString = "Missing Payments Made Count";
                    break;
            }
        }

        return errorString;
    }
}
