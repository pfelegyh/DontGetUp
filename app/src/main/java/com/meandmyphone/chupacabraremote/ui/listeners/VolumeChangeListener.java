package com.meandmyphone.chupacabraremote.ui.listeners;

import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

import com.meandmyphone.chupacabraremote.network.ServerConnectionInfo;
import com.meandmyphone.chupacabraremote.providers.impl.ClientContext;
import com.meandmyphone.chupacabraremote.service.api.VolumeService;
import com.meandmyphone.chupacabraremote.ui.views.ConfirmDialog;
import com.meandmyphone.chupacabraremote.ui.views.VolumeControlDialog;
import com.meandmyphone.shared.action.ActionType;

import java.util.Optional;

public class VolumeChangeListener implements OnSeekBarChangeListener {

    private final VolumeService volumeService;
    private final VolumeControlDialog volumeControlDialog;

    public VolumeChangeListener(VolumeService volumeService, VolumeControlDialog volumeControlDialog) {
        this.volumeService = volumeService;
        this.volumeControlDialog = volumeControlDialog;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean fromUser) {
        volumeService.changeVolume(i);
        if (fromUser) {
            volumeControlDialog.updateCurrentVolumeState(i);
        }

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        Optional<ServerConnectionInfo> connectedServerInfo = ClientContext.getInstance().getLocatedServerDetails().stream()
                .filter(serverConnectionInfo -> serverConnectionInfo.getHostAddress().equals(
                        ClientContext.getInstance().getConnection().getValue().getDetails().getHostAddress()))
                .findFirst();

        if (connectedServerInfo.isPresent() && connectedServerInfo.get().getInterpolationData() != null) {
            ConfirmDialog.showConfirmDialog(
                    seekBar.getContext(),
                    "Confirmation",
                    "Volume is currently interpolating, setting volume now will stop interpolation, are you sure?",
                    (dialog, which) -> {
                        ClientContext.getInstance().getActionSenderService().sendAction(
                                ActionType.INTERPOLATE_VOLUME,
                                ClientContext.getInstance().getPayloadFactory().createCancelInterpolationPayload());

                        connectedServerInfo.get().setInterpolationData(null);
                        volumeService.startVolumeChange(seekBar.getProgress());
                    },
                    (dialog, which) -> {
                        // unused
                    });
        } else {
            volumeService.startVolumeChange(seekBar.getProgress());
        }
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        volumeService.finishVolumeChange(seekBar.getProgress());
    }
}
