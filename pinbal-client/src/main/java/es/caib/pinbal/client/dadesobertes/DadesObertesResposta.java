package es.caib.pinbal.client.dadesobertes;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonAutoDetect
public class DadesObertesResposta {
    Integer totalElements;
    Integer paginaActual;
    Integer totalPagines;
    String properaPagina;
    List<DadesObertesRespostaConsulta> dades;
}
