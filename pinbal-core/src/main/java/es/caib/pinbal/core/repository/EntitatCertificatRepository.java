package es.caib.pinbal.core.repository;

import es.caib.pinbal.core.model.Entitat;
import es.caib.pinbal.core.model.EntitatCertificat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EntitatCertificatRepository extends JpaRepository<EntitatCertificat, Long> {

    List<EntitatCertificat> findByActiuTrue();

    List<EntitatCertificat> findByEntitatOrderByDataAltaDesc(Entitat entitat);

    List<EntitatCertificat> findByEntitatIdOrderByDataAltaDesc(Long entitatId);
}
