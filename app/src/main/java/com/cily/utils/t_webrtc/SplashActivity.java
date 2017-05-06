package com.cily.utils.t_webrtc;

import android.content.Intent;
import android.os.Bundle;

import com.cily.utils.app.Init;
import com.cily.utils.app.ac.BaseActivity;
import com.cily.utils.app.event.Event;
import com.cily.utils.app.utils.SpUtils;
import com.cily.utils.base.StrUtils;
import com.cily.utils.t_webrtc.pad.PadAc;
import com.cily.utils.t_webrtc.pad.PadAc2;
import com.cily.utils.t_webrtc.pad.PhoneAc;
import com.cily.utils.t_webrtc.pad.PhoneWaitAc;

import org.webrtc.Logging;

public class SplashActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_splash);
        Init.init(BuildConfig.DEBUG);

        Logging.enableLogToDebugOutput(Logging.Severity.LS_NONE);
        if (BuildConfig.TYPE_APP == -1){
            toAc(PadAc.class, null);
        }else {
            if (StrUtils.isEmpty(SpUtils.getStr(this, Conf.USER_ROOM, null))) {
                toAc(LoginAc.class, null);
            } else {
                Intent i = new Intent(this, WsService.class);
                i.putExtra(Conf.USER_ROOM, SpUtils.getStr(this, Conf.USER_ROOM, null));
                startService(i);

                if (BuildConfig.TYPE_APP == 0) {
                    toAc(PadAc2.class, null);
                } else if (BuildConfig.TYPE_APP == 1) {
                    toAc(PhoneWaitAc.class, null);
                }
            }
        }
        finish();
    }
}