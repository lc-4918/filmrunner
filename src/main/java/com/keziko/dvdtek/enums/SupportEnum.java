package com.keziko.dvdtek.enums;

import java.util.HashMap;
import java.util.Map;

public enum SupportEnum {
    XRIP("XRIP"),
    p720("720p"),
    p1080("1080"),
    DVD5("DVD5"),
    DVD9("DVD5"),
    BDR("BD-R");

    private static final Map<String, SupportEnum> BY_CODE = new HashMap<>();

    static {
        for (SupportEnum e : values()) {
            BY_CODE.put(e.code, e);
        }
    }
    public final String code;

    private SupportEnum(String code){
        this.code = code;
    }

    public static SupportEnum valueOfCode(String code){
        return BY_CODE.get(code);
    }
    private String getCode(){
        return this.code;
    }
}
