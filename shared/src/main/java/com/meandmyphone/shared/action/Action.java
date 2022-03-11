package com.meandmyphone.shared.action;

import com.meandmyphone.shared.payload.Payload;

import java.io.Serializable;

public interface Action extends Serializable {

    String getType();
    Payload getPayload();

}
