package es.caib.pinbal.back.view;

import es.caib.pinbal.logic.intf.dto.EntitatDto;
import es.caib.pinbal.logic.intf.dto.InformeProcedimentDto;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellType;
import org.junit.jupiter.api.Test;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class InformeProcedimentsExcelViewTest {

    private InformeProcedimentDto procediment(EntitatDto entitat, String departament, String codi, boolean actiu) {
        InformeProcedimentDto dada = new InformeProcedimentDto();
        dada.setEntitat(entitat);
        dada.setDepartament(departament);
        dada.setCodi(codi);
        dada.setNom("Procediment " + codi);
        dada.setActiu(actiu);
        return dada;
    }

    @Test
    public void buildExcelDocumentAgrupaPerEntitatIDepartamentAmbSubtotals() throws Exception {
        InformeProcedimentsExcelView view = new InformeProcedimentsExcelView();
        view.setMessageSource(ViewTestSupport.mockMessageSourceEcoDeLaClau());
        HttpServletRequest request = ViewTestSupport.mockRequest(Locale.forLanguageTag("ca"));
        HttpServletResponse response = mock(HttpServletResponse.class);

        EntitatDto entitat1 = new EntitatDto();
        entitat1.setId(1L);
        entitat1.setNom("Entitat 1");

        List<InformeProcedimentDto> dades = new ArrayList<>();
        dades.add(procediment(entitat1, "Departament A", "P1", true));
        dades.add(procediment(entitat1, "Departament A", "P2", false));
        dades.add(procediment(entitat1, null, "P3", true));

        Map<String, Object> model = new HashMap<>();
        model.put("informeDades", dades);

        HSSFWorkbook workbook = new HSSFWorkbook();
        view.buildExcelDocument(model, workbook, request, response);

        verify(response).setHeader("Content-Disposition", "Inline; filename=informeProcediments.xls");
        HSSFSheet sheet = workbook.getSheetAt(0);
        StringBuilder totText = new StringBuilder();
        for (int i = 0; i <= sheet.getLastRowNum(); i++) {
            var row = sheet.getRow(i);
            if (row != null && row.getCell(0) != null && row.getCell(0).getCellType() == CellType.STRING) {
                totText.append(row.getCell(0).getStringCellValue()).append("|");
            }
        }
        String contingut = totText.toString();
        assertTrue(contingut.contains("Entitat 1 (3)"));
        assertTrue(contingut.contains("Departament A (2)"));
        assertTrue(contingut.contains("sense.departament (1)"));
        assertTrue(contingut.contains("P1"));
        assertTrue(contingut.contains("P3"));
        workbook.close();
    }
}
