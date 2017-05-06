package com.cily.utils.t_webrtc.impl;

import com.cily.utils.t_webrtc.event.SignalingParameters;

/**
 * user:cily
 * time:2017/4/30
 * desc:Room parameters fetcher callbacks.
 */

public interface RoomParametersFetcherEvents {
    /**
     * Callback fired once the room's signaling parameters
     * SignalingParameters are extracted.
     */
    void onSignalingParametersReady(final SignalingParameters params);

    /**
     * Callback for room parameters extraction error.
     */
    void onSignalingParametersError(final String description);
}
