package es.caib.pinbal.client.procediments;

import es.caib.pinbal.client.comu.Create;
import es.caib.pinbal.client.comu.Update;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Procediment {

    @NotNull(groups = Update.class)
    private Long id;
    @NotEmpty(groups = {Create.class, Update.class})
    private String codi;
    @NotEmpty(groups = {Create.class, Update.class})
    private String nom;
    private String departament;
    private boolean actiu;
    @NotEmpty(groups = {Create.class, Update.class})
    private String entitatCodi;
    @NotEmpty(groups = {Create.class, Update.class})
    private String organGestorDir3;
    private String codiSia;
    private Boolean valorCampAutomatizado;
    private ClaseTramite valorCampClaseTramite;
}
