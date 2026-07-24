package es.caib.pinbal.ejb.resourceservice;

import es.caib.pinbal.ejb.config.AbstractServiceEjb;
import es.caib.pinbal.logic.intf.resourceservice.EntitatUsuariResourceService;
import lombok.experimental.Delegate;

import javax.ejb.Stateless;

/**
 * EJB que implementa EntitatUsuariResourceService. Accedeix a la funcionalitat del service Spring amb una clase delegada.
 *
 * @author Límit Tecnologies
 */
@Stateless
public class EntitatUsuariResourceServiceEjb extends AbstractServiceEjb<EntitatUsuariResourceService> implements EntitatUsuariResourceService {

	@Delegate
	private EntitatUsuariResourceService delegateService = null;

	@Override
	protected void setDelegateService(EntitatUsuariResourceService delegateService) {
		this.delegateService = delegateService;
	}

}
