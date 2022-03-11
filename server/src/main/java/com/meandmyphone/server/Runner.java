package com.meandmyphone.server;

import com.meandmyphone.server.notification.Notifier;
import com.meandmyphone.shared.ChupacabraRemote;
import com.meandmyphone.shared.JSONKeys;

import java.awt.AWTException;
import java.awt.Font;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.util.Locale;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import javax.imageio.ImageIO;

/**
 * Entry point of the server, creates the tray icon, and launches the server itself.
 */
public class Runner {

    private static final DefaultChupacabraRemoteServer server = new DefaultChupacabraRemoteServer();
    private static final Logger LOGGER = Logger.getLogger(Runner.class.getSimpleName());

    private static MenuItem startStopServer;
    private static TrayIcon trayIcon;

    private static ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

    public static void main(String[] args) throws Exception {

        LOGGER.info(() -> String.format(Locale.ROOT, "Starting server... verison=%d.%d", ChupacabraRemote.VERSION_MAJOR, ChupacabraRemote.VERSION_MINOR));

        // Check if server is already running on this machine
        try (Socket sock = new Socket("127.0.0.1", ChupacabraRemote.CLIENT_LISTENER_PORT)) {
            SystemTray tray = SystemTray.getSystemTray();
            URL imageLoc = Runner.class.getResource("/digital-clock.png");
            Image image = ImageIO.read(imageLoc);
            trayIcon = new TrayIcon(image);
            tray.add(trayIcon);
            javax.swing.SwingUtilities.invokeLater(() -> {
                trayIcon.displayMessage(
                        JSONKeys.APP_NAME,
                        "Already running!",
                        TrayIcon.MessageType.INFO
                );
                tray.remove(trayIcon);
            });
            return;
        } catch (IOException ioe) {
            // unused
        }


        // add to tray
        addAppToTray();

        // delayed start of server
        LOGGER.info("Delayed starting server, in 60 seconds");
        executor.schedule(() -> {
                    if (!server.isRunning()) {
                        startServer(trayIcon);
                    }
                }
                , 60, TimeUnit.SECONDS);
    }

    private static void addAppToTray() {
        try {
            java.awt.Toolkit.getDefaultToolkit();
            if (!SystemTray.isSupported()) {
                LOGGER.severe("No system tray support, application exiting.");
            }

            SystemTray tray = SystemTray.getSystemTray();
            URL imageLoc = Runner.class.getResource("/digital-clock.png");
            Image image = ImageIO.read(imageLoc);
            trayIcon = new TrayIcon(image);

            startStopServer = new MenuItem("Start server");
            startStopServer.addActionListener(event -> {
                        if (server.isRunning()) {
                            stopServer(trayIcon);
                        } else {
                            startServer(trayIcon);
                        }
                    }
            );

            Font defaultFont = Font.decode(null);
            Font boldFont = defaultFont.deriveFont(Font.BOLD);
            startStopServer.setFont(boldFont);

            MenuItem exitItem = new MenuItem("Exit");
            exitItem.addActionListener(event -> {
                server.stop();
                tray.remove(trayIcon);
                System.exit(0);
            });

            final PopupMenu popup = new PopupMenu();
            popup.add(startStopServer);
            popup.addSeparator();
            popup.add(exitItem);
            trayIcon.setPopupMenu(popup);

            tray.add(trayIcon);
        } catch (AWTException | IOException e) {
            LOGGER.severe("Unable to init system tray: " + e.getMessage());
        }
    }

    private static void stopServer(TrayIcon trayIcon) {
        server.stop();

        javax.swing.SwingUtilities.invokeLater(() -> {
            startStopServer.setLabel("Start server");
            trayIcon.displayMessage(
                    JSONKeys.APP_NAME,
                    "Server stopped!",
                    TrayIcon.MessageType.INFO
            );
        });
    }

    private static void startServer(TrayIcon trayIcon) {
        try {
            server.start();
            Notifier notifier = new Notifier(trayIcon);
            server.registerNotificationReceiver(notifier::showNotification);

            javax.swing.SwingUtilities.invokeLater(() -> {
                startStopServer.setLabel("Stop server");
                trayIcon.displayMessage(
                        JSONKeys.APP_NAME,
                        "Server started!",
                        TrayIcon.MessageType.INFO
                );
            });

        } catch (IOException e) {
            javax.swing.SwingUtilities.invokeLater(() ->
                    trayIcon.displayMessage(
                            JSONKeys.APP_NAME,
                            "Failed to start server!",
                            TrayIcon.MessageType.ERROR
                    ));
            LOGGER.severe("Unable to start server: " + e.getMessage());
        }
    }

}
