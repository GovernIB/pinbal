package es.caib.pinbal.core.ejb;


import es.caib.pinbal.core.dto.EntitatCertificatDto;
import es.caib.pinbal.core.service.EntitatCertificatService;
import es.caib.pinbal.core.service.exception.EntitatNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ejb.interceptor.SpringBeanAutowiringInterceptor;

import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
import java.util.List;

@Stateless
@Interceptors(SpringBeanAutowiringInterceptor.class)
public class EntitatCertificatServiceBean implements EntitatCertificatService {

    @Autowired
    EntitatCertificatService delegate;

    @Override
    public EntitatCertificatDto create(String entitatCodi, String certificatAlias) throws EntitatNotFoundException {
        return delegate.create(entitatCodi, certificatAlias);
    }

    @Override
    public EntitatCertificatDto update(Long id) {
        return delegate.update(id);
    }

    @Override
    public void delete(Long id) {
        delegate.delete(id);
    }

    @Override
    public List<EntitatCertificatDto> findByFiltrePaginat(String entitatCodi, String alias) {
        return delegate.findByFiltrePaginat(entitatCodi, alias);
    }

    @Override
    public EntitatCertificatDto findById(Long id) {
        return delegate.findById(id);
    }

    @Override
    public EntitatCertificatDto findByEntitat(String entitatCodi) {
        return delegate.findByEntitat(entitatCodi);
    }

    @Override
    public List<EntitatCertificatDto> findByEntitatId(Long entitatId) {
        return delegate.findByEntitatId(entitatId);
    }

}
