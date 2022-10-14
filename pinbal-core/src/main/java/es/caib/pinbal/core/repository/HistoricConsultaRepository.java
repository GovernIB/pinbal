/**
 * 
 */
package es.caib.pinbal.core.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import es.caib.pinbal.client.dadesobertes.DadesObertesRespostaConsulta;
import es.caib.pinbal.core.model.Consulta.EstatTipus;
import es.caib.pinbal.core.model.Entitat;
import es.caib.pinbal.core.model.HistoricConsulta;
import es.caib.pinbal.core.model.Usuari;

/**
 * Especifica els mètodes que s'han d'emprar per obtenir i modificar la
 * informació relativa a una consulta SCSP que està emmagatzemada a dins 
 * la base de dades.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public interface HistoricConsultaRepository extends JpaRepository<HistoricConsulta, Long> {

//	@Modifying
//	@Query(value = 	"insert into HistoricConsulta(id, scspPeticionId, scspSolicitudId, departamentNom, funcionariNom, funcionariDocumentNum, titularDocumentTipus, titularDocumentNum, titularNom, titularLlinatge1, titularLlinatge2, titularNomComplet, finalitat, consentiment, expedientId, dadesEspecifiques, estat, error, recobriment, multiple, justificantEstat, custodiat, custodiaId, custodiaUrl, justificantError, arxiuExpedientUuid, arxiuDocumentUuid, arxiuExpedientTancat, pare.id, procedimentServei, createdBy, createdDate, lastModifiedBy, lastModifiedDate, version) " +
//					"select id, scspPeticionId, scspSolicitudId, departamentNom, funcionariNom, funcionariDocumentNum, titularDocumentTipus, titularDocumentNum, titularNom, titularLlinatge1, titularLlinatge2, titularNomComplet, finalitat, consentiment, expedientId, dadesEspecifiques, estat, error, recobriment, multiple, justificantEstat, custodiat, custodiaId, custodiaUrl, justificantError, arxiuExpedientUuid, arxiuDocumentUuid, arxiuExpedientTancat, pare.id, procedimentServei, createdBy, createdDate, lastModifiedBy, lastModifiedDate, version " +
//					"from Consulta where createdDate < current_date - :dies " +
//					"order by id")
//	public int arxivaConsultes(@Param("dies") int dies);

	@Modifying
	@Query(value = 	"insert into pbl_consulta_hist(id, custodiat, departament, error, estat, peticion_id, titular_docnum, titular_doctip, titular_lling1, titular_lling2, titular_nom, procserv_id, recobriment, titular_nomcomplet, funcionari_nom, funcionari_docnum, solicitud_id, multiple, pare_id, custodia_url, justificant_error, justificant_estat, custodia_id, arxiu_expedient_uuid, arxiu_document_uuid, arxiu_expedient_tancat, finalitat, consentiment, expedient_id, dades_especifiques, createdby_codi, createddate, lastmodifiedby_codi, lastmodifieddate, version) " +
			"select id, custodiat, departament, error, estat, peticion_id, titular_docnum, titular_doctip, titular_lling1, titular_lling2, titular_nom, procserv_id, recobriment, titular_nomcomplet, funcionari_nom, funcionari_docnum, solicitud_id, multiple, pare_id, custodia_url, justificant_error, justificant_estat, custodia_id, arxiu_expedient_uuid, arxiu_document_uuid, arxiu_expedient_tancat, finalitat, consentiment, expedient_id, dades_especifiques, createdby_codi, createddate, lastmodifiedby_codi, lastmodifieddate, version " +
			"from pbl_consulta where createddate < current_date - :dies ",
			nativeQuery = true)
	public int arxivaConsultesOracle(@Param("dies") int dies);

	@Modifying
	@Query(value = 	"insert into pbl_consulta_hist(id, custodiat, departament, error, estat, peticion_id, titular_docnum, titular_doctip, titular_lling1, titular_lling2, titular_nom, procserv_id, recobriment, titular_nomcomplet, funcionari_nom, funcionari_docnum, solicitud_id, multiple, pare_id, custodia_url, justificant_error, justificant_estat, custodia_id, arxiu_expedient_uuid, arxiu_document_uuid, arxiu_expedient_tancat, finalitat, consentiment, expedient_id, dades_especifiques, createdby_codi, createddate, lastmodifiedby_codi, lastmodifieddate, version) " +
			"select id, custodiat, departament, error, estat, peticion_id, titular_docnum, titular_doctip, titular_lling1, titular_lling2, titular_nom, procserv_id, recobriment, titular_nomcomplet, funcionari_nom, funcionari_docnum, solicitud_id, multiple, pare_id, custodia_url, justificant_error, justificant_estat, custodia_id, arxiu_expedient_uuid, arxiu_document_uuid, arxiu_expedient_tancat, finalitat, consentiment, expedient_id, dades_especifiques, createdby_codi, createddate, lastmodifiedby_codi, lastmodifieddate, version " +
			"from pbl_consulta where createddate < current_date - INTERVAL ':dies days' ",
			nativeQuery = true)
	public int arxivaConsultesPostgres(@Param("dies") int dies);

	@Modifying
	@Query(value = "delete Consulta where createdDate < current_date - :dies")
	public int purgaConsultes(@Param("dies") int dies);


	@Query(	"select " +
			"    c.procedimentServei.id, " +
			"    sum(case when c.recobriment = true and c.estat = :estatOk then 1 else 0 end), " +
			"    sum(case when c.recobriment = true and c.estat = :estatError then 1 else 0 end), " +
			"    sum(case when c.recobriment = true and c.estat <> :estatOk and c.estat <> :estatError then 1 else 0 end), " +
			"    sum(case when c.recobriment = false and c.estat = :estatOk then 1 else 0 end), " +
			"    sum(case when c.recobriment = false and c.estat = :estatError then 1 else 0 end), " +
			"    sum(case when c.recobriment = false and c.estat <> :estatOk and c.estat <> :estatError then 1 else 0 end) " +
			"from " +
			"    HistoricConsulta c " +
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
			"    HistoricConsulta c " +
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
			"    HistoricConsulta c " +
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
			"    HistoricConsulta c " +
			"where " +
			"    c.procedimentServei.procediment.entitat.id = :entitatId " +
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
			"    c.procedimentServei.procediment.entitat.id = :entitatId " +
			"and (:esNullCreatedBy = true or c.createdBy = :createdBy) " +
			"and (:esNullScspPeticionId = true or lower(c.scspPeticionId) like lower('%'||:scspPeticionId||'%')) " +
			"and (:esNullProcedimentId = true or c.procedimentServei.procediment.id = :procedimentId) " +
			"and (:esNullServeiCodi = true or c.procedimentServei.servei = :serveiCodi) " +
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
			"    (:esNullEntitatId = true or c.procedimentServei.procediment.entitat.id = :entitatId) " +
			"and (:esNullScspPeticionId = true or lower(c.scspPeticionId) like lower('%'||:scspPeticionId||'%')) " +
			"and (:esNullProcedimentId = true or c.procedimentServei.procediment.id = :procedimentId) " +
			"and (:esNullServeiCodi = true or c.procedimentServei.servei = :serveiCodi) " +
			"and (:esNullEstat = true or c.estat = :estat) " +
			"and (:esNullDataInici = true or c.createdDate >= :dataInici) " +
			"and (:esNullDataFi = true or c.createdDate <= :dataFi) " +
			"and (:esNullTitularNom = true or c.titularNom = :titularNom) " +
			"and (:esNullTitularDocument = true or c.titularDocumentNum = :titularDocument) " +
			"and (:esNullFuncionari = true or (lower(c.funcionariDocumentNum) like lower('%'||:funcionari||'%') or lower(c.funcionariNom) like lower('%'||:funcionari||'%'))) " +
			"and (:esNullUsuari = true or c.createdBy.codi = :usuari) " +
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
            Pageable pageable);

	@Query(	"select " +
			"    new es.caib.pinbal.client.dadesobertes.DadesObertesRespostaConsulta(" +
			"        c.procedimentServei.procediment.entitat.codi, " +
			"        c.procedimentServei.procediment.entitat.nom, " +
			"        c.procedimentServei.procediment.entitat.cif, " +
			"        cast(c.procedimentServei.procediment.entitat.tipus as string), " +
			"        c.transmision.codigoUnidadTramitadora, " +
			"        c.transmision.unidadTramitadora, " +
			"        c.procedimentServei.procediment.codi, " +
			"        c.procedimentServei.procediment.nom, " +
			"        c.procedimentServei.serveiScsp.codi, " +
			"        c.procedimentServei.serveiScsp.descripcio, " +
			"        c.procedimentServei.serveiScsp.scspEmisor.nom, " +
			"        c.procedimentServei.serveiScsp.scspEmisor.cif, " +
			"        c.transmision.consentimiento, " +
			"        c.transmision.finalidad, " +
			"        c.titularDocumentTipus, " +
			"        c.scspSolicitudId, " +
			"        c.createdDate, " +
			"        c.recobriment, " +
			"        cast(c.estat as string)) " +
			"from" +
			"    HistoricConsulta c " +
			"where " +
			"    (:esNullEntitatId = true or c.procedimentServei.procediment.entitat.id = :entitatId) " +
			"and (:esNullProcedimentId = true or c.procedimentServei.procediment.id = :procedimentId) " +
			"and (:esNullServeiCodi = true or c.procedimentServei.servei = :serveiCodi) " +
			"and (:esNullDataInici = true or c.createdDate >= :dataInici) " +
			"and (:esNullDataFi = true or c.createdDate <= :dataFi) " +
			"and (c.multiple = false) " +
			"order by " +
			"c.createdDate asc")
	public List<DadesObertesRespostaConsulta> findByOpendata(
            @Param("esNullEntitatId") boolean esNullEntitatId,
            @Param("entitatId") Long entitatId,
            @Param("esNullProcedimentId") boolean esNullProcedimentId,
            @Param("procedimentId") Long procedimentId,
            @Param("esNullServeiCodi") boolean esNullServeiCodi,
            @Param("serveiCodi") String serveiCodi,
            @Param("esNullDataInici") boolean esNullDataInici,
            @Param("dataInici") Date dataInici,
            @Param("esNullDataFi") boolean esNullDataFi,
            @Param("dataFi") Date dataFi);

	@Query(	"select count(c) " +
			"  from HistoricConsulta c " +
			" where (:esNullEntitatId = true or c.procedimentServei.procediment.entitat.id = :entitatId) " +
			"   and (:esNullProcedimentId = true or c.procedimentServei.procediment.id = :procedimentId) " +
			"   and (:esNullServeiCodi = true or c.procedimentServei.servei = :serveiCodi) " +
			"   and (:esNullDataInici = true or c.createdDate >= :dataInici) " +
			"   and (:esNullDataFi = true or c.createdDate <= :dataFi) " +
			"   and (c.multiple = false) " +
			" order by " +
			"c.createdDate asc")
	public Integer countByOpendata(
			@Param("esNullEntitatId") boolean esNullEntitatId,
			@Param("entitatId") Long entitatId,
			@Param("esNullProcedimentId") boolean esNullProcedimentId,
			@Param("procedimentId") Long procedimentId,
			@Param("esNullServeiCodi") boolean esNullServeiCodi,
			@Param("serveiCodi") String serveiCodi,
			@Param("esNullDataInici") boolean esNullDataInici,
			@Param("dataInici") Date dataInici,
			@Param("esNullDataFi") boolean esNullDataFi,
			@Param("dataFi") Date dataFi);

	@Query(	"select " +
			"    new es.caib.pinbal.client.dadesobertes.DadesObertesRespostaConsulta(" +
			"        c.procedimentServei.procediment.entitat.codi, " +
			"        c.procedimentServei.procediment.entitat.nom, " +
			"        c.procedimentServei.procediment.entitat.cif, " +
			"        cast(c.procedimentServei.procediment.entitat.tipus as string), " +
			"        c.transmision.codigoUnidadTramitadora, " +
			"        c.transmision.unidadTramitadora, " +
			"        c.procedimentServei.procediment.codi, " +
			"        c.procedimentServei.procediment.nom, " +
			"        c.procedimentServei.serveiScsp.codi, " +
			"        c.procedimentServei.serveiScsp.descripcio, " +
			"        c.procedimentServei.serveiScsp.scspEmisor.nom, " +
			"        c.procedimentServei.serveiScsp.scspEmisor.cif, " +
			"        c.transmision.consentimiento, " +
			"        c.transmision.finalidad, " +
			"        c.titularDocumentTipus, " +
			"        c.scspSolicitudId, " +
			"        c.createdDate, " +
			"        c.recobriment, " +
			"        cast(c.estat as string)) " +
			"from" +
			"    HistoricConsulta c " +
			"where " +
			"    (:esNullEntitatId = true or c.procedimentServei.procediment.entitat.id = :entitatId) " +
			"and (:esNullProcedimentId = true or c.procedimentServei.procediment.id = :procedimentId) " +
			"and (:esNullServeiCodi = true or c.procedimentServei.servei = :serveiCodi) " +
			"and (:esNullDataInici = true or c.createdDate >= :dataInici) " +
			"and (:esNullDataFi = true or c.createdDate <= :dataFi) " +
			"and (c.multiple = false) " +
			"order by " +
			"c.createdDate asc")
	public Page<DadesObertesRespostaConsulta> findByOpendata(
			@Param("esNullEntitatId") boolean esNullEntitatId,
			@Param("entitatId") Long entitatId,
			@Param("esNullProcedimentId") boolean esNullProcedimentId,
			@Param("procedimentId") Long procedimentId,
			@Param("esNullServeiCodi") boolean esNullServeiCodi,
			@Param("serveiCodi") String serveiCodi,
			@Param("esNullDataInici") boolean esNullDataInici,
			@Param("dataInici") Date dataInici,
			@Param("esNullDataFi") boolean esNullDataFi,
			@Param("dataFi") Date dataFi,
			Pageable pageable);

	@Query(	"select" +
			"    c " +
			"from" +
			"    HistoricConsulta c " +
			"where " +
			"    c.procedimentServei.procediment.entitat = :entitat " +
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
			"    c.procedimentServei.procediment.entitat = :entitat " +
			"and c.id in :ids " +
			"order by " +
			"    c.createdDate")
	public List<HistoricConsulta> findByEntitatAndIds(
            @Param("entitat") Entitat entitat,
            @Param("ids") List<Long> ids);

	@Query(	"select" +
			"    c, " +
			"    c.procedimentServei.procediment.entitat.id " +
			"from" +
			"    HistoricConsulta c " +
			"where " +
			"    c.id in :ids " +
			"order by " +
			"    c.procedimentServei.procediment.entitat.id, " +
			"    c.createdDate")
	public List<Object[]> findByIds(
            @Param("ids") List<Long> ids);

	public List<HistoricConsulta> findByPareOrderByScspSolicitudIdAsc(HistoricConsulta pare);

//	public List<HistoricConsulta> findByEstatAndMultipleOrderByIdAsc(
//            EstatTipus estat,
//            boolean multiple);
//
//	public List<HistoricConsulta> findByEstatAndMultipleAndConsentimentNotNullOrderByIdAsc(
//            EstatTipus estat,
//            boolean multiple);
//
//	public List<HistoricConsulta> findByEstatAndMultipleAndProcedimentServeiAndConsentimentNotNullOrderByIdAsc(
//            EstatTipus estat,
//            boolean multiple,
//            ProcedimentServei procedimentServei);
//
//	public List<HistoricConsulta> findByEstatAndJustificantEstatAndMultipleAndArxiuExpedientTancatOrderByIdAsc(
//            EstatTipus estat,
//            JustificantEstat justificantEstat,
//            boolean multiple,
//            boolean arxiuExpedientTancat);
//
//	public int countByPare(HistoricConsulta pare);
//
//	public int countByPareAndCustodiat(
//            HistoricConsulta pare,
//            boolean custodiat);
//
//	@Query(	"select " +
//			"    count(*) " +
//			"from " +
//			"    HistoricConsulta c " +
//			"where " +
//			"    c.estat = :estat " +
//			"and c.createdBy = :createdBy " +
//			"and c.multiple = true")
//	public long countByEstatAndCreatedByAndMultipleTrue(
//            @Param("estat") EstatTipus estat,
//            @Param("createdBy") Usuari createdBy);

	@Query(	"select " +
			"    c.procedimentServei.procediment.entitat.id, " +
			"    c.procedimentServei.procediment.id, " +
			"    c.procedimentServei.servei, " +
			"    c.estat, " +
			"    count(*) " +
			"from " +
			"    HistoricConsulta c " +
			"where " +
			"    c.createdDate >= :dataInici " +
			"and c.createdDate <= :dataFi " +
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
	public List<Object[]> countGroupByProcedimentServeiEstat(
            @Param("dataInici") Date dataInici,
            @Param("dataFi") Date dataFi);

	public HistoricConsulta findByScspPeticionIdAndScspSolicitudId(
            String scspPeticionId,
            String scspSolicitudId);

//	@Query(	"select " +
//			"	new es.caib.pinbal.core.dto.CarregaDto( " +
//			"		sum(case c.recobriment when true then 0 else 1 end), " +
//			"		sum(case c.recobriment when true then 1 else 0 end), " +
//			"		c.procedimentServei.procediment.entitat.id, " +
//			"		c.procedimentServei.procediment.entitat.codi, " +
//			"		c.procedimentServei.procediment.entitat.nom, " +
//			"		c.procedimentServei.procediment.entitat.cif, " +
//			"		c.departamentNom, " +
//			"		c.procedimentServei.id, " +
//			"		c.procedimentServei.procediment.codi, " +
//			"		c.procedimentServei.procediment.nom, " +
//			"		s.codi, " +
//			"		s.descripcio) " +
//			"from " +
//			"	HistoricConsulta c, " +
//			"	Servei s " +
//			"where " +
//			"	 c.createdDate >= :dataInici " +
//			"and s.codi = c.procedimentServei.servei " +
//			"group by " +
//			"	c.procedimentServei.procediment.entitat.id, " +
//			"	c.procedimentServei.procediment.entitat.codi, " +
//			"	c.procedimentServei.procediment.entitat.nom, " +
//			"	c.procedimentServei.procediment.entitat.cif, " +
//			"	c.departamentNom, " +
//			"	c.procedimentServei.id, " +
//			"	c.procedimentServei.procediment.codi, " +
//			"	c.procedimentServei.procediment.nom, " +
//			"	s.codi, " +
//			"	s.descripcio " +
//			"order by " +
//			"	c.procedimentServei.procediment.entitat.nom, " +
//			"	c.departamentNom, " +
//			"	c.procedimentServei.procediment.nom, " +
//			"	s.descripcio")
//	public List<CarregaDto> findCarrega(
//            @Param("dataInici") Date dataInici);

	@Modifying
	@Query(
			value = "ALTER SESSION SET OPTIMIZER_MODE = RULE",
			nativeQuery = true)
	public void setSessionOptimizerModeToRule();

}
