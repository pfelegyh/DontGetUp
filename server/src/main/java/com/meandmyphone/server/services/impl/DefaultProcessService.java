package com.meandmyphone.server.services.impl;

import com.meandmyphone.server.services.api.ProcessService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.logging.Logger;

public class DefaultProcessService implements ProcessService {

    private static final Logger LOGGER = Logger.getLogger(DefaultProcessService.class.getSimpleName());

    @Override
    public int executeCommand(String... command) throws IOException, InterruptedException {
        return executeCommand(false, command);
    }

    @Override
    public int executeCommand(boolean silent, String... command) throws IOException, InterruptedException {
        if (!silent) {
            LOGGER.info(() -> "Executing command: " + Arrays.toString(command));
        }

        Process process = Runtime.getRuntime().exec(command);

        int exitCode = process.waitFor();

        if (exitCode == 0) {
            if (!silent) {
                try (BufferedReader output = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                    String line = null;
                    while ((line = output.readLine()) != null) {
                        LOGGER.info(line);
                    }
                }
            }
        } else {
            if (!silent) {
                try (BufferedReader output = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
                    String line = null;
                    while ((line = output.readLine()) != null) {
                        LOGGER.info(line);
                    }
                }
            }
        }
        process.destroy();
        return exitCode;
    }

}
