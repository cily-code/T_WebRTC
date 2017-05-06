package com.cily.utils.t_webrtc.utils;

import com.cily.utils.app.utils.L;

import org.json.JSONException;
import org.json.JSONObject;
import org.webrtc.IceCandidate;

import java.util.HashMap;
import java.util.Map;

/**
 * user:cily
 * time:2017/5/1
 * desc:
 */

public class JsonUtils {

    // Converts a Java candidate to a JSONObject.
    public static JSONObject toJsonCandidate(final IceCandidate candidate) {
//        JSONObject json = new JSONObject();
//        jsonPut(json, "label", candidate.sdpMLineIndex);
//        jsonPut(json, "id", candidate.sdpMid);
//        jsonPut(json, "candidate", candidate.sdp);
//        return json;

        Map<String, Object> map = new HashMap<>();
        if (candidate != null){
            map.put("label", candidate.sdpMLineIndex);
            map.put("id", candidate.sdpMid);
            map.put("candidate", candidate.sdp);
        }

        return toJson(map);
    }

    // Put a |key|->|value| mapping in |json|.
    public static void jsonPut(JSONObject json, String key, Object value) {
        try {
            json.put(key, value);
        } catch (JSONException e) {
            L.printException(e);
            throw new RuntimeException(e);
        }
    }

    public static JSONObject toJson(Map<String, Object> map){
        JSONObject json = new JSONObject();
        if (map != null){
            for (String s : map.keySet()){
                jsonPut(json, s, map.get(s));
            }
        }
        return json;
    }

    public static String toJsonStr(Map<String, Object> map){
        return toJson(map).toString();
    }

    // Converts a JSON candidate to a Java object.
    public static IceCandidate toJavaCandidate(JSONObject json) throws JSONException {
        return new IceCandidate(json.getString("id"), json.getInt("label"), json.getString("candidate"));
    }

    public static Object getObjFromJson(JSONObject o, String key) throws JSONException {
        return o.get(key);
    }

    public static String getStrFromJson(JSONObject o, String key) throws JSONException{
        return o.getString(key);
    }

    public static JSONObject toJson(String jsonStr) throws JSONException {
        return new JSONObject(jsonStr);
    }
}
