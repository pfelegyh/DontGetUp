package com.meandmyphone.chupacabraremote.ui.viewholders;

import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.meandmyphone.chupacabraremote.R;

import java.util.Objects;

public class ServerListItemHolder extends RecyclerView.ViewHolder {

    private final ImageView computerImage;
    private final TextView hostName;
    private final TextView hostAddress;
    private final TextView lastSeen;
    private final TextView upTime;

    private final ViewSwitcher favouriteSwitcher;
    private final ViewSwitcher serverConnectSwitcher;

    private final ImageButton favouriteServer;
    private final ImageButton unFavouriteServer;
    private final ImageButton serverConnect;
    private final ImageButton serverDisconnect;


    public ServerListItemHolder(@NonNull View itemView) {
        super(itemView);
        computerImage = itemView.findViewById(R.id.computerIcon);
        hostName = itemView.findViewById(R.id.server_host_name);
        hostAddress = itemView.findViewById(R.id.server_host_address);
        lastSeen = itemView.findViewById(R.id.server_last_seen);
        serverConnect = itemView.findViewById(R.id.server_connect);
        upTime = itemView.findViewById(R.id.server_up_time);
        favouriteSwitcher = itemView.findViewById(R.id.favourite_switcher);
        favouriteServer = itemView.findViewById(R.id.favourite_server);
        unFavouriteServer = itemView.findViewById(R.id.unfavourite_server);
        serverConnectSwitcher = itemView.findViewById(R.id.server_connect_switcher);
        serverDisconnect = itemView.findViewById(R.id.server_disconnect);
    }


    public ImageView getComputerImage() {
        return computerImage;
    }

    public TextView getHostName() {
        return hostName;
    }

    public TextView getHostAddress() {
        return hostAddress;
    }

    public TextView getLastSeen() {
        return lastSeen;
    }

    public TextView getUpTime() {
        return upTime;
    }

    public ViewSwitcher getFavouriteSwitcher() {
        return favouriteSwitcher;
    }

    public ViewSwitcher getServerConnectSwitcher() {
        return serverConnectSwitcher;
    }

    public ImageButton getFavouriteServer() {
        return favouriteServer;
    }

    public ImageButton getUnFavouriteServer() {
        return unFavouriteServer;
    }

    public ImageButton getServerConnect() {
        return serverConnect;
    }

    public ImageButton getServerDisconnect() {
        return serverDisconnect;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ServerListItemHolder that = (ServerListItemHolder) o;
        return Objects.equals(computerImage, that.computerImage) &&
                Objects.equals(hostName, that.hostName) &&
                Objects.equals(hostAddress, that.hostAddress) &&
                Objects.equals(lastSeen, that.lastSeen) &&
                Objects.equals(upTime, that.upTime) &&
                Objects.equals(favouriteSwitcher, that.favouriteSwitcher) &&
                Objects.equals(serverConnectSwitcher, that.serverConnectSwitcher) &&
                Objects.equals(favouriteServer, that.favouriteServer) &&
                Objects.equals(unFavouriteServer, that.unFavouriteServer) &&
                Objects.equals(serverConnect, that.serverConnect) &&
                Objects.equals(serverDisconnect, that.serverDisconnect);
    }

    @Override
    public int hashCode() {
        return Objects.hash(computerImage, hostName, hostAddress, lastSeen, upTime, favouriteSwitcher, serverConnectSwitcher, favouriteServer, unFavouriteServer, serverConnect, serverDisconnect);
    }
}
