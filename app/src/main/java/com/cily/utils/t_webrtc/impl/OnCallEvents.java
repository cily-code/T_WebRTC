package com.cily.utils.t_webrtc.impl;

import org.webrtc.RendererCommon;

/**
 * user:cily
 * time:2017/4/30
 * desc:Call control interface for container activity
 */

public interface OnCallEvents {
    void onCallHangUp();

    void onCameraSwitch();

    void onVideoScalingSwitch(RendererCommon.ScalingType scalingType);

    void onCaptureFormatChange(int width, int height, int framerate);

    boolean onToggleMic();
}
