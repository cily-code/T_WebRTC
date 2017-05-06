package com.cily.utils.t_webrtc;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.projection.MediaProjection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.URLUtil;

import com.cily.utils.app.ac.BaseActivity;
import com.cily.utils.app.event.Event;
import com.cily.utils.app.utils.L;
import com.cily.utils.app.utils.SpUtils;
import com.cily.utils.base.StrUtils;
import com.cily.utils.t_webrtc.client.AppRTCAudioManager;
import com.cily.utils.t_webrtc.client.PeerConnectionClient;
import com.cily.utils.t_webrtc.event.RoomConnectionParameters;
import com.cily.utils.t_webrtc.event.SignalingParameters;
import com.cily.utils.t_webrtc.impl.ObserverImpl;
import com.cily.utils.t_webrtc.parameter.DataChannelParameters;
import com.cily.utils.t_webrtc.parameter.PeerConnectionParameters;
import com.cily.utils.t_webrtc.utils.ConnUtils;
import com.cily.utils.t_webrtc.utils.ObserverUtils;

import org.webrtc.Camera1Enumerator;
import org.webrtc.Camera2Enumerator;
import org.webrtc.CameraEnumerator;
import org.webrtc.EglBase;
import org.webrtc.FileVideoCapturer;
import org.webrtc.ScreenCapturerAndroid;
import org.webrtc.VideoCapturer;
import org.webrtc.VideoRenderer;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import static com.cily.utils.t_webrtc.Conf.EXTRA_VIDEO_FILE_AS_CAMERA;

/**
 * user:cily
 * time:2017/4/30
 * desc:
 */

public abstract class BaseAc extends BaseActivity implements ObserverImpl {
    protected final String[] MANDATORY_PERMISSIONS = {
            "android.permission.MODIFY_AUDIO_SETTINGS",
            "android.permission.RECORD_AUDIO",
            "android.permission.INTERNET"};

    String roomId;
    boolean loopback;
    boolean tracing;
    int videoWidth;
    int videoHeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Check for mandatory permissions.
        for (String permission : MANDATORY_PERMISSIONS) {
            if (checkCallingOrSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                showToast("Permission " + permission + " is not granted");
                setResult(RESULT_CANCELED);
                finish();
                return;
            }
        }


    }

    @Override
    protected void onStart() {
        super.onStart();
        ObserverUtils.getInstance().regist(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        ObserverUtils.getInstance().unregist(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    protected boolean getBoolean(int attributeId, String intentName,
                                 int defaultId, boolean useFromIntent) {

        boolean defaultValue = Boolean.valueOf(getString(defaultId));
        if (useFromIntent) {
            return getIntent().getBooleanExtra(intentName, defaultValue);
        } else {
            String attributeName = getString(attributeId);
            return SpUtils.getBoolean(this, attributeName, defaultValue);
        }
    }

    protected String getStr(int attributeId, String intentName,
                            int defaultId, boolean useFromIntent) {
        String defaultValue = getString(defaultId);
        if (useFromIntent) {
            String value = getIntent().getStringExtra(intentName);
            if (value != null) {
                return value;
            }
            return defaultValue;
        } else {
            String attributeName = getString(attributeId);
            return SpUtils.getStr(this, attributeName, defaultValue);
        }
    }

    protected int getInt(int attributeId, String intentName,
                         int defaultId, boolean useFromIntent) {

        String defaultString = getString(defaultId);
        int defaultValue = Integer.parseInt(defaultString);
        if (useFromIntent) {
            return getIntent().getIntExtra(intentName, defaultValue);
        } else {
            String attributeName = getString(attributeId);
            String value = SpUtils.getStr(this, attributeName, defaultString);
            try {
                return Integer.parseInt(value);
            } catch (NumberFormatException e) {
                L.w(TAG, "Wrong setting for: " + attributeName + ":" + value);
                L.printException(e);
                return defaultValue;
            }
        }
    }

    protected boolean validateUrl(String url) {
        if (URLUtil.isHttpsUrl(url) || URLUtil.isHttpUrl(url)) {
            return true;
        }

        new AlertDialog.Builder(this)
                .setTitle(getText(R.string.invalid_url_title))
                .setMessage(getString(R.string.invalid_url_text, url))
                .setCancelable(false)
                .setNeutralButton(R.string.ok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        })
                .create()
                .show();
        return false;
    }

    protected boolean isError;
    protected boolean screencaptureEnabled = false;
    protected static int mediaProjectionPermissionResultCode;
    protected static Intent mediaProjectionPermissionResultData;
    protected boolean commandLineRun;
    protected boolean activityRunning;
    protected PeerConnectionParameters peerConnectionParameters;

    protected RoomConnectionParameters roomConnectionParameters;
    DataChannelParameters dataChannelParameters = null;

    protected static SignalingParameters signalParams;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    protected VideoCapturer createVideoCapturer() {
        VideoCapturer videoCapturer = null;
        String videoFileAsCamera = getIntent().getStringExtra(EXTRA_VIDEO_FILE_AS_CAMERA);
        if (videoFileAsCamera != null) {
            try {
                videoCapturer = new FileVideoCapturer(videoFileAsCamera);
            } catch (IOException e) {
                reportError("Failed to open video file for emulated camera");
                L.printException(e);
                return null;
            }
        } else if (screencaptureEnabled) {
            if (mediaProjectionPermissionResultCode != Activity.RESULT_OK) {
                reportError("User didn't give permission to capture the screen.");
                return null;
            }
            return new ScreenCapturerAndroid(
                    mediaProjectionPermissionResultData, new MediaProjection.Callback() {
                @Override
                public void onStop() {
                    reportError("User revoked permission to capture the screen.");
                }
            });
        } else if (useCamera2()) {
            if (!captureToTexture()) {
                reportError(getString(R.string.camera2_texture_only_error));
                return null;
            }

            L.d(TAG, "Creating capturer using camera2 API.");
            videoCapturer = createCameraCapturer(new Camera2Enumerator(this));
        } else {
            L.d(TAG, "Creating capturer using camera1 API.");
            videoCapturer = createCameraCapturer(new Camera1Enumerator(captureToTexture()));
        }
        if (videoCapturer == null) {
            reportError("Failed to open camera");
            return null;
        }
        return videoCapturer;
    }

    protected boolean useCamera2() {
        return Camera2Enumerator.isSupported(this) && getIntent().getBooleanExtra(Conf.EXTRA_CAMERA2, true);
    }

    protected boolean captureToTexture() {
        return getIntent().getBooleanExtra(Conf.EXTRA_CAPTURETOTEXTURE_ENABLED, false);
    }

    protected void reportError(final String description) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (!isError) {
                    isError = true;
                    disconnectWithErrorMessage(description);
                }
            }
        });
    }

    protected void disconnectWithErrorMessage(final String errorMessage) {
        if (commandLineRun || !activityRunning) {
            L.e(TAG, "Critical error: " + errorMessage);
            disconnect();
        } else {
            new AlertDialog.Builder(this)
                    .setTitle(getText(R.string.channel_error_title))
                    .setMessage(errorMessage)
                    .setCancelable(false)
                    .setNeutralButton(R.string.ok,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                    disconnect();
                                }
                            })
                    .create()
                    .show();
        }
    }

    protected VideoCapturer createCameraCapturer(CameraEnumerator enumerator) {
        final String[] deviceNames = enumerator.getDeviceNames();

        // First, try to find front facing camera
        L.d(TAG, "Looking for front facing cameras.");
        for (String deviceName : deviceNames) {
            if (enumerator.isFrontFacing(deviceName)) {
                L.d(TAG, "Creating front facing camera capturer.");
                VideoCapturer videoCapturer = enumerator.createCapturer(deviceName, null);

                if (videoCapturer != null) {
                    return videoCapturer;
                }
            }
        }

        // Front facing camera not found, try something else
        L.d(TAG, "Looking for other cameras.");
        for (String deviceName : deviceNames) {
            if (!enumerator.isFrontFacing(deviceName)) {
                L.d(TAG, "Creating other camera capturer.");
                VideoCapturer videoCapturer = enumerator.createCapturer(deviceName, null);

                if (videoCapturer != null) {
                    return videoCapturer;
                }
            }
        }

        return null;
    }



    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    protected void createPeerConnection(PeerConnectionClient peerConnectionClient,
                                        EglBase.Context cx, VideoRenderer.Callbacks localRender,
                                        List<VideoRenderer.Callbacks> remoteRenders,
                                        VideoCapturer videoCapturer, SignalingParameters params){

        peerConnectionClient.createPeerConnection(cx, localRender, remoteRenders,
                videoCapturer, params);
    }

    protected String messageUrl, leaveUrl;
    protected void signalingParametersReady(SignalingParameters signalingParameters){
        L.d(TAG, "Room connection completed.");

        if (roomConnectionParameters == null){
            L.w(TAG, "The RoomConnectionParameters is null!!!");
            return;
        }
        if (roomConnectionParameters.loopback
                && (!signalingParameters.initiator
                || signalingParameters.offerSdp != null)) {

            reportError("Loopback room is busy.");
            return;
        }

        if (!roomConnectionParameters.loopback && !signalingParameters.initiator
                && signalingParameters.offerSdp == null) {
            L.w(TAG, "No offer SDP in room response.");
        }
        messageUrl = ConnUtils.getMessageUrl(roomConnectionParameters, signalingParameters);
        leaveUrl = ConnUtils.getLeaveUrl(roomConnectionParameters, signalingParameters);


    }

    protected abstract void disconnect();

    @Override
    public void doEvent(Event e) {
        L.i(TAG, "收到event事件");
        doRxEvent(e);
    }

    protected void doRxEvent(Event e){

    }
}
