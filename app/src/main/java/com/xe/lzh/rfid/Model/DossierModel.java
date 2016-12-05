package com.xe.lzh.rfid.Model;


import com.xe.lzh.rfid.tree.TreeNodeId;
import com.xe.lzh.rfid.tree.TreeNodeLabel;
import com.xe.lzh.rfid.tree.TreeNodePid;

/**
 * Created by Administrator on 2016/9/30.
 */
public class DossierModel {
    @TreeNodeId
    private int ID;
    @TreeNodePid
    private int PGROUP;
    @TreeNodeLabel
    private String GROUPNAME;

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public int getPGROUP() {
        return PGROUP;
    }

    public void setPGROUP(int PGROUP) {
        this.PGROUP = PGROUP;
    }

    public String getGROUPNAME() {
        return GROUPNAME;
    }

    public void setGROUPNAME(String GROUPNAME) {
        this.GROUPNAME = GROUPNAME;
    }

    public DossierModel(int ID, int PGROUP, String GROUPNAME) {
        this.ID = ID;
        this.PGROUP = PGROUP;
        this.GROUPNAME = GROUPNAME;
    }

    public DossierModel() {
    }
}
