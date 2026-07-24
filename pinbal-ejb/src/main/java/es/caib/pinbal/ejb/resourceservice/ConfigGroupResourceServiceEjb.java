package es.caib.pinbal.ejb.resourceservice;

import es.caib.pinbal.ejb.config.AbstractServiceEjb;
import es.caib.pinbal.logic.intf.resourceservice.ConfigGroupResourceService;
import lombok.experimental.Delegate;

import javax.ejb.Stateless;

/**
 * EJB que implementa ConfigGroupResourceService. Accedeix a la funcionalitat del service Spring amb una clase delegada.
 *
 * @author Límit Tecnologies
 */
@Stateless
public class ConfigGroupResourceServiceEjb extends AbstractServiceEjb<ConfigGroupResourceService> implements ConfigGroupResourceService {

	@Delegate
	private ConfigGroupResourceService delegateService = null;

	@Override
	protected void setDelegateService(ConfigGroupResourceService delegateService) {
		this.delegateService = delegateService;
	}

}
