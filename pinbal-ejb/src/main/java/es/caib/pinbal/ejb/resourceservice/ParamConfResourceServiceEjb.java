package es.caib.pinbal.ejb.resourceservice;

import es.caib.pinbal.ejb.config.AbstractServiceEjb;
import es.caib.pinbal.logic.intf.resourceservice.ParamConfResourceService;
import lombok.experimental.Delegate;

import javax.ejb.Stateless;

/**
 * EJB que implementa ParamConfResourceService. Accedeix a la funcionalitat del service Spring amb una clase delegada.
 *
 * @author Límit Tecnologies
 */
@Stateless
public class ParamConfResourceServiceEjb extends AbstractServiceEjb<ParamConfResourceService> implements ParamConfResourceService {

	@Delegate
	private ParamConfResourceService delegateService = null;

	@Override
	protected void setDelegateService(ParamConfResourceService delegateService) {
		this.delegateService = delegateService;
	}

}
