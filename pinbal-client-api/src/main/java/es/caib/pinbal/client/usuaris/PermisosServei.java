package es.caib.pinbal.client.usuaris;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.NotEmpty;

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
