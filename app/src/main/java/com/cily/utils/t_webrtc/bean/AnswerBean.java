package com.cily.utils.t_webrtc.bean;

import java.io.Serializable;

/**
 *
 */

public class AnswerBean implements Serializable {
    private String Sdp;
    private String Type;

    public String getSdp() {
        return Sdp;
    }

    public void setSdp(String sdp) {
        Sdp = sdp;
    }

    public String getType() {
        return Type;
    }

    public void setType(String type) {
        Type = type;
    }
}
