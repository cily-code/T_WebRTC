package com.cily.utils.t_webrtc.pad;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.cily.utils.app.ac.BaseActivity;
import com.cily.utils.app.event.Event;
import com.cily.utils.app.utils.L;
import com.cily.utils.app.utils.SpUtils;
import com.cily.utils.base.RandomUtils;
import com.cily.utils.t_webrtc.BaseAc;
import com.cily.utils.t_webrtc.BuildConfig;
import com.cily.utils.t_webrtc.CallAc;
import com.cily.utils.t_webrtc.Conf;
import com.cily.utils.t_webrtc.R;
import com.cily.utils.t_webrtc.TestAc;
import com.cily.utils.t_webrtc.TestAc3;


public class PadAc extends BaseAc {
    private static final int CONNECTION_REQUEST = 1;

    EditText ed_room_num;
    private boolean commandLineRun = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_pad);

        ed_room_num = findView(R.id.ed_input_room_num_id);
        ed_room_num.setText(RandomUtils.getRandomStr(6, RandomUtils.NUM));

        findBtn(R.id.btn_call).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                connectRoom(ed_room_num.getText().toString().trim(), false, false, false, 0);
            }
        });

        findBtn(R.id.btn_barcode).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                debugToast("btn_barcode");

//                toTest(ed_room_num.getText().toString().trim(), false);
                toAc(TestAc3.class, null);
            }
        });

        final Intent intent = getIntent();
        if ("android.intent.action.VIEW".equals(intent.getAction()) && !commandLineRun) {
            boolean loopback = intent.getBooleanExtra(Conf.EXTRA_LOOPBACK, false);
            int runTimeMs = intent.getIntExtra(Conf.EXTRA_RUNTIME, 0);
            boolean useValuesFromIntent =
                    intent.getBooleanExtra(Conf.EXTRA_USE_VALUES_FROM_INTENT, false);
            String room = SpUtils.getStr(this, getString(R.string.pref_room_key), "");
            connectRoom(room, true, loopback, useValuesFromIntent, runTimeMs);
        }
    }



    @Override
    protected void disconnect() {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CONNECTION_REQUEST && commandLineRun) {
            L.i(TAG, "Return: " + resultCode);
            setResult(resultCode);
            commandLineRun = false;
            finish();
        }
    }

    private void connectRoom(String roomId, boolean commandLineRun,
                             boolean loopBack, boolean useValueFromIntent,
                             int runTimeMs) {

        this.commandLineRun = commandLineRun;

        if (loopBack) {
            roomId = RandomUtils.getRandomStr(100000000, RandomUtils.NUM);
        }

        boolean videoCallEnable = getBoolean(R.string.pref_videocall_key, Conf.EXTRA_VIDEO_CALL,
                R.string.pref_videocall_default, useValueFromIntent);

        boolean useScreenCapture = getBoolean(R.string.pref_screencapture_key,
                Conf.EXTRA_SCREENCAPTURE, R.string.pref_screencapture_default, useValueFromIntent);

        boolean useCamera2 = getBoolean(R.string.pref_camera2_key, Conf.EXTRA_CAMERA2,
                R.string.pref_camera2_default, useValueFromIntent);

        String videoCodec = getStr(R.string.pref_videocodec_key, Conf.EXTRA_VIDEOCODEC,
                R.string.pref_videocodec_default, useValueFromIntent);

        String audioCodec = getStr(R.string.pref_audiocodec_key,
                Conf.EXTRA_AUDIOCODEC, R.string.pref_audiocodec_default, useValueFromIntent);

        // Check HW codec flag.
        boolean hwCodec = getBoolean(R.string.pref_hwcodec_key,
                Conf.EXTRA_HWCODEC_ENABLED, R.string.pref_hwcodec_default, useValueFromIntent);

        // Check Capture to texture.
        boolean captureToTexture = getBoolean(R.string.pref_capturetotexture_key,
                Conf.EXTRA_CAPTURETOTEXTURE_ENABLED, R.string.pref_capturetotexture_default,
                useValueFromIntent);

        // Check FlexFEC.
        boolean flexfecEnabled = getBoolean(R.string.pref_flexfec_key,
                Conf.EXTRA_FLEXFEC_ENABLED, R.string.pref_flexfec_default, useValueFromIntent);

        // Check Disable Audio Processing flag.
        boolean noAudioProcessing = getBoolean(R.string.pref_noaudioprocessing_key,
                Conf.EXTRA_NOAUDIOPROCESSING_ENABLED, R.string.pref_noaudioprocessing_default,
                useValueFromIntent);

        // Check Disable Audio Processing flag.
        boolean aecDump = getBoolean(R.string.pref_aecdump_key,
                Conf.EXTRA_AECDUMP_ENABLED, R.string.pref_aecdump_default, useValueFromIntent);

        // Check OpenSL ES enabled flag.
        boolean useOpenSLES = getBoolean(R.string.pref_opensles_key,
                Conf.EXTRA_OPENSLES_ENABLED, R.string.pref_opensles_default, useValueFromIntent);

        // Check Disable built-in AEC flag.
        boolean disableBuiltInAEC = getBoolean(R.string.pref_disable_built_in_aec_key,
                Conf.EXTRA_DISABLE_BUILT_IN_AEC, R.string.pref_disable_built_in_aec_default,
                useValueFromIntent);

        // Check Disable built-in AGC flag.
        boolean disableBuiltInAGC = getBoolean(R.string.pref_disable_built_in_agc_key,
                Conf.EXTRA_DISABLE_BUILT_IN_AGC, R.string.pref_disable_built_in_agc_default,
                useValueFromIntent);

        // Check Disable built-in NS flag.
        boolean disableBuiltInNS = getBoolean(R.string.pref_disable_built_in_ns_key,
                Conf.EXTRA_DISABLE_BUILT_IN_NS, R.string.pref_disable_built_in_ns_default,
                useValueFromIntent);

        // Check Enable level control.
        boolean enableLevelControl = getBoolean(R.string.pref_enable_level_control_key,
                Conf.EXTRA_ENABLE_LEVEL_CONTROL, R.string.pref_enable_level_control_key,
                useValueFromIntent);

        int videoWidth = 0;
        int videoHeight = 0;
        if (useValueFromIntent) {
            videoWidth = getIntent().getIntExtra(Conf.EXTRA_VIDEO_WIDTH, 0);
            videoHeight = getIntent().getIntExtra(Conf.EXTRA_VIDEO_HEIGHT, 0);
        }

        if (videoWidth == 0 && videoHeight == 0) {
            String resolution =
                    SpUtils.getStr(this, getString(R.string.pref_resolution_key), getString(R.string.pref_resolution_default));
            String[] dimensions = resolution.split("[ x]+");
            if (dimensions.length == 2) {
                try {
                    videoWidth = Integer.parseInt(dimensions[0]);
                    videoHeight = Integer.parseInt(dimensions[1]);
                } catch (NumberFormatException e) {
                    videoWidth = 0;
                    videoHeight = 0;
                    L.w(TAG, "Wrong video resolution setting: " + resolution);
                    L.printException(e);
                }
            }
        }

        int cameraFps = 0;
        if (useValueFromIntent) {
            cameraFps = getIntent().getIntExtra(Conf.EXTRA_VIDEO_FPS, 0);
        }
        if (cameraFps == 0) {
            String fps = SpUtils.getStr(this, getString(R.string.pref_fps_key),
                    getString(R.string.pref_fps_default));

            String[] fpsValues = fps.split("[ x]+");
            if (fpsValues.length == 2) {
                try {
                    cameraFps = Integer.parseInt(fpsValues[0]);
                } catch (NumberFormatException e) {
                    cameraFps = 0;
                    L.w(TAG, "Wrong camera fps setting: " + fps);
                    L.printException(e);
                }
            }
        }

        // Check capture quality slider flag.
        boolean captureQualitySlider = getBoolean(R.string.pref_capturequalityslider_key,
                Conf.EXTRA_VIDEO_CAPTUREQUALITYSLIDER_ENABLED,
                R.string.pref_capturequalityslider_default, useValueFromIntent);

        // Get video and audio start bitrate.
        int videoStartBitrate = 0;
        if (useValueFromIntent) {
            videoStartBitrate = getIntent().getIntExtra(Conf.EXTRA_VIDEO_BITRATE, 0);
        }
        if (videoStartBitrate == 0) {
            String bitrateTypeDefault = getString(R.string.pref_maxvideobitrate_default);
            String bitrateType = SpUtils.getStr(this, getString(R.string.pref_maxvideobitrate_key),
                    bitrateTypeDefault);

            if (!bitrateType.equals(bitrateTypeDefault)) {
                String bitrateValue = SpUtils.getStr(this, getString(R.string.pref_maxvideobitratevalue_key),
                        getString(R.string.pref_maxvideobitratevalue_default));

                videoStartBitrate = Integer.parseInt(bitrateValue);
            }
        }

        int audioStartBitrate = 0;
        if (useValueFromIntent) {
            audioStartBitrate = getIntent().getIntExtra(Conf.EXTRA_AUDIO_BITRATE, 0);
        }
        if (audioStartBitrate == 0) {
            String bitrateTypeDefault = getString(R.string.pref_startaudiobitrate_default);
            String bitrateType = SpUtils.getStr(this,
                    getString(R.string.pref_startaudiobitrate_key), bitrateTypeDefault);

            if (!bitrateType.equals(bitrateTypeDefault)) {
                String bitrateValue = SpUtils.getStr(this,
                        getString(R.string.pref_startaudiobitratevalue_key),
                        getString(R.string.pref_startaudiobitratevalue_default));

                audioStartBitrate = Integer.parseInt(bitrateValue);
            }
        }

        // Check statistics display option.
        boolean displayHud = getBoolean(R.string.pref_displayhud_key,
                Conf.EXTRA_DISPLAY_HUD, R.string.pref_displayhud_default, useValueFromIntent);

        boolean tracing = getBoolean(R.string.pref_tracing_key, Conf.EXTRA_TRACING,
                R.string.pref_tracing_default, useValueFromIntent);

        // Get datachannel options
        boolean dataChannelEnabled = getBoolean(R.string.pref_enable_datachannel_key,
                Conf.EXTRA_DATA_CHANNEL_ENABLED, R.string.pref_enable_datachannel_default,
                useValueFromIntent);
        boolean ordered = getBoolean(R.string.pref_ordered_key, Conf.EXTRA_ORDERED,
                R.string.pref_ordered_default, useValueFromIntent);
        boolean negotiated = getBoolean(R.string.pref_negotiated_key,
                Conf.EXTRA_NEGOTIATED, R.string.pref_negotiated_default, useValueFromIntent);
        int maxRetrMs = getInt(R.string.pref_max_retransmit_time_ms_key,
                Conf.EXTRA_MAX_RETRANSMITS_MS, R.string.pref_max_retransmit_time_ms_default,
                useValueFromIntent);
        int maxRetr =
                getInt(R.string.pref_max_retransmits_key, Conf.EXTRA_MAX_RETRANSMITS,
                        R.string.pref_max_retransmits_default, useValueFromIntent);
        int id = getInt(R.string.pref_data_id_key, Conf.EXTRA_ID,
                R.string.pref_data_id_default, useValueFromIntent);
        String protocol = getStr(R.string.pref_data_protocol_key,
                Conf.EXTRA_PROTOCOL, R.string.pref_data_protocol_default, useValueFromIntent);

        // Start AppRTCMobile activity.
        String roomUrl = BuildConfig.URL_ROOM;

        L.i(TAG, "Connecting to room " + roomId + " at URL " + roomUrl);

        if (validateUrl(roomUrl)) {
            toCall(roomId, loopBack, videoCallEnable, useScreenCapture,
                    useCamera2, videoWidth, videoHeight, cameraFps, captureQualitySlider,
                    videoStartBitrate, videoCodec, hwCodec, captureToTexture, flexfecEnabled,
                    noAudioProcessing, aecDump, useOpenSLES, disableBuiltInAEC,
                    disableBuiltInAGC, disableBuiltInNS, enableLevelControl,
                    audioStartBitrate, audioCodec, displayHud, tracing,
                    commandLineRun, runTimeMs, dataChannelEnabled, ordered,
                    maxRetrMs, maxRetr, protocol, negotiated, id, useValueFromIntent
            );
        }
    }

    private void toTest(String roomId, boolean loopBack){
        if (loopBack) {
            roomId = RandomUtils.getRandomStr(100000000, RandomUtils.NUM);
        }

        Intent intent = new Intent(this, TestAc.class);
        intent.putExtra(Conf.EXTRA_ROOMID, roomId);
        intent.putExtra(Conf.EXTRA_LOOPBACK, loopBack);

        startActivity(intent);
    }

    private void toCall( String roomId, boolean loopBack,
                        boolean videoCallEnable, boolean useScreenCapture,
                        boolean useCamera2, int videoWidth, int videoHeight,
                        int cameraFps, boolean captureQualitySlider,
                        int videoStartBitrate, String videoCodec, boolean hwCodec,
                        boolean captureToTexture, boolean flexfecEnabled, boolean noAudioProcessing,
                        boolean aecDump, boolean useOpenSLES, boolean disableBuiltInAEC,
                        boolean disableBuiltInAGC, boolean disableBuiltInNS, boolean enableLevelControl,
                        int audioStartBitrate, String audioCodec, boolean displayHud, boolean tracing,
                        boolean commandLineRun, int runTimeMs, boolean dataChannelEnabled,
                        boolean ordered, int maxRetrMs, int maxRetr, String protocol, boolean negotiated,
                        int id, boolean useValueFromIntent) {

        Intent intent = new Intent(this, CallAc.class);
        intent.putExtra(Conf.EXTRA_ROOMID, roomId);
        intent.putExtra(Conf.EXTRA_LOOPBACK, loopBack);
        intent.putExtra(Conf.EXTRA_VIDEO_CALL, videoCallEnable);
        intent.putExtra(Conf.EXTRA_SCREENCAPTURE, useScreenCapture);
        intent.putExtra(Conf.EXTRA_CAMERA2, useCamera2);
        intent.putExtra(Conf.EXTRA_VIDEO_WIDTH, videoWidth);
        intent.putExtra(Conf.EXTRA_VIDEO_HEIGHT, videoHeight);
        intent.putExtra(Conf.EXTRA_VIDEO_FPS, cameraFps);
        intent.putExtra(Conf.EXTRA_VIDEO_CAPTUREQUALITYSLIDER_ENABLED, captureQualitySlider);
        intent.putExtra(Conf.EXTRA_VIDEO_BITRATE, videoStartBitrate);
        intent.putExtra(Conf.EXTRA_VIDEOCODEC, videoCodec);
        intent.putExtra(Conf.EXTRA_HWCODEC_ENABLED, hwCodec);
        intent.putExtra(Conf.EXTRA_CAPTURETOTEXTURE_ENABLED, captureToTexture);
        intent.putExtra(Conf.EXTRA_FLEXFEC_ENABLED, flexfecEnabled);
        intent.putExtra(Conf.EXTRA_NOAUDIOPROCESSING_ENABLED, noAudioProcessing);
        intent.putExtra(Conf.EXTRA_AECDUMP_ENABLED, aecDump);
        intent.putExtra(Conf.EXTRA_OPENSLES_ENABLED, useOpenSLES);
        intent.putExtra(Conf.EXTRA_DISABLE_BUILT_IN_AEC, disableBuiltInAEC);
        intent.putExtra(Conf.EXTRA_DISABLE_BUILT_IN_AGC, disableBuiltInAGC);
        intent.putExtra(Conf.EXTRA_DISABLE_BUILT_IN_NS, disableBuiltInNS);
        intent.putExtra(Conf.EXTRA_ENABLE_LEVEL_CONTROL, enableLevelControl);
        intent.putExtra(Conf.EXTRA_AUDIO_BITRATE, audioStartBitrate);
        intent.putExtra(Conf.EXTRA_AUDIOCODEC, audioCodec);
        intent.putExtra(Conf.EXTRA_DISPLAY_HUD, displayHud);
        intent.putExtra(Conf.EXTRA_TRACING, tracing);
        intent.putExtra(Conf.EXTRA_CMDLINE, commandLineRun);
        intent.putExtra(Conf.EXTRA_RUNTIME, runTimeMs);

        intent.putExtra(Conf.EXTRA_DATA_CHANNEL_ENABLED, dataChannelEnabled);

        if (dataChannelEnabled) {
            intent.putExtra(Conf.EXTRA_ORDERED, ordered);
            intent.putExtra(Conf.EXTRA_MAX_RETRANSMITS_MS, maxRetrMs);
            intent.putExtra(Conf.EXTRA_MAX_RETRANSMITS, maxRetr);
            intent.putExtra(Conf.EXTRA_PROTOCOL, protocol);
            intent.putExtra(Conf.EXTRA_NEGOTIATED, negotiated);
            intent.putExtra(Conf.EXTRA_ID, id);
        }

        if (useValueFromIntent) {
            if (getIntent().hasExtra(Conf.EXTRA_VIDEO_FILE_AS_CAMERA)) {
                String videoFileAsCamera =
                        getIntent().getStringExtra(Conf.EXTRA_VIDEO_FILE_AS_CAMERA);
                intent.putExtra(Conf.EXTRA_VIDEO_FILE_AS_CAMERA, videoFileAsCamera);
            }

            if (getIntent().hasExtra(Conf.EXTRA_SAVE_REMOTE_VIDEO_TO_FILE)) {
                String saveRemoteVideoToFile =
                        getIntent().getStringExtra(Conf.EXTRA_SAVE_REMOTE_VIDEO_TO_FILE);
                intent.putExtra(Conf.EXTRA_SAVE_REMOTE_VIDEO_TO_FILE, saveRemoteVideoToFile);
            }

            if (getIntent().hasExtra(Conf.EXTRA_SAVE_REMOTE_VIDEO_TO_FILE_WIDTH)) {
                int videoOutWidth =
                        getIntent().getIntExtra(Conf.EXTRA_SAVE_REMOTE_VIDEO_TO_FILE_WIDTH, 0);
                intent.putExtra(Conf.EXTRA_SAVE_REMOTE_VIDEO_TO_FILE_WIDTH, videoOutWidth);
            }

            if (getIntent().hasExtra(Conf.EXTRA_SAVE_REMOTE_VIDEO_TO_FILE_HEIGHT)) {
                int videoOutHeight =
                        getIntent().getIntExtra(Conf.EXTRA_SAVE_REMOTE_VIDEO_TO_FILE_HEIGHT, 0);
                intent.putExtra(Conf.EXTRA_SAVE_REMOTE_VIDEO_TO_FILE_HEIGHT, videoOutHeight);
            }
        }

        startActivityForResult(intent, CONNECTION_REQUEST);
    }
}
