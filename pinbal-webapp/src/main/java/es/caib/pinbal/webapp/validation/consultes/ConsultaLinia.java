package es.caib.pinbal.webapp.validation.consultes;

import es.caib.pinbal.webapp.command.ConsultaCommand;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConsultaLinia {
    String[] linia;
    ConsultaCommand commandLinia;
    List<String> errorsLinia;
}
