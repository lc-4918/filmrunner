package com.keziko.dvdtek.enums;

import java.util.HashMap;
import java.util.Map;

public enum NormeEnum {
    NTSC("ntsc"),
    PAL("PAL");
    private static final Map<String, NormeEnum> BY_CODE = new HashMap<>();
    static {
        for (NormeEnum e : values()) {
            BY_CODE.put(e.code, e);
        }
    }
    private String code;

    private NormeEnum(String code){
        this.code = code;
    }

    public static NormeEnum valueOfCode(String code){
        return BY_CODE.get(code);
    }

    private String getCode(){
        return this.code;
    }
}
