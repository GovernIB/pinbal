package es.caib.pinbal.core.dto.regles;

import lombok.Builder;
import lombok.Data;

import java.util.Set;


@Data
@Builder
public class VariableFact {

    // Regla
    private ModificatEnum modificat;
    private Set<String> modificatValors;
    private Set<String> afectatValors;
    private AccioEnum accio;

    // Variable comprovada
    private TipusVarEnum tipus;
    private Long varId;
    private String varCodi;

    // Dades auxiliars per el càlcul de la regla
    private boolean aplicaReglaCondicio = false;
    private Set<String> grupsModificats;
    private Set<String> campsModificats;

    // Camps que defineixen com mostrar la variable
    private boolean visible;
    private boolean editable;
    private boolean obligatori;

}

