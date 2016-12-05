package com.xe.lzh.rfid.Model;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/9/19.
 */
public class EpcModel implements Serializable {
    private String EPC;
    private String DH;//档案号
    private String GroupID;//库号
    private String DataID;
    private int count;
    private String state;

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public EpcModel(String EPC, String DH, String groupID, String dataID, String state) {
        this.EPC = EPC;
        this.DH = DH;
        GroupID = groupID;
        DataID = dataID;
        this.state = state;
    }

    public EpcModel(String EPC, String DH, String groupID, String dataID, int count) {
        this.EPC = EPC;
        this.DH = DH;
        GroupID = groupID;
        DataID = dataID;
        this.count = count;
    }

    public EpcModel() {
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getEPC() {
        return EPC;
    }

    public void setEPC(String EPC) {
        this.EPC = EPC;
    }

    public String getDH() {
        return DH;
    }

    public void setDH(String DH) {
        this.DH = DH;
    }

    public String getGroupID() {
        return GroupID;
    }

    public void setGroupID(String groupID) {
        GroupID = groupID;
    }

    public String getDataID() {
        return DataID;
    }

    public void setDataID(String dataID) {
        DataID = dataID;
    }

    @Override
    public String toString() {
        return "EpcModel{" +
                "EPC='" + EPC + '\'' +
                ", DH='" + DH + '\'' +
                ", GroupID='" + GroupID + '\'' +
                ", DataID='" + DataID + '\'' +
                ", count=" + count +
                ", state='" + state + '\'' +
                '}';
    }
}
