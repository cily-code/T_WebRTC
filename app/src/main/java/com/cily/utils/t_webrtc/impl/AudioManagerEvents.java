package com.cily.utils.t_webrtc.impl;

import com.cily.utils.t_webrtc.client.AppRTCAudioManager;

import java.util.Set;

/**
 * user:cily
 * time:2017/4/30
 * desc:Selected audio device change event.
 */

public interface AudioManagerEvents {
    // Callback fired once audio device is changed or list of available audio devices changed.
    void onAudioDeviceChanged(AppRTCAudioManager.AudioDevice selectedAudioDevice,
                              Set<AppRTCAudioManager.AudioDevice> availableAudioDevices);
}
