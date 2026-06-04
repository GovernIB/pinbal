package es.caib.pinbal.back.view;

import com.opencsv.CSVWriter;
import es.caib.pinbal.logic.intf.dto.ConsultaDto;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.web.servlet.support.RequestContext;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AuditorGenerarCsvView extends AbstractCsvView implements MessageSourceAware {

    private MessageSource messageSource;

    @Override
    @SuppressWarnings("unchecked")
    protected void buildCsvDocument(
            Map<String, Object> model,
            CSVWriter csvWriter,
            HttpServletRequest request,
            HttpServletResponse response) throws IOException {

        response.setHeader("Content-Disposition", "Inline; filename=auditoria.csv");

        List<ConsultaDto> consultes = (List<ConsultaDto>) model.get("consultes");
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy  HH:mm:ss");

        String[] header = {
                getMessage(request, "auditor.list.taula.peticion.id"),
                getMessage(request, "auditor.list.taula.data"),
                getMessage(request, "auditor.list.taula.usuari"),
                getMessage(request, "auditor.list.taula.funcionari.nom"),
                getMessage(request, "auditor.list.taula.funcionari.nif"),
                getMessage(request, "auditor.list.taula.procediment"),
                getMessage(request, "auditor.list.taula.servei"),
                getMessage(request, "auditor.list.taula.estat")
        };
        csvWriter.writeNext(header);

        if (consultes != null) {
            for (ConsultaDto consulta : consultes) {
                List<String> fila = new ArrayList<>();
                fila.add(consulta.getScspPeticionId());
                fila.add(consulta.getCreacioData() != null ? sdf.format(consulta.getCreacioData()) : "");
                fila.add(consulta.getCreacioUsuari() != null ? consulta.getCreacioUsuari().getNom() : "");
                fila.add(consulta.getFuncionariNom());
                fila.add(consulta.getFuncionariNif());
                fila.add(consulta.getProcedimentNom());
                fila.add(consulta.getServeiDescripcio());
                fila.add(consulta.getEstat());
                csvWriter.writeNext(fila.toArray(new String[0]));
            }
        }
    }

    @Override
    public void setMessageSource(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    private String getMessage(HttpServletRequest request, String key) {
        return messageSource.getMessage(key, null, "???" + key + "???",
                new RequestContext(request).getLocale());
    }
}
