package com.xe.lzh.rfid.Model;

/**
 * Created by Administrator on 2016/10/12 0012.
 */
public class UserModel {
    private String ID;
    private String ACCOUNT;
    private String PASSWORD;
    private String USERNAME;
    private String YKTH;
    private String ORGID;
    private String KIND;
    private String PHONE;
    private String EMAIL;
    private String SESSIONID;
    private long LY ;
    private long  STATE;
    private String LASTLOGINTIMEL;
    private String CREATETIME;
    private long  CREATOR;
    private String ADDRESS;
    private String DEPT;
    private long VERIFY;

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getACCOUNT() {
        return ACCOUNT;
    }

    public void setACCOUNT(String ACCOUNT) {
        this.ACCOUNT = ACCOUNT;
    }

    public String getPASSWORD() {
        return PASSWORD;
    }

    public void setPASSWORD(String PASSWORD) {
        this.PASSWORD = PASSWORD;
    }

    public String getUSERNAME() {
        return USERNAME;
    }

    public void setUSERNAME(String USERNAME) {
        this.USERNAME = USERNAME;
    }

    public String getORGID() {
        return ORGID;
    }

    public void setORGID(String ORGID) {
        this.ORGID = ORGID;
    }

    public String getYKTH() {
        return YKTH;
    }

    public void setYKTH(String YKTH) {
        this.YKTH = YKTH;
    }

    public String getKIND() {
        return KIND;
    }

    public void setKIND(String KIND) {
        this.KIND = KIND;
    }

    public String getPHONE() {
        return PHONE;
    }

    public void setPHONE(String PHONE) {
        this.PHONE = PHONE;
    }

    public String getEMAIL() {
        return EMAIL;
    }

    public void setEMAIL(String EMAIL) {
        this.EMAIL = EMAIL;
    }

    public String getSESSIONID() {
        return SESSIONID;
    }

    public void setSESSIONID(String SESSIONID) {
        this.SESSIONID = SESSIONID;
    }

    public long getLY() {
        return LY;
    }

    public void setLY(long LY) {
        this.LY = LY;
    }

    public long getSTATE() {
        return STATE;
    }

    public void setSTATE(long STATE) {
        this.STATE = STATE;
    }

    public String getLASTLOGINTIMEL() {
        return LASTLOGINTIMEL;
    }

    public void setLASTLOGINTIMEL(String LASTLOGINTIMEL) {
        this.LASTLOGINTIMEL = LASTLOGINTIMEL;
    }

    public String getCREATETIME() {
        return CREATETIME;
    }

    public void setCREATETIME(String CREATETIME) {
        this.CREATETIME = CREATETIME;
    }

    public long getCREATOR() {
        return CREATOR;
    }

    public void setCREATOR(long CREATOR) {
        this.CREATOR = CREATOR;
    }

    public String getADDRESS() {
        return ADDRESS;
    }

    public void setADDRESS(String ADDRESS) {
        this.ADDRESS = ADDRESS;
    }

    public String getDEPT() {
        return DEPT;
    }

    public void setDEPT(String DEPT) {
        this.DEPT = DEPT;
    }

    public long getVERIFY() {
        return VERIFY;
    }

    public void setVERIFY(long VERIFY) {
        this.VERIFY = VERIFY;
    }
}
