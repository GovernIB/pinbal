package es.caib.pinbal.back.view;

import es.caib.pinbal.logic.intf.dto.ServeiDto;
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

public class InformeServeisExcelViewTest {

    @Test
    public void buildExcelDocumentGeneraUnaFilaPerServei() throws Exception {
        InformeServeisExcelView view = new InformeServeisExcelView();
        view.setMessageSource(ViewTestSupport.mockMessageSourceEcoDeLaClau());

        HttpServletRequest request = ViewTestSupport.mockRequest(Locale.forLanguageTag("ca"));
        HttpServletResponse response = mock(HttpServletResponse.class);

        ServeiDto servei1 = new ServeiDto();
        servei1.setCodi("SERV1");
        servei1.setDescripcio("Servei 1");
        ServeiDto servei2 = new ServeiDto();
        servei2.setCodi("SERV2");
        servei2.setDescripcio("Servei 2");
        Map<String, Object> model = new HashMap<>();
        model.put("informeDades", List.of(servei1, servei2));

        HSSFWorkbook workbook = new HSSFWorkbook();
        view.buildExcelDocument(model, workbook, request, response);

        verify(response).setHeader("Content-Disposition", "Inline; filename=informeServeis.xls");
        HSSFSheet sheet = workbook.getSheetAt(0);
        assertEquals("SERV1", sheet.getRow(1).getCell(0).getStringCellValue());
        assertEquals("Servei 1", sheet.getRow(1).getCell(1).getStringCellValue());
        assertEquals("SERV2", sheet.getRow(2).getCell(0).getStringCellValue());
        workbook.close();
    }
}
