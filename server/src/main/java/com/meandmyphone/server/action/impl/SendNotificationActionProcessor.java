package com.meandmyphone.server.action.impl;

import com.meandmyphone.server.action.api.ActionProcessor;
import com.meandmyphone.shared.JSONKeys;
import com.meandmyphone.shared.action.Action;
import com.meandmyphone.shared.action.ActionType;
import com.meandmyphone.shared.action.SuccessAction;
import com.meandmyphone.shared.payload.Payload;

import org.json.JSONObject;

import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

public class SendNotificationActionProcessor implements ActionProcessor {

    private final Consumer<String> notificationReceiver;

    public SendNotificationActionProcessor(Consumer<String> notificationReceiver) {
        this.notificationReceiver = notificationReceiver;
    }

    @Override
    public List<Action> process(Payload action) {
        JSONObject json = new JSONObject(action.getContent());
        String message = json.getString(JSONKeys.NOTIFICATION_MESSAGE);
        notificationReceiver.accept(message);
        return Collections.singletonList(new SuccessAction(ActionType.SEND_NOTIFICATION));
    }
}
