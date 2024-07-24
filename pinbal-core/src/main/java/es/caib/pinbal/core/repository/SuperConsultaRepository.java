/**
 * 
 */
package es.caib.pinbal.core.repository;

import es.caib.pinbal.core.model.SuperConsulta;
import es.caib.pinbal.core.model.explotacio.ExplotConsultaDimensio;
import es.caib.pinbal.core.model.explotacio.ExplotConsultaFets;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

/**
 * Especifica els mètodes que s'han d'emprar per obtenir i modificar la
 * informació relativa a una consulta SCSP que està emmagatzemada a dins 
 * la base de dades.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface SuperConsultaRepository extends JpaRepository<SuperConsulta, Long> {

	@Query(	"select new es.caib.pinbal.core.model.explotacio.ExplotConsultaFets( " +
			"    c.entitat.id, " +
			"    c.procediment.id, " +
			"    c.serveiCodi, " +
			"    c.createdBy.codi, " +
			"    sum(case when c.recobriment = true and c.estat = es.caib.pinbal.core.dto.EstatTipus.Tramitada then 1 else 0 end), " +
			"    sum(case when c.recobriment = true and c.estat = es.caib.pinbal.core.dto.EstatTipus.Error then 1 else 0 end), " +
			"    sum(case when c.recobriment = true and c.estat <> es.caib.pinbal.core.dto.EstatTipus.Tramitada and c.estat <> es.caib.pinbal.core.dto.EstatTipus.Error then 1 else 0 end), " +
			"    sum(case when c.recobriment = false and c.estat = es.caib.pinbal.core.dto.EstatTipus.Tramitada then 1 else 0 end), " +
			"    sum(case when c.recobriment = false and c.estat = es.caib.pinbal.core.dto.EstatTipus.Error then 1 else 0 end), " +
			"    sum(case when c.recobriment = false and c.estat <> es.caib.pinbal.core.dto.EstatTipus.Tramitada and c.estat <> es.caib.pinbal.core.dto.EstatTipus.Error then 1 else 0 end)) " +
			"from " +
			"    SuperConsulta c " +
			"where (:esNullData = true or c.createdDate < :data) " +
			"  and c.procediment.id is not null " +
			"  and c.serveiCodi is not null " +
			"group by " +
			"    c.entitat.id, " +
			"    c.procediment.id, " +
			"    c.serveiCodi, " +
			"    c.createdBy.codi " +
			"order by " +
			"    c.entitat.id, " +
			"    c.procediment.id, " +
			"    c.serveiCodi," +
			"	 c.createdBy.codi")
	public List<ExplotConsultaFets> getConsultesPerEstadistiques(
			@Param("esNullData") boolean esNullData,
			@Param("data") Date data);

	@Query(	"select new es.caib.pinbal.core.model.explotacio.ExplotConsultaDimensio(c.entitat.id, c.procediment.id, c.serveiCodi, c.createdBy.codi) " +
			"from  SuperConsulta c " +
			"where c.procediment.id is not null " +
			"  and c.serveiCodi is not null " +
			"group by " +
			"    c.entitat.id, " +
			"    c.procediment.id, " +
			"    c.serveiCodi, " +
			"    c.createdBy.codi " +
			"order by " +
			"	 c.entitat.id, " +
			"	 c.procediment.id, " +
			"	 c.serveiCodi, " +
			"	 c.createdBy.codi")
	public List<ExplotConsultaDimensio> getDimensionsPerEstadistiques();
}
