package com.cily.utils.t_webrtc.impl;

/**
 * user:cily
 * time:2017/4/30
 * desc:Callback interface for messages delivered on TCP Connection. All callbacks are invoked from the
 * looper executor thread.
 */

public interface TCPChannelEvents {
    void onTCPConnected(boolean server);

    void onTCPMessage(String message);

    void onTCPError(String description);

    void onTCPClose();
}
