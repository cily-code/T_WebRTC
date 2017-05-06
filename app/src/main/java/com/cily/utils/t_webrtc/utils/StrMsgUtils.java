package com.cily.utils.t_webrtc.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.util.TypeUtils;
import com.cily.utils.t_webrtc.Conf;
import com.cily.utils.t_webrtc.bean.ActionBean;

/**
 * user:cily
 * time:2017/5/2
 * desc:
 */

public class StrMsgUtils {

    public static String login(String roomNum){
        return toStr(Conf.ACTION_LOGIN, roomNum, null, "login");
    }

    public static String close(String roomNum, String doorNum){
        return toStr(Conf.ACTION_LOGOUT, roomNum, doorNum, null);
    }

    public static String callVoice(String roomNum, String doorNum){
        return toStr(Conf.ACTION_CALL_VOICE, roomNum, doorNum, null);
    }

    public static String hold(String roomNum, String doorNum){
        return toStr(Conf.ACTION_HOLD, roomNum, doorNum, null);
    }

    public static String hungup(String roomNum, String doorNum){
        return toStr(Conf.ACTION_HUNG_UP, roomNum, doorNum, null);
    }

    public static String callVedio(String roomNum, String doorNum, String content){
        return toStr(Conf.ACTION_CALL_VIDEO, roomNum, doorNum, content);
    }

    public static String agree(String roomNum, String doorNum, String content){
        return toStr(Conf.ACTION_AGREE, roomNum, doorNum, content);
    }

    public static String refuse(String roomNum, String doorNum){
        return toStr(Conf.ACTION_REFUSE, roomNum, doorNum, null);
    }

    public static String msg(String roomNum, String doorNum, String msg){
        return toStr(11, roomNum, doorNum, msg);
    }

    private static String toStr(int action, String roomNum, String doorNum, String content){
        ActionBean b = new ActionBean();
        if (roomNum == null){
            roomNum = "";
        }
        b.setSenderRoomNumber(roomNum);
        if (doorNum == null){
            doorNum = "";
        }
        b.setReceiverRoomNumber(doorNum);
        b.setCode(action);
        if (content == null){
            content = "";
        }
        b.setContent(content);

        TypeUtils.compatibleWithFieldName = true;

        return JSON.toJSONString(b);
    }
}