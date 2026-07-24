package es.caib.pinbal.ejb.resourceservice;

import es.caib.pinbal.ejb.config.AbstractServiceEjb;
import es.caib.pinbal.logic.intf.resourceservice.EmissorCertResourceService;
import lombok.experimental.Delegate;

import javax.ejb.Stateless;

/**
 * EJB que implementa EmissorCertResourceService. Accedeix a la funcionalitat del service Spring amb una clase delegada.
 *
 * @author Límit Tecnologies
 */
@Stateless
public class EmissorCertResourceServiceEjb extends AbstractServiceEjb<EmissorCertResourceService> implements EmissorCertResourceService {

	@Delegate
	private EmissorCertResourceService delegateService = null;

	@Override
	protected void setDelegateService(EmissorCertResourceService delegateService) {
		this.delegateService = delegateService;
	}

}
