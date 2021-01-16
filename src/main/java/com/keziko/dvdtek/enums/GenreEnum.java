package com.keziko.dvdtek.enums;

import java.util.HashMap;
import java.util.Map;

public enum GenreEnum {
    DOC("DOC","documentaire"),
    ANI("ANIME","animé"),
    FIC("FIC","fiction"),
    EXP("EXP","expérimental");

    private static final Map<String, GenreEnum> BY_CODE = new HashMap<>();
    private static final Map<String, GenreEnum> BY_LABEL = new HashMap<>();
    static {
        for (GenreEnum e : values()) {
            BY_CODE.put(e.code, e);
            BY_LABEL.put(e.label,e);
        }
    }
    public final String code;
    public final String label;

    private GenreEnum(String code, String label){
        this.code = code;
        this.label = label;
    }

    public static GenreEnum valueOfCode(String code){
        return BY_CODE.get(code);
    }
    private static GenreEnum valueOfLabel(String label){
        return BY_LABEL.get(label);
    }
}
