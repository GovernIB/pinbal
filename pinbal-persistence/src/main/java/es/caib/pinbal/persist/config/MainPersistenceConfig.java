package es.caib.pinbal.persist.config;

import es.caib.pinbal.logic.intf.base.config.BaseConfig;
import es.caib.pinbal.persist.base.config.BasePersistenceConfig;
import es.caib.pinbal.persist.base.repository.BaseRepositoryImpl;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Configuració dels components de persistència.
 * 
 * @author Límit Tecnologies
 */
@Configuration
@EnableJpaRepositories(
		basePackages = {
				BaseConfig.BASE_PACKAGE + ".persist.repository",
				BaseConfig.BASE_PACKAGE + ".persist.resourcerepository",
		},
		entityManagerFactoryRef = "mainEntityManager",
		transactionManagerRef = "mainTransactionManager",
		repositoryBaseClass = BaseRepositoryImpl.class
)
public class MainPersistenceConfig extends BasePersistenceConfig {

	protected String[] getEntityPackages() {
		return new String[] {
				BaseConfig.BASE_PACKAGE + ".persist.entity",
				BaseConfig.BASE_PACKAGE + ".persist.resourceentity",
		};
	}

}
