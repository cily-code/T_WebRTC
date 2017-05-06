package com.cily.utils.t_webrtc.event;

import org.webrtc.IceCandidate;
import org.webrtc.PeerConnection;
import org.webrtc.SessionDescription;

import java.io.Serializable;
import java.util.List;

/**
 * user:cily
 * time:2017/4/30
 * desc:
 */

public class SignalingParameters implements Serializable {
    public final List<PeerConnection.IceServer> iceServers;
    public final boolean initiator;
    public final String clientId;
    public final String wssUrl;
    public final String wssPostUrl;
    public final SessionDescription offerSdp;
    public final List<IceCandidate> iceCandidates;

    public SignalingParameters(List<PeerConnection.IceServer> iceServers, boolean initiator,
                               String clientId, String wssUrl, String wssPostUrl, SessionDescription offerSdp,
                               List<IceCandidate> iceCandidates) {
        this.iceServers = iceServers;
        this.initiator = initiator;
        this.clientId = clientId;
        this.wssUrl = wssUrl;
        this.wssPostUrl = wssPostUrl;
        this.offerSdp = offerSdp;
        this.iceCandidates = iceCandidates;
    }

    @Override
    public String toString() {
        return "SignalingParameters{" +
                "iceServers=" + iceServers +
                ", initiator=" + initiator +
                ", clientId='" + clientId + '\'' +
                ", wssUrl='" + wssUrl + '\'' +
                ", wssPostUrl='" + wssPostUrl + '\'' +
                ", offerSdp=" + offerSdp +
                ", iceCandidates=" + iceCandidates +
                '}';
    }
}
