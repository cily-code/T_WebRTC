package com.cily.utils.t_webrtc;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.cily.utils.app.utils.SpUtils;
import com.cily.utils.base.StrUtils;
import com.cily.utils.t_webrtc.utils.WsUtils;

/**
 * user:cily
 * time:2017/5/2
 * desc:
 */

public class WsService extends Service {

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent == null || StrUtils.isEmpty(intent.getStringExtra(Conf.USER_ROOM))){
            WsUtils.conn(this, SpUtils.getStr(this, Conf.USER_ROOM, null));
        }else{
            WsUtils.conn(this, intent.getStringExtra(Conf.USER_ROOM));
        }


        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        WsUtils.close(SpUtils.getStr(this, Conf.USER_ROOM, null));
        SpUtils.remove(this, Conf.USER_ROOM);

        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
