package es.caib.pinbal.ejb.resourceservice;

import es.caib.pinbal.ejb.config.AbstractServiceEjb;
import es.caib.pinbal.logic.intf.resourceservice.ClauPrivadaResourceService;
import lombok.experimental.Delegate;

import javax.ejb.Stateless;

/**
 * EJB que implementa ClauPrivadaResourceService. Accedeix a la funcionalitat del service Spring amb una clase delegada.
 *
 * @author Límit Tecnologies
 */
@Stateless
public class ClauPrivadaResourceServiceEjb extends AbstractServiceEjb<ClauPrivadaResourceService> implements ClauPrivadaResourceService {

	@Delegate
	private ClauPrivadaResourceService delegateService = null;

	@Override
	protected void setDelegateService(ClauPrivadaResourceService delegateService) {
		this.delegateService = delegateService;
	}

}
