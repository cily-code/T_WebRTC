package com.cily.utils.t_webrtc.pad;

import android.annotation.TargetApi;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.cily.utils.app.utils.L;
import com.cily.utils.app.utils.ScreenUtils;
import com.cily.utils.app.utils.SpUtils;
import com.cily.utils.base.StrUtils;
import com.cily.utils.media.Player;
import com.cily.utils.t_webrtc.BaseAc3;
import com.cily.utils.t_webrtc.BuildConfig;
import com.cily.utils.t_webrtc.Conf;
import com.cily.utils.t_webrtc.R;
import com.cily.utils.t_webrtc.bean.ActionBean;
import com.cily.utils.t_webrtc.client.CpuMonitor;
import com.cily.utils.t_webrtc.client.PeerConnectionClient;
import com.cily.utils.t_webrtc.event.RoomConnectionParameters;
import com.cily.utils.t_webrtc.event.SignalingParameters;
import com.cily.utils.t_webrtc.fg.CallFragment;
import com.cily.utils.t_webrtc.fg.HudFragment;
import com.cily.utils.t_webrtc.impl.OnCallEvents;
import com.cily.utils.t_webrtc.impl.PeerConnectionEvents;
import com.cily.utils.t_webrtc.impl.SignalingEvents;
import com.cily.utils.t_webrtc.impl.VideoCaptureImpl;
import com.cily.utils.t_webrtc.parameter.DataChannelParameters;
import com.cily.utils.t_webrtc.parameter.PeerConnectionParameters;
import com.cily.utils.t_webrtc.utils.AudioManagerUtils;
import com.cily.utils.t_webrtc.utils.SpAcUtils;
import com.cily.utils.t_webrtc.utils.StrMsgUtils;
import com.cily.utils.t_webrtc.utils.Utils;
import com.cily.utils.t_webrtc.utils.WsUtils;
import com.cily.utils.t_webrtc.widget.PercentFrameLayout;

import org.webrtc.EglBase;
import org.webrtc.IceCandidate;
import org.webrtc.Logging;
import org.webrtc.PeerConnectionFactory;
import org.webrtc.RendererCommon;
import org.webrtc.RendererCommon.ScalingType;
import org.webrtc.SessionDescription;
import org.webrtc.StatsReport;
import org.webrtc.SurfaceViewRenderer;
import org.webrtc.VideoCapturer;
import org.webrtc.VideoFileRenderer;
import org.webrtc.VideoRenderer;

import java.util.ArrayList;
import java.util.List;

public class VideoAc extends BaseAc3 implements OnCallEvents, SignalingEvents {
    private int type_call;
    private ScalingType scalingType = ScalingType.SCALE_ASPECT_FILL;

    private SurfaceViewRenderer localRender;
    private SurfaceViewRenderer remoteRenderScreen;
    private VideoFileRenderer videoFileRenderer;
    private final List<VideoRenderer.Callbacks> remoteRenderers = new ArrayList<VideoRenderer.Callbacks>();
    private PercentFrameLayout localRenderLayout;
    private PercentFrameLayout remoteRenderLayout;
    private CallFragment callFragment;
    private HudFragment hudFragment;
    private EglBase rootEglBase;

    private boolean iceConnected = false;
    private boolean commandLineRun = false;
    private long runTimeMs = 0;
    private boolean screencaptureEnabled;

    private boolean useValueFromIntent = false;

    private PeerConnectionParameters peerParam;
    private RoomConnectionParameters roomConnParam;
    private CpuMonitor cpuMonitor;
    private PeerConnectionClient peerClient;

    private String roomId;  //被呼叫者的物理房间号
    private String userRoom;
    private boolean fromPad = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

        setContentView(R.layout.ac_call);
        initIntent();

        initUI();
        initData();
        initMedia();

        checkPermission();

//        WsUtils.sendMsg(StrMsgUtils.agree(userRoom, roomId));
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (peerClient != null && !screencaptureEnabled){
            peerClient.startVideoSource();
        }
        cpuMonitor.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (peerClient != null && !screencaptureEnabled) {
            peerClient.stopVideoSource();
        }
        cpuMonitor.pause();
    }

    @Override
    protected void onStop() {
        super.onStop();

        WsUtils.sendMsg(StrMsgUtils.hungup(userRoom, roomId));

        AudioManagerUtils.stop();
    }

    private void initIntent() {
        roomId = getIntent().getStringExtra(Conf.EXTRA_ROOMID);
        if (StrUtils.isEmpty(roomId)) {
            showToast("请输入房间号");
            finish();
        }

        type_call = getIntent().getIntExtra(Conf.INTENT_CALL_TYPE, Conf.ACTION_CALL_VIDEO);
        fromPad = getIntent().getBooleanExtra(Conf.INTENT_FROM_PAD, false);
        if (fromPad){
            initiator = true;
        }

        userRoom = SpUtils.getStr(this, Conf.USER_ROOM, "");
        roomId = getIntent().getStringExtra(Conf.EXTRA_ROOMID);
    }

    private void initUI() {
        localRender = findView(R.id.local_video_view);
        localRender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                toggleCallControlFragmentVisibility();
            }
        });

        remoteRenderScreen = findView(R.id.remote_video_view);
        remoteRenderScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                toggleCallControlFragmentVisibility();
            }
        });

        localRenderLayout = findView(R.id.local_video_layout);
        remoteRenderLayout = findView(R.id.remote_video_layout);

        remoteRenderers.add(remoteRenderScreen);

        Bundle bundle = new Bundle();
        if (fromPad){
            bundle.putString(Conf.EXTRA_ROOMID, roomId);
        }else{
            bundle.putString(Conf.EXTRA_ROOMID, userRoom);
        }
        callFragment = new CallFragment();
        callFragment.setArguments(bundle);
        hudFragment = new HudFragment();
        hudFragment.setArguments(bundle);

        rootEglBase = EglBase.create();
        localRender.init(rootEglBase.getEglBaseContext(), null);
        remoteRenderScreen.init(rootEglBase.getEglBaseContext(), null);

        localRender.setZOrderMediaOverlay(true);
        localRender.setEnableHardwareScaler(true);

        updateVideoView();
    }

    private void initData() {
        Intent i = getIntent();

        boolean tracing = SpAcUtils.tracing(this, useValueFromIntent, i);

        int videoWidth = SpAcUtils.videoWidth(this, useValueFromIntent, i);
        int videoHeight = SpAcUtils.videoHeight(this, useValueFromIntent, i);

        screencaptureEnabled = SpAcUtils.useScreenCapture(this, useValueFromIntent, i);
        if (screencaptureEnabled && videoWidth == 0 && videoHeight == 0) {
            videoWidth = ScreenUtils.getScreenWidth(this);
            videoHeight = ScreenUtils.getScreenHeight(this);
        }

        DataChannelParameters dateChannel = null;
        boolean dataChannelEnabled = SpAcUtils.dataChannelEnabled(this, useValueFromIntent, i);
        if (dataChannelEnabled) {
            boolean ordered = SpAcUtils.ordered(this, useValueFromIntent, i);
            int maxRetrMs = SpAcUtils.maxRetrMs(this, useValueFromIntent, i);
            int maxRetr = SpAcUtils.maxRetr(this, useValueFromIntent, i);
            String protocol = SpAcUtils.protocol(this, useValueFromIntent, i);
            boolean negotiated = SpAcUtils.negotiated(this, useValueFromIntent, i);
            int id = SpAcUtils.id(this, useValueFromIntent, i);

            dateChannel = new DataChannelParameters(ordered, maxRetrMs, maxRetr,
                    protocol, negotiated, id);
        }

        boolean videoCallEnabled = SpAcUtils.videoCallEnable(this, useValueFromIntent, i);
        int cameraFps = SpAcUtils.cameraFps(this, useValueFromIntent, i);
        int videoStartBitrate = SpAcUtils.videoStartBitrate(this, useValueFromIntent, i);
        String videoCodec = SpAcUtils.videoCodec(this, useValueFromIntent, i);
        boolean hwCodec = SpAcUtils.hwCodec(this, useValueFromIntent, i);
        boolean flexfecEnabled = SpAcUtils.flexfecEnabled(this, useValueFromIntent, i);
        int audioStartBitrate = SpAcUtils.audioStartBitrate(this, useValueFromIntent, i);
        String audioCodec = SpAcUtils.audioCodec(this, useValueFromIntent, i);
        boolean noAudioProcessing = SpAcUtils.noAudioProcessing(this, useValueFromIntent, i);
        boolean aecDump = SpAcUtils.aecDump(this, useValueFromIntent, i);
        boolean useOpenSLES = SpAcUtils.useOpenSLES(this, useValueFromIntent, i);
        boolean disableBuiltInAEC = SpAcUtils.disableBuiltInAEC(this, useValueFromIntent, i);
        boolean disableBuiltInAGC = SpAcUtils.disableBuiltInAGC(this, useValueFromIntent, i);
        boolean disableBuiltInNS = SpAcUtils.disableBuiltInNS(this, useValueFromIntent, i);
        boolean enableLevelControl = SpAcUtils.enableLevelControl(this, useValueFromIntent, i);

        peerParam = new PeerConnectionParameters(videoCallEnabled,
                Utils.loopback, tracing, videoWidth, videoHeight,
                cameraFps, videoStartBitrate, videoCodec, hwCodec,
                flexfecEnabled, audioStartBitrate, audioCodec,
                noAudioProcessing, aecDump, useOpenSLES,
                disableBuiltInAEC, disableBuiltInAGC,
                disableBuiltInNS, enableLevelControl, dateChannel);

        if (fromPad){
            roomConnParam = new RoomConnectionParameters(
                    BuildConfig.URL_ROOM, roomId, Utils.loopback);
        }else{
            roomConnParam = new RoomConnectionParameters(
                    BuildConfig.URL_ROOM, userRoom, Utils.loopback);
        }

        cpuMonitor = new CpuMonitor(this);
        hudFragment.setCpuMonitor(cpuMonitor);

        init();

    }

    private IceCandidate iceCandi;
    SessionDescription sdpe;
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void init() {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.add(R.id.call_fragment_container, callFragment);
        ft.add(R.id.hud_fragment_container, hudFragment);
        ft.commit();

        if (commandLineRun && runTimeMs > 0) {
            //TODO
            finish();
        }

        peerClient = PeerConnectionClient.getInstance();

        if (Utils.loopback) {
            PeerConnectionFactory.Options options = new PeerConnectionFactory.Options();
            options.networkIgnoreMask = 0;
            peerClient.setPeerConnectionFactoryOptions(options);
        }

        peerClient.createPeerConnectionFactory(this, peerParam, new PeerConnectionEvents() {
            @Override
            public void onLocalDescription(SessionDescription sdp) {
                L.i(TAG, "createPeerConnectionFactory onLocalDescription sdp = " + sdp.toString());
                //交换sdp信息
                sdpe = sdp;
                sendCall();
            }

            @Override
            public void onIceCandidate(IceCandidate candidate) {
                L.i(TAG, "createPeerConnectionFactory onIceCandidate candidate = " + candidate.toString());
                //给对方发送IceCandidate
                iceCandi = candidate;

            }

            @Override
            public void onIceCandidatesRemoved(IceCandidate[] candidates) {
                L.i(TAG, "createPeerConnectionFactory onIceCandidatesRemoved candidates = " + candidates.length);
                //给对方发送IceCandidate被移除的指令
            }

            @Override
            public void onIceConnected() {
                L.i(TAG, "createPeerConnectionFactory onIceConnected ");
                iceConnected = true;
                //连接成功
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        updateVideoView();
                    }
                });
            }

            @Override
            public void onIceDisconnected() {
                L.i(TAG, "createPeerConnectionFactory onIceDisconnected ");
                //ice断开连接
                iceConnected = false;
            }

            @Override
            public void onPeerConnectionClosed() {
                L.i(TAG, "createPeerConnectionFactory onPeerConnectionClosed ");
            }

            @Override
            public void onPeerConnectionStatsReady(final StatsReport[] reports) {
                L.i(TAG, "createPeerConnectionFactory onPeerConnectionStatsReady reports = " + reports.length);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        hudFragment.updateEncoderStatistics(reports);
                    }
                });
            }

            @Override
            public void onPeerConnectionError(String description) {
                L.i(TAG, "createPeerConnectionFactory onPeerConnectionError description = " + description);
            }
        });
        peerClient.createOffer();

        if (screencaptureEnabled) {
            MediaProjectionManager mpm = (MediaProjectionManager) getSystemService(
                    Context.MEDIA_PROJECTION_SERVICE);

            startActivityForResult(mpm.createScreenCaptureIntent(),
                    Conf.CAPTURE_PERMISSION_REQUEST_CODE);
        }
    }

    private void sendCall(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (fromPad){
                    startTimer();
                    Utils.sendCall(peerParam.loopback, userRoom, roomId, sdpe, iceCandi, initiator, VideoAc.this);
                }else{
                    Utils.senAnswer(peerParam.loopback, userRoom, roomId,  sdpe);
                }
            }
        });
    }

    private final String TYPE_OFFER = "OFFER";
    private final String TYPE_ANSWER = "ANSWER";
    private String type_video = TYPE_OFFER;
    private boolean initiator = true;  //initiator是否是发起人
    private void startCall(){
        AudioManagerUtils.create(this);
        AudioManagerUtils.start();


        if (fromPad){
            type_video = TYPE_OFFER;
        }else{
            type_video = TYPE_ANSWER;
        }
        SignalingParameters params = Utils.createSignalingParameters(type_video, userRoom, initiator);
        connectRoomInternal(createVideoCapture(), params, peerClient, rootEglBase, localRender, remoteRenderers );

    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void initMedia() {
        if (screencaptureEnabled){
            MediaProjectionManager mediaProjectionManager =
                    (MediaProjectionManager) getApplication().getSystemService(
                            Context.MEDIA_PROJECTION_SERVICE);
            startActivityForResult(
                    mediaProjectionManager.createScreenCaptureIntent(), Conf.CAPTURE_PERMISSION_REQUEST_CODE);
        }else{
            startCall();
        }
    }

    private void disconnect() {
        if (peerClient != null) {
            peerClient.close();
            peerClient = null;
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
        if (rootEglBase != null){
            rootEglBase.release();
        }
    }

    private void updateVideoView() {
        remoteRenderLayout.setPosition(Conf.REMOTE_X,
                Conf.REMOTE_Y, Conf.REMOTE_WIDTH, Conf.REMOTE_HEIGHT);

        remoteRenderScreen.setScalingType(scalingType);
        remoteRenderScreen.setMirror(false);

        if (iceConnected) {
            localRenderLayout.setPosition(Conf.LOCAL_X_CONNECTED,
                    Conf.LOCAL_Y_CONNECTED, Conf.LOCAL_WIDTH_CONNECTED,
                    Conf.LOCAL_HEIGHT_CONNECTED);

            localRender.setScalingType(ScalingType.SCALE_ASPECT_FIT);
        } else {
            localRenderLayout.setPosition(Conf.LOCAL_X_CONNECTING,
                    Conf.LOCAL_Y_CONNECTING, Conf.LOCAL_WIDTH_CONNECTING,
                    Conf.LOCAL_HEIGHT_CONNECTING);
            localRender.setScalingType(scalingType);
        }

        localRender.setMirror(true);

        localRender.requestLayout();
        remoteRenderScreen.requestLayout();
    }

    private void checkPermission() {
        for (String perm : Conf.MANDATORY_PERMISSIONS) {
            if (checkCallingOrSelfPermission(perm) != PackageManager.PERMISSION_GRANTED) {
                showToast("权限不足:" + perm);
                finish();
            }
        }
    }

    @Override
    protected void doResponse(Object b) {
        if (b == null){
            return;
        }

        mHandler.sendMessage(mHandler.obtainMessage(0, b));
    }

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            if (msg.what == 0){
                if (msg.obj instanceof ActionBean){
                    ActionBean b = (ActionBean)msg.obj;
                    showToast(b.toString());

                    if (b != null){
                        roomId = b.getSenderRoomNumber();
                        int code = b.getCode();

                        if (code == Conf.ACTION_BUSY){
                            showToast("对方忙，请稍后呼叫");
                            finish();
                        }else if (code == Conf.ACTION_REFUSE){
                            showToast("拒绝接听");
                            finish();
                        }else if (code == Conf.ACTION_USER_NOT_EXIST){
                            showToast("该房间为空号");
                            finish();
                        }else if (code == Conf.ACTION_HUNG_UP){
                            showToast("聊天已结束");
                            finish();
                        }else if (code == Conf.ACTION_ERROR){
                            showToast("未知错误，聊天已结束");
                            finish();
                        }else if (code == Conf.ACTION_AGREE){
                            //接听
                            showToast("同意接听");
                            cancelTimer();
                            stop();
                            Utils.doAgree(b.getContent(), initiator, VideoAc.this);
                        }else if (code == Conf.ACTION_HOLD){
                            cancelTimer();
                        }
                    }
                }
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stop();
        cancelTimer();
        release();

        disconnect();
    }

    private void connectRoomInternal(VideoCapturer videoCapturer,
                                     SignalingParameters params,
                                     PeerConnectionClient peerClient, EglBase rootEglBase,
                                     SurfaceViewRenderer localRender,
                                     List<VideoRenderer.Callbacks> remoteRenderers) {

        Utils.onConnectedToRoomInternal(this, videoCapturer, params, peerClient,
                rootEglBase, localRender, remoteRenderers );
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private VideoCapturer createVideoCapture(){
        String videoFileAsCamera = SpUtils.getStr(this, Conf.EXTRA_VIDEO_FILE_AS_CAMERA, null);
        return Utils.createVideoCapturer(this, videoFileAsCamera, screencaptureEnabled,
                mediaProjectionPermissionResultCode, mediaProjectionPermissionResultData,
                new VideoCaptureImpl() {
                    @Override
                    public void failed(int code, String msg) {
                        showToast("code = " + code + "---msg = " + msg);
                    }
                });
    }

    int mediaProjectionPermissionResultCode = 0;
    Intent mediaProjectionPermissionResultData = null;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Conf.CAPTURE_PERMISSION_REQUEST_CODE) {
            mediaProjectionPermissionResultCode = resultCode;
            mediaProjectionPermissionResultData = data;
            startCall();
        }
    }
    /*************************fg回调***************************/
    @Override
    public void onCallHangUp() {
        L.i(TAG, "onCallHangUp");
        finish();
    }

    @Override
    public void onCameraSwitch() {
        L.i(TAG, "onCameraSwitch");
        if (peerClient != null){
            peerClient.switchCamera();
        }
    }

    @Override
    public void onVideoScalingSwitch(ScalingType scalingType) {
        L.i(TAG, "onVideoScalingSwitch scalingType = " + scalingType);
        this.scalingType = scalingType;
        updateVideoView();
    }

    @Override
    public void onCaptureFormatChange(int width, int height, int framerate) {
        L.i(TAG, "onCaptureFormatChange width = " + width +"--- height = " + height + "---framerate = " + framerate);
        if (peerClient != null) {
            peerClient.changeCaptureFormat(width, height, framerate);
        }
    }

    private boolean micEnabled = true;
    @Override
    public boolean onToggleMic() {
        L.i(TAG, "onToggleMic");
        if (peerClient != null){
            micEnabled = !micEnabled;
            peerClient.setAudioEnabled(micEnabled);
        }
        return micEnabled;
    }

    /************************************signal************************************/
    @Override
    public void onConnectedToRoom(SignalingParameters params) {
        L.i(TAG, "onConnectedToRoom = " + params);
    }

    @Override
    public void onRemoteDescription(final SessionDescription sdp) {
        L.i(TAG, "onRemoteDescription = " + sdp);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                peerClient.setRemoteDescription(sdp);
            }
        });
    }

    @Override
    public void onRemoteIceCandidate(final IceCandidate candidate) {
        L.i(TAG, "onRemoteIceCandidate = " + candidate);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                peerClient.addRemoteIceCandidate(candidate);
            }
        });
    }

    @Override
    public void onRemoteIceCandidatesRemoved(final IceCandidate[] candidates) {
        L.i(TAG, "onRemoteIceCandidatesRemoved = " + candidates);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                peerClient.removeRemoteIceCandidates(candidates);
            }
        });
    }

    @Override
    public void onChannelClose() {
        L.i(TAG, "onChannelClose = " );
        finish();
    }

    @Override
    public void onChannelError(final String description) {
        L.i(TAG, "onChannelError = " + description);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                showToast(description);
            }
        });
    }
    /*************************************************************************/

    CountDownTimer timer;
    private void startTimer(){
        if (timer == null){
            timer = new CountDownTimer(Conf.TIMER, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {

                }

                @Override
                public void onFinish() {
                    stop();
                    cancelTimer();
                    finish();
                }
            };
        }
        timer.cancel();
        timer.start();
        play();
    }

    private void cancelTimer(){
        if (timer != null){
            timer.cancel();
        }
    }

    private Player player;
    private void play(){
        if (BuildConfig.DEBUG && false){
            showToast("播放音乐");
        }else{
            if (player == null){
                player = new Player();
            }
            player.start(this, R.raw.waiting_0, true);
        }
    }

    private void stop(){
        if (player != null){
            player.stop();
        }
    }

    private void release(){
        if (player != null){
            player.release();
        }
    }
}
