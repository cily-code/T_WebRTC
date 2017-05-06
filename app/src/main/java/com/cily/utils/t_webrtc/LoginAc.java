package com.cily.utils.t_webrtc;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.cily.utils.app.utils.SpUtils;
import com.cily.utils.base.StrUtils;
import com.cily.utils.t_webrtc.bean.ActionBean;
import com.cily.utils.t_webrtc.pad.PadAc2;
import com.cily.utils.t_webrtc.pad.PhoneAc;
import com.cily.utils.t_webrtc.pad.PhoneWaitAc;

public class LoginAc extends BaseAc3 {
    private EditText ed;
    private String userRoom;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_login);

        ed = findView(R.id.ed_room);
        findBtn(R.id.btn_login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });
    }

    private void login(){
        userRoom = ed.getText().toString().trim();
        if (StrUtils.isEmpty(userRoom)){
            showToast("请输入房间号");
            return;
        }

        Intent i = new Intent(this, WsService.class);
        i.putExtra(Conf.USER_ROOM, userRoom);
        startService(i);
    }

    @Override
    protected void doResponse(Object b) {
        if (b instanceof ActionBean){
            if (((ActionBean) b).getCode() == Conf.ACTION_LOGIN){
                SpUtils.putStr(this, Conf.USER_ROOM, userRoom);
                toNext();
            }else{
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showToast("登录失败，请重试");
                    }
                });
            }
        }
    }

    private void toNext(){
        if (BuildConfig.TYPE_APP == 0){
            toAc(PadAc2.class, null);
        }else{
            toAc(PhoneWaitAc.class, null);
        }
        finish();
    }
}
