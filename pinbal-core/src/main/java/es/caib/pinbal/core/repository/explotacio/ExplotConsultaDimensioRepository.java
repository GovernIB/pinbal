package es.caib.pinbal.core.repository.explotacio;

import es.caib.pinbal.core.model.explotacio.ExplotConsultaDimensioEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ExplotConsultaDimensioRepository extends JpaRepository<ExplotConsultaDimensioEntity, Long> {

    @Query("from ExplotConsultaDimensioEntity order by entitatId, procedimentId, serveiCodi, usuariCodi")
    List<ExplotConsultaDimensioEntity> findAllOrdered();
}
