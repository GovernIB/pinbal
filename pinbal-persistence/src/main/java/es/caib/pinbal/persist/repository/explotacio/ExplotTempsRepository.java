package es.caib.pinbal.persist.repository.explotacio;

import es.caib.pinbal.persist.entity.explotacio.ExplotTempsEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;

public interface ExplotTempsRepository extends JpaRepository<ExplotTempsEntity, Long> {

    ExplotTempsEntity findFirstByData(Date data);

}
