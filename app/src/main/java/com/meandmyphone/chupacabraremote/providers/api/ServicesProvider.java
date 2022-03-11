package com.meandmyphone.chupacabraremote.providers.api;

import com.meandmyphone.chupacabraremote.service.api.ActionSenderService;
import com.meandmyphone.chupacabraremote.service.api.CommunicationService;
import com.meandmyphone.chupacabraremote.service.api.KeyboardService;
import com.meandmyphone.chupacabraremote.service.api.MouseService;
import com.meandmyphone.chupacabraremote.service.api.ShutdownService;
import com.meandmyphone.chupacabraremote.service.api.VolumeService;
import com.meandmyphone.shared.payload.PayloadFactory;

/**
 * An interface used to obtain an instance of various services.
 */
public interface ServicesProvider {

    PayloadFactory getPayloadFactory();
    CommunicationService getCommunicationService();
    ActionSenderService getActionSenderService();
    ShutdownService getShutdownService();
    VolumeService getVolumeService();
    MouseService getMouseService();
    KeyboardService getKeyboardService();
}
