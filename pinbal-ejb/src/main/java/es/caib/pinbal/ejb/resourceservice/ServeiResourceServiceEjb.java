package es.caib.pinbal.ejb.resourceservice;

import es.caib.pinbal.ejb.config.AbstractServiceEjb;
import es.caib.pinbal.logic.intf.resourceservice.ServeiResourceService;
import lombok.experimental.Delegate;

import javax.ejb.Stateless;

/**
 * EJB que implementa ServeiResourceService. Accedeix a la funcionalitat del service Spring amb una clase delegada.
 *
 * @author Límit Tecnologies
 */
@Stateless
public class ServeiResourceServiceEjb extends AbstractServiceEjb<ServeiResourceService> implements ServeiResourceService {

	@Delegate
	private ServeiResourceService delegateService = null;

	@Override
	protected void setDelegateService(ServeiResourceService delegateService) {
		this.delegateService = delegateService;
	}

}
