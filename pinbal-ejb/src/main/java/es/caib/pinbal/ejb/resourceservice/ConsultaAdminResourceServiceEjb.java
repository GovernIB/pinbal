package es.caib.pinbal.ejb.resourceservice;

import es.caib.pinbal.ejb.config.AbstractServiceEjb;
import es.caib.pinbal.logic.intf.resourceservice.ConsultaAdminResourceService;
import lombok.experimental.Delegate;

import javax.ejb.Stateless;

/**
 * EJB que implementa ConsultaAdminResourceService. Accedeix a la funcionalitat del service Spring amb una clase delegada.
 *
 * @author Límit Tecnologies
 */
@Stateless
public class ConsultaAdminResourceServiceEjb extends AbstractServiceEjb<ConsultaAdminResourceService> implements ConsultaAdminResourceService {

    @Delegate
    private ConsultaAdminResourceService delegateService = null;

    @Override
    protected void setDelegateService(ConsultaAdminResourceService delegateService) {
        this.delegateService = delegateService;
    }

}
