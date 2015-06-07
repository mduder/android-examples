package net.mduder.examples.random_task;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import java.lang.ref.WeakReference;

/**
 * Text watcher used by mortgage loan calculator fragment
 */
public class NumericTextWatcher implements TextWatcher {
    private final WeakReference<EditText> editTextWeakReference;

    public NumericTextWatcher(EditText editText) {
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
        if (filteredString.equals("")) {
            editText.addTextChangedListener(this);
            return;
        } else if (Integer.valueOf(filteredString) < 0) {
            filteredString = "0";
        } else if (Integer.valueOf(filteredString) > 360) {
            filteredString = "360";
        }

        while (filteredString.indexOf("0") == 0 && filteredString.length() > 1) {
            filteredString = filteredString.substring(1);
        }
        editText.setText(filteredString);
        editText.setSelection(filteredString.length());
        editText.addTextChangedListener(this);
    }
}
