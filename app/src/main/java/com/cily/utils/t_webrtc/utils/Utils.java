package com.cily.utils.t_webrtc.utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.projection.MediaProjection;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.RequiresApi;

import com.alibaba.fastjson.JSON;
import com.cily.utils.app.Init;
import com.cily.utils.app.utils.L;
import com.cily.utils.app.utils.SpUtils;
import com.cily.utils.app.utils.ToastUtils;
import com.cily.utils.t_webrtc.Conf;
import com.cily.utils.t_webrtc.R;
import com.cily.utils.t_webrtc.bean.AnswerBean;
import com.cily.utils.t_webrtc.bean.CallBean;
import com.cily.utils.t_webrtc.client.PeerConnectionClient;
import com.cily.utils.t_webrtc.event.RoomConnectionParameters;
import com.cily.utils.t_webrtc.event.SignalingParameters;
import com.cily.utils.t_webrtc.impl.SignalingEvents;
import com.cily.utils.t_webrtc.impl.VideoCaptureImpl;
import com.cily.utils.t_webrtc.parameter.PeerConnectionParameters;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.webrtc.Camera1Enumerator;
import org.webrtc.Camera2Enumerator;
import org.webrtc.CameraEnumerator;
import org.webrtc.EglBase;
import org.webrtc.FileVideoCapturer;
import org.webrtc.IceCandidate;
import org.webrtc.PeerConnection;
import org.webrtc.ScreenCapturerAndroid;
import org.webrtc.SessionDescription;
import org.webrtc.SurfaceViewRenderer;
import org.webrtc.VideoCapturer;
import org.webrtc.VideoRenderer;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import static com.cily.utils.t_webrtc.Conf.EXTRA_VIDEO_FILE_AS_CAMERA;

/**
 * user:cily
 * time:2017/5/1
 * desc:
 */

public class Utils {
    private final static String TAG = Utils.class.getSimpleName();

    public static boolean loopback = false;

    // Helper method for debugging purposes. Ensures that WebSocket method is
    // called on a looper thread.
    public static void checkIfCalledOnValidThread(Handler handler) {
        if (Thread.currentThread() != handler.getLooper().getThread()) {
            throw new IllegalStateException("WebSocket method is not called on valid thread");
        }
    }

    public static boolean useCamera2(Context cx, boolean defValue) {
        return Camera2Enumerator.isSupported(cx) && defValue;
    }

    private static long callStartedTimeMs = 0;
    public static void onConnectedToRoomInternal(Context cx,
                                                 VideoCapturer videoCapturer,
                                                 final SignalingParameters signalingParame,
                                                 PeerConnectionClient peerClient,
                                                 EglBase rootEglBase,
                                                 SurfaceViewRenderer localRender,
                                                 List<VideoRenderer.Callbacks> remoteRenderers) {

        final long delta = System.currentTimeMillis() - callStartedTimeMs;

        createPeerConnection(peerClient, rootEglBase.getEglBaseContext(),
                localRender, remoteRenderers, videoCapturer, signalingParame);

        if (signalingParame.initiator) {
            ToastUtils.showToast(cx, "Creating OFFER...", Init.isShowToast());
            // Create offer. Offer SDP will be sent to answering client in
            // PeerConnectionEvents.onLocalDescription event.
            peerClient.createOffer();
        } else {
            if (signalingParame.offerSdp != null) {
                peerClient.setRemoteDescription(signalingParame.offerSdp);
                ToastUtils.showToast(cx, "Creating ANSWER...", Init.isShowToast());
                // Create answer. Answer SDP will be sent to offering client in
                // PeerConnectionEvents.onLocalDescription event.
                peerClient.createAnswer();
            }
            if (signalingParame.iceCandidates != null) {
                // Add remote ICE candidates from room.
                for (IceCandidate iceCandidate : signalingParame.iceCandidates) {
                    peerClient.addRemoteIceCandidate(iceCandidate);
                }
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static void createPeerConnection(PeerConnectionClient peerConnectionClient,
                                        EglBase.Context cx, VideoRenderer.Callbacks localRender,
                                        List<VideoRenderer.Callbacks> remoteRenders,
                                        VideoCapturer videoCapturer, SignalingParameters params){

        peerConnectionClient.createPeerConnection(cx, localRender, remoteRenders,
                videoCapturer, params);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public static VideoCapturer createVideoCapturer(Context cx, String videoFileAsCamera,
                                                    boolean screencaptureEnabled,
                                                    int mediaProjectionPermissionResultCode,
                                                    Intent mediaProjectionPermissionResultData,
                                                    final VideoCaptureImpl videoImpl) {

        VideoCapturer videoCapturer = null;
        if (videoFileAsCamera != null) {
            try {
                videoCapturer = new FileVideoCapturer(videoFileAsCamera);
            } catch (IOException e) {
                if (videoImpl != null){
                    videoImpl.failed(0, "Failed to open video file for emulated camera");
                }
                L.printException(e);
                return null;
            }
        } else if (screencaptureEnabled) {
            if (mediaProjectionPermissionResultCode != Activity.RESULT_OK) {
                videoImpl.failed(1, "User didn't give permission to capture the screen.");
                return null;
            }
            return new ScreenCapturerAndroid(
                    mediaProjectionPermissionResultData, new MediaProjection.Callback() {
                @Override
                public void onStop() {
                    videoImpl.failed(2, "User revoked permission to capture the screen.");
                }
            });
        } else if (useCamera2(cx, cameraDef(cx))) {
            if (!captureToTexture(cx)) {
                videoImpl.failed(3, cx.getString(R.string.camera2_texture_only_error));
                return null;
            }

            L.d(TAG, "Creating capturer using camera2 API.");
            videoCapturer = createCameraCapturer(new Camera2Enumerator(cx));
        } else {
            L.d(TAG, "Creating capturer using camera1 API.");
            videoCapturer = createCameraCapturer(new Camera1Enumerator(captureToTexture(cx)));
        }
        if (videoCapturer == null) {
            videoImpl.failed(4, "Failed to open camera");
            return null;
        }
        return videoCapturer;
    }

    private static VideoCapturer createCameraCapturer(CameraEnumerator enumerator) {
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

    private static boolean cameraDef(Context cx){
        return SpUtils.getBoolean(cx, Conf.EXTRA_CAPTURETOTEXTURE_ENABLED, false);
    }

    public static boolean captureToTexture(Context cx) {
        return SpUtils.getBoolean(cx, Conf.EXTRA_CAPTURETOTEXTURE_ENABLED, false);
    }

    //OFFER或者Answer
    public static SignalingParameters createSignalingParameters(String type, String clientId,
                                                                boolean initiator, IceCandidate iceCandidate){
        LinkedList<PeerConnection.IceServer> iceServers = new LinkedList<>();
        iceServers.add(new PeerConnection.IceServer("turn:119.23.251.238:3478", "helloword", "helloword"));

        SessionDescription offerSdp = new SessionDescription(
                SessionDescription.Type.fromCanonicalForm(type), null);

        LinkedList<IceCandidate> iceCandidates = new LinkedList<IceCandidate>();
        if (iceCandidate != null) {
            iceCandidates.add(iceCandidate);
        }

        SignalingParameters params = new SignalingParameters(iceServers, initiator,
                clientId, WsUtils.URL_WS, Conf.URL_POST, offerSdp, iceCandidates);

        return params;
    }

    public final static String TYPE_OFFER = "offer";
    public final static String TYPE_ANSWER = "answer";
    public final static String TYPE_CANDIDATE = "candidate";
    public static void sendCall(boolean loopback, String userRoom, String roomId,
                                SessionDescription sdp, IceCandidate candidate,
                                boolean initiator,
                                SignalingEvents events){
        CallBean b = new CallBean();
        b.setSdp(sdp.description);
//        b.setType(TYPE_OFFER);
        b.setType(TYPE_CANDIDATE);
        b.setId(candidate.sdpMid);
        b.setLabel(candidate.sdpMLineIndex);
        b.setCandidate(candidate.sdp);

        if (loopback){
            SessionDescription sd = new SessionDescription(
                    SessionDescription.Type.fromCanonicalForm(TYPE_ANSWER),
                    sdp.description);
            if(events != null){
                events.onRemoteDescription(sd);
            }
        }

        if (initiator){
            if (loopback){
                if (events != null){
                    events.onRemoteIceCandidate(candidate);
                }
            }
        }

        WsUtils.sendMsg(StrMsgUtils.callVedio(userRoom, roomId, JSON.toJSONString(b)));

    }

    public static void senAnswer(boolean loopback, String userRoom,
                                 String roomId, SessionDescription sdp){
        if (loopback){
            L.i(TAG, "Sending answer n loopback mode.");
            return;
        }

        CallBean b = new CallBean();
        b.setType(TYPE_ANSWER);
        b.setSdp(sdp.description);

        WsUtils.sendMsg(StrMsgUtils.agree(userRoom, roomId, JSON.toJSONString(b)));
    }

    public static void doAgree(String msg, boolean initiator, SignalingEvents events){
        try{
            JSONObject json = new JSONObject(msg);
            String type = json.optString("Type");
            if (TYPE_OFFER.equals(type)){
                    events.onRemoteIceCandidate(toJavaCandidate(json));

                    SessionDescription sdp = new SessionDescription(
                            SessionDescription.Type.fromCanonicalForm(type), json.getString("Sdp"));
                    if (events != null) {
                        events.onRemoteDescription(sdp);
                    }

            }else if(TYPE_ANSWER.equals(type)){

                if (events != null){
                    SessionDescription sd = new SessionDescription(
                            SessionDescription.Type.fromCanonicalForm(type), json.getString("Sdp"));
                    if (events != null){
                        events.onRemoteDescription(sd);
                    }
                }

            }else if(TYPE_CANDIDATE.equals(type)){
                events.onRemoteIceCandidate(toJavaCandidate(json));
            }
        }catch (JSONException e){
            L.printException(e);
        }
    }

    private static IceCandidate toJavaCandidate(JSONObject json) throws JSONException {
        return new IceCandidate(
                json.getString("Id"), json.getInt("Label"), json.getString("Candidate"));
    }

    public static void sendLocalIceCandidateRemovals(boolean loopback, boolean initiator,
                                                     PeerConnectionParameters param,
                                                     IceCandidate[] candidates,
                                                     SignalingEvents events){
        JSONObject json = new JSONObject();
        jsonPut(json, "Type", "remove-candidates");
        JSONArray jsonArray = new JSONArray();
        for (final IceCandidate candidate : candidates) {
            jsonArray.put(toJsonCandidate(candidate));
        }
        jsonPut(json, "Candidates", jsonArray);
        if (initiator) {
            if (param.loopback) {
                events.onRemoteIceCandidatesRemoved(candidates);
            }
        } else {
            // Call receiver sends ice candidates to websocket server.
            //TODO
//            wsClient.send(json.toString());
        }
    }

    private static JSONObject toJsonCandidate(final IceCandidate candidate) {
        JSONObject json = new JSONObject();
        jsonPut(json, "Label", candidate.sdpMLineIndex);
        jsonPut(json, "Id", candidate.sdpMid);
        jsonPut(json, "Candidate", candidate.sdp);
        return json;
    }

    private static void jsonPut(JSONObject json, String key, Object value) {
        try {
            json.put(key, value);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    private static LinkedList<PeerConnection.IceServer> iceServersFromPCConfigJSON(String pcConfig)
            throws JSONException {
        JSONObject json = new JSONObject(pcConfig);
        JSONArray servers = json.getJSONArray("iceServers");
        LinkedList<PeerConnection.IceServer> ret = new LinkedList<PeerConnection.IceServer>();
        for (int i = 0; i < servers.length(); ++i) {
            JSONObject server = servers.getJSONObject(i);
            String url = server.getString("urls");
            String credential = server.has("credential") ? server.getString("credential") : "";
            ret.add(new PeerConnection.IceServer(url, "", credential));
        }
        return ret;
    }

}