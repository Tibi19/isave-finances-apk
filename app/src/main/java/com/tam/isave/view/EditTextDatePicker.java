package com.tam.isave.view;

import android.app.DatePickerDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;

import com.tam.isave.databinding.PopupAddPaymentBinding;
import com.tam.isave.utils.Date;
import com.tam.isave.utils.DebugUtils;

import java.util.Calendar;
import java.util.TimeZone;

public class EditTextDatePicker implements View.OnClickListener, DatePickerDialog.OnDateSetListener {

    private EditText datePickerView;
    private Context context;
    private Date date;

    private int year;
    private int month;
    private int day;

    public static void build(Context context, EditText datePickerView) {
        new EditTextDatePicker(context, datePickerView);
    }

    public EditTextDatePicker(Context context, EditText datePickerView) {
        this.context = context;
        this.datePickerView = datePickerView;
        this.datePickerView.setOnClickListener(this);

        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH); // Month is counted from 0.
        day = calendar.get(Calendar.DAY_OF_MONTH);
        date = new Date(day, month + 1, year); // Month is counted from 1.
        datePickerView.setText(date.toString());
    }

    @Override
    public void onClick(View view) {
        DatePickerDialog datePickerDialog = new DatePickerDialog(context, this, year, month, day);
        datePickerDialog.show();
    }

    @Override
    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
        date = new Date(i2, i1 + 1, i); // Month is counted from 1, whereas i1 is counted from 0.
        datePickerView.setText(date.toString());
    }

}
