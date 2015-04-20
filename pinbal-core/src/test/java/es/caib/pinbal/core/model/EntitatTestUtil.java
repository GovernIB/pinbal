/**
 * 
 */
package es.caib.pinbal.core.model;

import es.caib.pinbal.core.dto.EntitatDto;
import es.caib.pinbal.core.model.Entitat.EntitatTipus;

/**
 * Classe d'utilitat que conté mètodes d'ajuda per a l'execució de proves
 * unitàries de les funcions relacionades amb entitats.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class EntitatTestUtil {

	public static EntitatDto createDto(Long id, String nom) {
		EntitatDto dto = new EntitatDto();
		dto.setId(id);
		dto.setNom(nom);
		return dto;
	}

	public static Entitat createModelObject(
			Long id,
			String codi,
			String nom,
			String cif,
			String responsable) {
		Entitat model = Entitat.getBuilder(
				codi,
				nom,
				cif,
				EntitatTipus.ALTRES).build();
		model.configurarIdPerTest(id);
		return model;
	}

}
