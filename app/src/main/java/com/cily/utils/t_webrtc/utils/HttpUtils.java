package com.cily.utils.t_webrtc.utils;

import com.cily.utils.base.StrUtils;
import com.cily.utils.t_webrtc.client.AsyncHttpURLConnection;
import com.cily.utils.t_webrtc.impl.AsyncHttpEvents;

/**
 * user:cily
 * time:2017/5/2
 * desc:
 */

public class HttpUtils {
    private final static String TAG = HttpUtils.class.getSimpleName();

    public static void request(final String method, String postUrl, String msg, AsyncHttpEvents events){
        new AsyncHttpURLConnection(method, postUrl, msg, events).send();
    }

    public static void leaveRoom(String url, String userRoom, String room, AsyncHttpEvents events){
        url = StrUtils.join(url, "/", room, "/", userRoom);

        request("DELETE", url, "", events);
    }
}