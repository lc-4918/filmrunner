package com.keziko.dvdtek.enums;


public enum NoteEnum {
    INC("INC"),
    CM("CM"),
    VFR("VFR"),
    SCR("SCR"),
    TVR("TVR"),
    XFR("XFR"),
    XES("XES"),
    XPT("XPT"),
    XEN("XEN"),
    XIT("XIT"),
    X2P("X2P"),
    X3P("X3P"),
    X4P("X4P");

    private String code;

    private NoteEnum(String code){
        this.code = code;
    }

    private String getCode(){
        return this.code;
    }

}
