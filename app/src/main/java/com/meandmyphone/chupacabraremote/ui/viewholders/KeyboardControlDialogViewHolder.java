package com.meandmyphone.chupacabraremote.ui.viewholders;

import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;

import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.Toolbar;

import com.meandmyphone.chupacabraremote.R;

public class KeyboardControlDialogViewHolder {

    private final RelativeLayout contentRoot;
    private final Toolbar toolbar;
    private final EditText keyboardEditor;
    private final AppCompatImageButton helpButton;


    public KeyboardControlDialogViewHolder(View keyboardControlDialog) {
        contentRoot = keyboardControlDialog.findViewById(R.id.contentRoot);
        toolbar = keyboardControlDialog.findViewById(R.id.toolbar);
        keyboardEditor = keyboardControlDialog.findViewById(R.id.keyboard_editor);
        helpButton = keyboardControlDialog.findViewById(R.id.helpButton);
    }

    public RelativeLayout getContentRoot() {
        return contentRoot;
    }

    public Toolbar getToolbar() {
        return toolbar;
    }

    public EditText getKeyboardEditor() {
        return keyboardEditor;
    }

    public AppCompatImageButton getHelpButton() {
        return helpButton;
    }
}