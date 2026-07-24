package es.caib.pinbal.ejb.resourceservice;

import es.caib.pinbal.ejb.config.AbstractServiceEjb;
import es.caib.pinbal.logic.intf.resourceservice.ServeiBusResourceService;
import lombok.experimental.Delegate;

import javax.ejb.Stateless;

/**
 * EJB que implementa ServeiBusResourceService. Accedeix a la funcionalitat del service Spring amb una clase delegada.
 *
 * @author Límit Tecnologies
 */
@Stateless
public class ServeiBusResourceServiceEjb extends AbstractServiceEjb<ServeiBusResourceService> implements ServeiBusResourceService {

	@Delegate
	private ServeiBusResourceService delegateService = null;

	@Override
	protected void setDelegateService(ServeiBusResourceService delegateService) {
		this.delegateService = delegateService;
	}

}
