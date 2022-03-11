package com.meandmyphone.chupacabraremote.service.impl;

import android.graphics.Point;

import com.meandmyphone.chupacabraremote.logger.Log;
import com.meandmyphone.chupacabraremote.providers.impl.ClientContext;
import com.meandmyphone.chupacabraremote.service.api.ActionSenderService;
import com.meandmyphone.chupacabraremote.service.api.MouseService;
import com.meandmyphone.shared.JSONKeys;
import com.meandmyphone.shared.action.Action;
import com.meandmyphone.shared.action.ActionType;
import com.meandmyphone.shared.payload.DefaultPayload;
import com.meandmyphone.shared.payload.PayloadFactory;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class DefaultMouseService implements MouseService {

    private static final String TAG = DefaultMouseService.class.getSimpleName();
    private static final int MOVE_MIN_DISTANCE_THRESHOLD = 25;
    private static final int MOVE_MIN_TIME_DELTA_THRESHOLD = 250;

    private final ActionSenderService actionSenderService;
    private final PayloadFactory payloadFactory;
    private Point previousMousePosition = null;
    private long lastSendTime = 0;

    public DefaultMouseService(ActionSenderService actionSenderService, PayloadFactory payloadFactory) {
        this.actionSenderService = actionSenderService;
        this.payloadFactory = payloadFactory;
    }

    @Override
    public void clickCurrentMousePosition(int button) {
        actionSenderService.sendAction(ActionType.CLICK_MOUSE, payloadFactory.createMouseClickPayload(0, button, -1, -1));
    }

    @Override
    public ScreenConnection buildMouseConnection() {
        return new ScreenConnection(ClientContext.getInstance().getCommunicationService());
    }

    @Override
    public void holdMouseButton(int button) {
        actionSenderService.sendAction(ActionType.HOLD_MOUSE_BUTTON, payloadFactory.createHoldMouseButtonPayload(button));
    }

    @Override
    public void releaseMouseButton(int button) {
        actionSenderService.sendAction(ActionType.RELEASE_MOUSE_BUTTON, payloadFactory.createReleaseMouseButtonPayload(button));
    }

    @Deprecated
    @Override
    public List<Screen> getScreens() {
        List<Screen> screens = new ArrayList<>();
        try {
            List<Action> actions = actionSenderService.sendActionSync(ActionType.GET_SCREENS, new DefaultPayload(""));
            JSONObject payloadJson = new JSONObject(actions.get(0).getPayload().getContent());
            JSONArray screenArray = payloadJson.getJSONArray(JSONKeys.SCREENS);
            for (int i = 0; i < screenArray.length(); i++) {
                JSONObject screenJson = screenArray.getJSONObject(i);
                Screen screen = new Screen(
                        screenJson.getInt(JSONKeys.SCREEN_INDEX),
                        screenJson.getInt(JSONKeys.SCREEN_LEFT),
                        screenJson.getInt(JSONKeys.SCREEN_TOP),
                        screenJson.getInt(JSONKeys.SCREEN_RIGHT),
                        screenJson.getInt(JSONKeys.SCREEN_BOTTOM)
                );
                screens.add(screen);
            }
        } catch (IOException | ExecutionException | JSONException e) {
            Log.e(TAG, "Failed to retrieve screens: " + e.getMessage());
        } catch (InterruptedException e) {
            Log.e(TAG, e.getMessage());
            Thread.currentThread().interrupt();
        }
        return screens;
    }

    @Deprecated
    @Override
    public void clickScreen(int index, int button, int x, int y) {
        actionSenderService.sendAction(ActionType.CLICK_MOUSE, payloadFactory.createMouseClickPayload(index, button, x, y));

    }

    @Deprecated
    @Override
    public void moveMouseTo(int x, int y) {
        if (System.currentTimeMillis() - lastSendTime > MOVE_MIN_TIME_DELTA_THRESHOLD) {
            lastSendTime = System.currentTimeMillis();
            Point currentMousePosition = new Point(x, y);

            if (previousMousePosition == null || distance(currentMousePosition, previousMousePosition) >= MOVE_MIN_DISTANCE_THRESHOLD) {
                previousMousePosition = currentMousePosition;
                actionSenderService.sendAction(ActionType.MOVE_MOUSE, payloadFactory.createMoveMousePayload(x, y));
            }
        }
    }

    @Deprecated
    @Override
    public void moveMouseBy(double vectorX, double vectorY) {
        if (System.currentTimeMillis() - lastSendTime > MOVE_MIN_TIME_DELTA_THRESHOLD) {
            lastSendTime = System.currentTimeMillis();
            actionSenderService.sendAction(ActionType.MOVE_MOUSE_BY, payloadFactory.createMoveMouseByPayload(vectorX, vectorY));
        }
    }

    private double distance(Point currentMousePosition, Point previousMousePosition) {
        return Math.sqrt(Math.pow(currentMousePosition.x - previousMousePosition.x, 2) + Math.pow(currentMousePosition.y - previousMousePosition.y, 2));

    }
}
