package com.meandmyphone.chupacabraremote.ui.adapters;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.meandmyphone.chupacabraremote.R;
import com.meandmyphone.chupacabraremote.network.ServerConnectionInfo;
import com.meandmyphone.chupacabraremote.properties.ObservableList;
import com.meandmyphone.chupacabraremote.providers.impl.ClientContext;
import com.meandmyphone.chupacabraremote.ui.listeners.ConnectionListener;
import com.meandmyphone.chupacabraremote.ui.viewholders.ServerListItemHolder;
import com.meandmyphone.chupacabraremote.util.TimeIntervalFormatter;
import com.meandmyphone.shared.JSONKeys;

import java.util.Locale;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ServerListAdapter extends RecyclerView.Adapter<ServerListItemHolder> implements Observer {

    private final ObservableList<ServerConnectionInfo> locatedServers;
    private final ConnectionListener connectionListener;
    private final Context context;
    private long previousInvalidation = -1;

    public ServerListAdapter(Context context, ObservableList<ServerConnectionInfo> locatedServers, ConnectionListener connectionListener) {
        this.context = context;
        this.locatedServers = locatedServers;
        this.connectionListener = connectionListener;
        locatedServers.addObserver(this);
        ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        scheduledExecutorService.scheduleAtFixedRate(new InvalidatorTask(this), 1, 1, TimeUnit.SECONDS);
    }

    @NonNull
    @Override
    public ServerListItemHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int position) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View itemView = inflater.inflate(R.layout.serverlist_item, viewGroup, false);
        return new ServerListItemHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ServerListItemHolder serverListItemHolder, int position) {
        final int pos = serverListItemHolder.getAdapterPosition();

        updateComputerIconIfNecessary(serverListItemHolder, pos);
        updateFavouriteButtonIfNecessary(serverListItemHolder, pos);
        updateConnectionButtonIfNecessary(serverListItemHolder, pos);
        updateTexts(serverListItemHolder, pos);

        serverListItemHolder.getServerConnect().setOnClickListener(view -> {
            ClientContext.getInstance().connect(locatedServers.get(pos).getHostAddress(), connectionListener);
            serverListItemHolder.getServerConnectSwitcher().showNext();
        });

        serverListItemHolder.getServerDisconnect().setOnClickListener(view -> {
            ClientContext.getInstance().connect(null, connectionListener);
            serverListItemHolder.getServerConnectSwitcher().showNext();
            notifyDataSetChanged();
        });

        serverListItemHolder.getFavouriteServer().setOnClickListener(view -> {
            serverListItemHolder.getFavouriteSwitcher().showNext();
            context.getSharedPreferences(JSONKeys.APP_NAME, Context.MODE_PRIVATE).edit()
                    .putString(JSONKeys.FAVOURITE_SERVER, locatedServers.get(pos).getHostName())
                    .apply();
            notifyDataSetChanged();
        });

        serverListItemHolder.getUnFavouriteServer().setOnClickListener(view -> {
            serverListItemHolder.getFavouriteSwitcher().showNext();
            context.getSharedPreferences(JSONKeys.APP_NAME, Context.MODE_PRIVATE).edit()
                    .remove(JSONKeys.FAVOURITE_SERVER)
                    .apply();
        });
    }

    private void updateComputerIconIfNecessary(ServerListItemHolder serverListItemHolder, int pos) {
        if (locatedServers.get(pos).isAlive()) {
            serverListItemHolder.getComputerImage().setImageResource(R.drawable.ic_desktop_windows_black_24dp);
        } else {
            serverListItemHolder.getComputerImage().setImageResource(R.drawable.ic_warning_black_24dp);
        }
    }

    private void updateTexts(@NonNull ServerListItemHolder serverListItemHolder, int pos) {
        serverListItemHolder.getHostAddress().setText(locatedServers.get(pos).getHostAddress());
        serverListItemHolder.getHostName().setText(locatedServers.get(pos).getHostName());
        if (locatedServers.get(pos).isAlive()) {
            if (System.currentTimeMillis() - locatedServers.get(pos).getLastSeen() >= 3000) {
                serverListItemHolder.getLastSeen().setText(
                        String.format(Locale.ROOT, "Last seen: %s ago",
                                TimeIntervalFormatter.toStringInterval(
                                        System.currentTimeMillis() - locatedServers.get(pos).getLastSeen()))
                );
            } else {
                serverListItemHolder.getLastSeen().setText("Last seen: just now");
            }
        } else {
            serverListItemHolder.getLastSeen().setText("disconnected");
        }
        serverListItemHolder.getUpTime().setText(
                String.format(Locale.ROOT, "Uptime: %s",
                        TimeIntervalFormatter.toStringInterval(
                                locatedServers.get(pos).getServerTime() - locatedServers.get(pos).getServerStartTime()))
        );
    }

    private void updateConnectionButtonIfNecessary(@NonNull ServerListItemHolder serverListItemHolder, int pos) {
        if (ClientContext.getInstance().getConnection().getValue() != null &&
                ClientContext.getInstance().getConnection().getValue().getHost().equals(locatedServers.get(pos).getHostAddress())) {

            if (serverListItemHolder.getServerConnectSwitcher().getNextView().getId() == R.id.server_disconnect) {
                serverListItemHolder.getServerConnectSwitcher().showNext();
            }

        } else {
            if (serverListItemHolder.getServerConnectSwitcher().getNextView().getId() != R.id.server_disconnect) {
                serverListItemHolder.getServerConnectSwitcher().showNext();
            }
        }
        if (!locatedServers.get(pos).isAlive()) {
            if (serverListItemHolder.getServerConnectSwitcher().getNextView().getId() != R.id.server_disconnect) {
                serverListItemHolder.getServerConnectSwitcher().showNext();
            }
            serverListItemHolder.getServerConnect().setEnabled(false);
            serverListItemHolder.getServerConnect().setImageAlpha(128);
        } else {
            serverListItemHolder.getServerConnect().setEnabled(true);
            serverListItemHolder.getServerConnect().setImageAlpha(255);
        }


    }

    private void updateFavouriteButtonIfNecessary(@NonNull ServerListItemHolder serverListItemHolder, int pos) {
        String favouriteServer = context.getSharedPreferences(JSONKeys.APP_NAME, Context.MODE_PRIVATE).getString(JSONKeys.FAVOURITE_SERVER, null);
        if (favouriteServer != null) {
            if (favouriteServer.equals(locatedServers.get(pos).getHostName())) {
                if (serverListItemHolder.getFavouriteSwitcher().getNextView().getId() == R.id.unfavourite_server) {
                    serverListItemHolder.getFavouriteSwitcher().showNext();
                }
            } else {
                if (serverListItemHolder.getFavouriteSwitcher().getNextView().getId() != R.id.unfavourite_server) {
                    serverListItemHolder.getFavouriteSwitcher().showNext();
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        return locatedServers.size();
    }

    @Override
    public void update(Observable observable, Object o) {
        notifyDataSetChanged();
        previousInvalidation = System.currentTimeMillis();
    }

    public long getPreviousInvalidation() {
        return previousInvalidation;
    }

    private class InvalidatorTask implements Runnable {

        private static final int INVALIDATION_THRESHOLD = 5000;

        private final ServerListAdapter serverListAdapter;

        public InvalidatorTask(ServerListAdapter serverListAdapter) {
            this.serverListAdapter = serverListAdapter;
        }

        @Override
        public void run() {
            if (System.currentTimeMillis() - serverListAdapter.getPreviousInvalidation() > INVALIDATION_THRESHOLD) {
                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(serverListAdapter::notifyDataSetChanged);
            }
        }
    }

}