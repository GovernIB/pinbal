package es.caib.pinbal.back.view;

import es.caib.pinbal.back.command.ConsultaFiltreCommand;
import es.caib.pinbal.logic.intf.dto.ConsultaDto;
import es.caib.pinbal.logic.intf.dto.UsuariDto;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.junit.jupiter.api.Test;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

/**
 * {@code ConsultaExcelView}, {@code ConsultaAdminExcelView}, {@code ConsultaAuditorExcelView} i
 * {@code ConsultaSuperauditorExcelView} són variants gairebé idèntiques (mateixa estructura,
 * diferent joc de columnes segons el rol) que generen un llistat Excel de consultes.
 */
public class ConsultaExcelViewsTest {

    private ConsultaDto consultaCompleta() {
        ConsultaDto consulta = new ConsultaDto();
        consulta.setScspPeticionId("PET1");
        consulta.setCreacioData(new Date(0));
        UsuariDto usuari = new UsuariDto();
        usuari.setCodi("U1");
        usuari.setNom("Usuari 1");
        consulta.setCreacioUsuari(usuari);
        consulta.setFuncionariNom("Func Nom");
        consulta.setFuncionariNif("12345678Z");
        consulta.setProcedimentCodi("PROC1");
        consulta.setProcedimentNom("Procediment 1");
        consulta.setServeiCodi("SERV1");
        consulta.setServeiDescripcio("Servei 1");
        consulta.setTitularNom("Titular Nom");
        consulta.setTitularDocumentNum("87654321X");
        consulta.setEstat("Finalitzada");
        consulta.setDataEsperadaResposta(new Date(0));
        return consulta;
    }

    private Map<String, Object> model(ConsultaDto consulta) {
        Map<String, Object> model = new HashMap<>();
        model.put("consultaFiltreCommand", new ConsultaFiltreCommand());
        model.put("consultaList", List.of(consulta));
        return model;
    }

    private HttpServletRequest request() {
        return ViewTestSupport.mockRequest(Locale.forLanguageTag("ca"));
    }

    @Test
    public void consultaExcelViewGeneraColumnesDeTitular() throws Exception {
        ConsultaExcelView view = new ConsultaExcelView();
        view.setMessageSource(ViewTestSupport.mockMessageSourceEcoDeLaClau());
        HSSFWorkbook workbook = new HSSFWorkbook();

        view.buildExcelDocument(model(consultaCompleta()), workbook, request(), mock(HttpServletResponse.class));

        HSSFSheet sheet = workbook.getSheetAt(0);
        assertEquals("consulta.list.taula.numero_peticio", sheet.getRow(1).getCell(0).getStringCellValue());
        assertEquals("PET1", sheet.getRow(2).getCell(0).getStringCellValue());
        assertEquals("Titular Nom", sheet.getRow(2).getCell(4).getStringCellValue());
        assertEquals("Finalitzada", sheet.getRow(2).getCell(6).getStringCellValue());
        workbook.close();
    }

    @Test
    public void consultaAuditorExcelViewGeneraColumnesDUsuariIFuncionari() throws Exception {
        ConsultaAuditorExcelView view = new ConsultaAuditorExcelView();
        view.setMessageSource(ViewTestSupport.mockMessageSourceEcoDeLaClau());
        HSSFWorkbook workbook = new HSSFWorkbook();

        view.buildExcelDocument(model(consultaCompleta()), workbook, request(), mock(HttpServletResponse.class));

        HSSFSheet sheet = workbook.getSheetAt(0);
        assertEquals("PET1", sheet.getRow(2).getCell(0).getStringCellValue());
        assertEquals("Usuari 1 (U1)", sheet.getRow(2).getCell(2).getStringCellValue());
        assertEquals("Finalitzada", sheet.getRow(2).getCell(6).getStringCellValue());
        workbook.close();
    }

    @Test
    public void consultaSuperauditorExcelViewGeneraColumnesDUsuariIFuncionari() throws Exception {
        ConsultaSuperauditorExcelView view = new ConsultaSuperauditorExcelView();
        view.setMessageSource(ViewTestSupport.mockMessageSourceEcoDeLaClau());
        HSSFWorkbook workbook = new HSSFWorkbook();

        view.buildExcelDocument(model(consultaCompleta()), workbook, request(), mock(HttpServletResponse.class));

        HSSFSheet sheet = workbook.getSheetAt(0);
        assertEquals("PET1", sheet.getRow(2).getCell(0).getStringCellValue());
        assertEquals("Usuari 1 (U1)", sheet.getRow(2).getCell(2).getStringCellValue());
        workbook.close();
    }

    @Test
    public void consultaAdminExcelViewGeneraColumnaDerAmbData() throws Exception {
        ConsultaAdminExcelView view = new ConsultaAdminExcelView();
        view.setMessageSource(ViewTestSupport.mockMessageSourceEcoDeLaClau());
        HSSFWorkbook workbook = new HSSFWorkbook();

        view.buildExcelDocument(model(consultaCompleta()), workbook, request(), mock(HttpServletResponse.class));

        HSSFSheet sheet = workbook.getSheetAt(0);
        assertEquals("PET1", sheet.getRow(2).getCell(0).getStringCellValue());
        assertEquals("Finalitzada", sheet.getRow(2).getCell(6).getStringCellValue());
        assertEquals("01/01/1970 01:00:00", sheet.getRow(2).getCell(7).getStringCellValue());
        workbook.close();
    }

    @Test
    public void consultaMultipleExcelViewGeneraColumnesDeTitularIDer() throws Exception {
        ConsultaMultipleExcelView view = new ConsultaMultipleExcelView();
        view.setMessageSource(ViewTestSupport.mockMessageSourceEcoDeLaClau());
        HSSFWorkbook workbook = new HSSFWorkbook();

        view.buildExcelDocument(model(consultaCompleta()), workbook, request(), mock(HttpServletResponse.class));

        HSSFSheet sheet = workbook.getSheetAt(0);
        assertEquals("PET1", sheet.getRow(2).getCell(0).getStringCellValue());
        assertEquals("Titular Nom", sheet.getRow(2).getCell(4).getStringCellValue());
        assertEquals("Finalitzada", sheet.getRow(2).getCell(5).getStringCellValue());
        assertEquals("01/01/1970 01:00:00", sheet.getRow(2).getCell(6).getStringCellValue());
        workbook.close();
    }

    @Test
    public void consultaAdminExcelViewAmbDataEsperadaNullaDeixaCelBuida() throws Exception {
        ConsultaAdminExcelView view = new ConsultaAdminExcelView();
        view.setMessageSource(ViewTestSupport.mockMessageSourceEcoDeLaClau());
        HSSFWorkbook workbook = new HSSFWorkbook();
        ConsultaDto consulta = consultaCompleta();
        consulta.setDataEsperadaResposta(null);

        view.buildExcelDocument(model(consulta), workbook, request(), mock(HttpServletResponse.class));

        HSSFSheet sheet = workbook.getSheetAt(0);
        assertEquals("", sheet.getRow(2).getCell(7).getStringCellValue());
        workbook.close();
    }
}
