package es.caib.pinbal.back.view;

import es.caib.pinbal.logic.intf.dto.InformeProcedimentServeiDto;
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

public class InformeUsrEntOrgProcServExcelViewTest {

    private InformeProcedimentServeiDto dada() {
        InformeProcedimentServeiDto dada = new InformeProcedimentServeiDto();
        dada.setEntitatCodi("ENT1");
        dada.setEntitatNom("Entitat 1");
        dada.setEntitatCif("B00000000");
        dada.setOrganGestorCodi("OG1");
        dada.setOrganGestorNom("Organ 1");
        dada.setProcedimentCodi("PROC1");
        dada.setProcedimentNom("Procediment 1");
        dada.setServeiCodi("SERV1");
        dada.setServeiNom("Servei 1");
        dada.setUsuariCodi("U1");
        dada.setUsuariNom("Usuari 1");
        dada.setUsuariNif("12345678Z");
        return dada;
    }

    @Test
    public void buildExcelDocumentPerAdministradorIncloueixColumnesDEntitat() throws Exception {
        InformeUsrEntOrgProcServExcelView view = new InformeUsrEntOrgProcServExcelView();
        view.setMessageSource(ViewTestSupport.mockMessageSourceEcoDeLaClau());
        HttpServletRequest request = ViewTestSupport.mockRequest(Locale.forLanguageTag("ca"));
        HttpServletResponse response = mock(HttpServletResponse.class);

        Map<String, Object> model = new HashMap<>();
        model.put("informeDades", List.of(dada()));
        model.put("isAdministrador", Boolean.TRUE);

        HSSFWorkbook workbook = new HSSFWorkbook();
        view.buildExcelDocument(model, workbook, request, response);

        verify(response).setHeader("Content-Disposition", "Inline; filename=informeUsuarisEntitatOrganProcServei.xls");
        HSSFSheet sheet = workbook.getSheetAt(0);
        assertEquals("ENT1", sheet.getRow(1).getCell(0).getStringCellValue());
        assertEquals("Entitat 1", sheet.getRow(1).getCell(1).getStringCellValue());
        assertEquals("OG1", sheet.getRow(1).getCell(3).getStringCellValue());
        assertEquals("U1", sheet.getRow(1).getCell(9).getStringCellValue());
        workbook.close();
    }

    @Test
    public void buildExcelDocumentSenseAdministradorOmetColumnesDEntitat() throws Exception {
        InformeUsrEntOrgProcServExcelView view = new InformeUsrEntOrgProcServExcelView();
        view.setMessageSource(ViewTestSupport.mockMessageSourceEcoDeLaClau());
        HttpServletRequest request = ViewTestSupport.mockRequest(Locale.forLanguageTag("ca"));
        HttpServletResponse response = mock(HttpServletResponse.class);

        Map<String, Object> model = new HashMap<>();
        model.put("informeDades", List.of(dada()));
        model.put("isAdministrador", Boolean.FALSE);

        HSSFWorkbook workbook = new HSSFWorkbook();
        view.buildExcelDocument(model, workbook, request, response);

        HSSFSheet sheet = workbook.getSheetAt(0);
        assertEquals("OG1", sheet.getRow(1).getCell(0).getStringCellValue());
        assertEquals("PROC1", sheet.getRow(1).getCell(2).getStringCellValue());
        assertEquals("U1", sheet.getRow(1).getCell(6).getStringCellValue());
        workbook.close();
    }
}
