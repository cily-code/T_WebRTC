package com.cily.utils.t_webrtc.event;

/**
 * user:cily
 * time:2017/4/30
 * desc:
 */

public class RoomConnectionParameters {
    /**
     * Struct holding the connection parameters of an AppRTC room.
     */

    public final String roomUrl;
    public final String roomId;
    public final boolean loopback;

    public RoomConnectionParameters(String roomUrl, String roomId, boolean loopback) {
        this.roomUrl = roomUrl;
        this.roomId = roomId;
        this.loopback = loopback;
    }

}
