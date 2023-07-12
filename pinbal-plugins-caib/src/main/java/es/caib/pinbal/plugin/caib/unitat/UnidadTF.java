package es.caib.pinbal.plugin.caib.unitat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UnidadTF extends es.caib.dir3caib.ws.api.unidad.UnidadTF implements Serializable {

    private String denominacionCooficial;
}
