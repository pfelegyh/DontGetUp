package com.meandmyphone.chupacabraremote.service.api;

import java.io.IOException;

/**
 * A high level service used to initiate a delayed shutdown, or abort an already scheduled delayed
 * shutdown on the connected server-computer.
 */
public interface ShutdownService {

    /**
     * Shuts down connected computer in seconds
     *
     * @param seconds
     */
    void shutdown(int seconds) throws IOException;

    /**
     * aborts queued shutdown (if any)
     */
    void abortShutdown() throws IOException;

}
