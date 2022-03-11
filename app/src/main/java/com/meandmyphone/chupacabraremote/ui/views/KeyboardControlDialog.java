package com.meandmyphone.chupacabraremote.ui.views;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import com.meandmyphone.chupacabraremote.R;
import com.meandmyphone.chupacabraremote.providers.impl.ClientContext;
import com.meandmyphone.chupacabraremote.ui.help.Help;
import com.meandmyphone.chupacabraremote.ui.viewholders.KeyboardControlDialogViewHolder;
import com.meandmyphone.shared.JSONKeys;
import com.meandmyphone.shared.Keyboard;

import static com.meandmyphone.chupacabraremote.ui.help.Help.newBuilder;

public class KeyboardControlDialog extends DialogFragment {

    public static final String TAG = KeyboardControlDialog.class.getSimpleName();

    private KeyboardControlDialogViewHolder keyboardControlDialogViewHolder;
    private Help help;

    public KeyboardControlDialog() {
        // required
    }

    public static KeyboardControlDialog newInstance() {
        KeyboardControlDialog keyboardControlDialog = new KeyboardControlDialog();
        return keyboardControlDialog;
    }

    public static KeyboardControlDialog showKeyboardControlDialog(FragmentManager fragmentManager) {
        KeyboardControlDialog shutdownControlDialog = newInstance();
        shutdownControlDialog.show(fragmentManager, TAG);
        return shutdownControlDialog;
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

        if (getContext() != null) {
            boolean basicHelpShown = getContext()
                    .getSharedPreferences(JSONKeys.APP_NAME, Context.MODE_PRIVATE)
                    .getBoolean(KeyboardControlDialog.class.getSimpleName() + "." + JSONKeys.BASIC_HELP_SHOWN, false);
            if (!basicHelpShown) {
                help.show();
                getContext()
                        .getSharedPreferences(JSONKeys.APP_NAME, Context.MODE_PRIVATE)
                        .edit()
                        .putBoolean(KeyboardControlDialog.class.getSimpleName() + "." + JSONKeys.BASIC_HELP_SHOWN, true)
                        .apply();
            }
        }

        getDialog().getWindow().setWindowAnimations(R.style.KeyboardControlDialogAnimation_Window);

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.keyboard_control_dialog, container, false);
        keyboardControlDialogViewHolder = new KeyboardControlDialogViewHolder(view);
        keyboardControlDialogViewHolder.getKeyboardEditor().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // unused
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String change = s.toString().substring(start, start + count);
                ClientContext.getInstance().getKeyboardService().inputKeys(change);
            }

            @Override
            public void afterTextChanged(Editable s) {
                // unused
            }
        });
        keyboardControlDialogViewHolder.getKeyboardEditor().setOnKeyListener((v, keyCode, event) -> {
            if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_DEL) {
                ClientContext.getInstance().getKeyboardService().inputKeys(Keyboard.BACKSPACE_KEYCODE);
                return true;
            }
            return false;
        });

        keyboardControlDialogViewHolder.getKeyboardEditor().setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                ClientContext.getInstance().getKeyboardService().inputKeys(Keyboard.ENTER_KEYCODE);
                return true;
            } else {
                return false;
            }
        });

        keyboardControlDialogViewHolder.getKeyboardEditor().setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                dismiss();
            }
        });



        keyboardControlDialogViewHolder.getHelpButton().setOnClickListener(this::onShowHelpClicked);
        help = newBuilder(keyboardControlDialogViewHolder.getContentRoot())
                .addPage(Help.Page.newBuilder(getContext(), keyboardControlDialogViewHolder.getContentRoot(), R.string.keyboardControlDialogHelp1, Help.CutoutType.INNER_RECT).build())
                .addPage(Help.Page.newBuilder(getContext(), keyboardControlDialogViewHolder.getContentRoot(), R.string.keyboardControlDialogHelp2, Help.CutoutType.INNER_RECT).build())
                .build();

        return view;
    }

    public void onShowHelpClicked(View view) {
        help.show();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        keyboardControlDialogViewHolder.getToolbar().setNavigationOnClickListener(v -> dismiss());
        if (keyboardControlDialogViewHolder.getKeyboardEditor().requestFocus()) {
            getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }
}
