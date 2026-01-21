package es.caib.pinbal.core.helper.mock;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import es.caib.pinbal.core.dto.FitxerDto;
import es.caib.pinbal.core.dto.JustificantEstat;
import es.caib.pinbal.core.helper.JustificantHelper;
import es.caib.pinbal.core.model.IConsulta;
import es.caib.pinbal.scsp.ResultatEnviamentPeticio;
import es.caib.pinbal.scsp.ScspHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

/**
 * Mock de JustificantHelper per desenvolupament sense accés a plugins d'arxiu, custòdia i firma.
 *
 * Aquest mock genera justificants PDF simples sense utilitzar:
 * - Plugin d'arxiu (ArxiuPlugin)
 * - Plugin de custòdia (CustodiaPlugin)
 * - Plugin de firma (FirmaServidorPlugin, SignaturaIBKey)
 *
 * Els justificants es generen com a PDFs simples amb la informació bàsica de la consulta.
 *
 * Per activar-lo: perfil mock-scsp-db
 *
 * @author Pinbal Development Team
 */
@Slf4j
@Component
@Profile("mock-scsp-db")
public class JustificantHelperMock extends JustificantHelper {

    private static final String MOCK_PREFIX = "🔧 [MOCK JUSTIFICANT]";
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

    @Override
    public void generarCustodiarJustificantPendent(
            IConsulta consulta,
            ScspHelper scspHelper) {
        log.info("{} ============================================", MOCK_PREFIX);
        log.info("{} Generant justificant MOCK (sense custòdia)", MOCK_PREFIX);
        log.info("{} ID Petició: {}", MOCK_PREFIX, consulta.getScspPeticionId());
        log.info("{} ID Sol·licitud: {}", MOCK_PREFIX, consulta.getScspSolicitudId());
        log.info("{} ============================================", MOCK_PREFIX);

        try {
            // Genera el justificant simple
            FitxerDto justificant = generar(consulta, scspHelper);

            // Marca com a generat però sense custòdia
            consulta.updateJustificantEstat(
                    JustificantEstat.OK_NO_CUSTODIA,
                    false,  // No custodiat
                    null,   // No custodiaId
                    null,   // No custodiaUrl
                    null,   // No justificantError
                    null,   // No arxiuExpedientUuid
                    null);  // No arxiuDocumentUuid

            log.info("{} ✅ Justificant generat correctament (estat: OK_NO_CUSTODIA)", MOCK_PREFIX);
            log.info("{} Nom arxiu: {}", MOCK_PREFIX, justificant.getNom());
            log.info("{} Mida: {} bytes", MOCK_PREFIX, justificant.getContingut().length);

        } catch (Exception ex) {
            log.error("{} ❌ Error generant justificant mock", MOCK_PREFIX, ex);
            consulta.updateJustificantEstat(
                    JustificantEstat.ERROR,
                    false,
                    null,
                    null,
                    ex.getMessage(),
                    null,
                    null);
        }
    }

    @Override
    public FitxerDto descarregarFitxerGenerat(
            IConsulta consulta,
            ScspHelper scspHelper,
            boolean versioImprimible) throws Exception {

        log.info("{} Descarregant justificant MOCK", MOCK_PREFIX);
        log.info("{} ID Petició: {}", MOCK_PREFIX, consulta.getScspPeticionId());
        log.info("{} ID Sol·licitud: {}", MOCK_PREFIX, consulta.getScspSolicitudId());

        // Genera un nou justificant cada vegada (sense custòdia)
        FitxerDto justificant = generar(consulta, scspHelper);

        log.info("{} ✅ Justificant descarregat correctament", MOCK_PREFIX);

        return justificant;
    }

    @Override
    public FitxerDto generar(
            IConsulta consulta,
            ScspHelper scspHelper) {

        log.debug("{} Generant PDF del justificant", MOCK_PREFIX);

        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            Document document = new Document();
            PdfWriter.getInstance(document, baos);

            document.open();

            // Títol
            Font fontTitol = new Font(Font.HELVETICA, 18, Font.BOLD, Color.BLUE);
            Paragraph titol = new Paragraph("JUSTIFICANT DE CONSULTA - MOCK", fontTitol);
            titol.setAlignment(Element.ALIGN_CENTER);
            titol.setSpacingAfter(20);
            document.add(titol);

            // Informació bàsica
            Font fontLabel = new Font(Font.HELVETICA, 10, Font.BOLD);
            Font fontValue = new Font(Font.HELVETICA, 10, Font.NORMAL);

            PdfPTable table = new PdfPTable(2);
            table.setWidthPercentage(100);
            table.setWidths(new float[]{35, 65});
            table.setSpacingBefore(10);
            table.setSpacingAfter(10);

            addTableRow(table, "Data generació:", DATE_FORMAT.format(new Date()), fontLabel, fontValue);
            addTableRow(table, "ID Petició:", consulta.getScspPeticionId(), fontLabel, fontValue);
            addTableRow(table, "ID Sol·licitud:", consulta.getScspSolicitudId(), fontLabel, fontValue);

            if (consulta.getTitularDocumentNum() != null) {
                addTableRow(table, "Document titular:", consulta.getTitularDocumentNum(), fontLabel, fontValue);
            }

            if (consulta.getProcedimentServei() != null) {
                String procediment = consulta.getProcedimentServei().getProcediment() != null ?
                    consulta.getProcedimentServei().getProcediment().getNom() : "N/A";
                addTableRow(table, "Procediment:", procediment, fontLabel, fontValue);

                String servei = consulta.getProcedimentServei().getServeiScsp() != null ?
                    consulta.getProcedimentServei().getServeiScsp().getDescripcio() : "N/A";
                addTableRow(table, "Servei:", servei, fontLabel, fontValue);
            }

            document.add(table);

            // Obtenir estat de la petició
            try {
                Map<String, Object> dadesEspecifiques = scspHelper.getDadesEspecifiquesResposta(
                    consulta.getScspPeticionId(),
                    consulta.getScspSolicitudId());

                if (dadesEspecifiques != null && !dadesEspecifiques.isEmpty()) {
                    // Afegir avís que hi ha dades específiques
                    Paragraph avisDades = new Paragraph(
                        "\nAquest justificant conté dades específiques de la resposta SCSP.\n" +
                        "Per veure el detall complet, accediu a la consulta a l'aplicació.\n",
                        new Font(Font.HELVETICA, 9, Font.ITALIC, Color.GRAY));
                    avisDades.setSpacingBefore(10);
                    document.add(avisDades);
                }
            } catch (Exception e) {
                log.warn("{} No s'han pogut obtenir les dades específiques: {}", MOCK_PREFIX, e.getMessage());
            }

            // Peu de pàgina
            Paragraph peu = new Paragraph(
                "\n\n⚠️ ATENCIÓ: Aquest és un justificant generat pel MOCK de desenvolupament.\n" +
                "No té validesa legal. No s'ha aplicat signatura electrònica ni custòdia.",
                new Font(Font.HELVETICA, 8, Font.ITALIC, Color.RED));
            peu.setAlignment(Element.ALIGN_CENTER);
            document.add(peu);

            document.close();

            FitxerDto fitxer = new FitxerDto();
            fitxer.setNom(getNomArxiuJustificant(
                consulta.getScspPeticionId(),
                consulta.getScspSolicitudId()));
            fitxer.setContentType("application/pdf");
            fitxer.setContingut(baos.toByteArray());

            log.debug("{} PDF generat correctament: {} bytes", MOCK_PREFIX, fitxer.getContingut().length);

            return fitxer;

        } catch (Exception e) {
            log.error("{} Error generant PDF del justificant", MOCK_PREFIX, e);
            throw new RuntimeException("Error generant justificant mock", e);
        }
    }

    /**
     * Afegeix una fila a la taula amb label i valor
     */
    private void addTableRow(PdfPTable table, String label, String value, Font fontLabel, Font fontValue) {
        PdfPCell cellLabel = new PdfPCell(new Phrase(label, fontLabel));
        cellLabel.setBorder(PdfPCell.NO_BORDER);
        cellLabel.setPaddingBottom(5);
        table.addCell(cellLabel);

        PdfPCell cellValue = new PdfPCell(new Phrase(value != null ? value : "N/A", fontValue));
        cellValue.setBorder(PdfPCell.NO_BORDER);
        cellValue.setPaddingBottom(5);
        table.addCell(cellValue);
    }

    /**
     * Genera el nom de l'arxiu del justificant
     */
    private String getNomArxiuJustificant(String peticionId, String solicitudId) {
        return "justificant_" + peticionId + "_" + solicitudId + ".pdf";
    }
}
