/**
 * 
 */
package es.caib.pinbal.core.audit;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import es.caib.pinbal.core.model.Usuari;
import es.caib.pinbal.core.repository.UsuariRepository;

/**
 * Especifica els mètodes que s'han d'emprar per obtenir i modificar la
 * informació relativa a una entitat que està emmagatzemada a dins la base de
 * dades.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class PinbalAuditorAware implements AuditorAware<Usuari> {

	@Resource
	private UsuariRepository usuariRepository;

	@Override
	public Usuari getCurrentAuditor() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String auditorActual = (auth != null) ? auth.getName() : null;
		LOGGER.debug("Obtenint l'usuari auditor per a l'usuari (codi=" + auditorActual + ")");
		if (auditorActual == null) {
			LOGGER.debug("Auditor actual: null");
			return null;
		} else {
			Usuari usuari = usuariRepository.findOne(auditorActual);
			return usuari;
		}
	}

	private static final Logger LOGGER = LoggerFactory.getLogger(PinbalAuditorAware.class);

}
