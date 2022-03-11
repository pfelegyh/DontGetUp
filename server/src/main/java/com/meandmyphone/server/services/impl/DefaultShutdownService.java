package com.meandmyphone.server.services.impl;

import com.meandmyphone.server.services.api.ProcessService;
import com.meandmyphone.server.services.api.ShutdownService;

import org.joda.time.DateTime;
import org.joda.time.Seconds;

import java.io.IOException;
import java.util.logging.Logger;

public class DefaultShutdownService implements ShutdownService {


    private static final Logger LOGGER = Logger.getLogger(DefaultShutdownService.class.getSimpleName());
    private final ProcessService processService;

    private DateTime shutdownScheduledFor = null;

    public DefaultShutdownService(ProcessService processService) {
        this.processService = processService;
    }

    @Override
    public int shutdownInSeconds() {
        if (shutdownScheduledFor == null) {
            return -1;
        }
        return Seconds.secondsBetween(DateTime.now(), shutdownScheduledFor).getSeconds();
    }

    @Override
    public boolean shutdownDelayed(int delay) {
        try {
            int exitCode = processService.executeCommand("shutdown", "-s", "-t", String.valueOf(delay));
            if (exitCode == 0) {
                LOGGER.info("Succesfully processed Action");
                shutdownScheduledFor = DateTime.now().plusSeconds(delay);
                return true;
            } else {
                LOGGER.severe("Failed to process Action");
            }
        } catch (IOException e) {
            LOGGER.severe(e.getMessage());
        } catch (InterruptedException e) {
            LOGGER.severe(e.getMessage());
            Thread.currentThread().interrupt();
        }
        return false;
    }

    @Override
    public boolean abortDelayedShutdown() {
        try {
            int exitCode = processService.executeCommand("shutdown", "-a");
            if (exitCode == 0) {
                LOGGER.info("Succesfully processed Action");
                shutdownScheduledFor = null;
                return true;
            } else {
                LOGGER.severe("Failed to process Action");
            }
        } catch (IOException e) {
            LOGGER.severe(e.getMessage());
        } catch (InterruptedException e) {
            LOGGER.severe(e.getMessage());
            Thread.currentThread().interrupt();
        }
        return false;
    }
}
