package es.caib.pinbal.back.view;

import es.caib.pinbal.logic.intf.dto.EntitatDto;
import es.caib.pinbal.logic.intf.dto.InformeUsuariDto;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.junit.jupiter.api.Test;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

public class InformeUsuarisExcelViewTest {

    private InformeUsuariDto usuari(EntitatDto entitat, String departament, String codi, String nom) {
        InformeUsuariDto dada = new InformeUsuariDto();
        dada.setEntitat(entitat);
        dada.setDepartament(departament);
        dada.setCodi(codi);
        dada.setNom(nom);
        dada.setNif("12345678Z");
        return dada;
    }

    @Test
    public void buildExcelDocumentAgrupaPerEntitatIDepartamentAmbSubtotals() throws Exception {
        InformeUsuarisExcelView view = new InformeUsuarisExcelView();
        view.setMessageSource(ViewTestSupport.mockMessageSourceEcoDeLaClau());
        HttpServletRequest request = ViewTestSupport.mockRequest(Locale.forLanguageTag("ca"));
        HttpServletResponse response = mock(HttpServletResponse.class);

        EntitatDto entitat1 = new EntitatDto();
        entitat1.setId(1L);
        entitat1.setNom("Entitat 1");
        EntitatDto entitat2 = new EntitatDto();
        entitat2.setId(2L);
        entitat2.setNom("Entitat 2");

        List<InformeUsuariDto> dades = new ArrayList<>();
        dades.add(usuari(entitat1, "Departament A", "U1", "Usuari 1"));
        dades.add(usuari(entitat1, "Departament A", "U2", "Usuari 2"));
        dades.add(usuari(entitat1, null, "U3", "Usuari 3"));
        dades.add(usuari(entitat2, "Departament B", "U4", "Usuari 4"));

        Map<String, Object> model = new HashMap<>();
        model.put("informeDades", dades);

        HSSFWorkbook workbook = new HSSFWorkbook();
        view.buildExcelDocument(model, workbook, request, response);

        HSSFSheet sheet = workbook.getSheetAt(0);
        StringBuilder totText = new StringBuilder();
        for (int i = 0; i <= sheet.getLastRowNum(); i++) {
            var row = sheet.getRow(i);
            if (row != null && row.getCell(0) != null && row.getCell(0).getCellType() == org.apache.poi.ss.usermodel.CellType.STRING) {
                totText.append(row.getCell(0).getStringCellValue()).append("|");
            }
        }
        String contingut = totText.toString();
        assertTrue(contingut.contains("Entitat 1 (3)"));
        assertTrue(contingut.contains("Entitat 2 (1)"));
        assertTrue(contingut.contains("Departament A (2)"));
        assertTrue(contingut.contains("sense.departament (1)"));
        assertTrue(contingut.contains("Departament B (1)"));
        workbook.close();
    }
}
