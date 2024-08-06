package es.caib.pinbal.core.model;

import es.caib.pinbal.core.audit.PinbalAuditable;
import es.caib.pinbal.core.dto.ConsultaDto;
import es.caib.pinbal.core.dto.EstatTipus;
import es.caib.pinbal.core.dto.JustificantEstat;
import lombok.Getter;
import org.apache.commons.lang.StringUtils;

import javax.persistence.Column;
import javax.persistence.ConstraintMode;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.Version;

@Getter
@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class SuperConsulta extends PinbalAuditable<Long> implements IConsulta {

    public static final int ERROR_SCSP_MAX_LENGTH = 4000;
    public static final int ERROR_JUSTIFICANT_MAX_LENGTH = 2000;

    @Column(name = "peticion_id", length = 26, nullable = false)
    protected String scspPeticionId;
    @Column(name = "solicitud_id", length = 64, nullable = false)
    protected String scspSolicitudId;

    @Column(name = "departament", length = 250, nullable = false)
    protected String departamentNom;

    @Column(name = "funcionari_nom", length = 128)
    protected String funcionariNom;
    @Column(name = "funcionari_docnum", length = 16)
    protected String funcionariDocumentNum;
    @Column(name = "titular_doctip", length = 16, nullable = false)
    protected String titularDocumentTipus;
    @Column(name = "titular_docnum", length = 14, nullable = false)
    protected String titularDocumentNum;
    @Column(name = "titular_nom", length = 40)
    protected String titularNom;
    @Column(name = "titular_lling1", length = 40)
    protected String titularLlinatge1;
    @Column(name = "titular_lling2", length = 40)
    protected String titularLlinatge2;
    @Column(name = "titular_nomcomplet", length = 122)
    protected String titularNomComplet;

    @Column(name = "finalitat", length = 250)
    protected String finalitat;
    @Column(name = "consentiment")
    protected ConsultaDto.Consentiment consentiment;
    @Column(name = "expedient_id", length = 25)
    protected String expedientId;
    @Column(name = "dades_especifiques", length = 2048)
    protected String dadesEspecifiques;

    @Column(name = "estat", nullable = false)
    protected EstatTipus estat;

    @Column(name = "error", length = ERROR_SCSP_MAX_LENGTH)
    protected String error;

    @Column(name = "recobriment")
    protected boolean recobriment = false;
    @Column(name = "multiple")
    protected boolean multiple = false;

    @Column(name = "justificant_estat", nullable = false)
    protected JustificantEstat justificantEstat = JustificantEstat.PENDENT;
    @Column(name = "custodiat")
    protected boolean custodiat = false;
    @Column(name = "custodia_id", length = 255)
    protected String custodiaId;
    @Column(name = "custodia_url", length = 255)
    protected String custodiaUrl;
    @Column(name = "justificant_error", length = ERROR_JUSTIFICANT_MAX_LENGTH)
    protected String justificantError;
    @Column(name = "arxiu_expedient_uuid", length = 100)
    protected String arxiuExpedientUuid;
    @Column(name = "arxiu_document_uuid", length = 100)
    protected String arxiuDocumentUuid;
    @Column(name = "arxiu_expedient_tancat")
    protected boolean arxiuExpedientTancat = false;

//    @ManyToOne(optional=true, fetch = FetchType.LAZY)
//    @JoinColumn(
//            name = "pare_id",
//            foreignKey = @ForeignKey(name = "pbl_consultah_pare_fk"))
//    private SuperConsulta pare;
//
//    @OneToMany(mappedBy = "pare", cascade = {CascadeType.ALL})
//    @OrderBy("scspSolicitudId asc")
//    private List<SuperConsulta> fills = new ArrayList<>();

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(
            name = "procserv_id",
            foreignKey = @ForeignKey(name = "pbl_procserv_consulta_fk"))
    protected ProcedimentServei procedimentServei;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(
            name = "procediment_id",
            foreignKey = @ForeignKey(name = "pbl_consultah_proced_fk"))
    protected Procediment procediment;

    @Column(name = "servei_codi", length = 64)
    protected String serveiCodi;

    @ManyToOne(optional = true, fetch = FetchType.LAZY)
    @JoinColumn(
            name = "servei_codi",
            referencedColumnName = "CODCERTIFICADO",
            insertable = false, updatable = false,
            foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    protected Servei serveiScsp;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(
            name = "entitat_id",
            foreignKey = @ForeignKey(name = "pbl_consultah_entitat_fk"))
    protected Entitat entitat;

    @ManyToOne(optional = true, fetch = FetchType.LAZY)
    @JoinColumns(
            value = {
                    @JoinColumn(name = "peticion_id", referencedColumnName = "idpeticion", insertable = false, updatable = false),
                    @JoinColumn(name = "solicitud_id", referencedColumnName = "idsolicitud", insertable = false, updatable = false)
            },
            foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    protected Transmision transmision;

    public String getTitularNomSencer() {
        if (!StringUtils.isBlank(this.titularNomComplet))
            return this.titularNomComplet;

        String nomSencer = "";
        if (!StringUtils.isBlank(this.titularNom)) {
            nomSencer += this.titularNom;
        }
        if (!StringUtils.isBlank(this.titularLlinatge1)) {
            nomSencer += (nomSencer.length() > 0 ? " " : "") + this.titularLlinatge1;
        }
        if (!StringUtils.isBlank(this.titularLlinatge2)) {
            nomSencer += (nomSencer.length() > 0 ? " " : "") + this.titularLlinatge2;
        }
        return nomSencer;
    }

    @Version
    protected long version = 0;

    public Long getProcedimentId() {
        return procediment.getId();
    }
    public String getProcedimentNom() {
        return procediment.getNom();
    }
    public Long getEntitatId() {
        return entitat.getId();
    }
    public String getEntitatNom() {
        return entitat.getNom();
    }
    public String getEntitatCif() {
        return entitat.getCif();
    }
    public String getServeiDescriptio() {
        return serveiScsp.getDescripcio();
    }

    public void updateJustificantEstat(
            JustificantEstat justificantEstat,
            boolean custodiat,
            String custodiaId,
            String custodiaUrl,
            String justificantError,
            String arxiuExpedientUuid,
            String arxiuDocumentUuid) {
        this.justificantEstat = justificantEstat;
        this.custodiat = custodiat;
        this.custodiaId = custodiaId;
        this.custodiaUrl = custodiaUrl;
        if (justificantError != null && justificantError.length() > ERROR_JUSTIFICANT_MAX_LENGTH) {
            String tokenFinal = " [...]";
            this.justificantError = justificantError.substring(0, (ERROR_JUSTIFICANT_MAX_LENGTH - tokenFinal.length() - 2)) + tokenFinal;
        } else {
            this.justificantError = justificantError;
        }
        this.arxiuExpedientUuid = arxiuExpedientUuid;
        this.arxiuDocumentUuid = arxiuDocumentUuid;
    }
}
