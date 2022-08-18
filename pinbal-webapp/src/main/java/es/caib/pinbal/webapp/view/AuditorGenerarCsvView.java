package es.caib.pinbal.webapp.view;

import es.caib.pinbal.core.dto.ConsultaDto;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.web.servlet.support.RequestContext;
import org.springframework.web.servlet.view.AbstractView;
import org.supercsv.cellprocessor.FmtDate;
import org.supercsv.cellprocessor.Optional;
import org.supercsv.cellprocessor.constraint.Unique;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.CsvMapWriter;
import org.supercsv.io.ICsvMapWriter;
import org.supercsv.prefs.CsvPreference;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AuditorGenerarCsvView extends AbstractView implements MessageSourceAware {

    private MessageSource messageSource;

    @Override
    protected void renderMergedOutputModel(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) throws Exception {

        response.setHeader("Content-Disposition", "Inline; filename=auditoria.csv");
        response.setContentType("text/csv");

        List<ConsultaDto> consultes = (List<ConsultaDto>)model.get("consultes");
        final CellProcessor[] processors = {
                new Unique(), // Peticio ID
                new FmtDate("dd/MM/yyyy  HH:mm:ss"), // Data
                new Optional(), // Usuari
                new Optional(), // nom funcionari
                new Optional(), // nif funcionari
                new Optional(), // procediment
                new Optional(), // servei
                new Optional(), // estat
        };

        final String[] header = new String[] {
                getMessage(request, "auditor.list.taula.peticion.id"),
                getMessage(request, "auditor.list.taula.data"),
                getMessage(request, "auditor.list.taula.usuari"),
                getMessage(request, "auditor.list.taula.funcionari.nom"),
                getMessage(request, "auditor.list.taula.funcionari.nif"),
                getMessage(request, "auditor.list.taula.procediment"),
                getMessage(request, "auditor.list.taula.servei"),
                getMessage(request, "auditor.list.taula.estat")};

        ICsvMapWriter mapWriter = null;
        try {
            mapWriter = new CsvMapWriter(response.getWriter(), CsvPreference.EXCEL_NORTH_EUROPE_PREFERENCE);
            mapWriter.writeHeader(header);

            for (ConsultaDto consulta : consultes) {
                final Map<String, Object> fila = new HashMap<String, Object>();
                fila.put(header[0], consulta.getScspPeticionId());
                fila.put(header[1], consulta.getCreacioData());
                fila.put(header[2], consulta.getCreacioUsuari().getNom());
                fila.put(header[3], consulta.getFuncionariNom());
                fila.put(header[4], consulta.getFuncionariNif());
                fila.put(header[5], consulta.getProcedimentNom());
                fila.put(header[6], consulta.getServeiDescripcio());
                fila.put(header[7], consulta.getEstat());

                mapWriter.write(fila, header, processors);
            }
        } finally {
            if( mapWriter != null ) {
                mapWriter.close();
            }
        }

    }

    @Override
    public void setMessageSource(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    private String getMessage(
            HttpServletRequest request,
            String key) {
        String message = messageSource.getMessage(
                key,
                null,
                "???" + key + "???",
                new RequestContext(request).getLocale());
        return message;
    }

}
