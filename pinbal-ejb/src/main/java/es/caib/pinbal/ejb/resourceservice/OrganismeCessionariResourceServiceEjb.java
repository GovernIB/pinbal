package es.caib.pinbal.ejb.resourceservice;

import es.caib.pinbal.ejb.config.AbstractServiceEjb;
import es.caib.pinbal.logic.intf.resourceservice.OrganismeCessionariResourceService;
import lombok.experimental.Delegate;

import javax.ejb.Stateless;

/**
 * EJB que implementa OrganismeCessionariResourceService. Accedeix a la funcionalitat del service Spring amb una clase delegada.
 *
 * @author Límit Tecnologies
 */
@Stateless
public class OrganismeCessionariResourceServiceEjb extends AbstractServiceEjb<OrganismeCessionariResourceService> implements OrganismeCessionariResourceService {

	@Delegate
	private OrganismeCessionariResourceService delegateService = null;

	@Override
	protected void setDelegateService(OrganismeCessionariResourceService delegateService) {
		this.delegateService = delegateService;
	}

}
