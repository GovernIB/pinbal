package es.caib.pinbal.logic.helper;

import es.caib.pinbal.logic.intf.dto.EmisorDto;
import es.caib.pinbal.logic.intf.dto.InformeGeneralEstatDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.MessageSource;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class ExcelHelperTest {

    private ExcelHelper excelHelper;
    private MessageSource messageSource;

    @BeforeEach
    public void setUp() {
        excelHelper = new ExcelHelper();
        messageSource = mock(MessageSource.class);
        when(messageSource.getMessage(anyString(), any(), anyString(), any(Locale.class))).thenReturn("Col");
        excelHelper.setMessageSource(messageSource);
    }

    @Test
    public void generarReportEstatExcel_llistaVuida_retornaByteArray() {
        byte[] result = excelHelper.generarReportEstatExcel(Collections.emptyList());
        assertNotNull(result);
        assertTrue(result.length > 0);
    }

    @Test
    public void generarReportEstatExcel_amBDades_retornaByteArray() {
        InformeGeneralEstatDto dto = new InformeGeneralEstatDto();
        dto.setEntitatNom("Entitat Test");
        dto.setEntitatCif("A07002132");
        dto.setDepartament("Dept Test");
        dto.setProcedimentCodi("PROC01");
        dto.setProcedimentNom("Procediment Test");
        dto.setServeiCodi("SV01");
        dto.setServeiNom("Servei Test");
        EmisorDto emisor = new EmisorDto();
        emisor.setNom("Emissor Test");
        dto.setServeiEmisor(emisor);
        dto.setServeiUsuaris(5);
        dto.setPeticionsCorrectes(10);
        dto.setPeticionsErronees(2);

        byte[] result = excelHelper.generarReportEstatExcel(List.of(dto));
        assertNotNull(result);
        assertTrue(result.length > 0);
    }
}
