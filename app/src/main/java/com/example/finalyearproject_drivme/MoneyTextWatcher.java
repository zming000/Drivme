package com.example.finalyearproject_drivme;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import java.lang.ref.WeakReference;
import java.math.BigDecimal;

public class MoneyTextWatcher implements TextWatcher {
    private final WeakReference<EditText> editTextWeakReference;

    public MoneyTextWatcher(EditText editText) {
        editTextWeakReference = new WeakReference<>(editText);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        //nothing
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        //nothing
    }

    @Override
    public void afterTextChanged(Editable editable) {
        EditText editText = editTextWeakReference.get();
        if (editText == null) return;

        String s = editable.toString();
        if (s.isEmpty()) return;

        editText.removeTextChangedListener(this);

        String cleanString = s.replaceAll("[^0-9]", "");

        BigDecimal parsed = new BigDecimal(cleanString).setScale(2, BigDecimal.ROUND_FLOOR).divide(new BigDecimal(100), BigDecimal.ROUND_FLOOR);
        
        editText.setText(String.format("%.2f", parsed));
        editText.setSelection(String.format("%.2f", parsed).length());
        editText.addTextChangedListener(this);
    }
}
