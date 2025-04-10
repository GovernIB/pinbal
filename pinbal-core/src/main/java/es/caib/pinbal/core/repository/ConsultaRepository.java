/**
 * 
 */
package es.caib.pinbal.core.repository;

import es.caib.pinbal.core.dto.CarregaDto;
import es.caib.pinbal.core.dto.EstatTipus;
import es.caib.pinbal.core.dto.JustificantEstat;
import es.caib.pinbal.core.model.Consulta;
import es.caib.pinbal.core.model.Entitat;
import es.caib.pinbal.core.model.ProcedimentServei;
import es.caib.pinbal.core.model.Usuari;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
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
public interface ConsultaRepository extends JpaRepository<Consulta, Long> {

	@Query(	"select " +
			"    c.entitat.id, " +
			"    count(*) " +
			"from " +
			"    Consulta c " +
			"where " +
			"    (:esNullCreatedBy = true or c.createdBy = :createdBy) " +
			"and (:esNullProcedimentId = true or c.procediment.id = :procedimentId) " +
			"and (:esNullServeiCodi = true or c.serveiCodi = :serveiCodi) " +
			"and (:esNullEstat = true or c.estat = :estat) " +
			"and (:esNullDataInici = true or c.createdDate >= :dataInici) " +
			"and (:esNullDataFi = true or c.createdDate <= :dataFi) " +
			"group by " +
			"    c.entitat.id")
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
			"    c.entitat.id = :entitatId " +
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
			"    c.entitat.id = :entitatId " +
			"and (:esNullCreatedBy = true or c.createdBy = :createdBy) " +
			"and (:esNullScspPeticionId = true or lower(c.scspPeticionId) like lower('%'||:scspPeticionId||'%')) " +
			"and (:esNullProcedimentId = true or c.procediment.id = :procedimentId) " +
			"and (:esNullServeiCodi = true or c.serveiCodi = :serveiCodi) " +
			"and (:esNullEstat = true or c.estat = :estat) " +
			"and (:esNullDataInici = true or c.createdDate >= :dataInici) " +
			"and (:esNullDataFi = true or c.createdDate <= :dataFi) " +
			"and (:esNullTitularNom = true or c.titularNom = :titularNom) " +
			"and (:esNullTitularDocument = true or c.titularDocumentNum = :titularDocument) " +
			"and (:esNullFuncionari = true or (lower(c.funcionariDocumentNum) like lower('%'||:funcionari||'%') or lower(c.funcionariNom) like lower('%'||:funcionari||'%'))) " +
			"and (:esNullUsuari = true or c.createdBy.codi = :usuari) " +
			"and (c.multiple = :multiple) " +
			"and (:nomesSensePare = false or c.pare is null)")
	public Page<Consulta> findByCreatedByAndFiltrePaginat(
			@Param("entitatId") Long entitatId,
			@Param("esNullCreatedBy") boolean esNullCreatedBy,
			@Param("createdBy") Usuari createdBy,
			@Param("esNullScspPeticionId") boolean esNullScspPeticionId,
			@Param("scspPeticionId") String scspPeticionId,
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
			@Param("esNullFuncionari") boolean esNullFuncionari,
			@Param("funcionari") String funcionari,
			@Param("esNullUsuari") boolean esNullUsuari,
			@Param("usuari") String usuari,
			@Param("multiple")boolean multiple,
			@Param("nomesSensePare")boolean nomesSensePare,
			Pageable pageable);

	@Query(	"select" +
			"    c " +
			"from" +
			"    Consulta c " +
			"where " +
			"    (:esNullEntitatId = true or c.entitat.id = :entitatId) " +
			"and (:esNullScspPeticionId = true or lower(c.scspPeticionId) like lower('%'||:scspPeticionId||'%')) " +
			"and (:esNullProcedimentId = true or c.procediment.id = :procedimentId) " +
			"and (:esNullServeiCodi = true or c.serveiCodi = :serveiCodi) " +
			"and (:esNullEstat = true or c.estat = :estat) " +
			"and (:esNullDataInici = true or c.createdDate >= :dataInici) " +
			"and (:esNullDataFi = true or c.createdDate <= :dataFi) " +
			"and (:esNullTitularNom = true or c.titularNom = :titularNom) " +
			"and (:esNullTitularDocument = true or c.titularDocumentNum = :titularDocument) " +
			"and (:esNullFuncionari = true or (lower(c.funcionariDocumentNum) like lower('%'||:funcionari||'%') or lower(c.funcionariNom) like lower('%'||:funcionari||'%'))) " +
			"and (:esNullUsuari = true or c.createdBy.codi = :usuari) " +
			"and (:esNullRecobriment = true or c.recobriment = :recobriment) " +
			"and c.pare is null")
	public Page<Consulta> findByFiltrePaginatAdmin(
			@Param("esNullEntitatId") boolean esNullEntitatId,
			@Param("entitatId") Long entitatId,
			@Param("esNullScspPeticionId") boolean esNullScspPeticionId,
			@Param("scspPeticionId") String scspPeticionId,
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
			@Param("esNullFuncionari") boolean esNullFuncionari,
			@Param("funcionari") String funcionari,
			@Param("esNullUsuari") boolean esNullUsuari,
			@Param("usuari") String usuari,
			@Param("esNullRecobriment") Boolean esNullRecobriment,
			@Param("recobriment") Boolean recobriment,
			Pageable pageable);

	@Query(	"select" +
			"    c " +
			"from" +
			"    Consulta c " +
			"where " +
			"    c.entitat = :entitat " +
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
			"    c.entitat = :entitat " +
			"and c.id in :ids " +
			"order by " +
			"    c.createdDate")
	public List<Consulta> findByEntitatAndIds(
			@Param("entitat") Entitat entitat,
			@Param("ids") List<Long> ids);

	@Query(	"select" +
			"    c, " +
			"    c.entitat.id " +
			"from" +
			"    Consulta c " +
			"where " +
			"    c.id in :ids " +
			"order by " +
			"    c.entitat.id, " +
			"    c.createdDate")
	public List<Object[]> findByIds(
			@Param("ids") List<Long> ids);

	public List<Consulta> findByPareOrderByScspSolicitudIdAsc(Consulta pare);

	public List<Consulta> findByEstatAndMultipleOrderByIdAsc(
			EstatTipus estat,
			boolean multiple);

	public List<Consulta> findByEstatAndMultipleAndConsentimentNotNullOrderByIdAsc(
			EstatTipus estat,
			boolean multiple);
	
	public List<Consulta> findByEstatAndMultipleAndProcedimentServeiAndConsentimentNotNullOrderByIdAsc(
			EstatTipus estat,
			boolean multiple,
			ProcedimentServei procedimentServei);

	public List<Consulta> findByEstatAndJustificantEstatAndMultipleFalseAndArxiuExpedientTancatFalseOrderByIdAsc(
			EstatTipus estat,
			JustificantEstat justificantEstat);

	public List<Consulta> findByEstatAndJustificantEstatAndMultipleFalseAndArxiuExpedientTancatFalseAndRecobrimentFalseOrderByIdAsc(
			EstatTipus estat,
			JustificantEstat justificantEstat);

	public int countByPare(Consulta pare);

	public int countByPareAndCustodiat(
			Consulta pare,
			boolean custodiat);

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
			"    c.entitat.id, " +
			"    c.procediment.id, " +
			"    c.serveiCodi, " +
			"    c.estat, " +
			"    count(*) " +
			"from " +
			"    Consulta c " +
			"where " +
			"    c.createdDate >= :dataInici " +
			"and c.createdDate <= :dataFi " +
			"group by " +
			"    c.entitat.id, " +
			"    c.procediment.id, " +
			"    c.serveiCodi, " +
			"    c.estat " +
			"order by " +
			"    c.entitat.id asc, " +
			"    c.procediment.id asc, " +
			"    c.serveiCodi asc, " +
			"    c.estat asc")
	public List<Object[]> countGroupByProcedimentServeiEstat(
			@Param("dataInici") Date dataInici,
			@Param("dataFi") Date dataFi);

	public Consulta findByScspPeticionIdAndScspSolicitudId(
			String scspPeticionId,
			String scspSolicitudId);

	public Consulta findByScspPeticionIdAndMultipleIsTrue(String scspPeticionId);
	public List<Consulta> findByScspPeticionId(String scspPeticionId);

	@Query(	"select " +
			"	new es.caib.pinbal.core.dto.CarregaDto( " +
			"		sum(case c.recobriment when true then 0 else 1 end), " +
			"		sum(case c.recobriment when true then 1 else 0 end), " +
			"		c.entitat.id, " +
			"		c.entitat.codi, " +
			"		c.entitat.nom, " +
			"		c.entitat.cif, " +
			"		c.departamentNom, " +
			"		c.procedimentServei.id, " +
			"		c.procediment.codi, " +
			"		c.procediment.nom, " +
			"		s.codi, " +
			"		s.descripcio) " +
			"from " +
			"	Consulta c, " +
			"	Servei s " +
			"where " +
			"	 c.createdDate >= :dataInici " +
			"and s.codi = c.serveiCodi " +
			"group by " +
			"	c.entitat.id, " +
			"	c.entitat.codi, " +
			"	c.entitat.nom, " +
			"	c.entitat.cif, " +
			"	c.departamentNom, " +
			"	c.procedimentServei.id, " +
			"	c.procediment.codi, " +
			"	c.procediment.nom, " +
			"	s.codi, " +
			"	s.descripcio " +
			"order by " +
			"	c.entitat.nom, " +
			"	c.departamentNom, " +
			"	c.procediment.nom, " +
			"	s.descripcio")
	public List<CarregaDto> findCarrega(
			@Param("dataInici") Date dataInici);

	@Modifying
	@Query(
			value = "ALTER SESSION SET OPTIMIZER_MODE = RULE",
			nativeQuery = true)
	public void setSessionOptimizerModeToRule();




//	@Modifying
//	@Query(value = "UPDATE PBL_CONSULTA " +
//			"SET CREATEDBY_CODI = CASE WHEN CREATEDBY_CODI = :codiAntic THEN :codiNou ELSE CREATEDBY_CODI END, " +
//			"    LASTMODIFIEDBY_CODI = CASE WHEN LASTMODIFIEDBY_CODI = :codiAntic THEN :codiNou ELSE LASTMODIFIEDBY_CODI END " +
//			"WHERE CREATEDBY_CODI = :codiAntic OR LASTMODIFIEDBY_CODI = :codiAntic",
//			nativeQuery = true)
//	void updateUsuariAuditoria(@Param("codiAntic") String codiAntic, @Param("codiNou") String codiNou);

	@Modifying
	@Query(value = "UPDATE PBL_CONSULTA SET CREATEDBY_CODI = :codiNou WHERE CREATEDBY_CODI = :codiAntic", nativeQuery = true)
	int updateCreatedByCodi(@Param("codiAntic") String codiAntic, @Param("codiNou") String codiNou);

	@Modifying
	@Query(value = "UPDATE PBL_CONSULTA SET LASTMODIFIEDBY_CODI = :codiNou WHERE LASTMODIFIEDBY_CODI = :codiAntic", nativeQuery = true)
	int updateLastModifiedByCodi(@Param("codiAntic") String codiAntic, @Param("codiNou") String codiNou);


//	@Modifying
//	@Query(value = "UPDATE PBL_CONSULTA SET CREATEDBY_CODI = :codiNou " +
//			"WHERE CREATEDBY_CODI = :codiAntic " +
//			"  AND ROWNUM <= :batchSize", nativeQuery = true)
//	int updateCreatedByCodiBatch(@Param("codiAntic") String codiAntic,
//								 @Param("codiNou") String codiNou,
//								 @Param("batchSize") int batchSize);
//
//	@Modifying
//	@Query(value = "UPDATE PBL_CONSULTA SET LASTMODIFIEDBY_CODI = :codiNou " +
//			"WHERE LASTMODIFIEDBY_CODI = :codiAntic " +
//			"  AND ROWNUM <= :batchSize", nativeQuery = true)
//	int updateLastModifiedByCodiBatch(@Param("codiAntic") String codiAntic,
//									  @Param("codiNou") String codiNou,
//									  @Param("batchSize") int batchSize);
//
//	@Modifying
//	@Query(value = "ALTER TABLE PBL_CONSULTA NOLOGGING", nativeQuery = true)
//	void disableLogging();
//
//	@Modifying
//	@Query(value = "ALTER TABLE PBL_CONSULTA LOGGING", nativeQuery = true)
//	void enableLogging();

}
