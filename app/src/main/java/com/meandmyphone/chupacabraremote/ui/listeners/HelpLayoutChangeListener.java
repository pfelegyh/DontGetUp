package com.meandmyphone.chupacabraremote.ui.listeners;

import android.view.View;

import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.meandmyphone.chupacabraremote.ui.help.Help;

import java.util.function.Consumer;

public class HelpLayoutChangeListener implements View.OnLayoutChangeListener {

    private final Help help;
    private final Consumer<Help.Page> callback;
    private boolean first = true;

    public HelpLayoutChangeListener(Help help, Consumer<Help.Page> callback) {
        this.help = help;
        this.callback = callback;
    }

    @Override
    public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
        int width = right - left;
        int height = bottom - top;
        if (first || help.getCutoutOverlay().getLayoutParams().width != width || help.getCutoutOverlay().getLayoutParams().height != height) {
            first = false;
            help.getCutoutOverlay().setLayoutParams(new CoordinatorLayout.LayoutParams(width, height));
            if (help.getCurrentPage() != null) {
                callback.accept(help.getCurrentPage());
            }
        }
    }
}
