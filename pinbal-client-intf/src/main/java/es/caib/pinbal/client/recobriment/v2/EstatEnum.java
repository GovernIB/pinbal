package es.caib.pinbal.client.recobriment.v2;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public enum EstatEnum {
    PENDENT,
    PROCESSANT,
    TRAMITADA,
    ERROR,
    ENCUA;

    private static final Map<String, EstatEnum> lookup;
    static {
        lookup = new HashMap<String, EstatEnum>();
        for (EstatEnum s: EnumSet.allOf(EstatEnum.class))
            lookup.put(s.name(), s);
    }
    public static EstatEnum valorAsEnum(String valor) {
        if (valor == null)
            return null;
        return lookup.get(valor.toUpperCase());
    }
}
