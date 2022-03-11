package com.meandmyphone.chupacabraremote.properties;

import com.meandmyphone.chupacabraremote.network.ServerConnectionInfo;

import java.util.Objects;

/**
 * An object representing a connection to a server, containing all necessary information to build a
 * connection, and any available meta-data related to that server.
 */
public class Connection {

    private final String host;
    private final int port;
    private final ServerConnectionInfo details;

    public Connection(String host, int port, ServerConnectionInfo details) {
        this.host = host;
        this.port = port;
        this.details = details;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public ServerConnectionInfo getDetails() {
        return details;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Connection that = (Connection) o;
        return port == that.port &&
                Objects.equals(host, that.host);
    }

    @Override
    public int hashCode() {
        return Objects.hash(host, port);
    }

    @Override
    public String toString() {
        return "Connection{" +
                "host='" + host + '\'' +
                ", port=" + port +
                '}';
    }
}
