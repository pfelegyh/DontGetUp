package com.meandmyphone.chupacabraremote.ui.viewholders;

import android.view.View;
import android.widget.LinearLayout;

import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.Toolbar;

import com.meandmyphone.chupacabraremote.R;

public class MouseControlDialogViewHolder {

    private final Toolbar toolbar;
    private final LinearLayout contentRoot;
    private final AppCompatImageButton helpButton;
    private final View mouseControlView;
    private final AppCompatImageButton leftClick;
    private final AppCompatImageButton rightClick;

    public MouseControlDialogViewHolder(View mouseControlDialog) {
        toolbar = mouseControlDialog.findViewById(R.id.toolbar);
        contentRoot = mouseControlDialog.findViewById(R.id.contentRoot);
        helpButton = mouseControlDialog.findViewById(R.id.helpButton);
        mouseControlView = mouseControlDialog.findViewById(R.id.mouseControl);
        leftClick = mouseControlDialog.findViewById(R.id.left_click);
        rightClick = mouseControlDialog.findViewById(R.id.right_click);

    }

    public Toolbar getToolbar() {
        return toolbar;
    }

    public LinearLayout getContentRoot() {
        return contentRoot;
    }

    public AppCompatImageButton getHelpButton() {
        return helpButton;
    }

    public View getMouseControlView() {
        return mouseControlView;
    }

    public AppCompatImageButton getLeftClick() {
        return leftClick;
    }

    public AppCompatImageButton getRightClick() {
        return rightClick;
    }
}
