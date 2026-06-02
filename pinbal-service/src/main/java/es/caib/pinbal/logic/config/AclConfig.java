package es.caib.pinbal.logic.config;

import es.caib.pinbal.logic.base.config.BaseAclConfig;
import es.caib.pinbal.logic.intf.base.config.BaseConfig;
import org.springframework.context.annotation.Configuration;

/**
 * Configuració de les ACLs de Spring Security.
 * 
 * @author Límit Tecnologies
 */
@Configuration
public class AclConfig extends BaseAclConfig {

	@Override
	public String getDbTablePrefix() {
		return BaseConfig.DB_PREFIX;
	}

	@Override
	protected String getTableSequenceSuffix() {
		return "_seq";
	}

	@Override
	protected boolean isOracleSequenceLegacy() {
		return true;
	}

}
