package com.cily.utils.t_webrtc.bean;

import java.io.Serializable;

/**
 * user:cily
 * time:2017/5/2
 * desc:
 */

public class ActionBean implements Serializable {
    private int Code;
    private String Content;
    private String SenderRoomNumber;
    private String ReceiverRoomNumber;

    public String getContent() {
        return Content;
    }

    public void setContent(String content) {
        Content = content;
    }

    public String getSenderRoomNumber() {
        return SenderRoomNumber;
    }

    public void setSenderRoomNumber(String senderRoomNumber) {
        SenderRoomNumber = senderRoomNumber;
    }

    public String getReceiverRoomNumber() {
        return ReceiverRoomNumber;
    }

    public void setReceiverRoomNumber(String receiverRoomNumber) {
        ReceiverRoomNumber = receiverRoomNumber;
    }

    @Override
    public String toString() {
        return "ActionBean{" +
                "Code=" + Code +
                ", Content='" + Content + '\'' +
                ", SenderRoomNumber='" + SenderRoomNumber + '\'' +
                ", ReceiverRoomNumber='" + ReceiverRoomNumber + '\'' +
                '}';
    }

    public int getCode() {
        return Code;
    }

    public void setCode(int code) {
        this.Code = code;
    }
}
