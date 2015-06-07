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

/**
 * Reference: http://www.intmath.com/money-math/3-math-of-house-buying.php
 */
public class MortgageLoanCalcFragment extends Fragment {
    private int principal;
    private double APR;
    private int paymentsRequired;
    private int paymentsMade;
    private ColorStateList defaultTextViewColor;
    private DecimalFormat decimalFormat;

    public static MortgageLoanCalcFragment newInstance() {
        return new MortgageLoanCalcFragment();
    }

    public MortgageLoanCalcFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        principal = 500000;
        APR = 4.25;
        paymentsRequired = 360;
        paymentsMade = 0;
        decimalFormat = new DecimalFormat("#.##");
        decimalFormat.setRoundingMode(RoundingMode.HALF_EVEN);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mortgage_loan_calc, container, false);

        TextView.OnEditorActionListener onEditorActionListener = new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    recalculateValues();
                }
                return false;
            }
        };

        EditText editText = (EditText)view.findViewById(R.id.edittext_mortgage_loan_principal);
        editText.setText(NumberFormat.getCurrencyInstance().format(principal).replace(".00", ""));
        editText.setOnEditorActionListener(onEditorActionListener);
        editText.addTextChangedListener(new DollarTextWatcher(editText));

        editText = (EditText)view.findViewById(R.id.edittext_mortgage_loan_interest_rate);
        editText.setText(String.valueOf(APR));
        editText.setOnEditorActionListener(onEditorActionListener);
        editText.addTextChangedListener(new PercentageTextWatcher(editText));

        editText = (EditText)view.findViewById(R.id.edittext_mortgage_loan_payments_required);
        editText.setText(String.valueOf(paymentsRequired));
        editText.setOnEditorActionListener(onEditorActionListener);
        editText.addTextChangedListener(new NumericTextWatcher(editText));

        editText = (EditText)view.findViewById(R.id.edittext_mortgage_loan_payments_made);
        editText.setText(String.valueOf(paymentsMade));
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
        recalculateValues();
    }

    /* Validate user input, then update fields and display appropriate results */
    private void recalculateValues() {
        /**
         * Conversion value will divide into the APR display value
         * to generate the monthly interest rate value for computation
         */
        final int APR_CONVERSION = 1200;
        View view = getView();
        if (view == null) {
            return;
        }

        EditText editText = (EditText)view.findViewById(R.id.edittext_mortgage_loan_principal);
        String resultString = editText.getText().toString();
        if (resultString.equals("")) {
            setLabelsInvalid(view, "Invalid Principal field");
            return;
        }
        principal = Integer.parseInt(editText.getText().toString().replaceAll("[$,.]", ""));

        editText = (EditText)view.findViewById(R.id.edittext_mortgage_loan_interest_rate);
        resultString = editText.getText().toString();
        if (resultString.equals("")) {
            setLabelsInvalid(view, "Invalid APR field");
            return;
        }
        APR = Double.parseDouble(editText.getText().toString());
        if (APR == 0.0) {
            setLabelsInvalid(view, "Invalid APR field");
            return;
        }

        editText = (EditText)view.findViewById(R.id.edittext_mortgage_loan_payments_required);
        resultString = editText.getText().toString();
        if (resultString.equals("") || resultString.equals("0")) {
            setLabelsInvalid(view, "Invalid Payments Required field");
            return;
        }
        paymentsRequired = Integer.parseInt(resultString);
        editText = (EditText)view.findViewById(R.id.edittext_mortgage_loan_payments_made);
        resultString = editText.getText().toString();
        if (resultString.equals("")) {
            setLabelsInvalid(view, "Invalid Payments Made field");
            return;
        }
        paymentsMade = Integer.parseInt(resultString);
        if (paymentsRequired < paymentsMade) {
            setLabelsInvalid(view, "Invalid Payments field combination");
            return;
        }

        TextView textView = (TextView) view.findViewById(R.id.textview_mortgage_loan_results);
        textView.setText("Calculation Results");
        textView.setTextColor(defaultTextViewColor);

        Double num = principal * (APR / APR_CONVERSION);
        Double den = 1.0 - Math.pow(1.0 + (APR / APR_CONVERSION), paymentsRequired * -1);
        resultString = decimalFormat.format(num / den);
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

    private String cashValueStringRepr(Double rawValue) {
        BigDecimal parsed = new BigDecimal(rawValue)
                .setScale(2, BigDecimal.ROUND_FLOOR);
        return NumberFormat.getCurrencyInstance().format(parsed);
    }
}
