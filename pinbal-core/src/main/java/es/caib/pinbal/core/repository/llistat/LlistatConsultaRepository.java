package es.caib.pinbal.core.repository.llistat;

import es.caib.pinbal.core.dto.EstatTipus;
import es.caib.pinbal.core.model.Usuari;
import es.caib.pinbal.core.model.llistat.LlistatConsulta;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;

public interface LlistatConsultaRepository extends JpaRepository<LlistatConsulta, Long> {

    @Query(	"select" +
            "    c " +
            "from" +
            "    LlistatConsulta c " +
            "where " +
            "    c.entitatId = :entitatId " +
            "and c.pareId is null " +
            "and (:esNullUsuariCodi = true or c.usuariCodi = :usuariCodi) " +
            "and (c.multiple = :multiple) " +
            "and (:nomesSensePare = false or c.pareId is null)")
    public Page<LlistatConsulta> findByProcedimentServeiProcedimentEntitatIdAndCreatedBy(
            @Param("entitatId") Long entitatId,
            @Param("esNullUsuariCodi") boolean esNullUsuariCodi,
            @Param("usuariCodi") Usuari usuariCodi,
            @Param("multiple")Boolean multiple,
            @Param("nomesSensePare")boolean nomesSensePare,
            Pageable pageable);

    @Query(	"select" +
            "    c " +
            "from" +
            "    LlistatConsulta c " +
            "where " +
            "    c.entitatId = :entitatId " +
            "and (:esNullUsuariCodi = true or c.usuariCodi = :usuariCodi) " +
            "and (:esNullPeticioId = true or lower(c.peticioId) like lower('%'||:peticioId||'%')) " +
            "and (:esNullProcedimentId = true or c.procedimentId = :procedimentId) " +
            "and (:esNullServeiCodi = true or c.serveiCodi = :serveiCodi) " +
            "and (:esNullEstat = true or c.estat = :estat) " +
            "and (:esNullDataInici = true or c.data >= :dataInici) " +
            "and (:esNullDataFi = true or c.data <= :dataFi) " +
            "and (:esNullTitularNom = true or c.titularNom = :titularNom) " +
            "and (:esNullTitularDocument = true or c.titularDocumentNum = :titularDocument) " +
            "and (:esNullFuncionari = true or (lower(c.funcionariNif) like lower('%'||:funcionari||'%') or lower(c.funcionariNom) like lower('%'||:funcionari||'%'))) " +
            "and (:esNullUsuari = true or c.usuariCodi = :usuari) " +
            "and (c.multiple = :multiple) " +
            "and (:nomesSensePare = false or c.pareId is null)")
    public Page<LlistatConsulta> findByCreatedByAndFiltrePaginat(
            @Param("entitatId") Long entitatId,
            @Param("esNullUsuariCodi") boolean esNullUsuariCodi,
            @Param("usuariCodi") String usuariCodi,
            @Param("esNullPeticioId") boolean esNullPeticioId,
            @Param("peticioId") String peticioId,
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
            "    LlistatConsulta c " +
            "where " +
            "    (:esNullEntitatId = true or c.entitatId = :entitatId) " +
            "and (:esNullPeticioId = true or lower(c.peticioId) like lower('%'||:peticioId||'%')) " +
            "and (:esNullProcedimentId = true or c.procedimentId = :procedimentId) " +
            "and (:esNullServeiCodi = true or c.serveiCodi = :serveiCodi) " +
            "and (:esNullEstat = true or c.estat = :estat) " +
            "and (:esNullDataInici = true or c.data >= :dataInici) " +
            "and (:esNullDataFi = true or c.data <= :dataFi) " +
            "and (:esNullTitularNom = true or c.titularNom = :titularNom) " +
            "and (:esNullTitularDocument = true or c.titularDocumentNum = :titularDocument) " +
            "and (:esNullFuncionari = true or (lower(c.funcionariNif) like lower('%'||:funcionari||'%') or lower(c.funcionariNom) like lower('%'||:funcionari||'%'))) " +
            "and (:esNullUsuari = true or c.usuariCodi = :usuari) " +
            "and (:esNullRecobriment = true or c.recobriment = :recobriment) " +
            "and c.pareId is null")
    public Page<LlistatConsulta> findByFiltrePaginatAdmin(
            @Param("esNullEntitatId") boolean esNullEntitatId,
            @Param("entitatId") Long entitatId,
            @Param("esNullPeticioId") boolean esNullPeticioId,
            @Param("peticioId") String peticioId,
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

}
