package es.caib.pinbal.persist.entity;

import es.caib.pinbal.logic.intf.dto.ProcedimentClaseTramiteEnumDto;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class ProcedimentClasseTramiteConverterTest {

    private final ProcedimentClasseTramiteConverter converter = new ProcedimentClasseTramiteConverter();

    @Test
    public void testConvertToDatabaseColumn() {
        Long valor = converter.convertToDatabaseColumn(ProcedimentClaseTramiteEnumDto.CERTIFICADOS);

        assertEquals(22L, valor);
    }

    @Test
    public void testConvertToDatabaseColumnNull() {
        assertNull(converter.convertToDatabaseColumn(null));
    }

    @Test
    public void testConvertToEntityAttribute() {
        ProcedimentClaseTramiteEnumDto valor = converter.convertToEntityAttribute(22L);

        assertEquals(ProcedimentClaseTramiteEnumDto.CERTIFICADOS, valor);
    }

    @Test
    public void testConvertToEntityAttributeNull() {
        assertNull(converter.convertToEntityAttribute(null));
    }

    @Test
    public void testConvertToEntityAttributeDesconegut() {
        assertNull(converter.convertToEntityAttribute(999L));
    }
}
