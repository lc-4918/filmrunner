package com.keziko.dvdtek.enums;

import java.util.HashMap;
import java.util.Map;

public enum TypeEnum {
    LM("LM","long-métrage"),
    MM("MM","moyen-métrage"),
    CM("CM","court-métrage");
    private static final Map<String, TypeEnum> BY_CODE = new HashMap<>();
    private static final Map<String, TypeEnum> BY_LABEL = new HashMap<>();
    static {
        for (TypeEnum e : values()) {
            BY_CODE.put(e.code, e);
            BY_LABEL.put(e.label,e);
        }
    }
    public final String code;
    public final String label;

    private TypeEnum(String code, String label){
        this.code = code;
        this.label = label;
    }

    public static TypeEnum valueOfCode(String code){
        return BY_CODE.get(code);
    }
    private static TypeEnum valueOfLabel(String label){
        return BY_LABEL.get(label);
    }

}
