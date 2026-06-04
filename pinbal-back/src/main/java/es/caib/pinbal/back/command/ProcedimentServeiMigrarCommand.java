package es.caib.pinbal.back.command;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProcedimentServeiMigrarCommand {
    private Long procedimentId;
    private String serveiCodiOriginal;
    private String serveiCodiDesti;
}
