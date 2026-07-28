package es.caib.pinbal.back.view;

import es.caib.pinbal.logic.intf.dto.ServeiCampDto;
import es.caib.pinbal.logic.intf.dto.ServeiDto;
import org.junit.jupiter.api.Test;
import org.odftoolkit.odfdom.doc.OdfSpreadsheetDocument;
import org.odftoolkit.odfdom.doc.table.OdfTable;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class PeticioMultiplePlantillaOdsViewTest {

    private ServeiDto serveiMinim() {
        ServeiDto servei = new ServeiDto();
        servei.setCodi("SERV1");
        return servei;
    }

    @Test
    public void buildOdsDocumentAmbServeiMinimNomesIncloueixExpedientIDadesEspecifiques() throws Exception {
        PeticioMultiplePlantillaOdsView view = new PeticioMultiplePlantillaOdsView();
        view.setMessageSource(ViewTestSupport.mockMessageSourceEcoDeLaClau());
        HttpServletRequest request = ViewTestSupport.mockRequest(Locale.forLanguageTag("ca"));
        HttpServletResponse response = mock(HttpServletResponse.class);

        ServeiCampDto camp = new ServeiCampDto();
        camp.setEtiqueta("Camp1");
        camp.setPath("DatosEspecificos/camp1");
        camp.setObligatori(true);

        Map<String, Object> model = new HashMap<>();
        model.put("servei", serveiMinim());
        model.put("campsDadesEspecifiques", List.of(camp));

        OdfSpreadsheetDocument ods = OdfSpreadsheetDocument.newSpreadsheetDocument();
        view.buildOdsDocument(model, ods, request, response);

        verify(response).setHeader("Content-Disposition", "Inline; filename=plantilla_SERV1.ods");
        OdfTable sheet = ods.getTableList().get(0);
        assertEquals("consulta.plantilla.titol", sheet.getTableName());
        assertEquals("consulta.form.camp.expedient", sheet.getCellByPosition(0, 1).getStringValue());
        assertEquals("DatosGenericos/Solicitante/IdExpediente", sheet.getCellByPosition(0, 2).getStringValue());
        assertEquals("Camp1 *", sheet.getCellByPosition(1, 1).getStringValue());
        assertEquals("DatosEspecificos/camp1", sheet.getCellByPosition(1, 2).getStringValue());
        ods.close();
    }

    @Test
    public void buildOdsDocumentAmbTotsElsCampsActivatsElsIncloueixTots() throws Exception {
        PeticioMultiplePlantillaOdsView view = new PeticioMultiplePlantillaOdsView();
        view.setMessageSource(ViewTestSupport.mockMessageSourceEcoDeLaClau());
        HttpServletRequest request = ViewTestSupport.mockRequest(Locale.forLanguageTag("ca"));
        HttpServletResponse response = mock(HttpServletResponse.class);

        ServeiDto servei = serveiMinim();
        servei.setPinbalActiuCampDocument(true);
        servei.setPinbalPermesDocumentTipusNif(true);
        servei.setPinbalActiuCampNomComplet(true);
        servei.setPinbalActiuCampNom(true);
        servei.setPinbalActiuCampLlinatge1(true);
        servei.setPinbalActiuCampLlinatge2(true);

        Map<String, Object> model = new HashMap<>();
        model.put("servei", servei);
        model.put("campsDadesEspecifiques", List.of());

        OdfSpreadsheetDocument ods = OdfSpreadsheetDocument.newSpreadsheetDocument();
        view.buildOdsDocument(model, ods, request, response);

        OdfTable sheet = ods.getTableList().get(0);
        // columnes: 0=expedient, 1=document tipus, 2=document num, 3=nomcomplet, 4=nom, 5=llinatge1, 6=llinatge2
        assertEquals("DatosGenericos/Titular/TipoDocumentacion", sheet.getCellByPosition(1, 2).getStringValue());
        assertEquals("DatosGenericos/Titular/Documentacion", sheet.getCellByPosition(2, 2).getStringValue());
        assertEquals("DatosGenericos/Titular/NombreCompleto", sheet.getCellByPosition(3, 2).getStringValue());
        assertEquals("DatosGenericos/Titular/Nombre", sheet.getCellByPosition(4, 2).getStringValue());
        assertEquals("DatosGenericos/Titular/Apellido1", sheet.getCellByPosition(5, 2).getStringValue());
        assertEquals("DatosGenericos/Titular/Apellido2", sheet.getCellByPosition(6, 2).getStringValue());
        ods.close();
    }
}
