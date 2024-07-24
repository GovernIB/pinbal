package es.caib.pinbal.core.repository.explotacio;

import es.caib.pinbal.core.model.explotacio.ExplotConsultaFetsView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

public interface ExplotConsultaFetsViewRepository extends JpaRepository<ExplotConsultaFetsView, Long> {

    @Query(value = "select rownum, " +
            "    entitat_id, " +
            "    procediment_id, " +
            "    servei_codi, " +
            "    createdby_codi, " +
            "    sum(rebocrimentOk) rebocrimentOkSum, " +
            "    sum(rebocrimentError) rebocrimentErrorSum, " +
            "    sum(rebocrimentPend) rebocrimentPendSum, " +
            "    sum(webOk) webOkSum, " +
            "    sum(webError) webErrorSum, " +
            "    sum(webPend) webPendSum, " +
            "from (" +
            "   select " +
            "       c.entitat_id, " +
            "       c.procediment_id, " +
            "       c.servei_codi, " +
            "       c.createdby_codi, " +
            "       sum(case when c.recobriment = 1 and c.estat = 2 then 1 else 0 end) rebocrimentOk, " +
            "       sum(case when c.recobriment = 1 and c.estat = 3 then 1 else 0 end) rebocrimentError, " +
            "       sum(case when c.recobriment = 1 and c.estat <> 2 and c.estat <> 3 then 1 else 0 end) rebocrimentPend, " +
            "       sum(case when c.recobriment = 0 and c.estat = 2 then 1 else 0 end) webOk, " +
            "       sum(case when c.recobriment = 0 and c.estat = 3 then 1 else 0 end) webError, " +
            "       sum(case when c.recobriment = 0 and c.estat <> 2 and c.estat <> 3 then 1 else 0 end) webPend" +
            "   from " +
            "       pbl_consulta c " +
            "  where " +
            "       c.createddate < :data" +
            "   group by " +
            "       c.entitat_id, " +
            "       c.procediment_id, " +
            "       c.servei_codi, " +
            "       c.createdby_codi " +
            "union " +
            "   select "+
            "       h.entitat_id, " +
            "       h.procediment_id, " +
            "       h.servei_codi, " +
            "       h.createdby_codi, " +
            "       sum(case when h.recobriment = 1 and h.estat = 2 then 1 else 0 end) rebocrimentOk, " +
            "       sum(case when h.recobriment = 1 and h.estat = 3 then 1 else 0 end) rebocrimentError, " +
            "       sum(case when h.recobriment = 1 and h.estat <> 2 and h.estat <> 3 then 1 else 0 end) rebocrimentPend, " +
            "       sum(case when h.recobriment = 0 and h.estat = 2 then 1 else 0 end) webOk, " +
            "       sum(case when h.recobriment = 0 and h.estat = 3 then 1 else 0 end) webError, " +
            "       sum(case when h.recobriment = 0 and h.estat <> 2 and h.estat <> 3 then 1 else 0 end) webPend" +
            "   from " +
            "       pbl_consulta_hist h " +
            "  where " +
            "       h.createddate < :data" +
            "   group by " +
            "       h.entitat_id, " +
            "       h.procediment_id, " +
            "       h.servei_codi, " +
            "       h.createdby_codi " +
            ")" +
            "group by " +
            "    entitat_id, " +
            "    procediment_id, " +
            "    servei_codi, " +
            "    createdby_codi " +
            "order by " +
            "    entitat_id, " +
            "    procediment_id, " +
            "    serveiCodi, " +
            "    createdby_codi ", nativeQuery = true)
    List<Object[]> findByDate(@Param("data") Date data);
}
