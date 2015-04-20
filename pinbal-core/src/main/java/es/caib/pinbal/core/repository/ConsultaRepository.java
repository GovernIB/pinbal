/**
 * 
 */
package es.caib.pinbal.core.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import es.caib.pinbal.core.model.Consulta;
import es.caib.pinbal.core.model.Consulta.EstatTipus;
import es.caib.pinbal.core.model.Entitat;
import es.caib.pinbal.core.model.Usuari;

/**
 * Especifica els mètodes que s'han d'emprar per obtenir i modificar la
 * informació relativa a una consulta SCSP que està emmagatzemada a dins 
 * la base de dades.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface ConsultaRepository extends JpaRepository<Consulta, Long> {

	@Query(	"select " +
			"    c.procedimentServei.id, " +
			"    sum(case when c.recobriment = true and c.estat = :estatOk then 1 else 0 end), " +
			"    sum(case when c.recobriment = true and c.estat = :estatError then 1 else 0 end), " +
			"    sum(case when c.recobriment = true and c.estat <> :estatOk and c.estat <> :estatError then 1 else 0 end), " +
			"    sum(case when c.recobriment = false and c.estat = :estatOk then 1 else 0 end), " +
			"    sum(case when c.recobriment = false and c.estat = :estatError then 1 else 0 end), " +
			"    sum(case when c.recobriment = false and c.estat <> :estatOk and c.estat <> :estatError then 1 else 0 end) " +
			"from " +
			"    Consulta c " +
			"where " +
			"    (:esNullEntitatId = true or c.procedimentServei.procediment.entitat.id = :entitatId) " +
			"and (:esNullCreatedBy = true or c.createdBy = :createdBy) " +
			"and (:esNullProcedimentId = true or c.procedimentServei.procediment.id = :procedimentId) " +
			"and (:esNullServeiCodi = true or c.procedimentServei.servei = :serveiCodi) " +
			"and (:esNullEstat = true or c.estat = :estat) " +
			"and (:esNullDataInici = true or c.createdDate >= :dataInici) " +
			"and (:esNullDataFi = true or c.createdDate <= :dataFi) " +
			"group by " +
			"    c.procedimentServei.id," +
			"    c.procedimentServei.procediment.nom, " +
			"    c.procedimentServei.servei " +
			"order by " +
			"    c.procedimentServei.procediment.nom, " +
			"    c.procedimentServei.servei")
	public List<Object[]> countByProcedimentServei(
			@Param("esNullEntitatId") boolean esNullEntitatId,
			@Param("entitatId") Long entitatId,
			@Param("esNullCreatedBy") boolean esNullCreatedBy,
			@Param("createdBy") Usuari createdBy,
			@Param("esNullProcedimentId") boolean esNullProcedimentId,
			@Param("procedimentId") Long procedimentId,
			@Param("esNullServeiCodi") boolean esNullServeiCodi,
			@Param("serveiCodi") String serveiCodi,
			@Param("esNullEstat") boolean esNullEstat,
			@Param("estat") EstatTipus estat,
			@Param("esNullDataInici") boolean esNullDataInici,
			@Param("dataInici") Date dataInici,
			@Param("esNullDataFi") boolean esNullDataFi,
			@Param("dataFi") Date dataFi,
			@Param("estatOk") EstatTipus estatOk,
			@Param("estatError") EstatTipus estatError);

	@Query(	"select " +
			"    c.procedimentServei.id, " +
			"    sum(case when c.recobriment = true and c.estat = :estatOk then 1 else 0 end), " +
			"    sum(case when c.recobriment = true and c.estat = :estatError then 1 else 0 end), " +
			"    sum(case when c.recobriment = true and c.estat <> :estatOk and c.estat <> :estatError then 1 else 0 end), " +
			"    sum(case when c.recobriment = false and c.estat = :estatOk then 1 else 0 end), " +
			"    sum(case when c.recobriment = false and c.estat = :estatError then 1 else 0 end), " +
			"    sum(case when c.recobriment = false and c.estat <> :estatOk and c.estat <> :estatError then 1 else 0 end) " +
			"from " +
			"    Consulta c " +
			"where " +
			"    (:esNullEntitatId = true or c.procedimentServei.procediment.entitat.id = :entitatId) " +
			"and (:esNullCreatedBy = true or c.createdBy = :createdBy) " +
			"and (:esNullProcedimentId = true or c.procedimentServei.procediment.id = :procedimentId) " +
			"and (:esNullServeiCodi = true or c.procedimentServei.servei = :serveiCodi) " +
			"and (:esNullEstat = true or c.estat = :estat) " +
			"and (:esNullDataInici = true or c.createdDate >= :dataInici) " +
			"and (:esNullDataFi = true or c.createdDate <= :dataFi) " +
			"group by " +
			"    c.procedimentServei.id," +
			"    c.procedimentServei.servei, " +
			"    c.procedimentServei.procediment.nom " +
			"order by " +
			"    c.procedimentServei.servei, " +
			"    c.procedimentServei.procediment.nom")
	public List<Object[]> countByServeiProcediment(
			@Param("esNullEntitatId") boolean esNullEntitatId,
			@Param("entitatId") Long entitatId,
			@Param("esNullCreatedBy") boolean esNullCreatedBy,
			@Param("createdBy") Usuari createdBy,
			@Param("esNullProcedimentId") boolean esNullProcedimentId,
			@Param("procedimentId") Long procedimentId,
			@Param("esNullServeiCodi") boolean esNullServeiCodi,
			@Param("serveiCodi") String serveiCodi,
			@Param("esNullEstat") boolean esNullEstat,
			@Param("estat") EstatTipus estat,
			@Param("esNullDataInici") boolean esNullDataInici,
			@Param("dataInici") Date dataInici,
			@Param("esNullDataFi") boolean esNullDataFi,
			@Param("dataFi") Date dataFi,
			@Param("estatOk") EstatTipus estatOk,
			@Param("estatError") EstatTipus estatError);

	@Query(	"select " +
			"    c.procedimentServei.procediment.entitat.id, " +
			"    count(*) " +
			"from " +
			"    Consulta c " +
			"where " +
			"    (:esNullCreatedBy = true or c.createdBy = :createdBy) " +
			"and (:esNullProcedimentId = true or c.procedimentServei.procediment.id = :procedimentId) " +
			"and (:esNullServeiCodi = true or c.procedimentServei.servei = :serveiCodi) " +
			"and (:esNullEstat = true or c.estat = :estat) " +
			"and (:esNullDataInici = true or c.createdDate >= :dataInici) " +
			"and (:esNullDataFi = true or c.createdDate <= :dataFi) " +
			"group by " +
			"    c.procedimentServei.procediment.entitat.id")
	public List<Object[]> countByEntitat(
			@Param("esNullCreatedBy") boolean esNullCreatedBy,
			@Param("createdBy") Usuari createdBy,
			@Param("esNullProcedimentId") boolean esNullProcedimentId,
			@Param("procedimentId") Long procedimentId,
			@Param("esNullServeiCodi") boolean esNullServeiCodi,
			@Param("serveiCodi") String serveiCodi,
			@Param("esNullEstat") boolean esNullEstat,
			@Param("estat") EstatTipus estat,
			@Param("esNullDataInici") boolean esNullDataInici,
			@Param("dataInici") Date dataInici,
			@Param("esNullDataFi") boolean esNullDataFi,
			@Param("dataFi") Date dataFi);

	@Query(	"select" +
			"    c " +
			"from" +
			"    Consulta c " +
			"where " +
			"    c.procedimentServei.procediment.entitat.id = :entitatId " +
			"and c.pare is null " +
			"and (:esNullCreatedBy = true or c.createdBy = :createdBy) " +
			"and (c.multiple = :multiple) " +
			"and (:nomesSensePare = false or c.pare is null)")
	public Page<Consulta> findByProcedimentServeiProcedimentEntitatIdAndCreatedBy(
			@Param("entitatId") Long entitatId,
			@Param("esNullCreatedBy") boolean esNullCreatedBy,
			@Param("createdBy")Usuari createdBy,
			@Param("multiple")Boolean multiple,
			@Param("nomesSensePare")boolean nomesSensePare,
			Pageable pageable);

	@Query(	"select" +
			"    c " +
			"from" +
			"    Consulta c " +
			"where " +
			"    c.procedimentServei.procediment.entitat.id = :entitatId " +
			"and (:esNullCreatedBy = true or c.createdBy = :createdBy) " +
			"and (:esNullProcedimentId = true or c.procedimentServei.procediment.id = :procedimentId) " +
			"and (:esNullServeiCodi = true or c.procedimentServei.servei = :serveiCodi) " +
			"and (:esNullEstat = true or c.estat = :estat) " +
			"and (:esNullDataInici = true or c.createdDate >= :dataInici) " +
			"and (:esNullDataFi = true or c.createdDate <= :dataFi) " +
			"and (:esNullTitularNom = true or c.titularNom = :titularNom) " +
			"and (:esNullTitularDocument = true or c.titularDocumentNum = :titularDocument) " +
			"and (:esNullFuncionariNom = true or c.funcionariNom = :funcionariNom) " +
			"and (:esNullFuncionariDocument = true or c.funcionariDocumentNum = :funcionariDocument) " +
			"and (c.multiple = :multiple) " +
			"and (:nomesSensePare = false or c.pare is null)")
	public Page<Consulta> findByCreatedByAndFiltrePaginat(
			@Param("entitatId") Long entitatId,
			@Param("esNullCreatedBy") boolean esNullCreatedBy,
			@Param("createdBy") Usuari createdBy,
			@Param("esNullProcedimentId") boolean esNullProcedimentId,
			@Param("procedimentId") Long procedimentId,
			@Param("esNullServeiCodi") boolean esNullServeiCodi,
			@Param("serveiCodi") String serveiCodi,
			@Param("esNullEstat") boolean esNullEstat,
			@Param("estat") EstatTipus estat,
			@Param("esNullDataInici") boolean esNullDataInici,
			@Param("dataInici") Date dataInici,
			@Param("esNullDataFi") boolean esNullDataFi,
			@Param("dataFi") Date dataFi,
			@Param("esNullTitularNom") boolean esNullTitularNom,
			@Param("titularNom") String titularNom,
			@Param("esNullTitularDocument") boolean esNullTitularDocument,
			@Param("titularDocument") String titularDocument,
			@Param("esNullFuncionariNom") boolean esNullFuncionariNom,
			@Param("funcionariNom") String funcionariNom,
			@Param("esNullFuncionariDocument") boolean esNullFuncionariDocument,
			@Param("funcionariDocument") String funcionariDocument,
			@Param("multiple")boolean multiple,
			@Param("nomesSensePare")boolean nomesSensePare,
			Pageable pageable);

	@Query(	"select" +
			"    c " +
			"from" +
			"    Consulta c " +
			"where " +
			"    c.procedimentServei.procediment.entitat = :entitat " +
			"and c.createdDate >= :dataInici " +
			"and c.createdDate <= :dataFi")
	public List<Consulta> findByEntitatAndDataIniciAndDataFi(
			@Param("entitat") Entitat entitat,
			@Param("dataInici") Date dataInici,
			@Param("dataFi") Date dataFi);

	@Query(	"select" +
			"    c " +
			"from" +
			"    Consulta c " +
			"where " +
			"    c.procedimentServei.procediment.entitat = :entitat " +
			"and c.id in :ids " +
			"order by " +
			"    c.createdDate")
	public List<Consulta> findByEntitatAndIds(
			@Param("entitat") Entitat entitat,
			@Param("ids") List<Long> ids);

	@Query(	"select" +
			"    c, " +
			"    c.procedimentServei.procediment.entitat.id " +
			"from" +
			"    Consulta c " +
			"where " +
			"    c.id in :ids " +
			"order by " +
			"    c.procedimentServei.procediment.entitat.id, " +
			"    c.createdDate")
	public List<Object[]> findByIds(
			@Param("ids") List<Long> ids);

	public List<Consulta> findByPareOrderByScspSolicitudIdAsc(Consulta pare);

	public List<Consulta> findByEstatAndMultipleTrue(EstatTipus estat);

	@Query(	"select " +
			"    count(*) " +
			"from " +
			"    Consulta c " +
			"where " +
			"    c.estat = :estat " +
			"and c.createdBy = :createdBy " +
			"and c.multiple = true")
	public long countByEstatAndCreatedByAndMultipleTrue(
			@Param("estat") EstatTipus estat,
			@Param("createdBy") Usuari createdBy);
	
	@Query(	"select " +
			"    c.procedimentServei.procediment.entitat.id, " +
			"    c.procedimentServei.procediment.id, " +
			"    c.procedimentServei.servei, " +
			"    c.estat, " +
			"    count(*) " +
			"from " +
			"    Consulta c " +
			"group by " +
			"    c.procedimentServei.procediment.entitat.id, " +
			"    c.procedimentServei.procediment.id, " +
			"    c.procedimentServei.servei, " +
			"    c.estat " +
			"order by " +
			"    c.procedimentServei.procediment.entitat.id asc, " +
			"    c.procedimentServei.procediment.id asc, " +
			"    c.procedimentServei.servei asc, " +
			"    c.estat asc")
	public List<Object[]> countGroupByProcedimentServeiEstat();
	

}
