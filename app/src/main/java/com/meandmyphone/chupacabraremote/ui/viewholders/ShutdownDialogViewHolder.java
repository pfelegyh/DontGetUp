package com.meandmyphone.chupacabraremote.ui.viewholders;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.ViewSwitcher;

import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.meandmyphone.chupacabraremote.R;

public class ShutdownDialogViewHolder {

    private final CoordinatorLayout dialogRoot;
    private final Toolbar toolbar;
    private final AppCompatImageButton shutdownNow;
    private final ViewSwitcher delayedViewSwitcher;
    private final AppCompatImageButton shutdownDelayedStart;
    private final AppCompatImageButton abortShutdown;
    private final ViewSwitcher timePickerSwitcher;
    private final TimePicker shutdownDelayedTimePicker;
    private final RelativeLayout shutdownCounterPanel;
    private final TextView shutdownCounter;
    private final LinearLayout shutdownButtonBar;
    private final RelativeLayout delayedShutdownConfigurationPanel;
    private final AppCompatImageButton timePickerMoreOptions;
    private final AppCompatImageButton timePickerSetButton;
    private final LinearLayout contentRoot;
    private final AppCompatImageButton helpButton;

    public ShutdownDialogViewHolder(View shutdownControlDialog) {
        dialogRoot = shutdownControlDialog.findViewById(R.id.shutdownRootLayout);
        toolbar = shutdownControlDialog.findViewById(R.id.toolbar);
        shutdownNow = shutdownControlDialog.findViewById(R.id.shutdownNow);
        delayedViewSwitcher = shutdownControlDialog.findViewById(R.id.delayedViewSwitcher);
        shutdownDelayedStart = shutdownControlDialog.findViewById(R.id.shutdownDelayedStart);
        abortShutdown = shutdownControlDialog.findViewById(R.id.abortShutdown);
        timePickerSwitcher = shutdownControlDialog.findViewById(R.id.timePickerSwitcher);
        shutdownDelayedTimePicker = shutdownControlDialog.findViewById(R.id.shutdownDelayedTimePicker);
        shutdownCounterPanel = shutdownControlDialog.findViewById(R.id.shutdownCounterPanel);
        shutdownCounter = shutdownControlDialog.findViewById(R.id.shutdownCounter);
        shutdownButtonBar = shutdownControlDialog.findViewById(R.id.shutdownButtonBar);
        delayedShutdownConfigurationPanel = shutdownControlDialog.findViewById(R.id.delayedShutdownConfigurationPanel);
        timePickerMoreOptions = shutdownControlDialog.findViewById(R.id.timePickerMoreOptions);
        timePickerSetButton = shutdownControlDialog.findViewById(R.id.timePickerSetButton);
        contentRoot = shutdownControlDialog.findViewById(R.id.contentRoot);
        helpButton = shutdownControlDialog.findViewById(R.id.helpButton);
    }

    public CoordinatorLayout getDialogRoot() {
        return dialogRoot;
    }

    public Toolbar getToolbar() {
        return toolbar;
    }

    public AppCompatImageButton getShutdownNow() {
        return shutdownNow;
    }

    public ViewSwitcher getDelayedViewSwitcher() {
        return delayedViewSwitcher;
    }

    public AppCompatImageButton getShutdownDelayedStart() {
        return shutdownDelayedStart;
    }

    public AppCompatImageButton getAbortShutdown() {
        return abortShutdown;
    }

    public ViewSwitcher getTimePickerSwitcher() {
        return timePickerSwitcher;
    }

    public TimePicker getShutdownDelayedTimePicker() {
        return shutdownDelayedTimePicker;
    }

    public RelativeLayout getShutdownCounterPanel() {
        return shutdownCounterPanel;
    }

    public TextView getShutdownCounter() {
        return shutdownCounter;
    }

    public LinearLayout getShutdownButtonBar() {
        return shutdownButtonBar;
    }

    public RelativeLayout getDelayedShutdownConfigurationPanel() {
        return delayedShutdownConfigurationPanel;
    }

    public AppCompatImageButton getShutdownDelayedVolumeOptions() {
        return timePickerMoreOptions;
    }

    public AppCompatImageButton getTimePickerSetButton() {
        return timePickerSetButton;
    }

    public LinearLayout getContentRoot() {
        return contentRoot;
    }

    public AppCompatImageButton getHelpButton() {
        return helpButton;
    }
}
