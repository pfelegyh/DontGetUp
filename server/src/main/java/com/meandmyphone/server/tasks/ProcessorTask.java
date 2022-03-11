package com.meandmyphone.server.tasks;

import com.meandmyphone.server.action.api.ActionProcessor;
import com.meandmyphone.server.action.api.ActionProcessorFactory;
import com.meandmyphone.server.services.impl.MouseService;
import com.meandmyphone.server.vo.Point;
import com.meandmyphone.shared.Vector;
import com.meandmyphone.shared.action.Action;
import com.meandmyphone.shared.action.ActionType;
import com.meandmyphone.shared.action.FailureAction;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;
import java.util.logging.Logger;

public class ProcessorTask implements Runnable {

    private static final Logger LOGGER = Logger.getLogger(ProcessorTask.class.getSimpleName());
    private int mouseSensitivity = 10;

    private final Socket socket;
    private final ActionProcessorFactory actionProcessorFactory;
    private final MouseService mouseService;

    public ProcessorTask(Socket socket, ActionProcessorFactory actionProcessorFactory, MouseService mouseService) {
        this.socket = socket;
        this.actionProcessorFactory = actionProcessorFactory;
        this.mouseService = mouseService;
    }

    @Override
    public void run() {
        try (
                ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream())
        ) {
            Object object = objectInputStream.readObject();
            if (object instanceof Action) {
                Action action = (Action) object;

                String message = String.format("Received %s action, payload=%s", action.getType(), action.getPayload().getContent());
                LOGGER.info(message);

                processAction(objectOutputStream, action);

            } else if (object instanceof Vector) {
                long receitTime = System.currentTimeMillis();
                long lastReceitTime = receitTime;

                Vector vector = (Vector) object;
                processVector(objectInputStream, receitTime, vector);
            }

        } catch (IOException | ClassNotFoundException e) {
            LOGGER.severe("Failed to process action: " + e.getMessage());
        }
    }

    private void processVector(ObjectInputStream objectInputStream, long receitTime, Vector vector) throws IOException, ClassNotFoundException {
        long lastReceitTime;
        positionMouse(vector);
        while (!Vector.END_VECTOR.equals(vector)) {
            lastReceitTime = receitTime;
            vector = (Vector) objectInputStream.readObject();
            receitTime = System.currentTimeMillis();

            if (receitTime - lastReceitTime < 500) {
                mouseSensitivity = Math.min(mouseSensitivity + 5, 100);
            } else {
                mouseSensitivity = 10;
            }
            positionMouse(vector);
        }
        mouseSensitivity = 10;
    }

    private void processAction(ObjectOutputStream objectOutputStream, Action action) throws IOException {
        try {
            ActionProcessor actionProcessor = actionProcessorFactory.createActionProcessor(action.getType());
            List<Action> response = actionProcessor.process(action.getPayload());

            for (Action responseAction : response) {
                objectOutputStream.writeObject(responseAction);
            }
            objectOutputStream.writeObject(null);

        } catch (Exception e) {
            objectOutputStream.writeObject(new FailureAction(ActionType.ERROR, e.getMessage()));
        }
    }

    private void positionMouse(Vector vector) {
        Point currentMousePosition = mouseService.getMousePosition();
        mouseService.setMousePosition(currentMousePosition.getX()
                + (int) (vector.getX() * mouseSensitivity),
                currentMousePosition.getY()
                        + (int) (vector.getY() * mouseSensitivity));
    }
}
