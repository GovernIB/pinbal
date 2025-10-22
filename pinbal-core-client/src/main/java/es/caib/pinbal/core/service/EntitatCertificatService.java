package es.caib.pinbal.core.service;

import es.caib.pinbal.core.dto.EntitatCertificatDto;
import es.caib.pinbal.core.service.exception.EntitatNotFoundException;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

public interface EntitatCertificatService {

    /**
     * Registra un certificat per una entitat
     * Internament crea/actualitza un OrganismoCesionario a SCSP
     */
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public EntitatCertificatDto create(
            String entitatCodi,
            String certificatAlias) throws EntitatNotFoundException;

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public EntitatCertificatDto update(Long id);

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public void delete(Long id);

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public List<EntitatCertificatDto> findByFiltrePaginat(
            String entitatCodi,
            String alias);

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public EntitatCertificatDto findById(Long id);

    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_DELEG')")
    public EntitatCertificatDto findByEntitat(String entitatCodi);

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public List<EntitatCertificatDto> findByEntitatId(Long entitatId);
}
