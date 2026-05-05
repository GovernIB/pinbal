package es.caib.pinbal.client.recobriment.v2;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;
import java.util.Map;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public abstract class Resposta {

    protected boolean error;
    protected String missatge;
    protected EstatEnum estat;
    protected Map<String, List<String>> errorsValidacio;

}
