package com.cily.utils.t_webrtc.pad;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.cily.utils.base.StrUtils;
import com.cily.utils.t_webrtc.BaseAc3;
import com.cily.utils.t_webrtc.Conf;
import com.cily.utils.t_webrtc.LoginAc;
import com.cily.utils.t_webrtc.R;
import com.cily.utils.t_webrtc.WsService;
import com.zxing.activity.CaptureActivity;

public class PadAc2 extends BaseAc3 {
    EditText ed_room_num;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_pad);

        ed_room_num = findView(R.id.ed_input_room_num_id);

        findBtn(R.id.btn_call).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                call();
            }
        });

        findBtn(R.id.btn_barcode).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(PadAc2.this, CaptureActivity.class);
                startActivityForResult(i, 99);
            }
        });

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK){
            return;
        }

        if (requestCode == 99){
            if (data != null){
                showDia(data.getStringExtra("result"));
            }
        }
    }

    private void showDia(String str){
        new AlertDialog.Builder(this).setTitle("扫描结果").setMessage(str + "")
                .setPositiveButton("确定", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create().show();
    }

    private void logout(){
        Intent i = new Intent(this, WsService.class);
        stopService(i);

        toAc(LoginAc.class, null);
        finish();
    }

    private void call(){
        String roomId = ed_room_num.getText().toString().trim();
        if (StrUtils.isEmpty(roomId)){
            showToast("请输入住户房间号");
            return;
        }

        Bundle b = new Bundle();
        b.putString(Conf.EXTRA_ROOMID, roomId);
        b.putBoolean(Conf.INTENT_FROM_PAD, true);
        toAc(VideoAc.class, b);
    }

    @Override
    protected void doResponse(Object b) {

    }
}