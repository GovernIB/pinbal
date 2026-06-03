/**
 * 
 */
package es.caib.pinbal.logic.audit;

import es.caib.pinbal.persist.entity.Usuari;
import es.caib.pinbal.persist.repository.UsuariRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

/**
 * Especifica els mètodes que s'han d'emprar per obtenir i modificar la
 * informació relativa a una entitat que està emmagatzemada a dins la base de
 * dades.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@RequiredArgsConstructor
@Slf4j
public class PinbalAuditorAware implements AuditorAware<Usuari> {

	private final UsuariRepository usuariRepository;

	@Override
	public Optional<Usuari> getCurrentAuditor() {

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String auditorActual = (auth != null) ? auth.getName() : null;
		log.debug("Obtenint l'usuari auditor per a l'usuari (codi=" + auditorActual + ")");
		return auditorActual == null ? Optional.empty() : usuariRepository.findById(auditorActual);
	}

}
