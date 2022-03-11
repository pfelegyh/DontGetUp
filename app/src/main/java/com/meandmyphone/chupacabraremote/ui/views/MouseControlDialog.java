package com.meandmyphone.chupacabraremote.ui.views;

import static com.meandmyphone.chupacabraremote.ui.help.Help.CutoutType;
import static com.meandmyphone.chupacabraremote.ui.help.Help.Page;
import static com.meandmyphone.chupacabraremote.ui.help.Help.TextPosition;
import static com.meandmyphone.chupacabraremote.ui.help.Help.newBuilder;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import com.meandmyphone.chupacabraremote.R;
import com.meandmyphone.chupacabraremote.providers.impl.ClientContext;
import com.meandmyphone.chupacabraremote.service.impl.ScreenConnection;
import com.meandmyphone.chupacabraremote.ui.help.Help;
import com.meandmyphone.chupacabraremote.ui.viewholders.MouseControlDialogViewHolder;
import com.meandmyphone.shared.JSONKeys;
import com.meandmyphone.shared.Mouse;
import com.meandmyphone.shared.Vector;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

public class MouseControlDialog extends DialogFragment {

    public static final String TAG = MouseControlDialog.class.getSimpleName();

    private MouseControlDialogViewHolder mouseControlDialogViewHolder;
    private Vector center;
    private Vector current;
    private ScreenConnection screenConnection;
    private ExecutorService executorService = Executors.newSingleThreadExecutor();
    private Future<?> producerTask;
    private VectorProducer vectorProducer;
    private boolean rightClickHeld;
    private boolean leftClickHeld;
    private Help help;

    public MouseControlDialog() {
        // required
    }

    public static MouseControlDialog newInstance() {
        MouseControlDialog mouseControlDialog = new MouseControlDialog();
        return mouseControlDialog;
    }

    public static MouseControlDialog showMouseControlDialog(FragmentManager fragmentManager) {
        MouseControlDialog mouseControlDialog = newInstance();
        mouseControlDialog.show(fragmentManager, TAG);
        return mouseControlDialog;
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            dialog.getWindow().setLayout(width, height);
        }
        screenConnection = ClientContext.getInstance().getMouseService().buildMouseConnection();
        screenConnection.start();
        vectorProducer = new VectorProducer();
        producerTask = executorService.submit(vectorProducer);

        if (getContext() != null) {
            boolean basicHelpShown = getContext()
                    .getSharedPreferences(JSONKeys.APP_NAME, Context.MODE_PRIVATE)
                    .getBoolean(MouseControlDialog.class.getSimpleName() + "." + JSONKeys.BASIC_HELP_SHOWN, false);
            if (!basicHelpShown) {
                help.show();
                getContext()
                        .getSharedPreferences(JSONKeys.APP_NAME, Context.MODE_PRIVATE)
                        .edit()
                        .putBoolean(MouseControlDialog.class.getSimpleName() + "." + JSONKeys.BASIC_HELP_SHOWN, true)
                        .apply();
            }
        }

        getDialog().getWindow().setWindowAnimations(R.style.MouseControlDialogAnimation_Window);

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.mouse_control_dialog, container, false);
        mouseControlDialogViewHolder = new MouseControlDialogViewHolder(view);

        mouseControlDialogViewHolder.getMouseControlView().setOnTouchListener((mouseControl, motionEvent) -> {
            if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                mouseControl.performClick();
                current = null;
                return false;
            } else if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                center = new Vector(motionEvent.getX(), motionEvent.getY());
            } else if (motionEvent.getAction() == MotionEvent.ACTION_MOVE) {
                current = new Vector(motionEvent.getX(), motionEvent.getY());
            }
            return true;
        });

        mouseControlDialogViewHolder.getLeftClick().setOnClickListener(left -> {
                    ClientContext.getInstance().getMouseService().clickCurrentMousePosition(Mouse.LEFT_BUTTON);
                    leftClickHeld = false;
                }
        );

        mouseControlDialogViewHolder.getRightClick().setOnClickListener(right -> {
            ClientContext.getInstance().getMouseService().clickCurrentMousePosition(Mouse.RIGHT_BUTTON);
            rightClickHeld = false;
        });

        mouseControlDialogViewHolder.getLeftClick().setOnLongClickListener(buttonView -> {
            if (leftClickHeld) {
                ClientContext.getInstance().getMouseService().releaseMouseButton(Mouse.LEFT_BUTTON);
            } else {
                ClientContext.getInstance().getMouseService().holdMouseButton(Mouse.LEFT_BUTTON);
            }
            leftClickHeld = !leftClickHeld;
            mouseControlDialogViewHolder.getMouseControlView().animate().alpha(0.5f).setDuration(250).withEndAction(() -> mouseControlDialogViewHolder.getMouseControlView().animate().alpha(1.0f).setDuration(250).start()).start();
            return true;
        });
        mouseControlDialogViewHolder.getRightClick().setOnLongClickListener(buttonView -> {
            if (rightClickHeld) {
                ClientContext.getInstance().getMouseService().releaseMouseButton(Mouse.RIGHT_BUTTON);
            } else {
                ClientContext.getInstance().getMouseService().holdMouseButton(Mouse.RIGHT_BUTTON);
            }
            rightClickHeld = !rightClickHeld;
            return true;
        });

        mouseControlDialogViewHolder.getHelpButton().setOnClickListener(this::onShowHelpClicked);
        help = newBuilder(mouseControlDialogViewHolder.getContentRoot())
                .addPage(Page.newBuilder(getContext(), mouseControlDialogViewHolder.getMouseControlView(),
                        R.string.mouseControlDialogMouseControlHelp1, CutoutType.INNER_RECT).build())
                .addPage(Page.newBuilder(getContext(), mouseControlDialogViewHolder.getMouseControlView(),
                        R.string.mouseControlDialogMouseControlHelp2, CutoutType.INNER_RECT).build())
                .addPage(Page.newBuilder(getContext(), mouseControlDialogViewHolder.getLeftClick(),
                        R.string.mouseControlDialogLeftClickHelp1, CutoutType.CIRCLE)
                        .setTextPosition(TextPosition.CENTER).build())
                .addPage(Page.newBuilder(getContext(), mouseControlDialogViewHolder.getLeftClick(),
                        R.string.mouseControlDialogLeftClickHelp2, CutoutType.CIRCLE)
                        .setTextPosition(TextPosition.CENTER).build())
                .addPage(Page.newBuilder(getContext(), mouseControlDialogViewHolder.getRightClick(),
                        R.string.mouseControlDialogRightClickHelp1, CutoutType.CIRCLE)
                        .setTextPosition(TextPosition.CENTER).build())
                .addPage(Page.newBuilder(getContext(), mouseControlDialogViewHolder.getRightClick(),
                        R.string.mouseControlDialogRightClickHelp2, CutoutType.CIRCLE)
                        .setTextPosition(TextPosition.CENTER).build())
                .build();

        return view;
    }

    public void onShowHelpClicked(View view) {
        help.show();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mouseControlDialogViewHolder.getToolbar().setNavigationOnClickListener(v -> dismiss());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (leftClickHeld) {
            ClientContext.getInstance().getMouseService().releaseMouseButton(Mouse.LEFT_BUTTON);
        }
        if (rightClickHeld) {
            ClientContext.getInstance().getMouseService().releaseMouseButton(Mouse.RIGHT_BUTTON);
        }
        vectorProducer.running.set(false);
        producerTask.cancel(true);
        executorService.shutdown();
        screenConnection.stop();
    }

    private class VectorProducer implements Runnable {

        private long lastSendTime = -1;
        private static final long SEND_THRESHOLD = 40;

        private final AtomicBoolean running = new AtomicBoolean(true);

        @Override
        public void run() {
            while (running.get()) {
                if (current != null && System.currentTimeMillis() - lastSendTime > SEND_THRESHOLD) {
                    lastSendTime = System.currentTimeMillis();
                    Vector calculated = calculateMouseMovement(current.getX(), current.getY());
                    screenConnection.addVector(calculated);
                }
            }
        }

        private Vector calculateMouseMovement(float touchX, float touchY) {
            float x = touchX < center.getX() ? -1 * (center.getX() - touchX) : touchX - center.getX();
            float y = touchY < center.getY() ? touchY - center.getY() : -1 * (center.getY() - touchY);

            float vectorX = x / mouseControlDialogViewHolder.getMouseControlView().getWidth() / 2;
            float vectorY = y / mouseControlDialogViewHolder.getMouseControlView().getHeight() / 2;

            return new Vector(vectorX, vectorY);
        }
    }
}