package com.meandmyphone.chupacabraremote.ui.activities;

import static androidx.core.app.ShareCompat.IntentBuilder;
import static com.google.android.material.snackbar.BaseTransientBottomBar.LENGTH_LONG;
import static com.meandmyphone.shared.ChupacabraRemote.ANIMATIONS_ENABLED_KEY;
import static com.meandmyphone.shared.ChupacabraRemote.DARK_THEME;
import static com.meandmyphone.shared.ChupacabraRemote.LIGHT_THEME;
import static com.meandmyphone.shared.ChupacabraRemote.THEME_KEY;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.TypedValue;
import android.view.MenuInflater;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.OvershootInterpolator;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.snackbar.Snackbar;
import com.meandmyphone.chupacabraremote.R;
import com.meandmyphone.chupacabraremote.logger.Log;
import com.meandmyphone.chupacabraremote.network.ServerConnectionInfo;
import com.meandmyphone.chupacabraremote.properties.Connection;
import com.meandmyphone.chupacabraremote.properties.ObservableList;
import com.meandmyphone.chupacabraremote.providers.impl.ClientContext;
import com.meandmyphone.chupacabraremote.service.impl.DefaultConnectionService;
import com.meandmyphone.chupacabraremote.ui.adapters.ServerListAdapter;
import com.meandmyphone.chupacabraremote.ui.help.Help;
import com.meandmyphone.chupacabraremote.ui.listeners.ConnectionListener;
import com.meandmyphone.chupacabraremote.ui.listeners.ServerLocatedListener;
import com.meandmyphone.chupacabraremote.ui.viewholders.RemoteControllerActivityViewHolder;
import com.meandmyphone.chupacabraremote.ui.views.KeyboardControlDialog;
import com.meandmyphone.chupacabraremote.ui.views.MouseControlDialog;
import com.meandmyphone.chupacabraremote.ui.views.ShutdownControlDialog;
import com.meandmyphone.chupacabraremote.ui.views.VolumeControlDialog;
import com.meandmyphone.shared.InterpolationData;
import com.meandmyphone.shared.JSONKeys;
import com.meandmyphone.shared.ServerStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@SuppressWarnings("squid:S1172") // unused params are necessary
public class RemoteControllerActivity extends AppCompatActivity implements ConnectionListener, ServerLocatedListener {

    private static final int PERMISSION_REQUEST_CODE = 9001;
    private static final String TAG = RemoteControllerActivity.class.getSimpleName();
    private static final String APP_ID = "com.meandmyphone.chupacabraremote";
    private static final String SERVER_DOWNLOAD_URL = "https://bit.ly/2UgAzND";

    private RemoteControllerActivityViewHolder viewHolder;
    private DefaultConnectionService connectionService;
    private ServiceConnection serviceConnection;

    private boolean connectedToFavouriteOnStartup = false;
    private MouseControlDialog mouseControlDialog;
    private ShutdownControlDialog shutdownControlDialog;
    private VolumeControlDialog volumeControlDialog;
    private KeyboardControlDialog keyboardControlDialog;
    private Help help;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setAppTheme();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remote_controller_v2);

        viewHolder = new RemoteControllerActivityViewHolder(this);

        ObservableList<ServerConnectionInfo> locatedServers = ClientContext.getInstance().getLocatedServerDetails();
        ServerListAdapter serverListAdapter = new ServerListAdapter(this, locatedServers, this);

        viewHolder.getServerList().setAdapter(serverListAdapter);

        LinearLayoutManager serverListLayoutManager = new LinearLayoutManager(this);
        viewHolder.getServerList().setLayoutManager(serverListLayoutManager);
        viewHolder.getAppBar().addOnOffsetChangedListener((appBarLayout, offset) -> switchCollapseHelperDireciton(offset));

        float dip = 24f;
        float px = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dip,
                getResources().getDisplayMetrics()
        );

        int actionBarHeight = (int) px;

        help = createHelp(serverListLayoutManager, actionBarHeight);

        requestPermissionsIfNecessary();
    }

    @Override
    protected void onResume() {
        super.onResume();

        final ServerLocatedListener serverLocatedListener = RemoteControllerActivity.this;
        Intent startConnectionService = new Intent(this, DefaultConnectionService.class);

        serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                connectionService = ((DefaultConnectionService.ConnectionServiceBinder) service).getService();
                ClientContext.getInstance().setConnectionService(connectionService);
                connectionService.addServerLocatedListener(serverLocatedListener);
                connectionService.init();
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                ClientContext.getInstance().setConnectionService(null);
                connectionService = null;
            }
        };

        bindService(startConnectionService, serviceConnection, Context.BIND_AUTO_CREATE);


        if (isConnected()) {
            viewHolder.getRemoteControlRoot().setVisibility(View.VISIBLE);
            viewHolder.getLoadingRoundel().setVisibility(View.GONE);
            viewHolder.getLoadingTextTop().setVisibility(View.GONE);
            viewHolder.getLoadingTextBottom().setVisibility(View.GONE);
        } else {
            viewHolder.getRemoteControlRoot().setVisibility(View.GONE);
            viewHolder.getLoadingRoundel().setVisibility(View.VISIBLE);
        }

        boolean basicHelpShown = getSharedPreferences(JSONKeys.APP_NAME, Context.MODE_PRIVATE)
                .getBoolean(RemoteControllerActivity.class.getSimpleName() + "." + JSONKeys.BASIC_HELP_SHOWN, false);
        if (!basicHelpShown) {
            help.show();
            getSharedPreferences(JSONKeys.APP_NAME, Context.MODE_PRIVATE)
                    .edit()
                    .putBoolean(RemoteControllerActivity.class.getSimpleName() + "." + JSONKeys.BASIC_HELP_SHOWN, true)
                    .apply();
            showSnackbarWithAction(getResources().getString(R.string.skipText), R.string.skipAction, click -> {
                help.skip();
                getSharedPreferences(JSONKeys.APP_NAME, Context.MODE_PRIVATE)
                        .edit()
                        .putBoolean(KeyboardControlDialog.class.getSimpleName() + "." + JSONKeys.BASIC_HELP_SHOWN, true)
                        .putBoolean(MouseControlDialog.class.getSimpleName() + "." + JSONKeys.BASIC_HELP_SHOWN, true)
                        .putBoolean(ShutdownControlDialog.class.getSimpleName() + "." + JSONKeys.BASIC_HELP_SHOWN, true)
                        .putBoolean(VolumeControlDialog.class.getSimpleName() + "." + JSONKeys.BASIC_HELP_SHOWN, true)
                        .putBoolean(VolumeControlDialog.class.getSimpleName() + "." + JSONKeys.ADVANCED_HELP_SHOWN, true)
                        .apply();
            });
        }

        showHelpMessageIfNotConnecting();
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (mouseControlDialog != null) {
            mouseControlDialog.dismiss();
        }

        if (volumeControlDialog != null) {
            volumeControlDialog.dismiss();
        }

        if (keyboardControlDialog != null) {
            keyboardControlDialog.dismiss();
        }

        if (shutdownControlDialog != null) {
            shutdownControlDialog.dismiss();
        }

        Intent startConnectionService = new Intent(this, DefaultConnectionService.class);
        stopService(startConnectionService);
        unbindService(serviceConnection);

        viewHolder.getRemoteControlRoot().setVisibility(View.GONE);
    }

    @Override
    public void onConnectedToServer(Connection connection) {
        ServerConnectionInfo serverConnectionInfo = connection.getDetails();
        serverConnectionInfo.deleteObservers();
        viewHolder.getAppBar().setExpanded(false);
        ClientContext.getInstance().getConnection().setValue(connection);
        if (connection.getDetails().isShutdownScheduled()) {
            ClientContext.getInstance().startCountdownTask(
                    connection.getDetails().getInterpolationData(),
                    System.currentTimeMillis() + 1000 * connection.getDetails().getShutdownInSeconds());
        }
        viewHolder.getRemoteControlRoot().setVisibility(View.VISIBLE);

        showSnackbar(String.format(Locale.ROOT, "Connected to %s%n(%s)",
                serverConnectionInfo.getHostName(),
                serverConnectionInfo.getHostAddress())
        );

        if (getSharedPreferences(JSONKeys.APP_NAME, Context.MODE_PRIVATE).getBoolean(ANIMATIONS_ENABLED_KEY, true)) {
            viewHolder.getRemoteControlRoot().setScaleX(0.1f);
            viewHolder.getRemoteControlRoot().setScaleY(0.1f);
            viewHolder.getRemoteControlRoot().animate().rotationBy(360).scaleX(1).scaleY(1).setDuration(2500).setInterpolator(new OvershootInterpolator()).start();
        }
        viewHolder.getLoadingRoundel().setVisibility(View.GONE);
    }

    @Override
    public void onDisconnected(Connection connection) {
        showSnackbar(String.format(Locale.ROOT, "Disconnected from %s%n(%s)",
                connection.getDetails().getHostName(),
                connection.getDetails().getHostAddress())
        );

        if (getSharedPreferences(JSONKeys.APP_NAME, Context.MODE_PRIVATE).getBoolean(ANIMATIONS_ENABLED_KEY, true)) {
            viewHolder.getRemoteControlRoot().animate().rotationBy(-360).scaleX(0.1f).scaleY(0.1f).setDuration(1000).setInterpolator(new AccelerateInterpolator()).withEndAction(
                    () -> {
                        viewHolder.getRemoteControlRoot().setVisibility(View.GONE);
                        viewHolder.getRemoteControlRoot().setScaleX(1.0f);
                        viewHolder.getRemoteControlRoot().setScaleY(1.0f);
                    }
            ).start();
        }
        viewHolder.getRemoteControlRoot().setVisibility(View.GONE);

        ClientContext.getInstance().getConnection().setValue(null);
    }

    @Override
    public void onNewServerLocated(ServerConnectionInfo connectionInfo) {
        viewHolder.getLoadingRoundel().setVisibility(View.GONE);
        viewHolder.getLoadingTextTop().setVisibility(View.INVISIBLE);
        viewHolder.getLoadingTextBottom().setVisibility(View.INVISIBLE);
        ClientContext.getInstance().getLocatedServerDetails().add(connectionInfo);
        connectToFavouriteAutomaticallyWhenFirstSeen(connectionInfo);
    }

    @Override
    public void onServerLost(ServerConnectionInfo serverConnectionInfo) {
        if (isConnectedTo(serverConnectionInfo.getHostAddress())) {
            ClientContext.getInstance().connect(null, this);
        }
        if (viewHolder.getServerList().getAdapter() != null) {
            ClientContext.getInstance().getLocatedServerDetails().setChanged();
            ClientContext.getInstance().getLocatedServerDetails().notifyObservers();
        }
        viewHolder.getServerList().invalidate();
    }

    @Override
    public void onServerInformationUpdated(ServerConnectionInfo connectionInfo) {
        connectionInfo.setChanged();
        connectionInfo.notifyObservers();
        ClientContext.getInstance().getLocatedServerDetails().setChanged();
        ClientContext.getInstance().getLocatedServerDetails().notifyObservers();
    }

    private void requestPermissionsIfNecessary() {
        checkAndRequestPermissions(Manifest.permission.CHANGE_WIFI_MULTICAST_STATE, Manifest.permission.ACCESS_NETWORK_STATE, Manifest.permission.CHANGE_NETWORK_STATE);
    }

    private void checkAndRequestPermissions(String ... permissions) {
        List<String> requestedPermissions = new ArrayList<>();
        for (String permission : permissions) {
            checkAndAddPermissionToRequest(permission, requestedPermissions);
        }

        Log.d(TAG, "Requesting permissions: " + requestedPermissions.toString());

        this.requestPermissions(requestedPermissions.toArray(new String[0]), PERMISSION_REQUEST_CODE);

    }

    private void checkAndAddPermissionToRequest(String permission, List<String> requestedPermissions) {
        if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "adding permission: " + permission);
            requestedPermissions.add(permission);
        }
    }

    private void setAppTheme() {
        String theme = getSharedPreferences(JSONKeys.APP_NAME, Context.MODE_PRIVATE).getString(THEME_KEY, LIGHT_THEME);
        switch (theme) {
            case DARK_THEME:
                setTheme(R.style.AppTheme2);
                break;
            case LIGHT_THEME:
            default:
                setTheme(R.style.AppTheme);
                break;
        }
    }

    private Help createHelp(LinearLayoutManager serverListLayoutManager, int actionBarHeight) {
        return Help.newBuilder(viewHolder.getRemoteControlRoot())
                .withYOffset(-2 * actionBarHeight)
                .addPage(Help.Page.newBuilder(this, viewHolder.getRootLayout(), R.string.activityWelcomeHelp,
                        Help.CutoutType.INNER_RECT).setOnHelpStarted(onHelpStarted -> {
                    viewHolder.getLoadingRoundel().setVisibility(View.GONE);
                    viewHolder.getLoadingTextTop().setVisibility(View.INVISIBLE);
                    viewHolder.getLoadingTextBottom().setVisibility(View.INVISIBLE);
                    viewHolder.getRemoteControlRoot().setVisibility(View.VISIBLE);
                    if (ClientContext.getInstance().getLocatedServerDetails().size() == 0) {
                        ClientContext.getInstance().getLocatedServerDetails().add(new ServerConnectionInfo(new ServerStatus("demo", "127.0.0.1", System.currentTimeMillis(), System.currentTimeMillis(), 32000, -1, null, "1.0.0")));
                    }
                }).setOnHelpFinished(onHelpFinished -> {
                    ClientContext.getInstance().getLocatedServerDetails().removeIf(info -> info.getHostAddress().equals("127.0.0.1"));
                    viewHolder.getLoadingRoundel().setVisibility(ClientContext.getInstance().getLocatedServerDetails().size() > 0 ? View.GONE : View.VISIBLE);
                    viewHolder.getLoadingTextTop().setVisibility(ClientContext.getInstance().getLocatedServerDetails().size() > 0 ? View.INVISIBLE : View.VISIBLE);
                    viewHolder.getLoadingTextBottom().setVisibility(ClientContext.getInstance().getLocatedServerDetails().size() > 0 ? View.INVISIBLE : View.VISIBLE);
                    viewHolder.getRemoteControlRoot().setVisibility(isConnected() ? View.VISIBLE : View.GONE);
                }).build())
                .addPage(Help.Page.newBuilder(this, provider -> serverListLayoutManager.findViewByPosition(0), R.string.activityServerItemHelp, Help.CutoutType.RECT).build())
                .addPage(Help.Page.newBuilder(this, provider -> serverListLayoutManager.findViewByPosition(0).findViewById(R.id.computerIcon), R.string.activityConnectionIconHelp, Help.CutoutType.RECT).build())
                .addPage(Help.Page.newBuilder(this, provider -> serverListLayoutManager.findViewByPosition(0).findViewById(R.id.server_connect_switcher), R.string.activityConnectButtonHelp, Help.CutoutType.RECT).build())
                .addPage(Help.Page.newBuilder(this, provider -> serverListLayoutManager.findViewByPosition(0).findViewById(R.id.favourite_switcher), R.string.activityFavouritButtonHelp, Help.CutoutType.RECT).build())
                .addPage(Help.Page.newBuilder(this, viewHolder.getHelpButton(), R.string.activiyHelpButtonHelp, Help.CutoutType.CIRCLE).build())
                .addPage(Help.Page.newBuilder(this, viewHolder.getCollapseHelperExpander(), R.string.activityExpanderHelp, Help.CutoutType.CIRCLE).build())
                .addPage(Help.Page.newBuilder(this, viewHolder.getSendButton(), R.string.activitySendServerButtonHelp1, Help.CutoutType.CIRCLE).build())
                .addPage(Help.Page.newBuilder(this, viewHolder.getSendButton(), R.string.activitySendServerButtonHelp2, Help.CutoutType.CIRCLE).build())
                .addPage(Help.Page.newBuilder(this, viewHolder.getShowVolumeDialog(), R.string.activityOpenVolumeHelp, Help.CutoutType.INNER_RECT)
                        .setOnPageShown(onPageShown -> viewHolder.getAppBar().setExpanded(false, true)).build())
                .addPage(Help.Page.newBuilder(this, viewHolder.getShowPowerDialog(), R.string.activityOpenShutdownHelp, Help.CutoutType.INNER_RECT).build())
                .addPage(Help.Page.newBuilder(this, viewHolder.getShowMouseDialog(), R.string.activityOpenMouseHelp, Help.CutoutType.INNER_RECT).build())
                .addPage(Help.Page.newBuilder(this, viewHolder.getShowKeyboardDialog(), R.string.activityOpenKeyboardHelp, Help.CutoutType.INNER_RECT).build())
                .build();
    }

    private void switchCollapseHelperDireciton(int offset) {
        if (offset == 0) {
            if (viewHolder.getCollapseHelperButtonSwitcher().getNextView().getId() == R.id.collapseHelperShrinker) {
                viewHolder.getCollapseHelperButtonSwitcher().showNext();
            }
        } else {
            if (viewHolder.getCollapseHelperButtonSwitcher().getNextView().getId() == R.id.collapseHelperExpander) {
                viewHolder.getCollapseHelperButtonSwitcher().showNext();
            }
        }
    }

    private void showHelpMessageIfNotConnecting() {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(() -> {
            if (!help.isShowing()) {
                if (ClientContext.getInstance().getLocatedServerDetails().size() == 0) {
                    if (viewHolder.getLoadingTextTop().getVisibility() != View.VISIBLE) {
                        showNotConnectingHelpMessageTop();
                    } else {
                        showNotConnectingHelpMessageBottom();
                    }
                } else {
                    showHelpMessageIfNotConnecting();
                }
            }
        }, 9000);
    }

    private void showNotConnectingHelpMessageBottom() {
        if (getSharedPreferences(JSONKeys.APP_NAME, Context.MODE_PRIVATE).getBoolean(ANIMATIONS_ENABLED_KEY, true)) {
            viewHolder.getLoadingTextBottom().setAlpha(0.0f);
            viewHolder.getLoadingTextBottom().setScaleX(0.0f);
            viewHolder.getLoadingTextBottom().animate().alpha(1.0f).setDuration(2000).scaleX(1.0f).start();
        }
        viewHolder.getLoadingTextBottom().setVisibility(View.VISIBLE);
    }

    private void showNotConnectingHelpMessageTop() {
        if (getSharedPreferences(JSONKeys.APP_NAME, Context.MODE_PRIVATE).getBoolean(ANIMATIONS_ENABLED_KEY, true)) {
            viewHolder.getLoadingTextTop().setAlpha(0.0f);
            viewHolder.getLoadingTextTop().setScaleX(0.0f);
            viewHolder.getLoadingTextTop().animate().alpha(1.0f).setDuration(2000).scaleX(1.0f).start();
        }
        viewHolder.getLoadingTextTop().setVisibility(View.VISIBLE);
        showHelpMessageIfNotConnecting();
    }

    public void onShowVolumeDialogClicked(View view) {
        InterpolationData interpolationData = ClientContext.getInstance().getInterpolationData();
        volumeControlDialog = VolumeControlDialog.showVolumeControlDialog(
                getSupportFragmentManager(),
                interpolationData != null,
                interpolationData == null ? 0 : interpolationData.getDuration());
    }

    public void onShowPowerSettingsClicked(View view) {
        shutdownControlDialog = ShutdownControlDialog.showShutdownControlDialog(
                getSupportFragmentManager(),
                ClientContext.getInstance().getConnection().getValue().getDetails().isShutdownScheduled()
        );
    }

    public void onShowMouseControlDialogClicked(View view) {
        mouseControlDialog = MouseControlDialog.showMouseControlDialog(getSupportFragmentManager());
    }

    public void onShowKeyBoardControlDialogClicked(View view) {
        keyboardControlDialog = KeyboardControlDialog.showKeyboardControlDialog(getSupportFragmentManager());
    }

    public void onCollapseHelperDownArrowClicked(View view) {
        viewHolder.getAppBar().setExpanded(true, true);
    }

    public void onCollapseHelperUpArrowClicked(View view) {
        viewHolder.getAppBar().setExpanded(false, true);
    }

    public void onHelpClicked(View view) {
        help.show();
    }

    public void showContextMenu(View view) {
        PopupMenu popup = new PopupMenu(this, view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.actions, popup.getMenu());

        popup.getMenu().findItem(R.id.animations).setChecked(getSharedPreferences(JSONKeys.APP_NAME, Context.MODE_PRIVATE).getBoolean(ANIMATIONS_ENABLED_KEY, true));
        popup.getMenu().findItem(R.id.lightMode).setChecked(getSharedPreferences(JSONKeys.APP_NAME, Context.MODE_PRIVATE).getString(THEME_KEY, LIGHT_THEME).equals(LIGHT_THEME));
        popup.getMenu().findItem(R.id.darkMode).setChecked(getSharedPreferences(JSONKeys.APP_NAME, Context.MODE_PRIVATE).getString(THEME_KEY, LIGHT_THEME).equals(DARK_THEME));

        popup.setOnMenuItemClickListener(menuItem -> {
            switch (menuItem.getItemId()) {
                case R.id.rate:
                    onRateUsClicked();
                    break;
                case R.id.lightMode:
                    onLightModeClicked();
                    break;
                case R.id.darkMode:
                    onDarkModeClicked();
                    break;
                case R.id.animations:
                    onAnimationsClicked();
                    break;
                default:
                    Log.w(TAG, "Unknown menu item clicked!");
            }
            return false;
        });
        popup.show();

    }

    public void onSendClicked(View view) {
        IntentBuilder intentBuilder = new IntentBuilder(this);
        intentBuilder
                .setType("text/plain")
                .setChooserTitle("Share URL")
                .setSubject(String.format(
                        getResources().getString(R.string.sendServerSubject),
                        getResources().getString(R.string.app_name)))
                .setText(String.format(
                        getResources().getString(R.string.sendServerMessage),
                        getResources().getString(R.string.app_name), SERVER_DOWNLOAD_URL))
                .startChooser();
    }

    private void onRateUsClicked() {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + APP_ID)));
    }

    @SuppressLint("ApplySharedPref")
    private void onLightModeClicked() {
        getSharedPreferences(JSONKeys.APP_NAME, Context.MODE_PRIVATE)
                .edit()
                .putString(THEME_KEY, LIGHT_THEME)
                .commit();
        recreate();
    }

    @SuppressLint("ApplySharedPref")
    private void onDarkModeClicked() {
        getSharedPreferences(JSONKeys.APP_NAME, Context.MODE_PRIVATE)
                .edit()
                .putString(THEME_KEY, DARK_THEME)
                .commit();
        recreate();
    }

    private void onAnimationsClicked() {
        boolean checked = !getSharedPreferences(JSONKeys.APP_NAME, Context.MODE_PRIVATE).getBoolean(ANIMATIONS_ENABLED_KEY, true);
        getSharedPreferences(JSONKeys.APP_NAME, Context.MODE_PRIVATE)
                .edit()
                .putBoolean(ANIMATIONS_ENABLED_KEY, checked)
                .apply();
    }

    private boolean isConnected() {
        return ClientContext.getInstance().getConnection().getValue() != null;
    }

    private boolean isConnectedTo(String hostAddress) {
        return isConnected() && ClientContext.getInstance().getConnection().getValue().getHost().equals(hostAddress);
    }

    private void connectToFavouriteAutomaticallyWhenFirstSeen(ServerConnectionInfo serverConnectionInfo) {
        if (!connectedToFavouriteOnStartup) {
            String favouriteServer = getSharedPreferences(JSONKeys.APP_NAME, Context.MODE_PRIVATE).getString(JSONKeys.FAVOURITE_SERVER, null);
            if (serverConnectionInfo.getHostName().equals(favouriteServer)) {
                ClientContext.getInstance().connect(serverConnectionInfo.getHostAddress(), this);
                viewHolder.getServerList().invalidate();
                connectedToFavouriteOnStartup = true;
            }
        }
    }

    private void showSnackbar(String message) {
        Snackbar.make(
                viewHolder.getRootLayout(),
                message,
                LENGTH_LONG).show();
    }

    private void showSnackbarWithAction(String message, int actionText, View.OnClickListener listener) {
        Snackbar.make(
                viewHolder.getRootLayout(),
                message,
                LENGTH_LONG)
                .setAction(actionText, listener)
                .show();
    }
}