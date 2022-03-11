package com.meandmyphone.chupacabraremote.ui.views;

import android.content.Context;
import android.content.DialogInterface;

import androidx.appcompat.app.AlertDialog;

import com.meandmyphone.chupacabraremote.R;

public class ConfirmDialog {

    private ConfirmDialog() {}

    public static void showConfirmDialog(Context context, String title, String message, DialogInterface.OnClickListener onPositiveClicked, DialogInterface.OnClickListener onNegativeClicked) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title)
                .setMessage(message)
                .setPositiveButton(R.string.yes, onPositiveClicked )
                .setNegativeButton(R.string.no, onNegativeClicked)
                .create()
                .show();
    }
}
