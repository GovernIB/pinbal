package es.caib.pinbal.ejb.resourceservice;

import es.caib.pinbal.ejb.config.AbstractServiceEjb;
import es.caib.pinbal.logic.intf.resourceservice.HistoricConsultaResourceService;
import lombok.experimental.Delegate;

import javax.ejb.Stateless;

/**
 * EJB que implementa HistoricConsultaResourceService. Accedeix a la funcionalitat del service Spring amb una clase delegada.
 *
 * @author Límit Tecnologies
 */
@Stateless
public class HistoricConsultaResourceServiceEjb extends AbstractServiceEjb<HistoricConsultaResourceService> implements HistoricConsultaResourceService {

    @Delegate
    private HistoricConsultaResourceService delegateService = null;

    @Override
    protected void setDelegateService(HistoricConsultaResourceService delegateService) {
        this.delegateService = delegateService;
    }

}
