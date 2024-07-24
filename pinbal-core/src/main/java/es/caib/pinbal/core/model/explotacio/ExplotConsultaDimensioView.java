package es.caib.pinbal.core.model.explotacio;

import lombok.Data;
import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.Subselect;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;


@Data
@Entity
@Subselect("select rownum as id, entitat_id, procediment_id, servei_codi, createdby_codi " +
        "from (" +
        "    select distinct entitat_id, procediment_id, servei_codi, createdby_codi " +
        "    from ( select distinct c.entitat_id, c.procediment_id, c.servei_codi, c.createdby_codi from pbl_consulta c " +
        "               union " +
        "           select distinct h.entitat_id, h.procediment_id, h.servei_codi, h.createdby_codi from pbl_consulta_hist h ) " +
        "order by entitat_id, procediment_id, servei_codi, createdby_codi ")
@Immutable
public class ExplotConsultaDimensioView {

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

}
