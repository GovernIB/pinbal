package es.caib.pinbal.back.view;

import es.caib.pinbal.logic.intf.dto.ServeiCampDto;
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

public class PeticioMultiplePlantillaExcelViewTest {

    @Test
    public void buildExcelDocumentAmbServeiMinimNomesIncloueixExpedientIDadesEspecifiques() throws Exception {
        PeticioMultiplePlantillaExcelView view = new PeticioMultiplePlantillaExcelView();
        view.setMessageSource(ViewTestSupport.mockMessageSourceEcoDeLaClau());
        HttpServletRequest request = ViewTestSupport.mockRequest(Locale.forLanguageTag("ca"));
        HttpServletResponse response = mock(HttpServletResponse.class);

        ServeiDto servei = new ServeiDto();
        servei.setCodi("SERV1");
        ServeiCampDto camp = new ServeiCampDto();
        camp.setEtiqueta("Camp1");
        camp.setPath("DatosEspecificos/camp1");
        camp.setObligatori(true);

        Map<String, Object> model = new HashMap<>();
        model.put("servei", servei);
        model.put("campsDadesEspecifiques", List.of(camp));

        HSSFWorkbook workbook = new HSSFWorkbook();
        view.buildExcelDocument(model, workbook, request, response);

        verify(response).setHeader("Content-Disposition", "Inline; filename=plantilla_SERV1.xls");
        HSSFSheet sheet = workbook.getSheetAt(0);
        assertEquals("consulta.form.camp.expedient", sheet.getRow(1).getCell(0).getStringCellValue());
        assertEquals("DatosGenericos/Solicitante/IdExpediente", sheet.getRow(2).getCell(0).getStringCellValue());
        assertEquals("Camp1 *", sheet.getRow(1).getCell(1).getStringCellValue());
        assertEquals("DatosEspecificos/camp1", sheet.getRow(2).getCell(1).getStringCellValue());
        workbook.close();
    }
}
