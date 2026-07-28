package es.caib.pinbal.back.view;

import com.opencsv.CSVWriter;
import es.caib.pinbal.logic.intf.dto.ServeiCampDto;
import es.caib.pinbal.logic.intf.dto.ServeiDto;
import org.junit.jupiter.api.Test;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class PeticioMultiplePlantillaCsvViewTest {

    private ServeiDto serveiMinim() {
        ServeiDto servei = new ServeiDto();
        servei.setCodi("SERV1");
        return servei;
    }

    private ServeiCampDto campEspecific(String etiqueta, String path, boolean obligatori) {
        ServeiCampDto camp = new ServeiCampDto();
        camp.setEtiqueta(etiqueta);
        camp.setPath(path);
        camp.setObligatori(obligatori);
        return camp;
    }

    @Test
    public void buildCsvDocumentAmbServeiMinimNomesIncloueixExpedientIDadesEspecifiques() throws Exception {
        PeticioMultiplePlantillaCsvView view = new PeticioMultiplePlantillaCsvView();
        view.setMessageSource(ViewTestSupport.mockMessageSourceEcoDeLaClau());
        HttpServletRequest request = ViewTestSupport.mockRequest(Locale.forLanguageTag("ca"));
        HttpServletResponse response = mock(HttpServletResponse.class);

        Map<String, Object> model = new HashMap<>();
        model.put("servei", serveiMinim());
        model.put("campsDadesEspecifiques", List.of(campEspecific("Camp1", "DatosEspecificos/camp1", true)));

        StringWriter sw = new StringWriter();
        CSVWriter csvWriter = new CSVWriter(sw);
        view.buildCsvDocument(model, csvWriter, request, response);
        csvWriter.close();

        verify(response).setHeader("Content-Disposition", "attachment; filename=plantilla_SERV1.csv");
        String[] linies = sw.toString().split("\n");
        assertEquals(3, linies.length);
        assertTrue(linies[1].contains("consulta.form.camp.expedient"));
        assertTrue(linies[1].contains("Camp1 *"));
        assertTrue(linies[2].contains("DatosGenericos/Solicitante/IdExpediente"));
        assertTrue(linies[2].contains("DatosEspecificos/camp1"));
    }

    @Test
    public void buildCsvDocumentAmbTotsElsCampsActivatsElsIncloueixTots() throws Exception {
        PeticioMultiplePlantillaCsvView view = new PeticioMultiplePlantillaCsvView();
        view.setMessageSource(ViewTestSupport.mockMessageSourceEcoDeLaClau());
        HttpServletRequest request = ViewTestSupport.mockRequest(Locale.forLanguageTag("ca"));
        HttpServletResponse response = mock(HttpServletResponse.class);

        ServeiDto servei = serveiMinim();
        servei.setPinbalActiuCampDocument(true);
        servei.setPinbalDocumentObligatori(true);
        servei.setPinbalPermesDocumentTipusNif(true);
        servei.setPinbalActiuCampNomComplet(true);
        servei.setPinbalActiuCampNom(true);
        servei.setPinbalActiuCampLlinatge1(true);
        servei.setPinbalActiuCampLlinatge2(true);

        Map<String, Object> model = new HashMap<>();
        model.put("servei", servei);
        model.put("campsDadesEspecifiques", List.of());

        StringWriter sw = new StringWriter();
        CSVWriter csvWriter = new CSVWriter(sw);
        view.buildCsvDocument(model, csvWriter, request, response);
        csvWriter.close();

        String[] linies = sw.toString().split("\n");
        String capsalera = linies[1];
        String paths = linies[2];
        assertTrue(capsalera.contains("NIF"));
        assertTrue(paths.contains("DatosGenericos/Titular/TipoDocumentacion"));
        assertTrue(paths.contains("DatosGenericos/Titular/Documentacion"));
        assertTrue(paths.contains("DatosGenericos/Titular/NombreCompleto"));
        assertTrue(paths.contains("DatosGenericos/Titular/Nombre"));
        assertTrue(paths.contains("DatosGenericos/Titular/Apellido1"));
        assertTrue(paths.contains("DatosGenericos/Titular/Apellido2"));
    }

    @Test
    public void buildCsvDocumentAmbCampSenseEtiquetaUsaElCampNom() throws Exception {
        PeticioMultiplePlantillaCsvView view = new PeticioMultiplePlantillaCsvView();
        view.setMessageSource(ViewTestSupport.mockMessageSourceEcoDeLaClau());
        HttpServletRequest request = ViewTestSupport.mockRequest(Locale.forLanguageTag("ca"));
        HttpServletResponse response = mock(HttpServletResponse.class);

        ServeiCampDto camp = campEspecific(null, "DatosEspecificos/camp2", false);

        Map<String, Object> model = new HashMap<>();
        model.put("servei", serveiMinim());
        model.put("campsDadesEspecifiques", List.of(camp));

        StringWriter sw = new StringWriter();
        CSVWriter csvWriter = new CSVWriter(sw);
        view.buildCsvDocument(model, csvWriter, request, response);
        csvWriter.close();

        String capsalera = sw.toString().split("\n")[1];
        assertTrue(capsalera.contains("camp2"));
        assertFalse(capsalera.contains("camp2 *"));
    }
}
