package com.meandmyphone.chupacabraremote.ui.viewholders;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Spinner;

import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.Toolbar;

import com.github.mikephil.charting.charts.LineChart;
import com.meandmyphone.chupacabraremote.R;

public class VolumeControlDialogViewHolder {

    private final LinearLayout contentRoot;
    private final Toolbar toolbar;
    private final AppCompatImageButton muteButton;
    private final SeekBar volumeControl;
    private final Spinner interpolationTypeSpinner;
    private final LineChart interpolationChart;
    private final AppCompatImageButton interpolateVolumeButton;
    private final LinearLayout interpolationRootLayout;
    private final LinearLayout interpolationButtonBarLayout;
    private final AppCompatImageButton helpButton;
    private final AppCompatTextView helpText;

    public VolumeControlDialogViewHolder(View volumeControlDialog) {
        contentRoot = volumeControlDialog.findViewById(R.id.contentRoot);
        toolbar = volumeControlDialog.findViewById(R.id.toolbar);
        muteButton = volumeControlDialog.findViewById(R.id.muteButton);
        volumeControl = volumeControlDialog.findViewById(R.id.volumeControl);
        interpolationTypeSpinner = volumeControlDialog.findViewById(R.id.interpolationTypeSpinner);
        interpolationChart = volumeControlDialog.findViewById(R.id.interpolationChart);
        interpolateVolumeButton = volumeControlDialog.findViewById(R.id.interpolateVolumeButton);
        interpolationRootLayout = volumeControlDialog.findViewById(R.id.interpolationRoot);
        interpolationButtonBarLayout = volumeControlDialog.findViewById(R.id.interpolationButtonBar);
        helpButton = volumeControlDialog.findViewById(R.id.helpButton);
        helpText = volumeControlDialog.findViewById(R.id.helptText);
    }

    public LinearLayout getContentRoot() {
        return contentRoot;
    }

    public Toolbar getToolbar() {
        return toolbar;
    }

    public AppCompatImageButton getMuteButton() {
        return muteButton;
    }

    public SeekBar getVolumeControl() {
        return volumeControl;
    }

    public Spinner getInterpolationTypeSpinner() {
        return interpolationTypeSpinner;
    }

    public LineChart getInterpolationChart() {
        return interpolationChart;
    }

    public AppCompatImageButton getInterpolateVolumeButton() {
        return interpolateVolumeButton;
    }

    public LinearLayout getInterpolationRootLayout() {
        return interpolationRootLayout;
    }

    public LinearLayout getInterpolationButtonBarLayout() {
        return interpolationButtonBarLayout;
    }

    public AppCompatImageButton getHelpButton() {
        return helpButton;
    }

    public AppCompatTextView getHelpText() {
        return helpText;
    }
}
