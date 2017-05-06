package com.cily.utils.t_webrtc.bean;

import java.io.Serializable;

/**
 *
 */

public class CallBean implements Serializable{
    private String Sdp;
    private String Type;
    private int Label;
    private String Id;
    private String Candidate;

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

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getCandidate() {
        return Candidate;
    }

    public void setCandidate(String candidate) {
        Candidate = candidate;
    }

    public int getLabel() {
        return Label;
    }

    public void setLabel(int label) {
        Label = label;
    }
}
