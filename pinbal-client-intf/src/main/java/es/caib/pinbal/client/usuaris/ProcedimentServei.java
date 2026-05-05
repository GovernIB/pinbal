package es.caib.pinbal.client.usuaris;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProcedimentServei {

    private String procedimentCodi;
    private String serveiCodi;

}