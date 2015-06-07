package net.mduder.examples.random_task;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import java.lang.ref.WeakReference;
import java.math.BigInteger;
import java.text.NumberFormat;

/**
 * http://stackoverflow.com/questions/5107901/better-way-to-format-currency-input-edittext
 * Text watcher used by mortgage loan calculator fragment
 */
public class DollarTextWatcher implements TextWatcher {
    private final WeakReference<EditText> editTextWeakReference;

    public DollarTextWatcher(EditText editText) {
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
        if (filteredString.contains(".")) {
            filteredString = filteredString.split("\\.")[0];
        }
        filteredString = filteredString.replaceAll("[$,]", "");
        if (filteredString.equals("")) {
            editText.setText(filteredString);
            editText.setSelection(filteredString.length());
            editText.addTextChangedListener(this);
            return;
        } else if (filteredString.length() > 8) {
            filteredString = filteredString.substring(0, 8);
        }

        while (filteredString.indexOf("0") == 0 && filteredString.length() > 1) {
            filteredString = filteredString.substring(1);
        }
        BigInteger result = new BigInteger(filteredString);
        String formatted = NumberFormat.getCurrencyInstance().format(result).replace(".00", "");
        editText.setText(formatted);
        editText.setSelection(formatted.length());
        editText.addTextChangedListener(this);
    }
}
