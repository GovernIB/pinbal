package es.caib.pinbal.core.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.apache.commons.lang.StringUtils;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import es.caib.pinbal.core.audit.PinbalAuditable;
import es.caib.pinbal.core.dto.IntegracioAccioEstatEnumDto;
import es.caib.pinbal.core.dto.IntegracioAccioTipusEnumDto;
import lombok.Getter;
import lombok.Setter;

/**
 * Classe de model de dades que conté la informació dels organs gestors.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Entity
@Table(name = "pbl_mon_int")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
public class IntegracioAccioEntity extends PinbalAuditable<Long> {

	@Column(name = "codi", length = 64, nullable = false, unique = true)
	private String codi;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "data", nullable = false)
	private Date data;
	
	@Column(name = "descripcio", length = 1024)
	private String descripcio;
	
	@Column(name = "tipus", nullable = false)
	@Enumerated(EnumType.STRING)
	protected IntegracioAccioTipusEnumDto tipus;
	
	@Column(name = "temps_resposta")
	private Long tempsResposta;
	
	@Column(name = "estat")
	@Enumerated(EnumType.STRING)
	private IntegracioAccioEstatEnumDto estat = IntegracioAccioEstatEnumDto.OK;
	
	@Column(name = "codi_usuari", length = 64, nullable = false)
	private String codiUsuari;
	
	@Column(name = "id_peticio", length = 64)
	private String idPeticio;

	@Column(name = "codi_entitat", length = 64)
	private String codiEntitat;

	@Column(name = "error_descripcio", length = 1024)
	private String errorDescripcio;
	
	@Column(name = "excepcio_msg", length = 1024)
	private String excepcioMessage;
	
	@Column(name = "excepcio_stacktrace", length = 2048)
	private String excepcioStacktrace;
	
	@OneToMany(
			mappedBy = "integracioAccio",
			orphanRemoval = true, 
			cascade={CascadeType.ALL})
	private List<IntegracioAccioParamEntity> parametres = new ArrayList<IntegracioAccioParamEntity>();
	
		public static Builder getBuilder(
			String codi,	
			String idPeticio,
			Date data,
			String descripcio,
			IntegracioAccioTipusEnumDto tipus,
			long tempsResposta,
			IntegracioAccioEstatEnumDto estat,
			String errorDescripcio,
			String excepcioMessage,
			String excepcioStacktrace) {
		return new Builder(
				codi,	
				idPeticio,
				data,
				descripcio,
				tipus,
				tempsResposta,
				estat,
				errorDescripcio,
				excepcioMessage,
				excepcioStacktrace);
	}

	/**
	 * Builder per a crear noves instàncies d'aquesta classe.
	 * 
	 * @author Limit tecnologies
	 */
	public static class Builder {
		IntegracioAccioEntity built;
		Builder(
				String codi,
				String idPeticio,
				Date data,
				String descripcio,
				IntegracioAccioTipusEnumDto tipus,
				long tempsResposta,
				IntegracioAccioEstatEnumDto estat,
				String errorDescripcio,
				String excepcioMessage,
				String excepcioStacktrace) {
			built = new IntegracioAccioEntity();
	        built.codi = StringUtils.abbreviate(codi, 64);
	        built.idPeticio = idPeticio;
			built.data = data;
	        built.descripcio = StringUtils.abbreviate(descripcio, 1024);
			built.tipus = tipus;
			built.tempsResposta = tempsResposta;
			built.estat = estat;	    
	        built.errorDescripcio = StringUtils.abbreviate(errorDescripcio, 1024);
	        built.excepcioMessage = StringUtils.abbreviate(excepcioMessage, 1024);
	        built.excepcioStacktrace = StringUtils.abbreviate(excepcioStacktrace, 2048);
		}
		public IntegracioAccioEntity build() {
			return built;
		}
	}
	
	public List<IntegracioAccioParamEntity> getParametres() {
		return parametres;
	}

}
