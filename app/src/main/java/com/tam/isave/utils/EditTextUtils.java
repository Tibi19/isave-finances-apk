package com.tam.isave.utils;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import androidx.annotation.NonNull;

public class EditTextUtils {

    public static void setOnTextChangedListener(@NonNull EditText editText, Runnable textChangedRunnable) {
        editText.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
            @Override public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void afterTextChanged(Editable editable) { textChangedRunnable.run(); }
        });
    }
}
