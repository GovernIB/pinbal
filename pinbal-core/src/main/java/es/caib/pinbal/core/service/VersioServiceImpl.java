/**
 * 
 */
package es.caib.pinbal.core.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Implementació dels mètodes per a gestionar la versió de l'aplicació.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Service
public class VersioServiceImpl implements VersioService {

	private static final MajorVersion MAJOR_VERSION = MajorVersion.V1_3;
	private static final int MINOR_VERSION = 11;

	private enum MajorVersion {
		V1_0,
		V1_1,
		V1_2,
		V1_3
	}

	@Override
	public String getVersioActual() {
		LOGGER.debug("Obtenint versió actual");
		String majorVersionActual = MAJOR_VERSION.name();
		return majorVersionActual.substring(1).replace("_", ".") + "." + MINOR_VERSION;
	}

	private static final Logger LOGGER = LoggerFactory.getLogger(VersioServiceImpl.class);

}
