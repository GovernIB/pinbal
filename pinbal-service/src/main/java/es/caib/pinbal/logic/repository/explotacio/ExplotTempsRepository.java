package es.caib.pinbal.logic.repository.explotacio;

import es.caib.pinbal.logic.model.explotacio.ExplotTempsEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;

public interface ExplotTempsRepository extends JpaRepository<ExplotTempsEntity, Long> {

    ExplotTempsEntity findFirstByData(Date data);

}
