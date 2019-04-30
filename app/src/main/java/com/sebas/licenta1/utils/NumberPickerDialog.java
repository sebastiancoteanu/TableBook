package com.sebas.licenta1.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.widget.NumberPicker;

import com.sebas.licenta1.R;

public class NumberPickerDialog {
    private AlertDialog.Builder builder;
    private Dialog numberPickerDialog;
    private NumberPicker.OnValueChangeListener valueChangeListener;
    private static int colorAcc;

    public NumberPickerDialog(Activity activity, Integer minSeats, Integer maxSeats, Integer defaultValue) {
        final NumberPicker numberPicker = new NumberPicker(activity);
        numberPicker.setMinValue(minSeats);
        numberPicker.setMaxValue(maxSeats);
        numberPicker.setValue(defaultValue);

        builder = new AlertDialog.Builder(activity,  R.style.PickerDialogTheme);
        builder.setTitle("Seats");
        builder.setMessage("Choose number of seats");

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                valueChangeListener.onValueChange(numberPicker,
                        numberPicker.getValue(), numberPicker.getValue());
            }
        });

        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                valueChangeListener.onValueChange(numberPicker,
                        numberPicker.getValue(), numberPicker.getValue());
            }
        });

        colorAcc = activity.getColor(R.color.colorAccent);

        builder.setView(numberPicker);
        numberPickerDialog = builder.create();
    }

    public void show() {
        numberPickerDialog.show();
        ((AlertDialog) numberPickerDialog).getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(colorAcc);
        ((AlertDialog) numberPickerDialog).getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(colorAcc);
    }

    public void dismiss() {
        numberPickerDialog.dismiss();
    }

    public NumberPicker.OnValueChangeListener getValueChangeListener() {
        return valueChangeListener;
    }

    public void setValueChangeListener(NumberPicker.OnValueChangeListener valueChangeListener) {
        this.valueChangeListener = valueChangeListener;
    }
}
