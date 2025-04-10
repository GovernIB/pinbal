package es.caib.pinbal.core.repository.explotacio;

import es.caib.pinbal.core.model.explotacio.ExplotConsultaDimensioEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ExplotConsultaDimensioRepository extends JpaRepository<ExplotConsultaDimensioEntity, Long> {

    @Query("from ExplotConsultaDimensioEntity order by entitatId, procedimentId, serveiCodi, usuariCodi")
    List<ExplotConsultaDimensioEntity> findAllOrdered();




    @Modifying
    @Query(value = "UPDATE PBL_EXPLOT_CONSULTA_DIM SET USUARI_CODI = :codiNou WHERE USUARI_CODI = :codiAntic", nativeQuery = true)
    int updateUsuariCodi(@Param("codiAntic") String codiAntic, @Param("codiNou") String codiNou);
}
