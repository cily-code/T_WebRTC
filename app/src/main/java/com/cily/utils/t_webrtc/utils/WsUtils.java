package com.cily.utils.t_webrtc.utils;

import android.content.Context;
import android.content.Intent;

import com.alibaba.fastjson.JSON;
import com.cily.utils.app.RxBus;
import com.cily.utils.app.event.Event;
import com.cily.utils.app.utils.L;
import com.cily.utils.base.StrUtils;
import com.cily.utils.t_webrtc.Conf;
import com.cily.utils.t_webrtc.bean.ActionBean;
import com.cily.utils.t_webrtc.impl.ObserverImpl;
import com.cily.utils.t_webrtc.pad.PhoneWaitAc;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

/**
 * user:cily
 * time:2017/5/2
 * desc:
 */
public class WsUtils {
    private final static String TAG = WsUtils.class.getSimpleName();
    public final static String URL_WS = "ws://47.90.73.137:9001";
    private static WebSocket ws;
    private static boolean isConnected = false;

    private final static OkHttpClient mOkHttpClient = new OkHttpClient.Builder()
            .readTimeout(3000, TimeUnit.SECONDS)//设置读取超时时间
            .writeTimeout(3000, TimeUnit.SECONDS)//设置写的超时时间
            .connectTimeout(3000, TimeUnit.SECONDS)//设置连接超时时间
            .build();


    public static void conn(final Context cx, final String roomNum){
        if (ws != null){
            L.d(TAG, "The ws is not null");

            if (isConnected){
                L.d(TAG, "The ws is connected");
                return;
            }else{
                L.d(TAG, "close the connection");
                close(roomNum);
            }
        }

        Request request = new Request.Builder().url(URL_WS).build();
        ws = mOkHttpClient.newWebSocket(request, new WebSocketListener() {
            @Override
            public void onOpen(WebSocket webSocket, Response response) {
                super.onOpen(webSocket, response);
                try {
                    L.i(TAG, "***onOpen response = " + response.body().string());
                } catch (IOException e) {
                    L.printException(e);
                }
                isConnected = true;
            }

            @Override
            public void onMessage(WebSocket webSocket, String text) {
                super.onMessage(webSocket, text);
                L.i(TAG, "***onMessage text = " + text);

                if (StrUtils.isEmpty(text)){
                    return;
                }

                ActionBean action = parse(text);

                if (ObserverUtils.getInstance().canSend()){
                    if (action != null && action.getCode() == Conf.ACTION_CONNECT_SUCCESS){
                        sendMsg(StrMsgUtils.login(roomNum));
                        return;
                    }

                    Event e = Event.obtain(Conf.WS_MSG, action == null ? text : action);
                    ObserverUtils.getInstance().send(e);
                }else{
                    if (action != null && action.getCode() == Conf.ACTION_CALL_VIDEO){
                        Intent i = new Intent(cx, PhoneWaitAc.class);
                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        i.putExtra(Conf.EXTRA_ROOMID, action.getReceiverRoomNumber());
                        i.putExtra(Conf.INTENT_CALL_SENDER_ROOM_NUMBER, action.getSenderRoomNumber());
                        i.putExtra(Conf.INTENT_CALL_RECEIVER_ROOM_NUMBER, action.getReceiverRoomNumber());
                        i.putExtra(Conf.INTENT_CALL_BY_USER, true);
                        cx.startActivity(i);
                    }
                }
            }

            @Override
            public void onMessage(WebSocket webSocket, ByteString bytes) {
                super.onMessage(webSocket, bytes);
                L.i(TAG, "***onMessage ByteString");
            }

            @Override
            public void onClosing(WebSocket webSocket, int code, String reason) {
                super.onClosing(webSocket, code, reason);
                L.i(TAG, "***onClosing");
            }

            @Override
            public void onClosed(WebSocket webSocket, int code, String reason) {
                super.onClosed(webSocket, code, reason);
                L.i(TAG, "***onClosed");
                isConnected = false;
                ws = null;
            }

            @Override
            public void onFailure(WebSocket webSocket, Throwable t, Response response) {
                super.onFailure(webSocket, t, response);

                L.i(TAG, "***onFailure  response = ");

                L.printException(t);
//                close();
            }
        });
    }

    public static ActionBean parse(String responseStr){
        try{
            ActionBean b = JSON.parseObject(responseStr, ActionBean.class);
            return b;
        }catch (Exception ex){
            L.printException(ex);
            return null;
        }
    }

    public static void sendMsg(String str){
        if (StrUtils.isEmpty(str)){
            L.d(TAG, "The msg is null");
            return;
        }

        if (ws == null){
            L.d(TAG, "The ws is null");
            return;
        }

        if (!isConnected){
            L.d(TAG, "The ws not connect");
            return;
        }

        L.i(TAG, "发出的消息：" + str);
        ws.send(str);
    }

    public static void close(String userRoom){
        if (ws != null){
            if (isConnected){
                L.d(TAG, "send close cmd to server");

                if (!StrUtils.isEmpty(userRoom)) {
                    ws.send(StrMsgUtils.close(userRoom, null));
                }
                ws.close(1001, "close by user");
            }else{
                L.d(TAG, "cancel ws connect");
                ws.cancel();
            }
        }
        isConnected = false;
        ws = null;
    }
}