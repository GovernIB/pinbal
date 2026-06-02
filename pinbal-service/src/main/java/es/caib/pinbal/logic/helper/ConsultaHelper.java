package es.caib.pinbal.logic.helper;

import es.caib.pinbal.client.dadesobertes.DadesObertesRespostaConsulta.DadesObertesConsultaResultat;
import es.caib.pinbal.client.dadesobertes.DadesObertesRespostaConsulta.DadesObertesConsultaTipus;
import es.caib.pinbal.logic.intf.dto.EstatTipus;
import es.caib.pinbal.logic.intf.dto.IntegracioAccioTipusEnumDto;
import es.caib.pinbal.persist.entity.Consulta;
import es.caib.pinbal.persist.entity.EmissorCert;
import es.caib.pinbal.persist.entity.Entitat;
import es.caib.pinbal.persist.entity.Procediment;
import es.caib.pinbal.persist.entity.ProcedimentServei;
import es.caib.pinbal.persist.entity.Servei;
import es.caib.pinbal.persist.entity.Transmision;
import es.caib.pinbal.persist.entity.Usuari;
import es.caib.pinbal.persist.entity.dadesobertes.DadesObertesConsulta;
import es.caib.pinbal.persist.entity.llistat.LlistatConsulta;
import es.caib.pinbal.persist.repository.ConsultaRepository;
import es.caib.pinbal.persist.repository.dadesobertes.DadesObertesConsultaRepository;
import es.caib.pinbal.persist.repository.llistat.LlistatConsultaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Component
public class ConsultaHelper {

    private final LlistatConsultaRepository llistatConsultaRepository;
    private final DadesObertesConsultaRepository dadesObertesConsultaRepository;
    private final ConsultaRepository consultaRepository;
    private final IntegracioHelper integracioHelper;


    public void propagaCreacioConsulta(Consulta consulta) {

        Entitat entitat = consulta.getEntitat();
        Procediment procediment = consulta.getProcediment();
        Servei servei = consulta.getProcedimentServei().getServeiScsp();
        Transmision transmision = consulta.getTransmision();
        EmissorCert emisor = servei.getScspEmisor();
        Usuari createdBy = consulta.getCreatedBy()
                .orElseThrow(() -> new IllegalStateException("La consulta " + consulta.getId() + " no te usuari de creacio"));

        LlistatConsulta llistatConsulta = LlistatConsulta.builder()
                .id(consulta.getId())
                .peticioId(consulta.getScspPeticionId())
                .solicitudId(consulta.getScspSolicitudId())
                .data(Date.from(consulta.getCreatedDate().orElseThrow().atZone(ZoneId.systemDefault()).toInstant()))
                .departamentNom(consulta.getDepartamentNom())
                .recobriment(consulta.isRecobriment())
                .multiple(consulta.isMultiple())
                .usuariCodi(createdBy.getCodi())
                .usuariNom(createdBy.getNom())
                .funcionariNom(consulta.getFuncionariNom())
                .funcionariNif(consulta.getFuncionariDocumentNum())
                .titularNom(consulta.getTitularNomSencer())
                .titularDocumentTipus(consulta.getTitularDocumentTipus())
                .titularDocumentNum(consulta.getTitularDocumentNum())
                .procedimentId(procediment.getId())
                .procedimentCodi(procediment.getCodi())
                .procedimentNom(procediment.getNom())
                .serveiCodi(servei.getCodi())
                .serveiNom(servei.getDescripcio())
                .estat(consulta.getEstat())
                .error(consulta.getError())
                .justificantEstat(consulta.getJustificantEstat())
                .entitatId(entitat.getId())
                .entitatCodi(entitat.getCodi())
                .pareId(consulta.getPare() != null ? consulta.getPare().getId() : null)
                .dataEsperadaResposta(consulta.getDataEsperadaResposta())
                .build();
        llistatConsultaRepository.save(llistatConsulta);

        DadesObertesConsulta dadesObertesConsulta = DadesObertesConsulta.builder()
                .id(consulta.getId())
                .entitatCodi(entitat.getCodi())
                .entitatNom(entitat.getNom())
                .entitatNif(entitat.getCif())
                .entitatTipus(entitat.getTipus().name())
                .departamentCodi(transmision != null ? transmision.getCodigoUnidadTramitadora() : null)
//                .departamentNom(transmision != null ? transmision.getUnidadTramitadora() : null)
                .departamentNom(consulta.getDepartamentNom())
                .procedimentCodi(procediment.getCodi())
                .procedimentNom(procediment.getNom())
                .serveiCodi(servei.getCodi())
                .serveiNom(servei.getDescripcio())
                .emissorNom(emisor != null ? emisor.getNom() : null)
                .emissorNif(emisor != null ? emisor.getCif() : null)
                .consentiment(consulta.getConsentiment() != null ? consulta.getConsentiment().name() : null)
                .finalitat(getFinalitat(consulta.getFinalitat()))
                .titularDocumentTipus(consulta.getTitularDocumentTipus())
                .solicitudId(consulta.getScspSolicitudId())
                .data(Date.from(consulta.getCreatedDate().orElseThrow().atZone(ZoneId.systemDefault()).toInstant()))
                .tipus(consulta.isRecobriment() ? DadesObertesConsultaTipus.RECOBRIMENT : DadesObertesConsultaTipus.WEB)
                .resultat(getResultat(consulta.getEstat()))
                .multiple(consulta.isMultiple())
                .build();
        dadesObertesConsultaRepository.save(dadesObertesConsulta);
    }

    public void propagaCanviConsulta(Consulta consulta) {

        // Per assegurar qeu no actualitzem una consulta no creada... la crearem si no existeix
        LlistatConsulta llistatConsulta = llistatConsultaRepository.findById(consulta.getId()).orElse(null);
        if (llistatConsulta == null) {
            propagaCreacioConsulta(consulta);
            return;
        }

        llistatConsulta.update(
                consulta.getEstat(),
                consulta.getScspSolicitudId(),
                consulta.getError(),
                consulta.getDataEsperadaResposta());
        llistatConsultaRepository.save(llistatConsulta);
        DadesObertesConsulta dadesObertesConsulta = dadesObertesConsultaRepository.findById(consulta.getId()).orElseThrow();
        dadesObertesConsulta.update(
                getResultat(consulta.getEstat()),
                consulta.getScspSolicitudId());
        dadesObertesConsultaRepository.save(dadesObertesConsulta);
    }

    private static DadesObertesConsultaResultat getResultat(EstatTipus estat) {
        switch (estat) {
            case Pendent:
            case Processant:
                return DadesObertesConsultaResultat.PROCES;
            case Tramitada:
                return DadesObertesConsultaResultat.OK;
            case Error:
                return DadesObertesConsultaResultat.ERROR;
            default:
                return null;
        }
    }

    private static String getFinalitat(String finalitat) {
        if (finalitat == null) return null;

        int index = finalitat.lastIndexOf("#");
        if (index != -1) {
            return finalitat.substring(index + 1);
        } else {
            return finalitat;
        }
    }


    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void processarErrorConsulta(Long consultaId, String descripcio, Long temps, Exception ex) {
        Consulta consulta = consultaRepository.getOne(consultaId);
        if (consulta == null) {
            log.error("No s'ha trobat la consulta amb id: " + consultaId);
            return;
        }
        ProcedimentServei procedimentServei = consulta.getProcedimentServei();
        if (procedimentServei == null) {
            log.error("No s'ha trobat el procediment-servei de la consulta amb id: " + consultaId);
        }

        String error = "Error inesperat en l'enviament de la petició SCSP: " + ex.getMessage();
        Map<String, String> accioParams = new HashMap<String, String>();
        accioParams.put("consultaId", consulta.getId().toString());
        accioParams.put("procediment", procedimentServei.getProcediment() != null ? procedimentServei.getProcediment().getCodi() + " - " + procedimentServei.getProcediment().getNom() : "");
        accioParams.put("servei", procedimentServei.getServeiScsp() != null ? procedimentServei.getServeiScsp().getCodi() + " - " + procedimentServei.getServeiScsp().getDescripcio() : "");
        accioParams.put("idPeticion", consulta.getScspPeticionId());
        accioParams.put("idSolicitud", consulta.getScspSolicitudId());
        accioParams.put("estat", "[ERROR] " + error);

        consulta.updateEstatError(error);
        integracioHelper.addAccioError(
                consulta.getScspPeticionId(),
                IntegracioHelper.INTCODI_SERVEIS_SCSP,
                descripcio,
                accioParams,
                IntegracioAccioTipusEnumDto.ENVIAMENT,
                temps,
                error,
                ex);
        propagaCanviConsulta(consulta);
    }
}
