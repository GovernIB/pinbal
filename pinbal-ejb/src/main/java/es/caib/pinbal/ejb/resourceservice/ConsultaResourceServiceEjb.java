package es.caib.pinbal.ejb.resourceservice;

import es.caib.pinbal.ejb.config.AbstractServiceEjb;
import es.caib.pinbal.logic.intf.resourceservice.ConsultaResourceService;
import lombok.experimental.Delegate;

import javax.ejb.Stateless;

/**
 * EJB que implementa ConsultaResourceService. Accedeix a la funcionalitat del service Spring amb una clase delegada.
 *
 * @author Límit Tecnologies
 */
@Stateless
public class ConsultaResourceServiceEjb extends AbstractServiceEjb<ConsultaResourceService> implements ConsultaResourceService {

    @Delegate
    private ConsultaResourceService delegateService = null;

    @Override
    protected void setDelegateService(ConsultaResourceService delegateService) {
        this.delegateService = delegateService;
    }

}
