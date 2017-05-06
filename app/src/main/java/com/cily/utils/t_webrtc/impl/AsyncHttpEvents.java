package com.cily.utils.t_webrtc.impl;

/**
 * user:cily
 * time:2017/4/30
 * desc:
 */

public interface AsyncHttpEvents {
    void onHttpError(String errorMessage);

    void onHttpComplete(String response);
}
