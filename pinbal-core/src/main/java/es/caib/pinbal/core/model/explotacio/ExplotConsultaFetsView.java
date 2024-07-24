package es.caib.pinbal.core.model.explotacio;

import lombok.Data;
import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.Subselect;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Data
@Entity
@Subselect("select rownum as id, " +
        "       entitat_id, " +
        "       procediment_id, " +
        "       servei_codi, " +
        "       createdby_codi, " +
        "       rebocrimentOkSum, " +
        "       rebocrimentErrorSum, " +
        "       rebocrimentPendSum, " +
        "       webOkSum, " +
        "       webErrorSum, " +
        "       webPendSum " +
        "   from (" +
        "       select entitat_id, " +
        "           procediment_id, " +
        "           servei_codi, " +
        "           createdby_codi, " +
        "           sum(rebocrimentOk) rebocrimentOkSum, " +
        "           sum(rebocrimentError) rebocrimentErrorSum, " +
        "           sum(rebocrimentPend) rebocrimentPendSum, " +
        "           sum(webOk) webOkSum, " +
        "           sum(webError) webErrorSum, " +
        "           sum(webPend) webPendSum " +
        "       from (" +
        "          select " +
        "              c.entitat_id, " +
        "              c.procediment_id, " +
        "              c.servei_codi, " +
        "              c.createdby_codi, " +
        "              sum(case when c.recobriment = 1 and c.estat = 2 then 1 else 0 end) rebocrimentOk, " +
        "              sum(case when c.recobriment = 1 and c.estat = 3 then 1 else 0 end) rebocrimentError, " +
        "              sum(case when c.recobriment = 1 and c.estat <> 2 and c.estat <> 3 then 1 else 0 end) rebocrimentPend, " +
        "              sum(case when c.recobriment = 0 and c.estat = 2 then 1 else 0 end) webOk, " +
        "              sum(case when c.recobriment = 0 and c.estat = 3 then 1 else 0 end) webError, " +
        "              sum(case when c.recobriment = 0 and c.estat <> 2 and c.estat <> 3 then 1 else 0 end) webPend " +
        "          from " +
        "              pbl_consulta c " +
        "          group by " +
        "              c.entitat_id, " +
        "              c.procediment_id, " +
        "              c.servei_codi, " +
        "              c.createdby_codi " +
        "       union " +
        "          select "+
        "              h.entitat_id, " +
        "              h.procediment_id, " +
        "              h.servei_codi, " +
        "              h.createdby_codi, " +
        "              sum(case when h.recobriment = 1 and h.estat = 2 then 1 else 0 end) rebocrimentOk, " +
        "              sum(case when h.recobriment = 1 and h.estat = 3 then 1 else 0 end) rebocrimentError, " +
        "              sum(case when h.recobriment = 1 and h.estat <> 2 and h.estat <> 3 then 1 else 0 end) rebocrimentPend, " +
        "              sum(case when h.recobriment = 0 and h.estat = 2 then 1 else 0 end) webOk, " +
        "              sum(case when h.recobriment = 0 and h.estat = 3 then 1 else 0 end) webError, " +
        "              sum(case when h.recobriment = 0 and h.estat <> 2 and h.estat <> 3 then 1 else 0 end) webPend " +
        "          from " +
        "              pbl_consulta_hist h " +
        "          group by " +
        "              h.entitat_id, " +
        "              h.procediment_id, " +
        "              h.servei_codi, " +
        "              h.createdby_codi " +
        "       )" +
        "       group by " +
        "           entitat_id, " +
        "           procediment_id, " +
        "           servei_codi, " +
        "           createdby_codi " +
        "       order by " +
        "           entitat_id, " +
        "           procediment_id, " +
        "           servei_codi, " +
        "           createdby_codi) ")
@Immutable
public class ExplotConsultaFetsView {

    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "entitat_id")
    private Long entitatId;

    @Column(name = "procediment_id")
    private Long procedimentId;

    @Column(name = "servei_codi")
    private String serveiCodi;

    @Column(name = "createdby_codi")
    private String usuariCodi;

    @Column(name = "rebocrimentOkSum")
    private Long recobrimentOk;

    @Column(name = "rebocrimentErrorSum")
    private Long recobrimentError;

    @Column(name = "rebocrimentPendSum")
    private Long recobrimentPend;

    @Column(name = "webOkSum")
    private Long webOk;

    @Column(name = "webErrorSum")
    private Long webError;

    @Column(name = "webPendSum")
    private Long webPend;
}
