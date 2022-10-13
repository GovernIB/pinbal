package es.caib.pinbal.core.model;

import es.caib.pinbal.core.dto.ProcedimentClaseTramiteEnumDto;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
public class ProcedimentClasseTramiteConverter implements AttributeConverter<ProcedimentClaseTramiteEnumDto, Long> {

    @Override
    public Long convertToDatabaseColumn(ProcedimentClaseTramiteEnumDto attribute) {
        if (attribute == null)
            return null;
        return Short.valueOf(attribute.getShortValue()).longValue();
    }

    @Override
    public ProcedimentClaseTramiteEnumDto convertToEntityAttribute(Long dbData) {
        if (dbData == null)
            return null;
        return ProcedimentClaseTramiteEnumDto.from(dbData.shortValue());
    }
}
