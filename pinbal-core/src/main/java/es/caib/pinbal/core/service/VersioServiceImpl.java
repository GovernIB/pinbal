/**
 * 
 */
package es.caib.pinbal.core.service;

import java.io.IOException;
import java.util.Properties;

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

	private Properties versionProperties;

	@Override
	public String getVersioActual() throws IOException {
		LOGGER.debug("Obtenint versió actual de l'aplicació");
		return getProperties().getProperty("app.version"); // 1.4.13
	}

	private Properties getProperties() throws IOException {
		if (versionProperties == null) {
			versionProperties = new Properties();
			versionProperties.load(
					getClass().getResourceAsStream(
							"/es/caib/pinbal/core/version/version.properties"));
		}
		return versionProperties;
	}

	private static final Logger LOGGER = LoggerFactory.getLogger(VersioServiceImpl.class);

}
