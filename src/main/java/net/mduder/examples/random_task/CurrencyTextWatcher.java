package net.mduder.examples.random_task;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import java.lang.ref.WeakReference;

/**
 * http://stackoverflow.com/questions/5107901/better-way-to-format-currency-input-edittext
 * Text watcher used by exchange rate calculator fragment
 */
public class CurrencyTextWatcher implements TextWatcher {
    private final WeakReference<EditText> editTextWeakReference;

    public CurrencyTextWatcher(EditText editText) {
        editTextWeakReference = new WeakReference<EditText>(editText);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {}

    @Override
    public void afterTextChanged(Editable editable) {
        EditText editText = editTextWeakReference.get();
        if (editText == null) {
            return;
        }

        editText.removeTextChangedListener(this);
        String filteredString = editable.toString();
        int decimalIndex = filteredString.indexOf(".");
        int stringLength = filteredString.length();
        if (stringLength == 0) {
            editText.addTextChangedListener(this);
            return;
        } else if (stringLength > 8) {
            filteredString = filteredString.substring(0, 8);
        }

        String[] stringHalves = filteredString.split("\\.");
        String left = "";
        String right = "";
        if (decimalIndex == 0) {
            if (stringHalves.length == 2) {
                right = stringHalves[1];
            } else {
                left = "0";
            }
        } else if (decimalIndex == -1 || decimalIndex + 1 == stringLength) {
            left = stringHalves[0];
        }else {
            left = stringHalves[0];
            right = stringHalves[1];
        }

        if (left.indexOf("0") == 0 && left.length() > 1) {
            left = left.substring(1, left.length());
        }
        if (right.length() > 3) {
            right = right.substring(0, 3);
        }

        if (decimalIndex >= 0) {
            filteredString = left + "." + right;
        } else if (!left.equals("")) {
            filteredString = left;
        } else {
            filteredString = right;
        }
        editText.setText(filteredString);
        editText.setSelection(filteredString.length());
        editText.addTextChangedListener(this);
    }
}