package es.caib.pinbal.core.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Objecte DTO amb informació d'una entitat.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Getter @Setter @ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
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
	private String unitatArrel;
	private EntitatTipusDto tipus;
	private boolean activa;

	private List<EntitatUsuariDto> usuaris = new ArrayList<EntitatUsuariDto>();
	private List<String> serveis = new ArrayList<String>();

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
