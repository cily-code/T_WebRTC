package com.cily.utils.t_webrtc.utils;

import android.content.Context;
import android.content.Intent;

import com.cily.utils.app.utils.L;
import com.cily.utils.app.utils.SpUtils;
import com.cily.utils.t_webrtc.Conf;
import com.cily.utils.t_webrtc.R;

/**
 * user:cily
 * time:2017/5/3
 * desc:
 */
public class SpAcUtils {
    private final static String TAG = SpAcUtils.class.getSimpleName();

    public static boolean videoCallEnable(Context cx, boolean useValueFromIntent, Intent i){
        return getBoolean(cx, R.string.pref_videocall_key, Conf.EXTRA_VIDEO_CALL,
                R.string.pref_videocall_default, useValueFromIntent, i);
    }

    public static boolean useScreenCapture(Context cx, boolean useValueFromIntent, Intent i){
        return getBoolean(cx, R.string.pref_screencapture_key, Conf.EXTRA_SCREENCAPTURE,
                R.string.pref_screencapture_default, useValueFromIntent, i);
    }

    public static boolean useCamera2(Context cx, boolean useValueFromIntent, Intent i){
        return getBoolean(cx, R.string.pref_camera2_key, Conf.EXTRA_CAMERA2,
                R.string.pref_camera2_default, useValueFromIntent, i);
    }

    public static String videoCodec(Context cx, boolean useValueFromIntent, Intent i){
        return getStr(cx, R.string.pref_videocodec_key, Conf.EXTRA_VIDEOCODEC,
                R.string.pref_videocodec_default, useValueFromIntent, i);
    }

    public static String audioCodec(Context cx, boolean useValueFromIntent, Intent i){
        return getStr(cx, R.string.pref_audiocodec_key, Conf.EXTRA_AUDIOCODEC,
                R.string.pref_audiocodec_default, useValueFromIntent, i);
    }

    public static boolean hwCodec(Context cx, boolean useValueFromIntent, Intent i){
        return getBoolean(cx, R.string.pref_hwcodec_key, Conf.EXTRA_HWCODEC_ENABLED,
                R.string.pref_hwcodec_default, useValueFromIntent, i);
    }

    public static boolean captureToTexture(Context cx, boolean useValueFromIntent, Intent i){
        return getBoolean(cx, R.string.pref_capturetotexture_key,
                Conf.EXTRA_CAPTURETOTEXTURE_ENABLED, R.string.pref_capturetotexture_default,
                useValueFromIntent, i);
    }

    public static boolean flexfecEnabled(Context cx, boolean useValueFromIntent, Intent i){
        return getBoolean(cx, R.string.pref_flexfec_key, Conf.EXTRA_FLEXFEC_ENABLED,
                R.string.pref_flexfec_default, useValueFromIntent, i);
    }

    public static boolean noAudioProcessing(Context cx, boolean useValueFromIntent, Intent i){
        return getBoolean(cx, R.string.pref_noaudioprocessing_key,
                Conf.EXTRA_NOAUDIOPROCESSING_ENABLED,
                R.string.pref_noaudioprocessing_default,
                useValueFromIntent, i);
    }

    public static boolean aecDump(Context cx, boolean useValueFromIntent, Intent i){
        return getBoolean(cx, R.string.pref_aecdump_key, Conf.EXTRA_AECDUMP_ENABLED,
                R.string.pref_aecdump_default, useValueFromIntent, i);
    }

    public static boolean useOpenSLES(Context cx, boolean useValueFromIntent, Intent i){
        return getBoolean(cx, R.string.pref_opensles_key, Conf.EXTRA_OPENSLES_ENABLED,
                R.string.pref_opensles_default, useValueFromIntent, i);
    }

    public static boolean disableBuiltInAEC(Context cx, boolean useValueFromIntent, Intent i){
        return getBoolean(cx, R.string.pref_disable_built_in_aec_key,
                Conf.EXTRA_DISABLE_BUILT_IN_AEC, R.string.pref_disable_built_in_aec_default,
                useValueFromIntent, i);
    }

    public static boolean disableBuiltInAGC(Context cx, boolean useValueFromIntent, Intent i){
        return getBoolean(cx, R.string.pref_disable_built_in_agc_key,
                Conf.EXTRA_DISABLE_BUILT_IN_AGC, R.string.pref_disable_built_in_agc_default,
                useValueFromIntent, i);
    }

    public static boolean disableBuiltInNS(Context cx, boolean useValueFromIntent, Intent i){
        return getBoolean(cx, R.string.pref_disable_built_in_ns_key,
                Conf.EXTRA_DISABLE_BUILT_IN_NS, R.string.pref_disable_built_in_ns_default,
                useValueFromIntent, i);
    }

    public static boolean enableLevelControl(Context cx, boolean useValueFromIntent, Intent i){
        return getBoolean(cx, R.string.pref_enable_level_control_key,
                Conf.EXTRA_ENABLE_LEVEL_CONTROL, R.string.pref_enable_level_control_key,
                useValueFromIntent, i);
    }

    public static boolean captureQualitySlider(Context cx, boolean useValueFromIntent, Intent i){
        return getBoolean(cx, R.string.pref_capturequalityslider_key,
                Conf.EXTRA_VIDEO_CAPTUREQUALITYSLIDER_ENABLED,
                R.string.pref_capturequalityslider_default, useValueFromIntent, i);
    }

    public static boolean displayHud(Context cx, boolean useValueFromIntent, Intent i){
        return getBoolean(cx, R.string.pref_displayhud_key, Conf.EXTRA_DISPLAY_HUD,
                R.string.pref_displayhud_default, useValueFromIntent, i);
    }

    public static boolean tracing(Context cx, boolean useValueFromIntent, Intent i){
        return getBoolean(cx, R.string.pref_tracing_key, Conf.EXTRA_TRACING,
                R.string.pref_tracing_default, useValueFromIntent, i);
    }

    public static boolean dataChannelEnabled(Context cx, boolean useValueFromIntent, Intent i){
        return getBoolean(cx, R.string.pref_enable_datachannel_key,
                Conf.EXTRA_DATA_CHANNEL_ENABLED, R.string.pref_enable_datachannel_default,
                useValueFromIntent, i);
    }

    public static boolean ordered(Context cx, boolean useValueFromIntent, Intent i){
        return getBoolean(cx, R.string.pref_ordered_key, Conf.EXTRA_ORDERED,
                R.string.pref_ordered_default, useValueFromIntent, i);
    }

    public static boolean negotiated(Context cx, boolean useValueFromIntent, Intent i){
        return getBoolean(cx, R.string.pref_negotiated_key,
                Conf.EXTRA_NEGOTIATED, R.string.pref_negotiated_default, useValueFromIntent, i);
    }

    public static int maxRetrMs(Context cx, boolean useValueFromIntent, Intent i){
        return getInt(cx, R.string.pref_max_retransmit_time_ms_key,
                Conf.EXTRA_MAX_RETRANSMITS_MS, R.string.pref_max_retransmit_time_ms_default,
                useValueFromIntent, i);
    }

    public static int maxRetr(Context cx, boolean useValueFromIntent, Intent i){
        return getInt(cx, R.string.pref_max_retransmits_key, Conf.EXTRA_MAX_RETRANSMITS,
                R.string.pref_max_retransmits_default, useValueFromIntent, i);
    }

    public static int id(Context cx, boolean useValueFromIntent, Intent i){
        return getInt(cx, R.string.pref_data_id_key, Conf.EXTRA_ID,
                R.string.pref_data_id_default, useValueFromIntent, i);
    }

    public static String protocol(Context cx, boolean useValueFromIntent, Intent i){
        return getStr(cx, R.string.pref_data_protocol_key, Conf.EXTRA_PROTOCOL,
                R.string.pref_data_protocol_default, useValueFromIntent, i);
    }

    public static String resolution(Context cx){
        return SpUtils.getStr(cx, cx.getString(R.string.pref_resolution_key),
                cx.getString(R.string.pref_resolution_default));
    }

    public static String fps(Context cx){
        return SpUtils.getStr(cx, cx.getString(R.string.pref_fps_key),
                cx.getString(R.string.pref_fps_default));
    }

    public static String bitrateType(Context cx){
        return SpUtils.getStr(cx, cx.getString(R.string.pref_maxvideobitrate_key),
                cx.getString(R.string.pref_maxvideobitrate_default));
    }

    public static String bitrateValue(Context cx){
        return SpUtils.getStr(cx, cx.getString(R.string.pref_maxvideobitratevalue_key),
                cx.getString(R.string.pref_maxvideobitratevalue_default));
    }

    public static int videoWidth(Context cx, boolean useValueFromIntent, Intent i){
        int videoWidth = 0;
        if (useValueFromIntent){
            return i.getIntExtra(Conf.EXTRA_VIDEO_WIDTH, 0);
        }

        if (videoWidth == 0){
            String resolution = SpUtils.getStr(cx,
                    cx.getString(R.string.pref_resolution_key),cx.getString(R.string.pref_resolution_default));

            String[] dimensions = resolution.split("[ x]+");
            if (dimensions.length == 2){
                try {
                    videoWidth = Integer.parseInt(dimensions[0]);
                }catch (NumberFormatException e){
                    L.printException(e);
                }
            }
        }
        return videoWidth;
    }

    public static int videoHeight(Context cx, boolean useValueFromIntent, Intent i){
        int videoWidth = 0;
        if (useValueFromIntent){
            return i.getIntExtra(Conf.EXTRA_VIDEO_HEIGHT, 0);
        }

        if (videoWidth == 0){
            String resolution = SpUtils.getStr(cx,
                    cx.getString(R.string.pref_resolution_key),cx.getString(R.string.pref_resolution_default));

            String[] dimensions = resolution.split("[ x]+");
            if (dimensions.length == 2){
                try {
                    videoWidth = Integer.parseInt(dimensions[1]);
                }catch (NumberFormatException e){
                    L.printException(e);
                }
            }
        }
        return videoWidth;
    }

    public static int cameraFps(Context cx, boolean useValueFromIntent, Intent i){
        int cameraFps = 0;
        if (useValueFromIntent){
            cameraFps = i.getIntExtra(Conf.EXTRA_VIDEO_FPS, 0);
        }

        if (cameraFps == 0){
            String fps = SpUtils.getStr(cx,
                    cx.getString(R.string.pref_fps_key), cx.getString(R.string.pref_fps_default));

            String[] fpsValues = fps.split("[ x]+");
            if (fpsValues.length == 2) {
                try {
                    cameraFps = Integer.parseInt(fpsValues[0]);
                } catch (NumberFormatException e) {
                    cameraFps = 0;
                    L.e(TAG, "Wrong camera fps setting: " + fps);
                }
            }
        }

        return cameraFps;
    }

    public static int videoStartBitrate(Context cx, boolean useValueFromIntent, Intent i){
        int videoStartBitrate = 0;
        if (useValueFromIntent){
            videoStartBitrate = i.getIntExtra(Conf.EXTRA_VIDEO_BITRATE, 0);
        }

        if (videoStartBitrate == 0){
            String bitrateType = bitrateType(cx);
            if (!bitrateType.equals(cx.getString(R.string.pref_maxvideobitrate_default))){
                String bitrateValue = bitrateValue(cx);
                try{
                    videoStartBitrate = Integer.parseInt(bitrateValue);
                }catch (NumberFormatException e){
                    L.printException(e);
                }
            }
        }
        return videoStartBitrate;
    }

    public static int audioStartBitrate(Context cx, boolean useValueFromIntent, Intent i){
        return getInt(cx, R.string.pref_startaudiobitrate_default,
                Conf.EXTRA_AUDIO_BITRATE, R.string.pref_startaudiobitratevalue_default,
                useValueFromIntent, i);
    }







    public static boolean getBoolean(Context cx, int attributeId, String intentName,
                                 int defaultId, boolean useFromIntent, Intent i) {

        boolean defaultValue = Boolean.valueOf(cx.getString(defaultId));
        if (useFromIntent) {
            return i == null ? defaultValue : i.getBooleanExtra(intentName, defaultValue);
        } else {
            String attributeName = cx.getString(attributeId);
            return SpUtils.getBoolean(cx, attributeName, defaultValue);
        }
    }

    public static String getStr(Context cx, int attributeId, String intentName,
                            int defaultId, boolean useFromIntent, Intent i) {
        String defaultValue = cx.getString(defaultId);
        if (useFromIntent) {
            if (i == null){
                return defaultValue;
            }
            String value = i.getStringExtra(intentName);
            if (value != null) {
                return value;
            }
            return defaultValue;
        } else {
            String attributeName = cx.getString(attributeId);
            return SpUtils.getStr(cx, attributeName, defaultValue);
        }
    }

    public static int getInt(Context cx, int attributeId, String intentName,
                             int defaultId, boolean useFromIntent, Intent i) {

        String defaultString = cx.getString(defaultId);
        int defaultValue = Integer.parseInt(defaultString);
        if (useFromIntent) {
            return i == null ? defaultValue : i.getIntExtra(intentName, defaultValue);
        } else {
            String attributeName = cx.getString(attributeId);
            String value = SpUtils.getStr(cx, attributeName, defaultString);
            try {
                return Integer.parseInt(value);
            } catch (NumberFormatException e) {
                L.w(TAG, "Wrong setting for: " + attributeName + ":" + value);
                L.printException(e);
                return defaultValue;
            }
        }
    }
}