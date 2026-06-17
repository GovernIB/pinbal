package es.caib.pinbal.back.config;

import es.caib.pinbal.back.base.config.BaseMessageSourceConfig;
import es.caib.pinbal.logic.intf.base.config.BaseConfig;
import org.springframework.context.annotation.Configuration;

import java.util.Locale;

/**
 * Configuración del MessageSource de la aplicación.
 *
 * @author Límit Tecnologies
 */
@Configuration
public class MessageSourceConfig extends BaseMessageSourceConfig {

	@Override
	protected String[] getBasenames() {
		return new String[] {
				"messages",
				"pinbal-back-messages"
		};
	}

	@Override
	protected Locale getDefaultLocale() {
		return Locale.forLanguageTag(BaseConfig.DEFAULT_LOCALE);
	}

}
