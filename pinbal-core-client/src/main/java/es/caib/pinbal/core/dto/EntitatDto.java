package es.caib.pinbal.core.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.builder.ToStringBuilder;


/**
 * Objecte DTO amb informació d'una entitat.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class EntitatDto extends AbstractIdentificable<Long> implements Serializable {

	public enum EntitatTipusDto {
		ALTRES,
		GOVERN,
		CONSELL,
		AJUNTAMENT
	}

	private Long id;

	private String codi;
	private String nom;
	private String cif;
	private EntitatTipusDto tipus;
	private boolean activa;

	private List<EntitatUsuariDto> usuaris = new ArrayList<EntitatUsuariDto>();
	private List<String> serveis = new ArrayList<String>();

	public EntitatDto() {
	}

	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getCodi() {
		return codi;
	}
	public void setCodi(String codi) {
		this.codi = codi;
	}
	public String getNom() {
		return nom;
	}
	public void setNom(String nom) {
		this.nom = nom;
	}
	public String getCif() {
		return cif;
	}
	public void setCif(String cif) {
		this.cif = cif;
	}
	public EntitatTipusDto getTipus() {
		return tipus;
	}
	public void setTipus(EntitatTipusDto tipus) {
		this.tipus = tipus;
	}
	public boolean isActiva() {
		return activa;
	}
	public void setActiva(boolean activa) {
		this.activa = activa;
	}
	public List<String> getServeis() {
		return serveis;
	}
	public void setServeis(List<String> serveis) {
		this.serveis = serveis;
	}
	public List<EntitatUsuariDto> getUsuaris() {
		return usuaris;
	}
	public void setUsuaris(List<EntitatUsuariDto> usuaris) {
		this.usuaris = usuaris;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	public List<EntitatUsuariDto> getUsuarisAdmin() {
		List<EntitatUsuariDto> resposta = new ArrayList<EntitatUsuariDto>();
		for (EntitatUsuariDto dto: usuaris) {
			if (dto.isRepresentant() || dto.isDelegat() || dto.isAuditor() || dto.isAplicacio())
				resposta.add(dto);
		}
		return resposta;
	}
	public List<EntitatUsuariDto> getUsuarisRepresentant() {
		List<EntitatUsuariDto> resposta = new ArrayList<EntitatUsuariDto>();
		for (EntitatUsuariDto dto: usuaris) {
			if (dto.isRepresentant() || dto.isDelegat() || dto.isAplicacio())
				resposta.add(dto);
		}
		return resposta;
	}
	public List<EntitatUsuariDto> getUsuarisDelegat() {
		List<EntitatUsuariDto> resposta = new ArrayList<EntitatUsuariDto>();
		for (EntitatUsuariDto dto: usuaris) {
			if (dto.isDelegat())
				resposta.add(dto);
		}
		return resposta;
	}
	public List<EntitatUsuariDto> getUsuarisAuditor() {
		List<EntitatUsuariDto> resposta = new ArrayList<EntitatUsuariDto>();
		for (EntitatUsuariDto dto: usuaris) {
			if (dto.isAuditor())
				resposta.add(dto);
		}
		return resposta;
	}


	private static final long serialVersionUID = -2822106398117415005L;

}
