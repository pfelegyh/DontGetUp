package com.meandmyphone.server.services.api;

/**
 * High level service to schedule shutdowns, abort scheduled shutdowns.
 */
public interface ShutdownService {

    int shutdownInSeconds();
    boolean shutdownDelayed(int delay);
    boolean abortDelayedShutdown();
}
