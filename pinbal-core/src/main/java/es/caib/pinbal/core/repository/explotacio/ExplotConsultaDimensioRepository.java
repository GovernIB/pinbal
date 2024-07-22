package es.caib.pinbal.core.repository.explotacio;

import es.caib.pinbal.core.model.explotacio.ExplotConsultaDimensioEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExplotConsultaDimensioRepository extends JpaRepository<ExplotConsultaDimensioEntity, Long> {

    ExplotConsultaDimensioEntity findByProcedimentIdAndServeiCodiAndUsuariCodi(Long procedimentId, String serveiCodi, String usuariCodi);
}
