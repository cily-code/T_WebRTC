package com.cily.utils.t_webrtc.utils;

import com.cily.utils.app.event.Event;
import com.cily.utils.t_webrtc.impl.ObserverImpl;

import java.util.ArrayList;
import java.util.List;

/**
 * user:cily
 * time:2017/5/2
 * desc:
 */

public class ObserverUtils {

    private static class VH{
        private final static ObserverUtils utils = new ObserverUtils();
    }

    private ObserverUtils(){
        list = new ArrayList<>();
    }

    public static ObserverUtils getInstance(){
        return VH.utils;
    }

    private final List<ObserverImpl> list;

    public void regist(ObserverImpl impl){
        list.add(impl);
    }

    public void unregist(ObserverImpl impl){
        list.remove(impl);
    }

    public void send(Event e){
        if (e != null && canSend()){
            for (ObserverImpl impl : list){
                impl.doEvent(e);
            }
        }
    }

    public boolean canSend(){
        return list != null && list.size() > 0;
    }
}
