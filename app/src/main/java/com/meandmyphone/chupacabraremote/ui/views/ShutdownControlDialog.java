package com.meandmyphone.chupacabraremote.ui.views;

import static com.meandmyphone.chupacabraremote.ui.help.Help.CutoutType;
import static com.meandmyphone.chupacabraremote.ui.help.Help.Page;
import static com.meandmyphone.chupacabraremote.ui.help.Help.TextPosition;
import static com.meandmyphone.chupacabraremote.ui.help.Help.newBuilder;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import com.meandmyphone.chupacabraremote.R;
import com.meandmyphone.chupacabraremote.exceptions.ClientContextNotInitalizedException;
import com.meandmyphone.chupacabraremote.logger.Log;
import com.meandmyphone.chupacabraremote.providers.api.ShutdownDelayProvider;
import com.meandmyphone.chupacabraremote.providers.impl.ClientContext;
import com.meandmyphone.chupacabraremote.providers.impl.DefaultShutdownDelayProvider;
import com.meandmyphone.chupacabraremote.ui.help.Help;
import com.meandmyphone.chupacabraremote.ui.viewholders.ShutdownDialogViewHolder;
import com.meandmyphone.chupacabraremote.util.ViewHelper;
import com.meandmyphone.shared.JSONKeys;

import org.joda.time.DateTime;

import java.io.IOException;

public class ShutdownControlDialog extends DialogFragment {

    public static final String TAG = ShutdownControlDialog.class.getSimpleName();
    private static final String IS_SHUTDOWN_SCHEDULED_KEY = "isShutdownScheduled";

    private boolean isShutdownScheduled;
    private Help help;

    private ShutdownDialogViewHolder shutdownDialogViewHolder;
    private ShutdownDelayProvider shutdownDelayProvider = new DefaultShutdownDelayProvider();
    boolean timePickerExpandedBeforHelpWasShown;

    public ShutdownControlDialog() {
        // required
    }

    public static ShutdownControlDialog newInstance() {
        return new ShutdownControlDialog();
    }

    public static ShutdownControlDialog showShutdownControlDialog(FragmentManager fragmentManager, boolean isShutdownScheduled) {
        ShutdownControlDialog shutdownControlDialog = newInstance();
        Bundle arguments = new Bundle();
        arguments.putBoolean(IS_SHUTDOWN_SCHEDULED_KEY, isShutdownScheduled);
        shutdownControlDialog.setArguments(arguments);
        shutdownControlDialog.show(fragmentManager, TAG);
        return shutdownControlDialog;
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
            boolean basicHelpShown = getContext()
                    .getSharedPreferences(JSONKeys.APP_NAME, Context.MODE_PRIVATE)
                    .getBoolean(ShutdownControlDialog.class.getSimpleName() + "." + JSONKeys.BASIC_HELP_SHOWN, false);
            if (!basicHelpShown) {
                help.show();
                getContext()
                        .getSharedPreferences(JSONKeys.APP_NAME, Context.MODE_PRIVATE)
                        .edit()
                        .putBoolean(ShutdownControlDialog.class.getSimpleName() + "." + JSONKeys.BASIC_HELP_SHOWN, true)
                        .apply();
            }
        }

        getDialog().getWindow().setWindowAnimations(R.style.ShutdownControlDialogAnimation_Window);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        if (getArguments() != null) {
            isShutdownScheduled = getArguments().getBoolean(IS_SHUTDOWN_SCHEDULED_KEY, false);
        }

        View view = inflater.inflate(R.layout.shutdown_control_dialog, container, false);

        shutdownDialogViewHolder = new ShutdownDialogViewHolder(view);
        shutdownDialogViewHolder.getAbortShutdown().setOnClickListener(this::onAbortShutdownClicked);
        shutdownDialogViewHolder.getShutdownNow().setOnClickListener(this::onShutdownNowClicked);
        shutdownDialogViewHolder.getShutdownDelayedStart().setOnClickListener(this::onShowShutdownMoreOptionClicked);
        shutdownDialogViewHolder.getShutdownDelayedVolumeOptions().setOnClickListener(click -> {
            VolumeControlDialog.showVolumeControlDialog(this.getFragmentManager(), true, getMinutesToWait() * 60 * 1000);
            new Handler(Looper.getMainLooper()).postDelayed(this::dismiss, 500);
        });
        shutdownDialogViewHolder.getTimePickerSetButton().setOnClickListener(this::onTimeSetClicked);
        switchShutdownState(isShutdownScheduled);

        if (ClientContext.getInstance().getCountDownTask() != null) {
            ClientContext.getInstance().getCountDownTask().registerShutdownCounter(shutdownDialogViewHolder.getShutdownCounter());
        }

        shutdownDialogViewHolder.getHelpButton().setOnClickListener(this::onShowHelpClicked);
        help = newBuilder(shutdownDialogViewHolder.getContentRoot())
                .addPage(Page.newBuilder(getContext(), shutdownDialogViewHolder.getShutdownNow(), R.string.shutdownControlDialogShutdownNowHelp, CutoutType.CIRCLE).setTextPosition(TextPosition.CENTER).build())
                .addPage(Page.newBuilder(getContext(), shutdownDialogViewHolder.getShutdownDelayedStart(), R.string.shutdownControlDialogShutdownDelayedHelp, CutoutType.CIRCLE).setTextPosition(TextPosition.CENTER).build())
                .addPage(Page.newBuilder(getContext(), shutdownDialogViewHolder.getShutdownDelayedTimePicker(), R.string.shutdownControlDialogShutdownTimePickerHelp, CutoutType.RECT).setOnHelpStarted(
                        onHelpStarted -> {
                            shutdownDialogViewHolder.getDelayedShutdownConfigurationPanel().setVisibility(View.VISIBLE);
                            shutdownDialogViewHolder.getShutdownButtonBar().setVisibility(View.VISIBLE);
                        }).setOnHelpFinished(
                        onHelpFinished -> {
                            if (!timePickerExpandedBeforHelpWasShown) {
                                shutdownDialogViewHolder.getDelayedShutdownConfigurationPanel().setVisibility(View.GONE);
                                shutdownDialogViewHolder.getShutdownButtonBar().setVisibility(View.GONE);
                            }
                        }).build())
                .addPage(Page.newBuilder(getContext(), shutdownDialogViewHolder.getTimePickerSetButton(), R.string.shutdownControlDialogShutdownStartDelayedShutdownHelp, CutoutType.CIRCLE).setTextPosition(TextPosition.CENTER).setOnPageShown(onPageShown -> shutdownDialogViewHolder.getDelayedViewSwitcher().showNext()).build())
                .addPage(Page.newBuilder(getContext(), shutdownDialogViewHolder.getAbortShutdown(), R.string.shutdownControlDialogShutdownAbortDelayedShutdownHelp1, CutoutType.CIRCLE).setTextPosition(TextPosition.CENTER).build())
                .addPage(Page.newBuilder(getContext(), shutdownDialogViewHolder.getAbortShutdown(), R.string.shutdownControlDialogShutdownAbortDelayedShutdownHelp2, CutoutType.CIRCLE).setTextPosition(TextPosition.CENTER).build())
                .addPage(Page.newBuilder(getContext(), shutdownDialogViewHolder.getShutdownDelayedVolumeOptions(), R.string.shutdownControlDialogShutdownVolumeOptions, CutoutType.CIRCLE).setTextPosition(TextPosition.CENTER).setOnPageShown(onPageShown -> shutdownDialogViewHolder.getDelayedViewSwitcher().showNext()).build())
                .build();

        return view;
    }

    public void onShowHelpClicked(View view) {

        timePickerExpandedBeforHelpWasShown = shutdownDialogViewHolder.getDelayedShutdownConfigurationPanel().getVisibility() == View.VISIBLE;

        help.show();
    }

    private void switchShutdownState(boolean isShutdownScheduled) {
        if (isShutdownScheduled) {
            showScheduledShutdown();
        } else {
            showShutdownScheduler();
        }
    }

    private void showShutdownScheduler() {
        if (shutdownDialogViewHolder.getTimePickerSwitcher().getNextView().getId() != R.id.shutdownCounterPanel) {
            shutdownDialogViewHolder.getTimePickerSwitcher().showNext();
        }

        if (shutdownDialogViewHolder.getDelayedViewSwitcher().getNextView().getId() != R.id.abortShutdown) {
            shutdownDialogViewHolder.getDelayedViewSwitcher().showNext();
        }

        if (!shutdownDialogViewHolder.getShutdownNow().isEnabled()) {
            ViewHelper.switchEnabledState(shutdownDialogViewHolder.getShutdownNow());
        }

        if (!shutdownDialogViewHolder.getShutdownDelayedVolumeOptions().isEnabled()) {
            ViewHelper.switchEnabledState(shutdownDialogViewHolder.getShutdownDelayedVolumeOptions());
        }

        if (!shutdownDialogViewHolder.getTimePickerSetButton().isEnabled()) {
            ViewHelper.switchEnabledState(shutdownDialogViewHolder.getTimePickerSetButton());
        }
    }

    private void showScheduledShutdown() {
        if (shutdownDialogViewHolder.getTimePickerSwitcher().getNextView().getId() == R.id.shutdownCounterPanel) {
            shutdownDialogViewHolder.getTimePickerSwitcher().showNext();
        }

        if (shutdownDialogViewHolder.getDelayedViewSwitcher().getNextView().getId() == R.id.abortShutdown) {
            shutdownDialogViewHolder.getDelayedViewSwitcher().showNext();
        }

        if (shutdownDialogViewHolder.getShutdownNow().isEnabled()) {
            ViewHelper.switchEnabledState(shutdownDialogViewHolder.getShutdownNow());
        }

        if (shutdownDialogViewHolder.getShutdownDelayedVolumeOptions().isEnabled()) {
            ViewHelper.switchEnabledState(shutdownDialogViewHolder.getShutdownDelayedVolumeOptions());
        }

        if (shutdownDialogViewHolder.getTimePickerSetButton().isEnabled()) {
            ViewHelper.switchEnabledState(shutdownDialogViewHolder.getTimePickerSetButton());
        }

        shutdownDialogViewHolder.getDelayedShutdownConfigurationPanel().setVisibility(View.VISIBLE);
        shutdownDialogViewHolder.getShutdownButtonBar().setVisibility(View.VISIBLE);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        shutdownDialogViewHolder.getToolbar().setNavigationOnClickListener(v -> dismiss());
    }

    public void onAbortShutdownClicked(View view) {

        ClientContext.getInstance().stopCountdownTask();
        shutdownDelayProvider.setShutdownDelay(-1);
        switchShutdownState(false);
        try {
            ClientContext.getInstance().getShutdownService().abortShutdown();
        } catch (IOException | ClientContextNotInitalizedException e) {
            Log.e(TAG, "Failed to send abortShutdown action: " + e.getMessage());
        }
    }

    public void onShowShutdownMoreOptionClicked(View view) {
        if (shutdownDialogViewHolder.getDelayedShutdownConfigurationPanel().getVisibility() == View.VISIBLE) {
            shutdownDialogViewHolder.getDelayedShutdownConfigurationPanel().setVisibility(View.GONE);
            shutdownDialogViewHolder.getShutdownButtonBar().setVisibility(View.GONE);
        } else {
            shutdownDialogViewHolder.getDelayedShutdownConfigurationPanel().setVisibility(View.VISIBLE);
            shutdownDialogViewHolder.getShutdownButtonBar().setVisibility(View.VISIBLE);
        }
    }

    public void onShutdownNowClicked(View view) {
        ConfirmDialog.showConfirmDialog(this.getContext(), "Shutdown", "Shutdown immediately!", (dialog, which) -> {
            try {
                ClientContext.getInstance().getShutdownService().shutdown(1);
            } catch (IOException | ClientContextNotInitalizedException e) {
                Log.e(TAG, "Failed to send shutdown action: " + e.getMessage());
            }
        }, (dialog, which) -> {
            // unused
        });
    }

    public void onTimeSetClicked(View view) {
        int minutesToWait = getMinutesToWait();

        shutdownDelayProvider.setShutdownDelay(minutesToWait * 60);
        try {
            ClientContext.getInstance().getShutdownService().shutdown(shutdownDelayProvider.getShutdownDelay());
            long waitUntil = System.currentTimeMillis() + minutesToWait * 60 * 1000;
            ClientContext.getInstance().startCountdownTask(ClientContext.getInstance().getInterpolationData(), waitUntil);
            switchShutdownState(true);
            ClientContext.getInstance().getCountDownTask().registerShutdownCounter(shutdownDialogViewHolder.getShutdownCounter());

        } catch (IOException | ClientContextNotInitalizedException e) {
            Log.e(TAG, "Failed to send delayed shutdown action: " + e.getMessage());
        }
    }

    private int getMinutesToWait() {
        int thenMinute = shutdownDialogViewHolder.getShutdownDelayedTimePicker().getHour() * 60
                + shutdownDialogViewHolder.getShutdownDelayedTimePicker().getMinute();

        DateTime now = DateTime.now();

        int nowMinute = now.getHourOfDay() * 60 + now.getMinuteOfHour();

        int minutesToWait = thenMinute - nowMinute;
        if (minutesToWait < 0) {
            minutesToWait = 24 * 60 + minutesToWait;
        }
        return minutesToWait;
    }
}
