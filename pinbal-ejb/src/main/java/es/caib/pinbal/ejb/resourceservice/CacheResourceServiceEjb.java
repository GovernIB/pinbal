package es.caib.pinbal.ejb.resourceservice;

import es.caib.pinbal.ejb.config.AbstractServiceEjb;
import es.caib.pinbal.logic.intf.resourceservice.CacheResourceService;
import lombok.experimental.Delegate;

import javax.ejb.Stateless;

/**
 * EJB que implementa CacheResourceService. Accedeix a la funcionalitat del service Spring amb una clase delegada.
 *
 * @author Límit Tecnologies
 */
@Stateless
public class CacheResourceServiceEjb extends AbstractServiceEjb<CacheResourceService> implements CacheResourceService {

	@Delegate
	private CacheResourceService delegateService = null;

	@Override
	protected void setDelegateService(CacheResourceService delegateService) {
		this.delegateService = delegateService;
	}

}
