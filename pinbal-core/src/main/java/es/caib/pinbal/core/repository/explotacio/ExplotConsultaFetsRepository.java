package es.caib.pinbal.core.repository.explotacio;

import es.caib.pinbal.core.model.explotacio.ExplotConsultaFetsEntity;
import es.caib.pinbal.core.model.explotacio.ExplotTempsEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExplotConsultaFetsRepository extends JpaRepository<ExplotConsultaFetsEntity, Long> {

    public void deleteAllByTemps(ExplotTempsEntity ete);
}
