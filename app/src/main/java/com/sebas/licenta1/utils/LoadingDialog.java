package com.sebas.licenta1.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;

import com.sebas.licenta1.R;

public class LoadingDialog {
    private AlertDialog.Builder loadingBuilder;
    private Dialog loadingDialog;

    public void show() {
        loadingDialog.show();
        loadingDialog.getWindow().setLayout(400,400);
    }

    public void dismiss() {
        loadingDialog.dismiss();
    }

    public LoadingDialog(Activity activity) {
        loadingBuilder = new AlertDialog.Builder(activity,  R.style.AlertDialogTheme);
        loadingBuilder.setView(R.layout.dialog_loading);
        loadingDialog = loadingBuilder.create();
        loadingDialog.setCanceledOnTouchOutside(false);
    }
}
