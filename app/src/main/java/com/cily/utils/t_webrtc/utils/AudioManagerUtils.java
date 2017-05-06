package com.cily.utils.t_webrtc.utils;

import android.content.Context;

import com.cily.utils.app.utils.L;
import com.cily.utils.t_webrtc.client.AppRTCAudioManager;
import com.cily.utils.t_webrtc.impl.AudioManagerEvents;

import java.util.Set;

/**
 * user:cily
 * time:2017/5/1
 * desc:
 */

public class AudioManagerUtils {
    private final static String TAG = AudioManagerUtils.class.getSimpleName();
    private static AppRTCAudioManager audioManager = null;

    public static void create(Context cx){
        // Create and audio manager that will take care of audio routing,
        // audio modes, audio device enumeration etc.
        audioManager = AppRTCAudioManager.create(cx);
    }

    public static void start(){
        // Store existing audio settings and change audio mode to
        // MODE_IN_COMMUNICATION for best possible VoIP performance.
        L.d(TAG, "Starting the audio manager...");
        audioManager.start(new AudioManagerEvents() {
            // This method will be called each time the number of available audio
            // devices has changed.
            @Override
            public void onAudioDeviceChanged(
                    AppRTCAudioManager.AudioDevice audioDevice, Set<AppRTCAudioManager.AudioDevice> availableAudioDevices) {
                onAudioManagerDevicesChanged(audioDevice, availableAudioDevices);
            }
        });
    }

    public static void stop(){
        if (audioManager != null) {
            audioManager.stop();
            audioManager = null;
        }
    }

    // This method is called when the audio manager reports audio device change,
    // e.g. from wired headset to speakerphone.
    private static void onAudioManagerDevicesChanged(
            final AppRTCAudioManager.AudioDevice device, final Set<AppRTCAudioManager.AudioDevice> availableDevices) {
        L.d(TAG, "onAudioManagerDevicesChanged: " + availableDevices + ", "
                + "selected: " + device);
        // TODO(henrika): add callback handler.
    }

}
