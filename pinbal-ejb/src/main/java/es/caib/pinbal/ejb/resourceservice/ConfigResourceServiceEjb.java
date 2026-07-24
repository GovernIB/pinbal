package es.caib.pinbal.ejb.resourceservice;

import es.caib.pinbal.ejb.config.AbstractServiceEjb;
import es.caib.pinbal.logic.intf.resourceservice.ConfigResourceService;
import lombok.experimental.Delegate;

import javax.ejb.Stateless;

/**
 * EJB que implementa ConfigResourceService. Accedeix a la funcionalitat del service Spring amb una clase delegada.
 *
 * @author Límit Tecnologies
 */
@Stateless
public class ConfigResourceServiceEjb extends AbstractServiceEjb<ConfigResourceService> implements ConfigResourceService {

	@Delegate
	private ConfigResourceService delegateService = null;

	@Override
	protected void setDelegateService(ConfigResourceService delegateService) {
		this.delegateService = delegateService;
	}

}
