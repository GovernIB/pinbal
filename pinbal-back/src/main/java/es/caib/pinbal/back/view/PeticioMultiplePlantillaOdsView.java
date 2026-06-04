package es.caib.pinbal.back.view;

import es.caib.pinbal.logic.intf.dto.ServeiCampDto;
import es.caib.pinbal.logic.intf.dto.ServeiDto;
import org.odftoolkit.odfdom.doc.OdfSpreadsheetDocument;
import org.odftoolkit.odfdom.doc.table.OdfTable;
import org.odftoolkit.odfdom.doc.table.OdfTableCell;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.web.servlet.support.RequestContext;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * Vista per a generar la plantilla ODS per a consultes múltiples.
 */
public class PeticioMultiplePlantillaOdsView extends AbstractOdsView implements MessageSourceAware {

    private static final int ROW_TITLE = 0;
    private static final int ROW_HEADER = 1;
    private static final int ROW_PATH = 2;

    private MessageSource messageSource;

    @SuppressWarnings("unchecked")
    @Override
    protected void buildOdsDocument(
            Map<String, Object> model,
            OdfSpreadsheetDocument ods,
            HttpServletRequest request,
            HttpServletResponse response) throws Exception {

        ServeiDto servei = (ServeiDto) model.get("servei");
        response.setHeader("Content-Disposition", "Inline; filename=plantilla_" + servei.getCodi() + ".ods");
        String titol = getMessage(request, "consulta.plantilla.titol", new Object[]{servei.getCodi()});

        OdfTable sheet = ods.getTableList().get(0);
        sheet.setTableName(titol);

        List<ServeiCampDto> camps = (List<ServeiCampDto>) model.get("campsDadesEspecifiques");
        taulaCamps(titol, servei, camps, request, sheet);
    }

    @Override
    public void setMessageSource(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    private void taulaCamps(
            String titol,
            ServeiDto servei,
            List<ServeiCampDto> camps,
            HttpServletRequest request,
            OdfTable sheet) {

        int columna = 0;

        // Títol (fila 0)
        setCellValue(sheet, columna, ROW_TITLE, titol);

        // Expedient
        setCellValue(sheet, columna, ROW_HEADER, getMessage(request, "consulta.form.camp.expedient"));
        setCellValue(sheet, columna, ROW_PATH, "DatosGenericos/Solicitante/IdExpediente");

        // Document
        if (servei.isPinbalActiuCampDocument()) {
            setCellValue(sheet, ++columna, ROW_HEADER,
                    getMessage(request, "consulta.form.camp.document.tipus") +
                    (servei.isPinbalDocumentObligatori() ? " * (" : " (") +
                    (servei.isPinbalPermesDocumentTipusCif() ? "CIF " : "") +
                    (servei.isPinbalPermesDocumentTipusDni() ? "DNI " : "") +
                    (servei.isPinbalPermesDocumentTipusNie() ? "NIE " : "") +
                    (servei.isPinbalPermesDocumentTipusNif() ? "NIF " : "") +
                    (servei.isPinbalPermesDocumentTipusPas() ? "Passaport " : "") + ")");
            setCellValue(sheet, columna, ROW_PATH, "DatosGenericos/Titular/TipoDocumentacion");

            setCellValue(sheet, ++columna, ROW_HEADER,
                    getMessage(request, "consulta.form.camp.document.num") +
                    (servei.isPinbalDocumentObligatori() ? " *" : ""));
            setCellValue(sheet, columna, ROW_PATH, "DatosGenericos/Titular/Documentacion");
        }

        // Nom complet
        if (servei.isPinbalActiuCampNomComplet()) {
            setCellValue(sheet, ++columna, ROW_HEADER, getMessage(request, "consulta.form.camp.nomcomplet"));
            setCellValue(sheet, columna, ROW_PATH, "DatosGenericos/Titular/NombreCompleto");
        }

        // Nom
        if (servei.isPinbalActiuCampNom()) {
            setCellValue(sheet, ++columna, ROW_HEADER, getMessage(request, "consulta.form.camp.nom"));
            setCellValue(sheet, columna, ROW_PATH, "DatosGenericos/Titular/Nombre");
        }

        // 1er llinatge
        if (servei.isPinbalActiuCampLlinatge1()) {
            setCellValue(sheet, ++columna, ROW_HEADER, getMessage(request, "consulta.form.camp.llinatge1"));
            setCellValue(sheet, columna, ROW_PATH, "DatosGenericos/Titular/Apellido1");
        }

        // 2on llinatge
        if (servei.isPinbalActiuCampLlinatge2()) {
            setCellValue(sheet, ++columna, ROW_HEADER, getMessage(request, "consulta.form.camp.llinatge2"));
            setCellValue(sheet, columna, ROW_PATH, "DatosGenericos/Titular/Apellido2");
        }

        // Dades específiques
        for (ServeiCampDto camp : camps) {
            String label = camp.getEtiqueta() != null
                    ? camp.getEtiqueta() + (camp.isObligatori() ? " *" : "")
                    : camp.getCampNom() + (camp.isObligatori() ? " *" : "");
            setCellValue(sheet, ++columna, ROW_HEADER, label);
            setCellValue(sheet, columna, ROW_PATH, camp.getPath());
        }
    }

    private void setCellValue(OdfTable sheet, int col, int row, String value) {
        OdfTableCell cell = sheet.getCellByPosition(col, row);
        cell.setStringValue(value);
    }

    private String getMessage(HttpServletRequest request, String key) {
        return messageSource.getMessage(key, null, "???" + key + "???",
                new RequestContext(request).getLocale());
    }

    private String getMessage(HttpServletRequest request, String key, Object[] args) {
        return messageSource.getMessage(key, args, "???" + key + "???",
                new RequestContext(request).getLocale());
    }
}
