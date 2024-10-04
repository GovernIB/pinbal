package es.caib.pinbal.core.dto;

import lombok.Getter;

@Getter
public enum NumElementsPaginaEnum {
    DEU(10),
    VINT(20),
    CINQUANTA(50),
    CENT(100),
    DOSCENTSCINQUANTA(250);
    
    private int elements;
    
    NumElementsPaginaEnum(int elements) {
        this.elements = elements;
    }

    public static NumElementsPaginaEnum fromElements(Integer elements) {
        if (elements == null)
            return null;

        for (NumElementsPaginaEnum num : NumElementsPaginaEnum.values()) {
            if (num.getElements() == elements) {
                return num;
            }
        }
        throw new IllegalArgumentException("No enum constant with elements: " + elements);
    }

}
