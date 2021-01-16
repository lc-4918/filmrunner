package com.keziko.dvdtek.enums;

import java.util.HashMap;
import java.util.Map;

public enum SourceEnum {
    DVD("DVD","dvd"),
    VOD("VOD","vod"),
    WEB("WEB","web"),
    TV("TV","tv"),
    VHS("VHS","vhs");

    private static final Map<String, SourceEnum> BY_CODE = new HashMap<>();
    private static final Map<String, SourceEnum> BY_LABEL = new HashMap<>();
    static {
        for (SourceEnum e : values()) {
            BY_CODE.put(e.code, e);
            BY_LABEL.put(e.label,e);
        }
    }
    public final String code;
    public final String label;

    private SourceEnum(String code, String label){
        this.code = code;
        this.label = label;
    }

    public static SourceEnum valueOfCode(String code){
        return BY_CODE.get(code);
    }
    private static SourceEnum valueOfLabel(String label){
        return BY_LABEL.get(label);
    }
}
