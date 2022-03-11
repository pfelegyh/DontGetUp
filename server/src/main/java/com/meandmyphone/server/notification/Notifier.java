package com.meandmyphone.server.notification;

import com.meandmyphone.shared.JSONKeys;

import java.awt.TrayIcon;

public class Notifier {

    private final TrayIcon trayIcon;

    public Notifier(TrayIcon trayIcon) {

        this.trayIcon = trayIcon;
    }

    public void showNotification(String message) {
        javax.swing.SwingUtilities.invokeLater(() ->
                trayIcon.displayMessage(
                        JSONKeys.APP_NAME,
                        message,
                        TrayIcon.MessageType.INFO
                )
        );
    }
}
