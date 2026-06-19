package es.caib.pinbal.persist.entity;


import es.caib.pinbal.logic.intf.dto.JustificantEstat;

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
    boolean isAplicacioGuardaJustificantArxiu();
    boolean isRecobriment();

    void updateJustificantEstat(
            JustificantEstat justificantEstat,
            boolean custodiat,
            String custodiaId,
            String custodiaUrl,
            String justificantError,
            String arxiuExpedientUuid,
            String arxiuDocumentUuid);

    void updateArxiuExpedientUuid(String arxiuExpedientUuid);

    void updateArxiuDocumentUuid(String arxiuDocumentUuid);
}
