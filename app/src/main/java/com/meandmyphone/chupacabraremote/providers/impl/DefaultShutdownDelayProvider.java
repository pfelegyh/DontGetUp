package com.meandmyphone.chupacabraremote.providers.impl;

import com.meandmyphone.chupacabraremote.providers.api.ShutdownDelayProvider;

public class DefaultShutdownDelayProvider implements ShutdownDelayProvider {
    private int shutdownDelay = -1;

    @Override
    public void setShutdownDelay(int shutdownDelay) {
        this.shutdownDelay = shutdownDelay;
    }

    @Override
    public int getShutdownDelay() {
        return shutdownDelay;
    }
}
