/**
 * 
 */
package es.caib.pinbal.core.service;

import java.io.IOException;

import org.apache.commons.io.IOUtils;
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

	private String version;

	@Override
	public String getVersioActual() {
		LOGGER.debug("Obtenint versió actual de l'aplicació");
		if (version == null) {
			try {
				version = IOUtils.toString(
						getClass().getResourceAsStream("versio"),
						"UTF-8");
			} catch (IOException e) {
				version = "???";
			}
		}
		return version;
	}

	private static final Logger LOGGER = LoggerFactory.getLogger(VersioServiceImpl.class);

}
