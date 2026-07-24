package es.caib.pinbal.ejb.resourceservice;

import es.caib.pinbal.ejb.config.AbstractServiceEjb;
import es.caib.pinbal.logic.intf.resourceservice.EntitatServeiResourceService;
import lombok.experimental.Delegate;

import javax.ejb.Stateless;

/**
 * EJB que implementa EntitatServeiResourceService. Accedeix a la funcionalitat del service Spring amb una clase delegada.
 *
 * @author Límit Tecnologies
 */
@Stateless
public class EntitatServeiResourceServiceEjb extends AbstractServiceEjb<EntitatServeiResourceService> implements EntitatServeiResourceService {

	@Delegate
	private EntitatServeiResourceService delegateService = null;

	@Override
	protected void setDelegateService(EntitatServeiResourceService delegateService) {
		this.delegateService = delegateService;
	}

}
