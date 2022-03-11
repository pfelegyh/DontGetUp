package com.meandmyphone.chupacabraremote.ui.viewholders;

import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ViewSwitcher;

import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.meandmyphone.chupacabraremote.R;
import com.meandmyphone.chupacabraremote.ui.activities.RemoteControllerActivity;

public class RemoteControllerActivityViewHolder {

    private final CoordinatorLayout rootLayout;
    private final ProgressBar loadingRoundel;
    private final AppBarLayout appBar;
    private final CollapsingToolbarLayout collapsingToolbar;
    private final RelativeLayout connectionInfoPanel;
    private final RecyclerView serverList;
    private final LinearLayout collapseHelper;
    private final AppCompatImageButton collapseHelperExpander;
    private final AppCompatImageButton collapseHelperShrinker;
    private final ViewSwitcher collapseHelperButtonSwitcher;
    private final LinearLayout remoteControlRoot;
    private final AppCompatImageButton helpButton;
    private final AppCompatImageButton sendButton;
    private final AppCompatImageButton showVolumeDialog;
    private final AppCompatImageButton showPowerDialog;
    private final AppCompatImageButton showMouseDialog;
    private final AppCompatImageButton showKeyboardDialog;
    private final AppCompatTextView loadingTextTop;
    private final AppCompatTextView loadingTextBottom;

    public RemoteControllerActivityViewHolder(RemoteControllerActivity activity) {

        rootLayout = activity.findViewById(R.id.rootLayout);
        loadingRoundel = activity.findViewById(R.id.loadingRoundel);
        appBar = activity.findViewById(R.id.appBar);
        collapsingToolbar = activity.findViewById(R.id.collapsingToolbar);
        connectionInfoPanel = activity.findViewById(R.id.connectionInformationPanel);
        serverList = activity.findViewById(R.id.serverList);
        collapseHelper = activity.findViewById(R.id.collapseHelper);
        collapseHelperExpander = activity.findViewById(R.id.collapseHelperExpander);
        remoteControlRoot = activity.findViewById(R.id.contentRoot);
        collapseHelperShrinker = activity.findViewById(R.id.collapseHelperShrinker);
        collapseHelperButtonSwitcher = activity.findViewById(R.id.collapseHelperButtonSwitcher);
        helpButton = activity.findViewById(R.id.helpButton);
        sendButton = activity.findViewById(R.id.sendButton);
        showVolumeDialog = activity.findViewById(R.id.showVolumeDialog);
        showPowerDialog = activity.findViewById(R.id.showPowerSettingsDialog);
        showMouseDialog = activity.findViewById(R.id.showMouseControlDialog);
        showKeyboardDialog = activity.findViewById(R.id.showKeyboardControlDialog);
        loadingTextTop = activity.findViewById(R.id.loadingTextAbove);
        loadingTextBottom = activity.findViewById(R.id.loadingTextBelow);

    }

    public CoordinatorLayout getRootLayout() {
        return rootLayout;
    }

    public ProgressBar getLoadingRoundel() {
        return loadingRoundel;
    }

    public AppBarLayout getAppBar() {
        return appBar;
    }

    public CollapsingToolbarLayout getCollapsingToolbar() {
        return collapsingToolbar;
    }

    public RelativeLayout getConnectionInfoPanel() {
        return connectionInfoPanel;
    }

    public RecyclerView getServerList() {
        return serverList;
    }

    public LinearLayout getCollapseHelper() {
        return collapseHelper;
    }

    public AppCompatImageButton getCollapseHelperExpander() {
        return collapseHelperExpander;
    }

    public LinearLayout getRemoteControlRoot() {
        return remoteControlRoot;
    }

    public AppCompatImageButton getCollapseHelperShrinker() {
        return collapseHelperShrinker;
    }

    public ViewSwitcher getCollapseHelperButtonSwitcher() {
        return collapseHelperButtonSwitcher;
    }

    public AppCompatImageButton getHelpButton() {
        return helpButton;
    }

    public AppCompatImageButton getSendButton() {
        return sendButton;
    }

    public AppCompatImageButton getShowVolumeDialog() {
        return showVolumeDialog;
    }

    public AppCompatImageButton getShowPowerDialog() {
        return showPowerDialog;
    }

    public AppCompatImageButton getShowMouseDialog() {
        return showMouseDialog;
    }

    public AppCompatImageButton getShowKeyboardDialog() {
        return showKeyboardDialog;
    }

    public AppCompatTextView getLoadingTextTop() {
        return loadingTextTop;
    }

    public AppCompatTextView getLoadingTextBottom() {
        return loadingTextBottom;
    }
}
