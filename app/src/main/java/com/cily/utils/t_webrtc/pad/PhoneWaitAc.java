package com.cily.utils.t_webrtc.pad;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cily.utils.app.utils.L;
import com.cily.utils.app.utils.SpUtils;
import com.cily.utils.app.utils.TimerUtils;
import com.cily.utils.base.RandomUtils;
import com.cily.utils.base.StrUtils;
import com.cily.utils.media.Player;
import com.cily.utils.t_webrtc.BaseAc3;
import com.cily.utils.t_webrtc.BuildConfig;
import com.cily.utils.t_webrtc.Conf;
import com.cily.utils.t_webrtc.LoginAc;
import com.cily.utils.t_webrtc.R;
import com.cily.utils.t_webrtc.WsService;
import com.cily.utils.t_webrtc.bean.ActionBean;
import com.cily.utils.t_webrtc.client.PeerConnectionClient;
import com.cily.utils.t_webrtc.event.RoomConnectionParameters;
import com.cily.utils.t_webrtc.event.SignalingParameters;
import com.cily.utils.t_webrtc.impl.RoomParametersFetcherEvents;
import com.cily.utils.t_webrtc.impl.SignalingEvents;
import com.cily.utils.t_webrtc.impl.VideoCaptureImpl;
import com.cily.utils.t_webrtc.news.RoomParametersFetcher;
import com.cily.utils.t_webrtc.parameter.PeerConnectionParameters;
import com.cily.utils.t_webrtc.utils.ConnUtils;
import com.cily.utils.t_webrtc.utils.StrMsgUtils;
import com.cily.utils.t_webrtc.utils.Utils;
import com.cily.utils.t_webrtc.utils.WsUtils;

import org.webrtc.EglBase;
import org.webrtc.IceCandidate;
import org.webrtc.SessionDescription;
import org.webrtc.SurfaceViewRenderer;
import org.webrtc.VideoRenderer;

import java.util.List;

public class PhoneWaitAc extends BaseAc3 {
    LinearLayout ll;
    String roomId;  //被呼叫者的物理房间号
    Player player;
    private String userRoom;
    private int type_call = Conf.ACTION_CALL_VIDEO;
    private boolean callByUser = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_phone_wait);
        userRoom = SpUtils.getStr(this, Conf.USER_ROOM, "");


        TextView tv = findView(R.id.tv_user_room);
        tv.setText(userRoom);

        ll = findView(R.id.ll_option);
        findBtn(R.id.btn_answer).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toVideo();
            }
        });
        findBtn(R.id.btn_hold).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WsUtils.sendMsg(StrMsgUtils.hold(userRoom, roomId));
                cancelTimer();
            }
        });
        findBtn(R.id.btn_denine).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WsUtils.sendMsg(StrMsgUtils.refuse(userRoom, roomId));
                cancelTimer();
                stop();

                next(null);
            }
        });

        LinearLayout ll_set = findView(R.id.ll_set);
        if (BuildConfig.TYPE_APP == 1){
            ll_set.setVisibility(View.VISIBLE);

            findBtn(R.id.btn_logout).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    logout();
                }
            });

            findBtn(R.id.btn_setting).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    toAc(SettingsActivity.class, null);
                }
            });
        }
    }

    private void logout(){
        Intent i = new Intent(this, WsService.class);
        stopService(i);

        toAc(LoginAc.class, null);
        finish();
    }

    private String iceCandidate;
    private void toVideo(){
        cancelTimer();
        stop();

        Bundle b = new Bundle();
        b.putString(Conf.EXTRA_ROOMID, roomId);
        b.putInt(Conf.INTENT_CALL_TYPE, type_call);
        b.putString(Conf.INTENT_ICECANDIDATE, iceCandidate);
        toAc(VideoAc.class, b);

        if (BuildConfig.TYPE_APP == 0){
            finish();
        }else if (BuildConfig.TYPE_APP == 1){
            ll.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        boolean fromPadCall = getIntent().getBooleanExtra(Conf.INTENT_FROM_PAD, false);
        callByUser = getIntent().getBooleanExtra(Conf.INTENT_CALL_BY_USER, false);
        if (fromPadCall){
            roomId = getIntent().getStringExtra(Conf.EXTRA_ROOMID);
            WsUtils.sendMsg(StrMsgUtils.callVedio(userRoom, roomId, null));
            startTimer();
        }

        if (callByUser){
            roomId = getIntent().getStringExtra(Conf.EXTRA_ROOMID);
            startTimer();
            ll.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void doResponse(Object b) {
        mHandler.sendMessage(mHandler.obtainMessage(0, b));
    }

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            if (msg.obj instanceof ActionBean){
                ActionBean b = (ActionBean)msg.obj;
                showToast(b.toString());
                if (b != null){
                    roomId = b.getSenderRoomNumber();
                    int code = b.getCode();

                    if (code == Conf.ACTION_CALL_VIDEO || code == Conf.ACTION_CALL_VOICE){
                        startTimer();
                        ll.setVisibility(View.VISIBLE);
                        type_call = code;
                        iceCandidate = b.getContent();
                    }else if (code == Conf.ACTION_BUSY){
                        next("对方忙，请稍后呼叫");
                    }else if (code == Conf.ACTION_REFUSE){
                        next("拒绝接听");
                    }else if (code == Conf.ACTION_USER_NOT_EXIST){
                        next("该房间为空号");
                    }else if (code == Conf.ACTION_HUNG_UP){
                        next("聊天已结束");
                    }else if (code == Conf.ACTION_ERROR){
                        next("未知错误，聊天已结束");
                    }else if (code == Conf.ACTION_AGREE){
                        //接听
                        showToast("同意接听");
                        toVideo();

                    }else if (code == Conf.ACTION_HOLD){
                        cancelTimer();
                    }
                }
            }
        }
    };

    private void next(String str){
        stop();
        cancelTimer();

        showToast(str);
        if (BuildConfig.TYPE_APP == 0){
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    finish();
                }
            }, 2000);
        }else if (BuildConfig.TYPE_APP == 1){
            ll.setVisibility(View.GONE);
        }
    }

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

    @Override
    protected void onDestroy() {
        stop();
        cancelTimer();
        release();
        super.onDestroy();
    }

    private void play(){
        if (BuildConfig.DEBUG){
            showToast("播放音乐");
        }else{
            if (player == null){
                player = new Player();
            }
            player.start(this, R.raw.waiting_0, true);
        }
    }

    private void stop(){
        if (BuildConfig.DEBUG && false){
            showToast("停止播放");
        }else{
            if (player != null){
                player.stop();
            }
        }
    }

    private void release(){
        if (BuildConfig.DEBUG){

        }else{
            if (player != null){
                player.release();
            }
        }
    }
}