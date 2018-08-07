package com.cubosystems.findme.Models;

/**
 * Created by Administrator on 7/23/2018.
 */

public class Guard {
    String name;
    String appLang;
    String spokenLangs;
    String phoneNo;

    public Guard() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAppLang() {
        return appLang;
    }

    public void setAppLang(String appLang) {
        this.appLang = appLang;
    }

    public String getSpokenLangs() {
        return spokenLangs;
    }

    public void setSpokenLangs(String spokenLangs) {
        this.spokenLangs = spokenLangs;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }
}
