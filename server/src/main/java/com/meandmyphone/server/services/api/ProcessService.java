package com.meandmyphone.server.services.api;

import java.io.IOException;

/**
 * Can execute a command-line command on the server computer.
 */
public interface ProcessService {
    int executeCommand(String ... command) throws IOException, InterruptedException;
    int executeCommand(boolean silent, String... command) throws IOException, InterruptedException;
}
