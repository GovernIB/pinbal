package es.caib.pinbal.core.repository.explotacio;

import es.caib.pinbal.core.model.explotacio.ExplotConsultaFets;
import es.caib.pinbal.core.model.explotacio.ExplotConsultaFetsEntity;
import es.caib.pinbal.core.model.explotacio.ExplotTempsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ExplotConsultaFetsRepository extends JpaRepository<ExplotConsultaFetsEntity, Long> {

    public void deleteAllByTemps(ExplotTempsEntity ete);

    @Query("select new es.caib.pinbal.core.model.explotacio.ExplotConsultaFets(" +
            "    d.entitatId, " +
            "    d.procedimentId, " +
            "    d.serveiCodi, " +
            "    '', " +
            "    sum(f.numRecobrimentOk), " +
            "    sum(f.numRecobrimentError), " +
            "    sum(f.numRecobrimentPendent), " +
            "    sum(f.numRecobrimentProcessant), " +
            "    sum(f.numRecobrimentMassiuOk), " +
            "    sum(f.numRecobrimentMassiuError), " +
            "    sum(f.numRecobrimentMassiuPendent), " +
            "    sum(f.numRecobrimentMassiuProcessant), " +
            "    sum(f.numWebOk), " +
            "    sum(f.numWebError), " +
            "    sum(f.numWebPendent), " +
            "    sum(f.numWebProcessant), " +
            "    sum(f.numWebMassiuOk), " +
            "    sum(f.numWebMassiuError), " +
            "    sum(f.numWebMassiuPendent), " +
            "    sum(f.numWebMassiuProcessant)) " +
            "from ExplotConsultaFetsEntity f " +
            "   left outer join f.consultaDimensio d " +
            "where f.temps = :temps " +
            "   and (:esNullEntitatId = true or d.entitatId = :entitatId) " +
            "   and (:esNullProcedimentId = true or d.procedimentId = :procedimentId) " +
            "   and (:esNullServeiCodi = true or d.serveiCodi = :serveiCodi) " +
            "   and (:esNullUsuari = true or d.usuariCodi = :usuari) " +
            "group by d.entitatId, d.procedimentId, d.serveiCodi")
    public List<ExplotConsultaFets> findByFiltre(
            @Param("temps")ExplotTempsEntity temps,
            @Param("esNullEntitatId") boolean esNullEntitatId,
            @Param("entitatId") Long entitatId,
            @Param("esNullProcedimentId") boolean esNullProcedimentId,
            @Param("procedimentId") Long procedimentId,
            @Param("esNullServeiCodi") boolean esNullServeiCodi,
            @Param("serveiCodi") String serveiCodi,
            @Param("esNullUsuari") boolean esNullUsuari,
            @Param("usuari") String usuari);
}
