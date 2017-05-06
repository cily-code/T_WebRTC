package com.cily.utils.t_webrtc;

import com.alibaba.fastjson.JSON;
import com.cily.utils.app.ac.BaseActivity;
import com.cily.utils.app.event.Event;
import com.cily.utils.app.utils.L;
import com.cily.utils.t_webrtc.bean.ActionBean;
import com.cily.utils.t_webrtc.impl.ObserverImpl;
import com.cily.utils.t_webrtc.utils.ObserverUtils;

/**
 * user:cily
 * time:2017/5/3
 * desc:
 */

public abstract class BaseAc3 extends BaseActivity implements ObserverImpl {

    @Override
    protected void onStart() {
        super.onStart();

        ObserverUtils.getInstance().regist(this);
    }

    @Override
    protected void onStop() {
        super.onStop();

        ObserverUtils.getInstance().unregist(this);
    }

    @Override
    public void doEvent(Event e) {
        L.i(TAG, "收到消息：" + e.obj);
        if (e.what == Conf.WS_MSG){
            doResponse(e.obj);
        }
    }

    protected abstract void doResponse(Object b);
}
