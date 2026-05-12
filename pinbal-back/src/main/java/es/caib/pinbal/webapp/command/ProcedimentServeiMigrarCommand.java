package es.caib.pinbal.webapp.command;

import lombok.Data;

@Data
public class ProcedimentServeiMigrarCommand {
    private Long procedimentId;
    private String serveiCodiOriginal;
    private String serveiCodiDesti;
}
