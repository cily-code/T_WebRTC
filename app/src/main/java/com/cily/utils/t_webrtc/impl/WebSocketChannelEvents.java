package com.cily.utils.t_webrtc.impl;

/**
 * user:cily
 * time:2017/4/30
 * desc:Callback interface for messages delivered on WebSocket.
 * All events are dispatched from a looper executor thread.
 */

public interface WebSocketChannelEvents {
    void onWebSocketMessage(final String message);

    void onWebSocketClose();

    void onWebSocketError(final String description);
}
