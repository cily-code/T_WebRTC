package com.cily.utils.t_webrtc.news;

import com.cily.utils.app.utils.L;
import com.cily.utils.t_webrtc.client.AsyncHttpURLConnection;
import com.cily.utils.t_webrtc.event.SignalingParameters;
import com.cily.utils.t_webrtc.impl.AsyncHttpEvents;
import com.cily.utils.t_webrtc.impl.RoomParametersFetcherEvents;
import com.cily.utils.t_webrtc.utils.HttpUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.webrtc.IceCandidate;
import org.webrtc.PeerConnection;
import org.webrtc.SessionDescription;

import java.io.InputStream;
import java.util.LinkedList;
import java.util.Scanner;

/**
 * user:cily
 * time:2017/5/1
 * desc:
 */
public class RoomParametersFetcher {
    private static final String TAG = "RoomParametersFetcher";
    private static final int TURN_HTTP_TIMEOUT_MS = 5000;
    private final RoomParametersFetcherEvents events;
    private final String roomUrl;
    private final String roomMessage;

    public RoomParametersFetcher(String roomUrl, String roomMessage, final RoomParametersFetcherEvents events) {
        this.roomUrl = roomUrl;
        this.roomMessage = roomMessage;
        this.events = events;
    }

    public void makeRequest() {
        L.d(TAG, "Connecting to room: " + roomUrl);

        HttpUtils.request("POST", roomUrl, roomMessage, new AsyncHttpEvents() {
            @Override
            public void onHttpError(String errorMessage) {
                L.e(TAG, "Room connection error: " + errorMessage);
                events.onSignalingParametersError(errorMessage);
            }

            @Override
            public void onHttpComplete(String response) {
                L.i(TAG, "onHttpComplete response = " + response);
                roomHttpResponseParse(response);
            }
        });
    }

    private void roomHttpResponseParse(String response) {
        L.d(TAG, "Room response: " + response);
        try {
            LinkedList<IceCandidate> iceCandidates = null;
            SessionDescription offerSdp = null;
            JSONObject roomJson = new JSONObject(response);

            String result = roomJson.getString("result");
            if (!result.equals("SUCCESS")) {
                events.onSignalingParametersError("Room response error: " + result);
                return;
            }
            response = roomJson.getString("params");
            roomJson = new JSONObject(response);
            String roomId = roomJson.getString("room_id");
            String clientId = roomJson.getString("client_id");
            String wssUrl = roomJson.getString("wss_url");
            String wssPostUrl = roomJson.getString("wss_post_url");
            boolean initiator = (roomJson.getBoolean("is_initiator"));
            if (!initiator) {
                iceCandidates = new LinkedList<IceCandidate>();
                String messagesString = roomJson.getString("messages");
                JSONArray messages = new JSONArray(messagesString);
                for (int i = 0; i < messages.length(); ++i) {
                    String messageString = messages.getString(i);
                    JSONObject message = new JSONObject(messageString);
                    String messageType = message.getString("type");
                    L.d(TAG, "GAE->C #" + i + " : " + messageString);
                    if (messageType.equals("offer")) {
                        offerSdp = new SessionDescription(
                                SessionDescription.Type.fromCanonicalForm(messageType), message.getString("sdp"));
                    } else if (messageType.equals("candidate")) {
                        IceCandidate candidate = new IceCandidate(
                                message.getString("id"), message.getInt("label"), message.getString("candidate"));
                        iceCandidates.add(candidate);
                    } else {
                        L.e(TAG, "Unknown message: " + messageString);
                    }
                }
            }
            L.d(TAG, "RoomId: " + roomId + ". ClientId: " + clientId
                    + ". Initiator: " + initiator + ". WSS url: " + wssUrl
                    + ". WSS POST url: " + wssPostUrl);

            LinkedList<PeerConnection.IceServer> iceServers = iceServersFromPCConfigJSON(roomJson.getString("pc_config"));
            boolean isTurnPresent = false;
            for (PeerConnection.IceServer server : iceServers) {
                L.d(TAG, "IceServer: " + server);
                if (server.uri.startsWith("turn:")) {
                    isTurnPresent = true;
                    break;
                }
            }

            iceServers.add(new PeerConnection.IceServer("turn:119.23.251.238:3478", "helloword", "helloword"));

            SignalingParameters params = new SignalingParameters(iceServers, initiator,
                    clientId, wssUrl, wssPostUrl, offerSdp, iceCandidates);
            events.onSignalingParametersReady(params);
        } catch (JSONException e) {
            L.printException(e);
            events.onSignalingParametersError("Room JSON parsing error: " + e.toString());
        }
    }

    private LinkedList<PeerConnection.IceServer> iceServersFromPCConfigJSON(String pcConfig)
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