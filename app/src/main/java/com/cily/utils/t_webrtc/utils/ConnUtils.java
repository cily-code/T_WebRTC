package com.cily.utils.t_webrtc.utils;

import com.cily.utils.t_webrtc.client.WebSocketRTCClient;
import com.cily.utils.t_webrtc.event.RoomConnectionParameters;
import com.cily.utils.t_webrtc.event.SignalingParameters;

/**
 * user:cily
 * time:2017/5/1
 * desc:
 */

public class ConnUtils {

    // Helper functions to get connection, post message and leave message URLs
    public static String getConnectionUrl(RoomConnectionParameters connectionParameters) {
        return connectionParameters.roomUrl + "/" + WebSocketRTCClient.ROOM_JOIN + "/" + connectionParameters.roomId;
    }

    public static String getMessageUrl(RoomConnectionParameters connectionParameters,
                                 SignalingParameters signalingParameters) {

        return connectionParameters.roomUrl + "/" + WebSocketRTCClient.ROOM_MESSAGE + "/" + connectionParameters.roomId
                + "/" + signalingParameters.clientId;
    }

    public static String getLeaveUrl(RoomConnectionParameters connectionParameters,
                               SignalingParameters signalingParameters) {

        return connectionParameters.roomUrl + "/" + WebSocketRTCClient.ROOM_LEAVE + "/" + connectionParameters.roomId + "/"
                + signalingParameters.clientId;
    }

}
