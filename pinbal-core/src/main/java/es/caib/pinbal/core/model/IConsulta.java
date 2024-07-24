package es.caib.pinbal.core.model;

import es.caib.pinbal.core.dto.JustificantEstat;

public interface IConsulta {

    Long getId();
    ProcedimentServei getProcedimentServei();
    String getScspPeticionId();
    String getScspSolicitudId();
    JustificantEstat getJustificantEstat();
    String getArxiuDocumentUuid();
    String getCustodiaId();
    boolean isCustodiat();
    String getCustodiaUrl();
    String getArxiuExpedientUuid();
    String getTitularDocumentNum();

    void updateJustificantEstat(
            JustificantEstat justificantEstat,
            boolean custodiat,
            String custodiaId,
            String custodiaUrl,
            String justificantError,
            String arxiuExpedientUuid,
            String arxiuDocumentUuid);
}
