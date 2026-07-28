package es.caib.pinbal.back.view;

import com.opencsv.CSVWriter;
import es.caib.pinbal.logic.intf.dto.ConsultaDto;
import es.caib.pinbal.logic.intf.dto.UsuariDto;
import org.junit.jupiter.api.Test;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.StringWriter;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class AuditorGenerarCsvViewTest {

    @Test
    public void buildCsvDocumentEscriuCapsaleraIUnaFilaPerConsulta() throws Exception {
        AuditorGenerarCsvView view = new AuditorGenerarCsvView();
        view.setMessageSource(ViewTestSupport.mockMessageSourceEcoDeLaClau());
        HttpServletRequest request = ViewTestSupport.mockRequest(Locale.forLanguageTag("ca"));
        HttpServletResponse response = mock(HttpServletResponse.class);

        ConsultaDto consulta = new ConsultaDto();
        consulta.setScspPeticionId("PET1");
        consulta.setCreacioData(new Date(0));
        UsuariDto usuari = new UsuariDto();
        usuari.setNom("Usuari Prova");
        consulta.setCreacioUsuari(usuari);
        consulta.setFuncionariNom("Func Nom");
        consulta.setFuncionariNif("12345678Z");
        consulta.setProcedimentNom("Procediment 1");
        consulta.setServeiDescripcio("Servei 1");
        consulta.setEstat("Finalitzada");

        Map<String, Object> model = new HashMap<>();
        model.put("consultes", List.of(consulta));

        StringWriter sw = new StringWriter();
        CSVWriter csvWriter = new CSVWriter(sw);
        view.buildCsvDocument(model, csvWriter, request, response);
        csvWriter.close();

        verify(response).setHeader("Content-Disposition", "Inline; filename=auditoria.csv");
        String[] linies = sw.toString().split("\n");
        assertEquals(2, linies.length);
        assertTrue(linies[0].contains("auditor.list.taula.peticion.id"));
        assertTrue(linies[1].contains("PET1"));
        assertTrue(linies[1].contains("Usuari Prova"));
        assertTrue(linies[1].contains("Func Nom"));
        assertTrue(linies[1].contains("12345678Z"));
        assertTrue(linies[1].contains("Procediment 1"));
        assertTrue(linies[1].contains("Servei 1"));
        assertTrue(linies[1].contains("Finalitzada"));
    }

    @Test
    public void buildCsvDocumentSenseConsultesNomesEscriuLaCapsalera() throws Exception {
        AuditorGenerarCsvView view = new AuditorGenerarCsvView();
        view.setMessageSource(ViewTestSupport.mockMessageSourceEcoDeLaClau());
        HttpServletRequest request = ViewTestSupport.mockRequest(Locale.forLanguageTag("ca"));
        HttpServletResponse response = mock(HttpServletResponse.class);

        Map<String, Object> model = new HashMap<>();
        model.put("consultes", null);

        StringWriter sw = new StringWriter();
        CSVWriter csvWriter = new CSVWriter(sw);
        view.buildCsvDocument(model, csvWriter, request, response);
        csvWriter.close();

        assertEquals(1, sw.toString().split("\n").length);
    }

    @Test
    public void buildCsvDocumentAmbConsultaSenseUsuariNiDataDeixaCampsBuits() throws Exception {
        AuditorGenerarCsvView view = new AuditorGenerarCsvView();
        view.setMessageSource(ViewTestSupport.mockMessageSourceEcoDeLaClau());
        HttpServletRequest request = ViewTestSupport.mockRequest(Locale.forLanguageTag("ca"));
        HttpServletResponse response = mock(HttpServletResponse.class);

        ConsultaDto consulta = new ConsultaDto();
        consulta.setScspPeticionId("PET2");

        Map<String, Object> model = new HashMap<>();
        model.put("consultes", List.of(consulta));

        StringWriter sw = new StringWriter();
        CSVWriter csvWriter = new CSVWriter(sw);
        view.buildCsvDocument(model, csvWriter, request, response);
        csvWriter.close();

        String[] linies = sw.toString().split("\n");
        assertTrue(linies[1].contains("PET2"));
    }
}
