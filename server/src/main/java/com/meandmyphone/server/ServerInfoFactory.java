package com.meandmyphone.server;

import com.meandmyphone.server.services.api.ShutdownService;
import com.meandmyphone.server.services.impl.VolumeService;
import com.meandmyphone.shared.ChupacabraRemote;
import com.meandmyphone.shared.ServerStatus;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Locale;

public class ServerInfoFactory {

    private final ShutdownService shutdownService;
    private final VolumeService volumeService;
    private final long serverStartTime;
    private final String address;
    private final String hostName;

    public ServerInfoFactory(VolumeService volumeService, ShutdownService shutdownService, long serverStartTime) throws UnknownHostException {
        this.volumeService = volumeService;
        this.shutdownService = shutdownService;
        this.serverStartTime = serverStartTime;
        address = InetAddress.getLocalHost().getHostAddress();
        hostName = InetAddress.getLocalHost().getHostName();
    }

    public ServerStatus createHearbeatMessage() {
        return new ServerStatus(
                hostName,
                address,
                System.currentTimeMillis(),
                serverStartTime,
                volumeService.getVolume(),
                shutdownService.shutdownInSeconds(),
                volumeService.getRunningInterpolationData(),
                String.format(Locale.ENGLISH, "%d.%d", ChupacabraRemote.VERSION_MAJOR, ChupacabraRemote.VERSION_MINOR)
        );
    }
}
