package com.cubosystems.findme.Helper;

/**
 * Created by Administrator on 7/27/2018.
 */

public class BroadcastModel {
    String guardID;
    String LostPersonID;
    boolean hasOpened;

    public BroadcastModel(){}

    public String getGuardID() {
        return guardID;
    }

    public void setGuardID(String guardID) {
        this.guardID = guardID;
    }

    public String getLostPersonID() {
        return LostPersonID;
    }

    public void setLostPersonID(String lostPersonID) {
        LostPersonID = lostPersonID;
    }

    public boolean isHasOpened() {
        return hasOpened;
    }

    public void setHasOpened(boolean hasOpened) {
        this.hasOpened = hasOpened;
    }
}
