package es.caib.pinbal.back.view;

import es.caib.pinbal.logic.intf.dto.ConsultaDto;
import es.caib.pinbal.logic.intf.dto.EntitatDto;
import es.caib.pinbal.logic.intf.dto.UsuariDto;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.junit.jupiter.api.Test;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class AuditorGenerarExcelViewTest {

    private ConsultaDto consulta(String peticioId) {
        ConsultaDto consulta = new ConsultaDto();
        consulta.setScspPeticionId(peticioId);
        consulta.setScspSolicitudId(peticioId);
        consulta.setCreacioData(new Date(0));
        UsuariDto usuari = new UsuariDto();
        usuari.setNom("Usuari 1");
        consulta.setCreacioUsuari(usuari);
        consulta.setFuncionariNom("Func Nom");
        consulta.setFuncionariNif("12345678Z");
        consulta.setProcedimentNom("Procediment 1");
        consulta.setServeiDescripcio("Servei 1");
        consulta.setEstat("Finalitzada");
        return consulta;
    }

    @Test
    public void buildExcelDocumentAmbLlistaPlanaGeneraUnaSolaTaula() throws Exception {
        AuditorGenerarExcelView view = new AuditorGenerarExcelView();
        view.setMessageSource(ViewTestSupport.mockMessageSourceEcoDeLaClau());
        HttpServletRequest request = ViewTestSupport.mockRequest(Locale.forLanguageTag("ca"));
        HttpServletResponse response = mock(HttpServletResponse.class);

        Map<String, Object> model = new HashMap<>();
        model.put("consultes", List.of(consulta("PET1")));

        HSSFWorkbook workbook = new HSSFWorkbook();
        view.buildExcelDocument(model, workbook, request, response);

        verify(response).setHeader("Content-Disposition", "Inline; filename=auditoria.xls");
        HSSFSheet sheet = workbook.getSheetAt(0);
        assertEquals("auditor.list.taula.peticion.id", sheet.getRow(1).getCell(0).getStringCellValue());
        assertEquals("PET1", sheet.getRow(2).getCell(0).getStringCellValue());
        assertEquals("Finalitzada", sheet.getRow(2).getCell(7).getStringCellValue());
        workbook.close();
    }

    @Test
    public void buildExcelDocumentAgrupatPerEntitatGeneraUnaTaulaPerEntitat() throws Exception {
        AuditorGenerarExcelView view = new AuditorGenerarExcelView();
        view.setMessageSource(ViewTestSupport.mockMessageSourceEcoDeLaClau());
        HttpServletRequest request = ViewTestSupport.mockRequest(Locale.forLanguageTag("ca"));
        HttpServletResponse response = mock(HttpServletResponse.class);

        EntitatDto entitat1 = new EntitatDto();
        entitat1.setNom("Entitat 1");
        EntitatDto entitat2 = new EntitatDto();
        entitat2.setNom("Entitat 2");
        Map<EntitatDto, List<ConsultaDto>> consultesPerEntitat = new LinkedHashMap<>();
        consultesPerEntitat.put(entitat1, List.of(consulta("PET1")));
        consultesPerEntitat.put(entitat2, List.of(consulta("PET2")));

        Map<String, Object> model = new HashMap<>();
        model.put("consultesPerEntitat", consultesPerEntitat);

        HSSFWorkbook workbook = new HSSFWorkbook();
        view.buildExcelDocument(model, workbook, request, response);

        HSSFSheet sheet = workbook.getSheetAt(0);
        assertEquals("auditor.generar.excel.entitat.titol: Entitat 1", sheet.getRow(0).getCell(0).getStringCellValue());
        assertEquals("PET1", sheet.getRow(2).getCell(0).getStringCellValue());
        // La segona taula comença just després de la primera (fila 0=entitat, 1=capsalera, 2=dada) + 1 fila en blanc
        assertEquals("auditor.generar.excel.entitat.titol: Entitat 2", sheet.getRow(4).getCell(0).getStringCellValue());
        assertEquals("PET2", sheet.getRow(6).getCell(0).getStringCellValue());
        workbook.close();
    }
}
