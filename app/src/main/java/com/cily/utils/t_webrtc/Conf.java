package com.cily.utils.t_webrtc;

/**
 * user:cily
 * time:2017/4/30
 * desc:
 */

public interface Conf {
    String EXTRA_ROOMID = "org.appspot.apprtc.ROOMID";
    String EXTRA_LOOPBACK = "org.appspot.apprtc.LOOPBACK";
    String EXTRA_VIDEO_CALL = "org.appspot.apprtc.VIDEO_CALL";
    String EXTRA_SCREENCAPTURE = "org.appspot.apprtc.SCREENCAPTURE";
    String EXTRA_CAMERA2 = "org.appspot.apprtc.CAMERA2";
    String EXTRA_VIDEO_WIDTH = "org.appspot.apprtc.VIDEO_WIDTH";
    String EXTRA_VIDEO_HEIGHT = "org.appspot.apprtc.VIDEO_HEIGHT";
    String EXTRA_VIDEO_FPS = "org.appspot.apprtc.VIDEO_FPS";
    String EXTRA_VIDEO_CAPTUREQUALITYSLIDER_ENABLED =
            "org.appsopt.apprtc.VIDEO_CAPTUREQUALITYSLIDER";
    String EXTRA_VIDEO_BITRATE = "org.appspot.apprtc.VIDEO_BITRATE";
    String EXTRA_VIDEOCODEC = "org.appspot.apprtc.VIDEOCODEC";
    String EXTRA_HWCODEC_ENABLED = "org.appspot.apprtc.HWCODEC";
    String EXTRA_CAPTURETOTEXTURE_ENABLED = "org.appspot.apprtc.CAPTURETOTEXTURE";
    String EXTRA_FLEXFEC_ENABLED = "org.appspot.apprtc.FLEXFEC";
    String EXTRA_AUDIO_BITRATE = "org.appspot.apprtc.AUDIO_BITRATE";
    String EXTRA_AUDIOCODEC = "org.appspot.apprtc.AUDIOCODEC";
    String EXTRA_NOAUDIOPROCESSING_ENABLED =
            "org.appspot.apprtc.NOAUDIOPROCESSING";
    String EXTRA_AECDUMP_ENABLED = "org.appspot.apprtc.AECDUMP";
    String EXTRA_OPENSLES_ENABLED = "org.appspot.apprtc.OPENSLES";
    String EXTRA_DISABLE_BUILT_IN_AEC = "org.appspot.apprtc.DISABLE_BUILT_IN_AEC";
    String EXTRA_DISABLE_BUILT_IN_AGC = "org.appspot.apprtc.DISABLE_BUILT_IN_AGC";
    String EXTRA_DISABLE_BUILT_IN_NS = "org.appspot.apprtc.DISABLE_BUILT_IN_NS";
    String EXTRA_ENABLE_LEVEL_CONTROL = "org.appspot.apprtc.ENABLE_LEVEL_CONTROL";
    String EXTRA_DISPLAY_HUD = "org.appspot.apprtc.DISPLAY_HUD";
    String EXTRA_TRACING = "org.appspot.apprtc.TRACING";
    String EXTRA_CMDLINE = "org.appspot.apprtc.CMDLINE";
    String EXTRA_RUNTIME = "org.appspot.apprtc.RUNTIME";
    String EXTRA_VIDEO_FILE_AS_CAMERA = "org.appspot.apprtc.VIDEO_FILE_AS_CAMERA";
    String EXTRA_SAVE_REMOTE_VIDEO_TO_FILE =
            "org.appspot.apprtc.SAVE_REMOTE_VIDEO_TO_FILE";
    String EXTRA_SAVE_REMOTE_VIDEO_TO_FILE_WIDTH =
            "org.appspot.apprtc.SAVE_REMOTE_VIDEO_TO_FILE_WIDTH";
    String EXTRA_SAVE_REMOTE_VIDEO_TO_FILE_HEIGHT =
            "org.appspot.apprtc.SAVE_REMOTE_VIDEO_TO_FILE_HEIGHT";
    String EXTRA_USE_VALUES_FROM_INTENT =
            "org.appspot.apprtc.USE_VALUES_FROM_INTENT";
    String EXTRA_DATA_CHANNEL_ENABLED = "org.appspot.apprtc.DATA_CHANNEL_ENABLED";
    String EXTRA_ORDERED = "org.appspot.apprtc.ORDERED";
    String EXTRA_MAX_RETRANSMITS_MS = "org.appspot.apprtc.MAX_RETRANSMITS_MS";
    String EXTRA_MAX_RETRANSMITS = "org.appspot.apprtc.MAX_RETRANSMITS";
    String EXTRA_PROTOCOL = "org.appspot.apprtc.PROTOCOL";
    String EXTRA_NEGOTIATED = "org.appspot.apprtc.NEGOTIATED";
    String EXTRA_ID = "org.appspot.apprtc.ID";

    // Peer connection statistics callback period in ms.
    int STAT_CALLBACK_PERIOD = 1000;
    // Local preview screen position before call is connected.
    int LOCAL_X_CONNECTING = 0;
    int LOCAL_Y_CONNECTING = 0;
    int LOCAL_WIDTH_CONNECTING = 100;
    int LOCAL_HEIGHT_CONNECTING = 100;
    // Local preview screen position after call is connected.
    int LOCAL_X_CONNECTED = 72;
    int LOCAL_Y_CONNECTED = 72;
    int LOCAL_WIDTH_CONNECTED = 25;
    int LOCAL_HEIGHT_CONNECTED = 25;
    // Remote video screen position
    int REMOTE_X = 0;
    int REMOTE_Y = 0;
    int REMOTE_WIDTH = 100;
    int REMOTE_HEIGHT = 100;

    String[] MANDATORY_PERMISSIONS = {"android.permission.MODIFY_AUDIO_SETTINGS",
            "android.permission.RECORD_AUDIO", "android.permission.INTERNET"};



    String USER_ROOM = "userRoom";


    int WS_CONNECT_START = 1001;
    int WS_CONNECT_SUCCESS = 1002;
    int WS_CONNECT_FAILED = 1003;

    int WS_LOGIN_START = 1011;
    int WS_LOGIN_SUCCESS = 1012;
    int WS_LOGIN_FAILED = 101;

    int WS_MSG = 1021;

    int WS_CLOSING = 1031;
    int WS_CLOSED = 1032;

    //  0表示链接成功
    //  1表示登陆成功
    //  3表示hold (hungup)
    //  4表示重新链接
    //  5发送消息
    //  6表示呼叫
    int ACTION_CONNECT_SUCCESS = 0;    //连接成功
    int ACTION_LOGIN = 1;              //登录
    int ACTION_LOGOUT = 2;             //登出
    int ACTION_CALL_VOICE = 3;         //语音呼叫
    int ACTION_HOLD = 4;               //hold
    int ACTION_HUNG_UP = 5;            //挂断
    int ACTION_CALL_VIDEO = 6;         //音视频呼叫
    int ACTION_USER_NOT_EXIST = 7;     //对方空号
    int ACTION_BUSY = 8;               //对方忙
    int ACTION_AGREE = 9;              //接听
    int ACTION_REFUSE = 10;            //拒绝
    int ACTION_ERROR = -1;             //错误

    String INTENT_CALL_SENDER_ROOM_NUMBER = "INTENT_CALL_SENDER_ROOM_NUMBER";
    String INTENT_CALL_RECEIVER_ROOM_NUMBER = "INTENT_CALL_RECEIVER_ROOM_NUMBER";
    String INTENT_FROM_PAD = "INTENT_FROM_PAD";
    String INTENT_CALL_TYPE = "INTENT_CALL_TYPE";
    String INTENT_CALL_BY_USER = "INTENT_CALL_BY_USER";

    int CAPTURE_PERMISSION_REQUEST_CODE = 901;

    String URL_POST = "http://119.23.251.238:8089";

    long TIMER = 120000;
}
