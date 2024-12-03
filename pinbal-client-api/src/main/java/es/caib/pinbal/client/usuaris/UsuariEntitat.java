package es.caib.pinbal.client.usuaris;

import es.caib.pinbal.client.comu.Create;
import es.caib.pinbal.client.comu.Update;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.NotEmpty;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@UsuariUniqueField(groups = {Create.class, Update.class})
public class UsuariEntitat {

    @NotEmpty(groups = {Create.class, Update.class})
    private String entitatCodi;
    private String codi;
    private String nif;
    private String nom;
    private String departament;
    private boolean representant;
    private boolean delegat;
    private boolean auditor;
    private boolean aplicacio;
    private boolean actiu;

}
