/**
 * 
 */
package es.caib.pinbal.core.model;

import es.caib.pinbal.core.audit.PinbalAuditable;
import es.caib.pinbal.core.dto.ConsultaDto.Consentiment;
import es.caib.pinbal.core.model.Consulta.EstatTipus;
import es.caib.pinbal.core.model.Consulta.JustificantEstat;
import lombok.Getter;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.hibernate.annotations.Formula;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

import static es.caib.pinbal.core.model.Consulta.ERROR_JUSTIFICANT_MAX_LENGTH;
import static es.caib.pinbal.core.model.Consulta.ERROR_SCSP_MAX_LENGTH;

/**
 * Classe de model de dades que conté la informació d'una consulta.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter
@Entity
@Table(
		name = "pbl_consulta_hist",
		indexes = {
				@Index(name = "pbl_consultah_procserv_i", columnList = "procserv_id"),
				@Index(name = "pbl_consultah_createdby_i", columnList = "createdby_codi")})
@EntityListeners(AuditingEntityListener.class)
public class HistoricConsulta extends PinbalAuditable<Long> {

	private static final long serialVersionUID = -6657066865382086237L;

	@Column(name = "peticion_id", length = 26, nullable = false)
	private String scspPeticionId;
	@Column(name = "solicitud_id", length = 64, nullable = false)
	private String scspSolicitudId;

	@Column(name = "departament", length = 64, nullable = false)
	private String departamentNom;
	
	@Column(name = "funcionari_nom", length = 128)
	private String funcionariNom;
	@Column(name = "funcionari_docnum", length = 16)
	private String funcionariDocumentNum;
	@Column(name = "titular_doctip", length = 16, nullable = false)
	private String titularDocumentTipus;
	@Column(name = "titular_docnum", length = 14, nullable = false)
	private String titularDocumentNum;
	@Column(name = "titular_nom", length = 40)
	private String titularNom;
	@Column(name = "titular_lling1", length = 40)
	private String titularLlinatge1;
	@Column(name = "titular_lling2", length = 40)
	private String titularLlinatge2;
	@Column(name = "titular_nomcomplet", length = 122)
	private String titularNomComplet;

	@Column(name = "finalitat", length = 250)
	private String finalitat;
	@Column(name = "consentiment")
	private Consentiment consentiment;
	@Column(name = "expedient_id", length = 25)
	private String expedientId;
	@Column(name = "dades_especifiques", length = 2048)
	private String dadesEspecifiques;

	@Column(name = "estat", nullable = false)
	private EstatTipus estat;

	@Column(name = "error", length = ERROR_SCSP_MAX_LENGTH)
	private String error;

	@Column(name = "recobriment")
	private boolean recobriment = false;
	@Column(name = "multiple")
	private boolean multiple = false;

	@Column(name = "justificant_estat", nullable = false)
	private JustificantEstat justificantEstat = JustificantEstat.PENDENT;
	@Column(name = "custodiat")
	private boolean custodiat = false;
	@Column(name = "custodia_id", length = 255)
	private String custodiaId;
	@Column(name = "custodia_url", length = 255)
	private String custodiaUrl;
	@Column(name = "justificant_error", length = ERROR_JUSTIFICANT_MAX_LENGTH)
	private String justificantError;
	@Column(name = "arxiu_expedient_uuid", length = 100)
	private String arxiuExpedientUuid;
	@Column(name = "arxiu_document_uuid", length = 100)
	private String arxiuDocumentUuid;
	@Column(name = "arxiu_expedient_tancat")
	private boolean arxiuExpedientTancat = false;
	
	@ManyToOne(optional=true, fetch = FetchType.LAZY)
	@JoinColumn(
			name = "pare_id",
			foreignKey = @ForeignKey(name = "pbl_consultah_pare_fk"))
	private HistoricConsulta pare;

	@OneToMany(mappedBy = "pare", cascade = {CascadeType.ALL})
	@OrderBy("scspSolicitudId asc")
	private List<HistoricConsulta> fills = new ArrayList<>();

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(
			name = "procserv_id",
			foreignKey = @ForeignKey(name = "pbl_consultah_procserv_fk"))
	private ProcedimentServei procedimentServei;

	@ManyToOne(optional = true, fetch = FetchType.LAZY)
	@JoinColumns(
			value = {
					@JoinColumn(name = "peticion_id", referencedColumnName = "idpeticion", insertable = false, updatable = false),
					@JoinColumn(name = "solicitud_id", referencedColumnName = "idsolicitud", insertable = false, updatable = false)
			},
			foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
	private Transmision transmision;

	@Formula("(SELECT pps.servei_id FROM pbl_procediment_servei pps WHERE pps.id = procserv_id)")
	private String serveiCodi;
	@Formula("(SELECT cs.descripcion FROM pbl_procediment_servei pps, core_servicio cs WHERE pps.id = procserv_id AND cs.codcertificado = pps.servei_id)")
	private String serveiDescripcio;
	@Formula("(SELECT pps.procediment_id FROM pbl_procediment_servei pps WHERE pps.id = procserv_id)")
	private Long procedimentId;
	@Formula("(SELECT ppr.nom FROM pbl_procediment_servei pps, pbl_procediment ppr WHERE pps.id = procserv_id AND ppr.id = pps.procediment_id)")
	private String procedimentNom;
	@Formula("(SELECT ppr.entitat_id FROM pbl_procediment_servei pps, pbl_procediment ppr WHERE pps.id = procserv_id AND ppr.id = pps.procediment_id)")
	private Long entitatId;
	@Formula("(SELECT pen.nom FROM pbl_procediment_servei pps, pbl_procediment ppr, pbl_entitat pen WHERE pps.id = procserv_id AND ppr.id = pps.procediment_id AND pen.id = ppr.entitat_id)")
	private String entitatNom;
	@Formula("(SELECT pen.cif FROM pbl_procediment_servei pps, pbl_procediment ppr, pbl_entitat pen WHERE pps.id = procserv_id AND ppr.id = pps.procediment_id AND pen.id = ppr.entitat_id)")
	private String entitatCif;

	@Version
	private long version = 0;

//	public void updateEstat(EstatTipus estat) {
//		this.estat = estat;
//	}
//	public void updateEstatError(String error) {
//		this.estat = EstatTipus.Error;
//		if (error != null && error.length() > ERROR_SCSP_MAX_LENGTH) {
//			String tokenFinal = " [...]";
//			this.error = error.substring(0, (ERROR_SCSP_MAX_LENGTH - tokenFinal.length())) + tokenFinal;
//		} else {
//			this.error = error;
//		}
//	}
//	public void updateJustificantEstat(
//			JustificantEstat justificantEstat,
//			boolean custodiat,
//			String custodiaId,
//			String custodiaUrl,
//			String justificantError,
//			String arxiuExpedientUuid,
//			String arxiuDocumentUuid) {
//		this.justificantEstat = justificantEstat;
//		this.custodiat = custodiat;
//		this.custodiaId = custodiaId;
//		this.custodiaUrl = custodiaUrl;
//		if (justificantError != null && justificantError.length() > ERROR_JUSTIFICANT_MAX_LENGTH) {
//			String tokenFinal = " [...]";
//			this.justificantError = justificantError.substring(0, (ERROR_JUSTIFICANT_MAX_LENGTH - tokenFinal.length() - 2)) + tokenFinal;
//		} else {
//			this.justificantError = justificantError;
//		}
//		this.arxiuExpedientUuid = arxiuExpedientUuid;
//		this.arxiuDocumentUuid = arxiuDocumentUuid;
//	}
//	public void updateArxiuExpedientTancat(boolean arxiuExpedientTancat) {
//		this.arxiuExpedientTancat = arxiuExpedientTancat;
//	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((scspPeticionId == null) ? 0 : scspPeticionId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		HistoricConsulta other = (HistoricConsulta) obj;
		if (scspPeticionId == null) {
			if (other.scspPeticionId != null)
				return false;
		} else if (!scspPeticionId.equals(other.scspPeticionId))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

}
