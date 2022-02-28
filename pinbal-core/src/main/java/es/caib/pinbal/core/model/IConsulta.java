package es.caib.pinbal.core.model;

public interface IConsulta {

    Long getId();
    ProcedimentServei getProcedimentServei();
    String getScspPeticionId();
    String getScspSolicitudId();
    Consulta.JustificantEstat getJustificantEstat();
    String getArxiuDocumentUuid();
    String getCustodiaId();
    boolean isCustodiat();
    String getCustodiaUrl();
    String getArxiuExpedientUuid();
    String getTitularDocumentNum();

    void updateJustificantEstat(
            Consulta.JustificantEstat justificantEstat,
            boolean custodiat,
            String custodiaId,
            String custodiaUrl,
            String justificantError,
            String arxiuExpedientUuid,
            String arxiuDocumentUuid);
}
