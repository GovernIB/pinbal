package es.caib.pinbal.back.view;

import es.caib.pinbal.logic.intf.dto.EmisorDto;
import es.caib.pinbal.logic.intf.dto.InformeGeneralEstatDto;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.junit.jupiter.api.Test;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class InformeGeneralEstatExcelViewTest {

    @Test
    public void buildExcelDocumentGeneraUnaFilaPerInforme() throws Exception {
        InformeGeneralEstatExcelView view = new InformeGeneralEstatExcelView();
        view.setMessageSource(ViewTestSupport.mockMessageSourceEcoDeLaClau());
        HttpServletRequest request = ViewTestSupport.mockRequest(Locale.forLanguageTag("ca"));
        HttpServletResponse response = mock(HttpServletResponse.class);

        InformeGeneralEstatDto dada = new InformeGeneralEstatDto();
        dada.setEntitatNom("Entitat 1");
        dada.setEntitatCif("B00000000");
        dada.setDepartament("Departament");
        dada.setProcedimentCodi("PROC1");
        dada.setProcedimentNom("Procediment 1");
        dada.setServeiCodi("SERV1");
        dada.setServeiNom("Servei 1");
        EmisorDto emisor = new EmisorDto();
        emisor.setNom("Emissor 1");
        dada.setServeiEmisor(emisor);
        dada.setServeiUsuaris(3);
        dada.setPeticionsCorrectes(10);
        dada.setPeticionsErronees(2);

        Map<String, Object> model = new HashMap<>();
        model.put("informeDades", List.of(dada));

        HSSFWorkbook workbook = new HSSFWorkbook();
        view.buildExcelDocument(model, workbook, request, response);

        verify(response).setHeader("Content-Disposition", "Inline; filename=informeServeis.xls");
        HSSFSheet sheet = workbook.getSheetAt(0);
        assertEquals("Entitat 1", sheet.getRow(1).getCell(0).getStringCellValue());
        assertEquals("B00000000", sheet.getRow(1).getCell(1).getStringCellValue());
        assertEquals("Emissor 1", sheet.getRow(1).getCell(7).getStringCellValue());
        assertEquals(3, (int) sheet.getRow(1).getCell(8).getNumericCellValue());
        assertEquals(10, (int) sheet.getRow(1).getCell(9).getNumericCellValue());
        assertEquals(2, (int) sheet.getRow(1).getCell(10).getNumericCellValue());
        workbook.close();
    }
}
