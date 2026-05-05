package es.caib.pinbal.client.usuaris;

import javax.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PermisosServei {

    @NotEmpty
    private String usuariCodi;
    @NotEmpty
    private String entitatCodi;
    @NotEmpty
    private List<ProcedimentServei> procedimentServei;

}
