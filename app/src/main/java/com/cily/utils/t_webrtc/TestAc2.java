package com.cily.utils.t_webrtc;

import android.annotation.TargetApi;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.view.View;

import com.cily.utils.app.event.Event;
import com.cily.utils.app.utils.L;
import com.cily.utils.t_webrtc.client.PeerConnectionClient;
import com.cily.utils.t_webrtc.fg.CallFragment;
import com.cily.utils.t_webrtc.fg.HudFragment;
import com.cily.utils.t_webrtc.impl.OnCallEvents;
import com.cily.utils.t_webrtc.impl.PeerConnectionEvents;
import com.cily.utils.t_webrtc.utils.AudioManagerUtils;
import com.cily.utils.t_webrtc.widget.PercentFrameLayout;

import org.webrtc.EglBase;
import org.webrtc.IceCandidate;
import org.webrtc.RendererCommon;
import org.webrtc.SessionDescription;
import org.webrtc.StatsReport;
import org.webrtc.SurfaceViewRenderer;
import org.webrtc.VideoFileRenderer;
import org.webrtc.VideoRenderer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TestAc2 extends BaseAc2 implements OnCallEvents {
    private SurfaceViewRenderer remoteRenderScreen;
    private PercentFrameLayout localRenderLayout;
    private PercentFrameLayout remoteRenderLayout;
    private CallFragment callFragment;
    private HudFragment hudFragment;

    private EglBase rootEglBase;

    private PeerConnectionClient peerConnectionClient = null;
    private final List<VideoRenderer.Callbacks> remoteRenderers = new ArrayList<VideoRenderer.Callbacks>();
    private SurfaceViewRenderer localRender;

    private boolean iceConnected;
    private boolean callControlFragmentVisible = true;
    private VideoFileRenderer videoFileRenderer;

    private boolean micEnabled = true;

    private RendererCommon.ScalingType scalingType;

    private long callStartedTimeMs = 0;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_call);

        Intent i = getIntent();

        String saveRemoteVideoToFile = i.getStringExtra(Conf.EXTRA_SAVE_REMOTE_VIDEO_TO_FILE);
        if (saveRemoteVideoToFile != null) {
            int videoOutWidth = i.getIntExtra(Conf.EXTRA_SAVE_REMOTE_VIDEO_TO_FILE_WIDTH, 0);
            int videoOutHeight = i.getIntExtra(Conf.EXTRA_SAVE_REMOTE_VIDEO_TO_FILE_HEIGHT, 0);
            try {
                videoFileRenderer = new VideoFileRenderer(
                        saveRemoteVideoToFile, videoOutWidth, videoOutHeight, rootEglBase.getEglBaseContext());
                remoteRenderers.add(videoFileRenderer);
            } catch (IOException e) {
                L.e(TAG, "Failed to open video file for output: " + saveRemoteVideoToFile, e);
                L.printException(e);
            }
        }

        AudioManagerUtils.create(this);
        AudioManagerUtils.start();

        callStartedTimeMs = System.currentTimeMillis();

        initUI();
        scalingType = RendererCommon.ScalingType.SCALE_ASPECT_FILL;
    }

    @Override
    protected void onDestroy() {
        rootEglBase.release();
        disconnect();
        super.onDestroy();
    }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void initUI() {
        localRender = (SurfaceViewRenderer) findViewById(R.id.local_video_view);
        remoteRenderScreen = (SurfaceViewRenderer) findViewById(R.id.remote_video_view);

        localRenderLayout = (PercentFrameLayout) findViewById(R.id.local_video_layout);
        remoteRenderLayout = (PercentFrameLayout) findViewById(R.id.remote_video_layout);
        callFragment = new CallFragment();
        hudFragment = new HudFragment();

        localRender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleCallControlFragmentVisibility();
            }
        });
        remoteRenderScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleCallControlFragmentVisibility();
            }
        });

        // Create video renderers.
        rootEglBase = EglBase.create();
        localRender.init(rootEglBase.getEglBaseContext(), null);


        peerConnectionClient = PeerConnectionClient.getInstance();
        peerConnectionClient.createPeerConnectionFactory(this, peerConnectionParameters, new PeerConnectionEvents() {
            @Override
            public void onLocalDescription(SessionDescription sdp) {
                L.i(TAG, "onLocalDescription");

                final long delta = System.currentTimeMillis() - callStartedTimeMs;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
//                        if (appRtcClient != null) {
//                            showToast("Sending " + sdp.type + ", delay=" + delta + "ms");
//                            if (signalingParameters.initiator) {
//                                appRtcClient.sendOfferSdp(sdp);
//                            } else {
//                                appRtcClient.sendAnswerSdp(sdp);
//                            }
//                        }
//                        if (peerConnectionParameters.videoMaxBitrate > 0) {
//                            L.d(TAG, "Set video maximum bitrate: " + peerConnectionParameters.videoMaxBitrate);
//                            peerConnectionClient.setVideoMaxBitrate(peerConnectionParameters.videoMaxBitrate);
//                        }
                    }
                });
            }

            @Override
            public void onIceCandidate(final IceCandidate candidate) {
                L.i(TAG, "onIceCandidate");

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
//                        if (appRtcClient != null) {
//                            appRtcClient.sendLocalIceCandidate(candidate);
//                        }
                    }
                });
            }

            @Override
            public void onIceCandidatesRemoved(final IceCandidate[] candidates) {
                L.i(TAG, "onIceCandidatesRemoved");

//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        if (appRtcClient != null) {
//                            appRtcClient.sendLocalIceCandidateRemovals(candidates);
//                        }
//                    }
//                });
            }

            @Override
            public void onIceConnected() {
                L.i(TAG, "onIceConnected");

                final long delta = System.currentTimeMillis() - callStartedTimeMs;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showToast("ICE connected, delay=" + delta + "ms");
                        iceConnected = true;
                        callConnected();
                    }
                });
            }

            @Override
            public void onIceDisconnected() {
                L.i(TAG, "onIceDisconnected");

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showToast("ICE disconnected");
                        iceConnected = false;
                        disconnect();
                    }
                });
            }

            @Override
            public void onPeerConnectionClosed() {
                L.i(TAG, "onPeerConnectionClosed");
            }

            @Override
            public void onPeerConnectionStatsReady(final StatsReport[] reports) {
                L.i(TAG, "onPeerConnectionStatsReady");

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (!isError && iceConnected) {
                            hudFragment.updateEncoderStatistics(reports);
                        }
                    }
                });
            }

            @Override
            public void onPeerConnectionError(String description) {
                L.i(TAG, "onPeerConnectionError");

                reportError(description);
            }
        });


        createPeerConnection(peerConnectionClient,rootEglBase.getEglBaseContext(),
                localRender, remoteRenderers, createVideoCapturer(), signalParams);

        // Send intent arguments to fragments.
        callFragment.setArguments(getIntent().getExtras());
        hudFragment.setArguments(getIntent().getExtras());
        // Activate call and HUD fragments and start the call.
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.add(R.id.call_fragment_container, callFragment);
        ft.add(R.id.hud_fragment_container, hudFragment);
        ft.commit();
    }

    // Should be called from UI thread
    private void callConnected() {
        final long delta = System.currentTimeMillis() - callStartedTimeMs;
        L.i(TAG, "Call connected: delay=" + delta + "ms");
        if (peerConnectionClient == null || isError) {
            L.w(TAG, "Call is connected in closed or error state");
            return;
        }
        // Update video view.
        updateVideoView();
        // Enable statistics callback.
        peerConnectionClient.enableStatsEvents(true, Conf.STAT_CALLBACK_PERIOD);
    }

    private void toggleCallControlFragmentVisibility() {
        if (!iceConnected || !callFragment.isAdded()) {
            return;
        }
        // Show/hide call control fragment
        callControlFragmentVisible = !callControlFragmentVisible;
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        if (callControlFragmentVisible) {
            ft.show(callFragment);
            ft.show(hudFragment);
        } else {
            ft.hide(callFragment);
            ft.hide(hudFragment);
        }
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.commit();
    }

    @Override
    protected void disconnect() {
        activityRunning = false;
//        if (appRtcClient != null) {
//            appRtcClient.disconnectFromRoom();
//            appRtcClient = null;
//        }
        if (peerConnectionClient != null) {
            peerConnectionClient.close();
            peerConnectionClient = null;
        }
        if (localRender != null) {
            localRender.release();
            localRender = null;
        }
        if (videoFileRenderer != null) {
            videoFileRenderer.release();
            videoFileRenderer = null;
        }
        if (remoteRenderScreen != null) {
            remoteRenderScreen.release();
            remoteRenderScreen = null;
        }

        AudioManagerUtils.stop();

        if (iceConnected && !isError) {
            setResult(RESULT_OK);
        } else {
            setResult(RESULT_CANCELED);
        }
        finish();
    }

    @Override
    public void onCallHangUp() {
        disconnect();
    }

    @Override
    public void onCameraSwitch() {
        if (peerConnectionClient != null) {
            peerConnectionClient.switchCamera();
        }
    }

    @Override
    public void onVideoScalingSwitch(RendererCommon.ScalingType scalingType) {
        this.scalingType = scalingType;
        updateVideoView();
    }

    @Override
    public void onCaptureFormatChange(int width, int height, int framerate) {
        if (peerConnectionClient != null) {
            peerConnectionClient.changeCaptureFormat(width, height, framerate);
        }
    }

    @Override
    public boolean onToggleMic() {
        if (peerConnectionClient != null) {
            micEnabled = !micEnabled;
            peerConnectionClient.setAudioEnabled(micEnabled);
        }
        return micEnabled;
    }

    private void updateVideoView() {
        remoteRenderLayout.setPosition(Conf.REMOTE_X, Conf.REMOTE_Y,
                Conf.REMOTE_WIDTH, Conf.REMOTE_HEIGHT);
        remoteRenderScreen.setScalingType(scalingType);
        remoteRenderScreen.setMirror(false);

        if (iceConnected) {
            localRenderLayout.setPosition(
                    Conf.LOCAL_X_CONNECTED, Conf.LOCAL_Y_CONNECTED,
                    Conf.LOCAL_WIDTH_CONNECTED, Conf.LOCAL_HEIGHT_CONNECTED);
            localRender.setScalingType(RendererCommon.ScalingType.SCALE_ASPECT_FIT);
        } else {
            localRenderLayout.setPosition(
                    Conf.LOCAL_X_CONNECTING, Conf.LOCAL_Y_CONNECTING,
                    Conf.LOCAL_WIDTH_CONNECTING, Conf.LOCAL_HEIGHT_CONNECTING);
            localRender.setScalingType(scalingType);
        }
        localRender.setMirror(true);

        localRender.requestLayout();
        remoteRenderScreen.requestLayout();
    }
}
