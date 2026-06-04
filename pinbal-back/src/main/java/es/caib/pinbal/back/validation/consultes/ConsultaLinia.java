package es.caib.pinbal.back.validation.consultes;

import es.caib.pinbal.back.command.ConsultaCommand;
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
