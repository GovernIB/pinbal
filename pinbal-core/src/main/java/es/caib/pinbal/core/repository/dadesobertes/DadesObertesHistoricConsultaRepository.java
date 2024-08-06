package es.caib.pinbal.core.repository.dadesobertes;

import es.caib.pinbal.client.dadesobertes.DadesObertesRespostaConsulta;
import es.caib.pinbal.core.model.dadesobertes.DadesObertesHistoricConsulta;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

public interface DadesObertesHistoricConsultaRepository extends JpaRepository<DadesObertesHistoricConsulta, Long> {

    @Query(	"select count(c) " +
            "  from DadesObertesHistoricConsulta c " +
            " where (:esNullEntitatCodi = true or c.entitatCodi = :entitatCodi) " +
            "   and (:esNullProcedimentCodi = true or c.procedimentCodi = :procedimentCodi) " +
            "   and (:esNullServeiCodi = true or c.serveiCodi = :serveiCodi) " +
            "   and (:esNullDataInici = true or c.data >= :dataInici) " +
            "   and (:esNullDataFi = true or c.data <= :dataFi) " +
            "   and (c.multiple = false) " +
            " order by " +
            "c.data asc")
    Integer countByOpendata(
            @Param("esNullEntitatCodi") boolean esNullEntitatId,
            @Param("entitatCodi") String entitatCodi,
            @Param("esNullProcedimentCodi") boolean esNullProcedimentCodi,
            @Param("procedimentCodi") String procedimentCodi,
            @Param("esNullServeiCodi") boolean esNullServeiCodi,
            @Param("serveiCodi") String serveiCodi,
            @Param("esNullDataInici") boolean esNullDataInici,
            @Param("dataInici") Date dataInici,
            @Param("esNullDataFi") boolean esNullDataFi,
            @Param("dataFi") Date dataFi);

    @Query(	"select " +
            "    new es.caib.pinbal.client.dadesobertes.DadesObertesRespostaConsulta(" +
            "        c.entitatCodi, " +
            "        c.entitatNom, " +
            "        c.entitatNif, " +
            "        c.entitatTipus, " +
            "        c.departamentCodi, " +
            "        c.departamentNom, " +
            "        c.procedimentCodi, " +
            "        c.procedimentNom, " +
            "        c.serveiCodi, " +
            "        c.serveiNom, " +
            "        c.emissorNom, " +
            "        c.emissorNif, " +
            "        c.consentiment, " +
            "        c.finalitat, " +
            "        c.titularDocumentTipus, " +
            "        c.solicitudId, " +
            "        c.data, " +
            "        c.tipus, " +
            "        c.resultat) " +
            "from" +
            "    DadesObertesHistoricConsulta c " +
            " where (:esNullEntitatCodi = true or c.entitatCodi = :entitatCodi) " +
            "   and (:esNullProcedimentCodi = true or c.procedimentCodi = :procedimentCodi) " +
            "   and (:esNullServeiCodi = true or c.serveiCodi = :serveiCodi) " +
            "   and (:esNullDataInici = true or c.data >= :dataInici) " +
            "   and (:esNullDataFi = true or c.data <= :dataFi) " +
            "   and (c.multiple = false) " +
            " order by " +
            "c.data asc")
    public Page<DadesObertesRespostaConsulta> findByOpendata(
            @Param("esNullEntitatCodi") boolean esNullEntitatId,
            @Param("entitatCodi") String entitatCodi,
            @Param("esNullProcedimentCodi") boolean esNullProcedimentCodi,
            @Param("procedimentCodi") String procedimentCodi,
            @Param("esNullServeiCodi") boolean esNullServeiCodi,
            @Param("serveiCodi") String serveiCodi,
            @Param("esNullDataInici") boolean esNullDataInici,
            @Param("dataInici") Date dataInici,
            @Param("esNullDataFi") boolean esNullDataFi,
            @Param("dataFi") Date dataFi,
            Pageable pageable);

    @Query(	"select " +
            "    new es.caib.pinbal.client.dadesobertes.DadesObertesRespostaConsulta(" +
            "        c.entitatCodi, " +
            "        c.entitatNom, " +
            "        c.entitatNif, " +
            "        c.entitatTipus, " +
            "        c.departamentCodi, " +
            "        c.departamentNom, " +
            "        c.procedimentCodi, " +
            "        c.procedimentNom, " +
            "        c.serveiCodi, " +
            "        c.serveiNom, " +
            "        c.emissorNom, " +
            "        c.emissorNif, " +
            "        c.consentiment, " +
            "        c.finalitat, " +
            "        c.titularDocumentTipus, " +
            "        c.solicitudId, " +
            "        c.data, " +
            "        c.tipus, " +
            "        c.resultat) " +
            "from" +
            "    DadesObertesHistoricConsulta c " +
            " where (:esNullEntitatCodi = true or c.entitatCodi = :entitatCodi) " +
            "   and (:esNullProcedimentCodi = true or c.procedimentCodi = :procedimentCodi) " +
            "   and (:esNullServeiCodi = true or c.serveiCodi = :serveiCodi) " +
            "   and (:esNullDataInici = true or c.data >= :dataInici) " +
            "   and (:esNullDataFi = true or c.data <= :dataFi) " +
            "   and (c.multiple = false) " +
            " order by " +
            "c.data asc")
    public List<DadesObertesRespostaConsulta> findByOpendata(
            @Param("esNullEntitatCodi") boolean esNullEntitatId,
            @Param("entitatCodi") String entitatCodi,
            @Param("esNullProcedimentCodi") boolean esNullProcedimentCodi,
            @Param("procedimentCodi") String procedimentCodi,
            @Param("esNullServeiCodi") boolean esNullServeiCodi,
            @Param("serveiCodi") String serveiCodi,
            @Param("esNullDataInici") boolean esNullDataInici,
            @Param("dataInici") Date dataInici,
            @Param("esNullDataFi") boolean esNullDataFi,
            @Param("dataFi") Date dataFi);


    @Modifying
    @Query(value = 	"insert into pbl_consulta_hist_do(id, entitatCodi, entitatNom, entitatNif, entitatTipus, departamentCodi, departamentNom, procedimentCodi, procedimentNom, serveiCodi, serveiNom, emissorNom, emissorNif, consentiment, finalitat, titularDoctip, solicitudId, data, tipus, resultat, multiple) " +
            "select id, entitatCodi, entitatNom, entitatNif, entitatTipus, departamentCodi, departamentNom, procedimentCodi, procedimentNom, serveiCodi, serveiNom, emissorNom, emissorNif, consentiment, finalitat, titularDoctip, solicitudId, data, tipus, resultat, multiple " +
            "from pbl_consulta_do where data < current_date - :dies ",
            nativeQuery = true)
    public int arxivaConsultesOracle(@Param("dies") int dies);

    @Modifying
    @Query(value = 	"insert into pbl_consulta_hist_do(id, entitatCodi, entitatNom, entitatNif, entitatTipus, departamentCodi, departamentNom, procedimentCodi, procedimentNom, serveiCodi, serveiNom, emissorNom, emissorNif, consentiment, finalitat, titularDoctip, solicitudId, data, tipus, resultat, multiple) " +
            "select id, entitatCodi, entitatNom, entitatNif, entitatTipus, departamentCodi, departamentNom, procedimentCodi, procedimentNom, serveiCodi, serveiNom, emissorNom, emissorNif, consentiment, finalitat, titularDoctip, solicitudId, data, tipus, resultat, multiple " +
            "from pbl_consulta_do where data < current_date - INTERVAL ':dies days' ",
            nativeQuery = true)
    public int arxivaConsultesPostgres(@Param("dies") int dies);

    @Modifying
    @Query(value = "delete DadesObertesConsulta where data < current_date - :dies")
    public int purgaConsultes(@Param("dies") int dies);

}
