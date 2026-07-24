package es.caib.pinbal.ejb.resourceservice;

import es.caib.pinbal.ejb.config.AbstractServiceEjb;
import es.caib.pinbal.logic.intf.resourceservice.ClauPublicaResourceService;
import lombok.experimental.Delegate;

import javax.ejb.Stateless;

/**
 * EJB que implementa ClauPublicaResourceService. Accedeix a la funcionalitat del service Spring amb una clase delegada.
 *
 * @author Límit Tecnologies
 */
@Stateless
public class ClauPublicaResourceServiceEjb extends AbstractServiceEjb<ClauPublicaResourceService> implements ClauPublicaResourceService {

	@Delegate
	private ClauPublicaResourceService delegateService = null;

	@Override
	protected void setDelegateService(ClauPublicaResourceService delegateService) {
		this.delegateService = delegateService;
	}

}
