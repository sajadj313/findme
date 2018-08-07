package com.cubosystems.findme.Models;

/**
 * Created by Administrator on 7/24/2018.
 */

public class LostPerson {
    String id;
    String personName;
    String stationName;
    String contactNumber;
    String guardID;
    long lostDateTime;
    String status;

    public LostPerson() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPersonName() {
        return personName;
    }

    public void setPersonName(String personName) {
        this.personName = personName;
    }

    public String getStationName() {
        return stationName;
    }

    public void setStationName(String stationName) {
        this.stationName = stationName;
    }

    public String getContactPerson() {
        return contactNumber;
    }

    public void setContactPerson(String contactPerson) {
        this.contactNumber = contactPerson;
    }

    public String getGuardID() {
        return guardID;
    }

    public void setGuardID(String guardID) {
        this.guardID = guardID;
    }

    public long getLostTime() {
        return lostDateTime;
    }

    public void setLostTime(long lostTime) {
        this.lostDateTime = lostTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
