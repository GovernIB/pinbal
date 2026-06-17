package es.caib.pinbal.back.config;

import es.caib.pinbal.back.base.config.BaseHateoasMessageResolverConfig;
import es.caib.pinbal.logic.intf.base.config.BaseConfig;
import org.springframework.context.annotation.Configuration;

import java.util.Locale;

/**
 * Configuració del MessageResolver per a spring-hateoas.
 * 
 * @author Límit Tecnologies
 */
@Configuration
public class HateoasMessageResolverConfig extends BaseHateoasMessageResolverConfig {

	@Override
	protected String getBasename() {
		return "pinbal-back-rest-messages";
	}

    @Override
    protected Locale getDefaultLocale() {
        return Locale.forLanguageTag(BaseConfig.DEFAULT_LOCALE);
    }
}
