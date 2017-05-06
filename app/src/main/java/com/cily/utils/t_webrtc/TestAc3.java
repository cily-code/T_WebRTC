package com.cily.utils.t_webrtc;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.EditText;

import com.cily.utils.app.event.Event;
import com.cily.utils.app.utils.L;
import com.cily.utils.base.RandomUtils;
import com.cily.utils.base.StrUtils;
import com.cily.utils.t_webrtc.event.RoomConnectionParameters;
import com.cily.utils.t_webrtc.event.SignalingParameters;
import com.cily.utils.t_webrtc.impl.RoomParametersFetcherEvents;
import com.cily.utils.t_webrtc.news.RoomParametersFetcher;
import com.cily.utils.t_webrtc.utils.ConnUtils;
import com.cily.utils.t_webrtc.utils.StrMsgUtils;
import com.cily.utils.t_webrtc.utils.WsUtils;

public class TestAc3 extends BaseAc {
    EditText ed_msg_receive;

    private boolean loopBack = false;
    private RoomConnectionParameters roomConn;
    private String roomId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_test3);

        final EditText ed = findView(R.id.ed_room_num);
        ed_msg_receive = findView(R.id.ed_msg_receive);
        final EditText ed_msg = findView(R.id.ed_msg);
        final EditText ed_room_num_receive = findView(R.id.ed_room_num_receive);

        findBtn(R.id.btn_conn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(TestAc3.this, WsService.class);
                i.putExtra("userRoom", ed.getText().toString().trim());
                startService(i);
            }
        });

        findBtn(R.id.btn_send_msg).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WsUtils.sendMsg(StrMsgUtils.msg(ed.getText().toString().trim(),
                        ed_room_num_receive.getText().toString().trim(),
                        ed_msg.getText().toString().trim()));
            }
        });

        findBtn(R.id.btn_call).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WsUtils.sendMsg(StrMsgUtils.callVedio(ed.getText().toString().trim(),
                        ed_room_num_receive.getText().toString().trim(), null));
            }
        });

        findBtn(R.id.btn_hold).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WsUtils.sendMsg(StrMsgUtils.hold(ed.getText().toString().trim(),
                        ed_room_num_receive.getText().toString().trim()));
            }
        });

        findBtn(R.id.btn_join_room).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                joinRoom();
            }
        });

        initData();
    }

    private void initData(){
        roomId = getIntent().getStringExtra(Conf.EXTRA_ROOMID);
        if (StrUtils.isEmpty(roomId)){
            roomId = RandomUtils.getRandomStr(4, RandomUtils.NUM);
        }
        roomConn = new RoomConnectionParameters(BuildConfig.URL_ROOM, roomId, loopBack);
    }

    @Override
    protected void doRxEvent(Event e) {
//        super.doRxEvent(e);

        mHandler.sendMessage(mHandler.obtainMessage(0, e));
    }

    private Handler mHandler = new Handler(){

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            L.i(TAG, "收到Message");

            if (msg.obj instanceof Event){
                if (((Event) msg.obj).obj instanceof String){
                    ed_msg_receive.append((String) ((Event)msg.obj).obj);
                    ed_msg_receive.append("\n");
                }

                ((Event)msg.obj).recycle();
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();

        Intent i = new Intent(this, WsService.class);
        stopService(i);
    }

    @Override
    protected void disconnect() {

    }

    private void joinRoom(){
        new RoomParametersFetcher(ConnUtils.getConnectionUrl(roomConn), null, new RoomParametersFetcherEvents() {
            @Override
            public void onSignalingParametersReady(SignalingParameters params) {
                L.i(TAG, "joinRoom RoomParametersFetcher onSignalingParametersReady params = " + params.toString());
            }

            @Override
            public void onSignalingParametersError(String description) {
                L.e(TAG, "joinRoom RoomParametersFetcher onSignalingParametersError description = " + description);
            }
        }).makeRequest();
    }
}
