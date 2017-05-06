package com.cily.utils.t_webrtc;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.cily.utils.t_webrtc.event.RoomConnectionParameters;
import com.cily.utils.t_webrtc.impl.OnCallEvents;
import com.cily.utils.t_webrtc.parameter.DataChannelParameters;
import com.cily.utils.t_webrtc.parameter.PeerConnectionParameters;

/**
 * user:cily
 * time:2017/5/1
 * desc:
 */

public abstract class BaseAc2 extends BaseAc {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

        initIntent();
    }

    private void initIntent(){
        Intent i = getIntent();
        roomId = getIntent().getStringExtra(Conf.EXTRA_ROOMID);
        loopback = getIntent().getBooleanExtra(Conf.EXTRA_LOOPBACK, false);

        videoWidth = i.getIntExtra(Conf.EXTRA_VIDEO_WIDTH, 0);
        videoHeight = i.getIntExtra(Conf.EXTRA_VIDEO_HEIGHT, 0);

        if (i.getBooleanExtra(Conf.EXTRA_DATA_CHANNEL_ENABLED, true)) {
            dataChannelParameters = new DataChannelParameters(i.getBooleanExtra(Conf.EXTRA_ORDERED, true),
                    i.getIntExtra(Conf.EXTRA_MAX_RETRANSMITS_MS, -1),
                    i.getIntExtra(Conf.EXTRA_MAX_RETRANSMITS, -1), i.getStringExtra(Conf.EXTRA_PROTOCOL),
                    i.getBooleanExtra(Conf.EXTRA_NEGOTIATED, false), i.getIntExtra(Conf.EXTRA_ID, -1));
        }

        roomConnectionParameters = new RoomConnectionParameters(BuildConfig.URL_ROOM,
                roomId, loopback);
        peerConnectionParameters =
                new PeerConnectionParameters(i.getBooleanExtra(Conf.EXTRA_VIDEO_CALL, true), loopback,
                        tracing, videoWidth, videoHeight, i.getIntExtra(Conf.EXTRA_VIDEO_FPS, 0),
                        i.getIntExtra(Conf.EXTRA_VIDEO_BITRATE, 0), i.getStringExtra(Conf.EXTRA_VIDEOCODEC),
                        i.getBooleanExtra(Conf.EXTRA_HWCODEC_ENABLED, true),
                        i.getBooleanExtra(Conf.EXTRA_FLEXFEC_ENABLED, false),
                        i.getIntExtra(Conf.EXTRA_AUDIO_BITRATE, 0), i.getStringExtra(Conf.EXTRA_AUDIOCODEC),
                        i.getBooleanExtra(Conf.EXTRA_NOAUDIOPROCESSING_ENABLED, false),
                        i.getBooleanExtra(Conf.EXTRA_AECDUMP_ENABLED, false),
                        i.getBooleanExtra(Conf.EXTRA_OPENSLES_ENABLED, false),
                        i.getBooleanExtra(Conf.EXTRA_DISABLE_BUILT_IN_AEC, false),
                        i.getBooleanExtra(Conf.EXTRA_DISABLE_BUILT_IN_AGC, false),
                        i.getBooleanExtra(Conf.EXTRA_DISABLE_BUILT_IN_NS, false),
                        i.getBooleanExtra(Conf.EXTRA_ENABLE_LEVEL_CONTROL, false), dataChannelParameters);

    }

}
