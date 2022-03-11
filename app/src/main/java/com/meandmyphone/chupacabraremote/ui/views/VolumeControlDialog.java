package com.meandmyphone.chupacabraremote.ui.views;

import static com.meandmyphone.chupacabraremote.ui.help.Help.CutoutType;
import static com.meandmyphone.chupacabraremote.ui.help.Help.Page;

import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.meandmyphone.chupacabraremote.R;
import com.meandmyphone.chupacabraremote.logger.Log;
import com.meandmyphone.chupacabraremote.network.ServerConnectionInfo;
import com.meandmyphone.chupacabraremote.properties.VolumeState;
import com.meandmyphone.chupacabraremote.providers.impl.ClientContext;
import com.meandmyphone.chupacabraremote.ui.help.Help;
import com.meandmyphone.chupacabraremote.ui.listeners.ServerUpdatedListener;
import com.meandmyphone.chupacabraremote.ui.listeners.VolumeChangeListener;
import com.meandmyphone.chupacabraremote.ui.viewholders.VolumeControlDialogViewHolder;
import com.meandmyphone.chupacabraremote.util.ViewHelper;
import com.meandmyphone.shared.InterpolationData;
import com.meandmyphone.shared.JSONKeys;
import com.meandmyphone.util.Ease;
import com.meandmyphone.util.Interpolation;

import org.joda.time.DateTime;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class VolumeControlDialog extends DialogFragment implements ServerUpdatedListener {

    public static final String TAG = VolumeControlDialog.class.getSimpleName();
    private static final String SHOULD_SHOW_INTERPOLATE_KEY = "shouldInterpolate";
    private static final String INTERPOLATION_DURATION = "interpolationDuration";

    private VolumeControlDialogViewHolder volumeControlDialogViewHolder;
    private boolean showInterpolationConfiguration;
    private long interpolationDuration;
    private int unmutedVolume;
    private VolumeState currentVolumeState = VolumeState.MUTED;

    private Help help;

    public VolumeControlDialog() {
        // required
    }

    public static VolumeControlDialog newInstance() {
        return new VolumeControlDialog();
    }

    public static VolumeControlDialog showVolumeControlDialog(FragmentManager fragmentManager, boolean showInterpolationConfiguration, long interpolationDuration) {
        VolumeControlDialog volumeControlDialog = newInstance();
        Bundle arguments = new Bundle();
        arguments.putBoolean(SHOULD_SHOW_INTERPOLATE_KEY, showInterpolationConfiguration);
        arguments.putLong(INTERPOLATION_DURATION, interpolationDuration);
        volumeControlDialog.setArguments(arguments);
        volumeControlDialog.show(fragmentManager, TAG);


        return volumeControlDialog;
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.WRAP_CONTENT;
            dialog.getWindow().setLayout(width, height);
        }

        if (getContext() != null) {
            boolean basicHelpShown = getContext().getSharedPreferences(JSONKeys.APP_NAME, Context.MODE_PRIVATE).getBoolean(VolumeControlDialog.class.getSimpleName() + "." + JSONKeys.BASIC_HELP_SHOWN, false);
            boolean advancedHelpShown = getContext().getSharedPreferences(JSONKeys.APP_NAME, Context.MODE_PRIVATE).getBoolean(VolumeControlDialog.class.getSimpleName() + "." + JSONKeys.ADVANCED_HELP_SHOWN, false);

            if (!showInterpolationConfiguration && !basicHelpShown) {
                help.show();
                getContext().getSharedPreferences(JSONKeys.APP_NAME, Context.MODE_PRIVATE).edit().putBoolean(VolumeControlDialog.class.getSimpleName() + "." + JSONKeys.BASIC_HELP_SHOWN, true).apply();
            }

            if (showInterpolationConfiguration && !advancedHelpShown) {
                help.show();
                getContext().getSharedPreferences(JSONKeys.APP_NAME, Context.MODE_PRIVATE).edit().putBoolean(VolumeControlDialog.class.getSimpleName() + "." + JSONKeys.BASIC_HELP_SHOWN, true).apply();
                getContext().getSharedPreferences(JSONKeys.APP_NAME, Context.MODE_PRIVATE).edit().putBoolean(VolumeControlDialog.class.getSimpleName() + "." + JSONKeys.ADVANCED_HELP_SHOWN, true).apply();
            }

        }

        getDialog().getWindow().setWindowAnimations(R.style.VolumeControlDialogAnimation_Window);

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        if (getArguments() != null) {
            showInterpolationConfiguration = getArguments().getBoolean(SHOULD_SHOW_INTERPOLATE_KEY, false);
            interpolationDuration = getArguments().getLong(INTERPOLATION_DURATION, 0);
        }

        View view = inflater.inflate(R.layout.volume_control_dialog, container, false);
        volumeControlDialogViewHolder = new VolumeControlDialogViewHolder(view);
        volumeControlDialogViewHolder.getVolumeControl().setProgress(ClientContext.getInstance().getConnection().getValue().getDetails().getCurrentVolume());
        volumeControlDialogViewHolder.getVolumeControl().setOnSeekBarChangeListener(new VolumeChangeListener(ClientContext.getInstance().getVolumeService(), this));
        volumeControlDialogViewHolder.getInterpolateVolumeButton().setOnClickListener(click -> {
            try {
                ViewHelper.switchEnabledState(volumeControlDialogViewHolder.getInterpolateVolumeButton());
                ClientContext.getInstance().getShutdownService().shutdown((int) (interpolationDuration / 1000));
                Interpolation selectedInterpolation = (Interpolation) volumeControlDialogViewHolder.getInterpolationTypeSpinner().getSelectedItem();
                ClientContext.getInstance().getVolumeService().interpolateVolumeToZero(
                        selectedInterpolation.getIntegerValue(),
                        interpolationDuration,
                        volumeControlDialogViewHolder.getVolumeControl().getProgress()
                );
                InterpolationData interpolationData = new InterpolationData(volumeControlDialogViewHolder.getVolumeControl().getProgress(), 0, selectedInterpolation.getIntegerValue(), interpolationDuration, System.currentTimeMillis());
                ClientContext.getInstance().setInterpolationData(interpolationData);
                ClientContext.getInstance().startCountdownTask(interpolationData, System.currentTimeMillis() + interpolationDuration);
                ClientContext.getInstance().getCountDownTask().registerLinechart(volumeControlDialogViewHolder.getInterpolationChart());
            } catch (IOException ioe) {
                Log.e(TAG, "Failed to start inteprolating volume.");
            }
        });
        volumeControlDialogViewHolder.getMuteButton().setOnClickListener(this::onMuteClicked);
        volumeControlDialogViewHolder.getInterpolationRootLayout().setVisibility(showInterpolationConfiguration ? View.VISIBLE : View.GONE);
        volumeControlDialogViewHolder.getInterpolationButtonBarLayout().setVisibility(showInterpolationConfiguration ? View.VISIBLE : View.GONE);
        if (ClientContext.getInstance().getCountDownTask() != null) {
            ClientContext.getInstance().getCountDownTask().registerLinechart(volumeControlDialogViewHolder.getInterpolationChart());
        }

        if (this.getContext() != null) {
            ArrayAdapter<Interpolation> spinnerAdapter = new ArrayAdapter<>(
                    this.getContext(),
                    R.layout.spinner_selected_item,
                    Arrays.asList(Interpolation.values()));
            spinnerAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);

            volumeControlDialogViewHolder.getInterpolationTypeSpinner().setAdapter(spinnerAdapter);
            if (this.getContext().getSharedPreferences(JSONKeys.APP_NAME, Context.MODE_PRIVATE).getInt(JSONKeys.INTERPOLATE_VOLUME_INTERPOLATIONTYPE, -1) != -1) {
                volumeControlDialogViewHolder.getInterpolationTypeSpinner().setSelection(this.getContext().getSharedPreferences(JSONKeys.APP_NAME, Context.MODE_PRIVATE).getInt(JSONKeys.INTERPOLATE_VOLUME_INTERPOLATIONTYPE, -1));
            }

            Drawable spinnerDrawable = volumeControlDialogViewHolder.getInterpolationTypeSpinner().getBackground().getConstantState().newDrawable();

            TypedValue typedValue = new TypedValue();
            Resources.Theme theme = getContext().getTheme();
            theme.resolveAttribute(R.attr.colorPrimaryDark, typedValue, true);
            @ColorInt int color = typedValue.data;

            spinnerDrawable.setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
            volumeControlDialogViewHolder.getInterpolationTypeSpinner().setBackground(spinnerDrawable);
        }


        volumeControlDialogViewHolder.getInterpolationTypeSpinner().setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                getContext().getSharedPreferences(JSONKeys.APP_NAME, Context.MODE_PRIVATE).edit().putInt(JSONKeys.INTERPOLATE_VOLUME_INTERPOLATIONTYPE, position).apply();
                populateLineChart(position);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // unused
            }
        });

        if (getContext().getSharedPreferences(JSONKeys.APP_NAME, Context.MODE_PRIVATE).getInt(JSONKeys.INTERPOLATE_VOLUME_INTERPOLATIONTYPE, -1) != -1) {
            volumeControlDialogViewHolder.getInterpolationTypeSpinner().setSelection(getContext().getSharedPreferences(JSONKeys.APP_NAME, Context.MODE_PRIVATE).getInt(JSONKeys.INTERPOLATE_VOLUME_INTERPOLATIONTYPE, -1));
        }

        populateLineChart(volumeControlDialogViewHolder.getInterpolationTypeSpinner().getSelectedItemPosition());


        volumeControlDialogViewHolder.getHelpButton().setOnClickListener(this::onShowHelpClicked);

        ClientContext.getInstance().getConnectionService().addServerUpdatedListener(this);

        help = Help.newBuilder(volumeControlDialogViewHolder.getContentRoot())
                .addPage(Page.newBuilder(
                        getContext(),
                        volumeControlDialogViewHolder.getVolumeControl(),
                        R.string.volumeControlDialogVolumeControlHelp,
                        CutoutType.RECT)
                        .build())
                .addPage(Page.newBuilder(
                        getContext(),
                        volumeControlDialogViewHolder.getMuteButton(),
                        R.string.volumeControlDialogMuteButtonHelp,
                        CutoutType.CIRCLE)
                        .build())
                .addPage(Page.newBuilder(
                        getContext(),
                        volumeControlDialogViewHolder.getInterpolationTypeSpinner(),
                        R.string.volumeControlDialogInterpolationSpinnerHelp,
                        CutoutType.RECT)
                        .setOnCondition(
                                onCondition -> showInterpolationConfiguration)
                        .setOnPageShown(
                                pageShown -> {
                                    volumeControlDialogViewHolder.getInterpolationRootLayout().setVisibility(View.VISIBLE);
                                    volumeControlDialogViewHolder.getInterpolationButtonBarLayout().setVisibility(View.VISIBLE);
                                }).setOnHelpFinished(helpFinished -> {
                            volumeControlDialogViewHolder.getInterpolationRootLayout().setVisibility(showInterpolationConfiguration ? View.VISIBLE : View.GONE);
                            volumeControlDialogViewHolder.getInterpolationButtonBarLayout().setVisibility(showInterpolationConfiguration ? View.VISIBLE : View.GONE);
                        })
                        .build())
                .addPage(Page.newBuilder(
                        getContext(),
                        volumeControlDialogViewHolder.getInterpolationChart(),
                        R.string.volumeControlDialogInterpolationChartHelp,
                        CutoutType.RECT)
                        .setOnCondition(
                                onCondition -> showInterpolationConfiguration).build())
                .addPage(Page.newBuilder(
                        getContext(),
                        volumeControlDialogViewHolder.getInterpolateVolumeButton(),
                        R.string.volumeControlDialogInterpolateVolumeButtonHelp,
                        CutoutType.CIRCLE).
                        setOnCondition(
                                onCondition -> showInterpolationConfiguration)
                        .build())
                .build();


        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        volumeControlDialogViewHolder.getToolbar().setNavigationOnClickListener(v -> dismiss());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ClientContext.getInstance().getConnectionService().removeServerUpdatedListener(this);
    }

    @Override
    public void onServerInformationUpdated(ServerConnectionInfo serverConnectionInfo) {
        if (ClientContext.getInstance().getConnection().getValue() != null && ClientContext.getInstance().getConnection().getValue().getHost().equals(serverConnectionInfo.getHostAddress())) {
            if (!ClientContext.getInstance().getVolumeService().isVolumeChanging() &&
                    serverConnectionInfo.getCurrentVolume() != volumeControlDialogViewHolder.getVolumeControl().getProgress()) {
                volumeControlDialogViewHolder.getVolumeControl().setProgress(serverConnectionInfo.getCurrentVolume());
                updateCurrentVolumeState(serverConnectionInfo.getCurrentVolume());
            }
        }
    }

    public void onShowHelpClicked(View view) {
        help.show();
    }

    public void onMuteClicked(View view) {
        if (volumeControlDialogViewHolder.getVolumeControl().getProgress() == 0) {
            volumeControlDialogViewHolder.getVolumeControl().setProgress(unmutedVolume);
            ClientContext.getInstance().getVolumeService().changeVolume(unmutedVolume);
        } else {
            unmutedVolume = volumeControlDialogViewHolder.getVolumeControl().getProgress();
            volumeControlDialogViewHolder.getVolumeControl().setProgress(0);
            ClientContext.getInstance().getVolumeService().changeVolume(0);
        }
        updateCurrentVolumeState(volumeControlDialogViewHolder.getVolumeControl().getProgress());
    }

    public void updateCurrentVolumeState(int currentVolume) {
        if (currentVolume > currentVolumeState.getMax() || currentVolume < currentVolumeState.getMin()) {
            currentVolumeState = VolumeState.forVolume(currentVolume);
            volumeControlDialogViewHolder.getMuteButton().setImageResource(currentVolumeState.getResId());
        }
    }


    private void populateLineChart(int position) {

        int minutesToWait = Math.max(5, (int) (interpolationDuration / 1000 / 60));
        float step = minutesToWait / 20.0f;
        int startValue = volumeControlDialogViewHolder.getVolumeControl().getProgress();
        int endValue = 0;

        Interpolation interpolation = (Interpolation) volumeControlDialogViewHolder.getInterpolationTypeSpinner().getAdapter().getItem(position);

        List<Entry> yVals = new ArrayList<>();

        for (float i = 0.0f; i <= minutesToWait; i += step) {
            float value = Ease.calculateFloat(interpolation.getIntegerValue(), i, startValue, (float) endValue - startValue, minutesToWait);
            int index = yVals.size();
            yVals.add(new Entry(index, value));
        }

        LineDataSet selectedInterpolationChartDataSet = new LineDataSet(yVals, "Volume");
        selectedInterpolationChartDataSet.setDrawCircles(false);

        TypedValue typedValue = new TypedValue();
        Resources.Theme theme = getContext().getTheme();
        theme.resolveAttribute(R.attr.colorPrimaryDark, typedValue, true);
        @ColorInt int color = typedValue.data;
        selectedInterpolationChartDataSet.setColor(color);

        volumeControlDialogViewHolder.getInterpolationChart().setGridBackgroundColor(R.color.transparent);

        volumeControlDialogViewHolder.getInterpolationChart().getXAxis().setDrawGridLines(false);
        volumeControlDialogViewHolder.getInterpolationChart().getAxisLeft().setDrawGridLines(false);
        volumeControlDialogViewHolder.getInterpolationChart().getXAxis().setTextColor(color);
        volumeControlDialogViewHolder.getInterpolationChart().getAxisLeft().setTextColor(color);

        selectedInterpolationChartDataSet.setLabel("Volume");
        LineData data = new LineData(selectedInterpolationChartDataSet);
        data.setDrawValues(false);
        volumeControlDialogViewHolder.getInterpolationChart().setData(data);
        volumeControlDialogViewHolder.getInterpolationChart().getDescription().setText("");

        volumeControlDialogViewHolder.getInterpolationChart().getLegend().setEnabled(false);
        volumeControlDialogViewHolder.getInterpolationChart().getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        volumeControlDialogViewHolder.getInterpolationChart().getXAxis().setValueFormatter((value, axis) -> {
            DateTime val = DateTime.now().plusMinutes((int) (value * step));
            return val.toString("hh:mm", Locale.ROOT);
        });
        volumeControlDialogViewHolder.getInterpolationChart().getAxisRight().setEnabled(false);
        volumeControlDialogViewHolder.getInterpolationChart().getAxisLeft().setValueFormatter((value, axis) -> {
            float percentage = 100 * (value / 65535);
            return String.format(Locale.ROOT, "%.0f%%", percentage);
        });

        volumeControlDialogViewHolder.getInterpolationChart().setHighlightPerDragEnabled(false);
        volumeControlDialogViewHolder.getInterpolationChart().setHighlightPerTapEnabled(false);

        selectedInterpolationChartDataSet.setHighLightColor(Color.RED);

        volumeControlDialogViewHolder.getInterpolationChart().animateX(250);


    }

}
