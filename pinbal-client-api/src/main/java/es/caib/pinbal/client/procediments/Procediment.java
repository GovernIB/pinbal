package es.caib.pinbal.client.procediments;

import com.fasterxml.jackson.annotation.JsonView;
import es.caib.pinbal.client.comu.Create;
import es.caib.pinbal.client.comu.Update;
import es.caib.pinbal.client.comu.Vistes;
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

    @JsonView(Vistes.VistaDetallada.class)
    @NotNull(groups = Update.class)
    private Long id;
    @NotEmpty(groups = {Create.class, Update.class})
    private String codi;
    @NotEmpty(groups = {Create.class, Update.class})
    private String nom;
    @JsonView(Vistes.VistaDetallada.class)
    private String departament;
    @JsonView(Vistes.VistaDetallada.class)
    private boolean actiu;
    @JsonView(Vistes.VistaDetallada.class)
    @NotEmpty(groups = {Create.class, Update.class})
    private String entitatCodi;
    @NotEmpty(groups = {Create.class, Update.class})
    private String organGestorDir3;
    @JsonView(Vistes.VistaDetallada.class)
    private String codiSia;
    @JsonView(Vistes.VistaDetallada.class)
    private Boolean valorCampAutomatizado;
    @JsonView(Vistes.VistaDetallada.class)
    private ClaseTramite valorCampClaseTramite;
}
