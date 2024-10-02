/**
 * 
 */
package es.caib.pinbal.core.repository;

import es.caib.pinbal.core.dto.EstatTipus;
import es.caib.pinbal.core.model.Entitat;
import es.caib.pinbal.core.model.HistoricConsulta;
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
public interface HistoricConsultaRepository extends JpaRepository<HistoricConsulta, Long> {

	@Modifying
	@Query(value = 	"insert into pbl_consulta_hist(id, custodiat, departament, error, estat, peticion_id, titular_docnum, titular_doctip, titular_lling1, titular_lling2, titular_nom, procserv_id, procediment_id, servei_codi, entitat_id, recobriment, titular_nomcomplet, funcionari_nom, funcionari_docnum, solicitud_id, multiple, pare_id, custodia_url, justificant_error, justificant_estat, custodia_id, arxiu_expedient_uuid, arxiu_document_uuid, arxiu_expedient_tancat, finalitat, consentiment, expedient_id, dades_especifiques, createdby_codi, createddate, lastmodifiedby_codi, lastmodifieddate, version) " +
			"select id, custodiat, departament, error, estat, peticion_id, titular_docnum, titular_doctip, titular_lling1, titular_lling2, titular_nom, procserv_id, procediment_id, servei_codi, entitat_id, recobriment, titular_nomcomplet, funcionari_nom, funcionari_docnum, solicitud_id, multiple, pare_id, custodia_url, justificant_error, justificant_estat, custodia_id, arxiu_expedient_uuid, arxiu_document_uuid, arxiu_expedient_tancat, finalitat, consentiment, expedient_id, dades_especifiques, createdby_codi, createddate, lastmodifiedby_codi, lastmodifieddate, version " +
			"from pbl_consulta where createddate < current_date - :dies ",
			nativeQuery = true)
	public int arxivaConsultesOracle(@Param("dies") int dies);

	@Modifying
	@Query(value = 	"insert into pbl_consulta_hist(id, custodiat, departament, error, estat, peticion_id, titular_docnum, titular_doctip, titular_lling1, titular_lling2, titular_nom, procserv_id, procediment_id, servei_codi, entitat_id, recobriment, titular_nomcomplet, funcionari_nom, funcionari_docnum, solicitud_id, multiple, pare_id, custodia_url, justificant_error, justificant_estat, custodia_id, arxiu_expedient_uuid, arxiu_document_uuid, arxiu_expedient_tancat, finalitat, consentiment, expedient_id, dades_especifiques, createdby_codi, createddate, lastmodifiedby_codi, lastmodifieddate, version) " +
			"select id, custodiat, departament, error, estat, peticion_id, titular_docnum, titular_doctip, titular_lling1, titular_lling2, titular_nom, procserv_id, procediment_id, servei_codi, entitat_id, recobriment, titular_nomcomplet, funcionari_nom, funcionari_docnum, solicitud_id, multiple, pare_id, custodia_url, justificant_error, justificant_estat, custodia_id, arxiu_expedient_uuid, arxiu_document_uuid, arxiu_expedient_tancat, finalitat, consentiment, expedient_id, dades_especifiques, createdby_codi, createddate, lastmodifiedby_codi, lastmodifieddate, version " +
			"from pbl_consulta where createddate < current_date - INTERVAL ':dies days' ",
			nativeQuery = true)
	public int arxivaConsultesPostgres(@Param("dies") int dies);

	@Modifying
	@Query(value = "delete Consulta where createdDate < current_date - :dies")
	public int purgaConsultes(@Param("dies") int dies);


	@Query(	"select " +
			"    c.entitat.id, " +
			"    count(*) " +
			"from " +
			"    HistoricConsulta c " +
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
			"    HistoricConsulta c " +
			"where " +
			"    c.entitat.id = :entitatId " +
			"and c.pare is null " +
			"and (:esNullCreatedBy = true or c.createdBy = :createdBy) " +
			"and (c.multiple = :multiple) " +
			"and (:nomesSensePare = false or c.pare is null)")
	public Page<HistoricConsulta> findByProcedimentServeiProcedimentEntitatIdAndCreatedBy(
            @Param("entitatId") Long entitatId,
            @Param("esNullCreatedBy") boolean esNullCreatedBy,
            @Param("createdBy") Usuari createdBy,
            @Param("multiple") Boolean multiple,
            @Param("nomesSensePare") boolean nomesSensePare,
            Pageable pageable);

	@Query(	"select" +
			"    c " +
			"from" +
			"    HistoricConsulta c " +
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
	public Page<HistoricConsulta> findByCreatedByAndFiltrePaginat(
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
            @Param("multiple") boolean multiple,
            @Param("nomesSensePare") boolean nomesSensePare,
            Pageable pageable);

	@Query(	"select" +
			"    c " +
			"from" +
			"    HistoricConsulta c " +
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
	public Page<HistoricConsulta> findByFiltrePaginatAdmin(
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
			"    HistoricConsulta c " +
			"where " +
			"    c.entitat = :entitat " +
			"and c.createdDate >= :dataInici " +
			"and c.createdDate <= :dataFi")
	public List<HistoricConsulta> findByEntitatAndDataIniciAndDataFi(
            @Param("entitat") Entitat entitat,
            @Param("dataInici") Date dataInici,
            @Param("dataFi") Date dataFi);

	@Query(	"select" +
			"    c " +
			"from" +
			"    HistoricConsulta c " +
			"where " +
			"    c.entitat = :entitat " +
			"and c.id in :ids " +
			"order by " +
			"    c.createdDate")
	public List<HistoricConsulta> findByEntitatAndIds(
            @Param("entitat") Entitat entitat,
            @Param("ids") List<Long> ids);

	@Query(	"select" +
			"    c, " +
			"    c.entitat.id " +
			"from" +
			"    HistoricConsulta c " +
			"where " +
			"    c.id in :ids " +
			"order by " +
			"    c.entitat.id, " +
			"    c.createdDate")
	public List<Object[]> findByIds(
            @Param("ids") List<Long> ids);

	public List<HistoricConsulta> findByPareOrderByScspSolicitudIdAsc(HistoricConsulta pare);

	@Query(	"select " +
			"    c.entitat.id, " +
			"    c.procediment.id, " +
			"    c.serveiCodi, " +
			"    c.estat, " +
			"    count(*) " +
			"from " +
			"    HistoricConsulta c " +
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

	public HistoricConsulta findByScspPeticionIdAndScspSolicitudId(
            String scspPeticionId,
            String scspSolicitudId);

	@Modifying
	@Query(
			value = "ALTER SESSION SET OPTIMIZER_MODE = RULE",
			nativeQuery = true)
	public void setSessionOptimizerModeToRule();

}
