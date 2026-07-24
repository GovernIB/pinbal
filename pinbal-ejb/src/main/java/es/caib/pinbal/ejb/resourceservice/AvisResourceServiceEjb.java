package es.caib.pinbal.ejb.resourceservice;

import es.caib.pinbal.ejb.config.AbstractServiceEjb;
import es.caib.pinbal.logic.intf.resourceservice.AvisResourceService;
import lombok.experimental.Delegate;

import javax.ejb.Stateless;

/**
 * EJB que implementa AvisResourceService. Accedeix a la funcionalitat del service Spring amb una clase delegada.
 *
 * @author Límit Tecnologies
 */
@Stateless
public class AvisResourceServiceEjb extends AbstractServiceEjb<AvisResourceService> implements AvisResourceService {

	@Delegate
	private AvisResourceService delegateService = null;

	@Override
	protected void setDelegateService(AvisResourceService delegateService) {
		this.delegateService = delegateService;
	}

}
