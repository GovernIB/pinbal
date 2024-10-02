package es.caib.pinbal.core.helper;

import es.caib.pinbal.client.dadesobertes.DadesObertesRespostaConsulta.DadesObertesConsultaResultat;
import es.caib.pinbal.client.dadesobertes.DadesObertesRespostaConsulta.DadesObertesConsultaTipus;
import es.caib.pinbal.core.dto.EstatTipus;
import es.caib.pinbal.core.model.Consulta;
import es.caib.pinbal.core.model.EmissorCert;
import es.caib.pinbal.core.model.Entitat;
import es.caib.pinbal.core.model.Procediment;
import es.caib.pinbal.core.model.Servei;
import es.caib.pinbal.core.model.Transmision;
import es.caib.pinbal.core.model.dadesobertes.DadesObertesConsulta;
import es.caib.pinbal.core.model.llistat.LlistatConsulta;
import es.caib.pinbal.core.repository.dadesobertes.DadesObertesConsultaRepository;
import es.caib.pinbal.core.repository.llistat.LlistatConsultaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ConsultaHelper {

    @Autowired
    private LlistatConsultaRepository llistatConsultaRepository;
    @Autowired
    private DadesObertesConsultaRepository dadesObertesConsultaRepository;


    public void propagaCreacioConsulta(Consulta consulta) {

        Entitat entitat = consulta.getEntitat();
        Procediment procediment = consulta.getProcediment();
        Servei servei = consulta.getProcedimentServei().getServeiScsp();
        Transmision transmision = consulta.getTransmision();
        EmissorCert emisor = servei.getScspEmisor();

        LlistatConsulta llistatConsulta = LlistatConsulta.builder()
                .id(consulta.getId())
                .peticioId(consulta.getScspPeticionId())
                .solicitudId(consulta.getScspSolicitudId())
                .data(consulta.getCreatedDate().toDate())
                .departamentNom(consulta.getDepartamentNom())
                .recobriment(consulta.isRecobriment())
                .multiple(consulta.isMultiple())
                .usuariCodi(consulta.getCreatedBy().getCodi())
                .usuariNom(consulta.getCreatedBy().getNom())
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
                .data(consulta.getCreatedDate().toDate())
                .tipus(consulta.isRecobriment() ? DadesObertesConsultaTipus.RECOBRIMENT : DadesObertesConsultaTipus.WEB)
                .resultat(getResultat(consulta.getEstat()))
                .multiple(consulta.isMultiple())
                .build();
        dadesObertesConsultaRepository.save(dadesObertesConsulta);
    }

    public void propagaCanviConsulta(Consulta consulta) {

        // Per assegurar qeu no actualitzem una consulta no creada... la crearem si no existeix
        LlistatConsulta llistatConsulta = llistatConsultaRepository.findOne(consulta.getId());
        if (llistatConsulta == null) {
            propagaCreacioConsulta(consulta);
            return;
        }

        llistatConsulta.update(
                consulta.getEstat(),
                consulta.getScspSolicitudId(),
                consulta.getError());
        llistatConsultaRepository.save(llistatConsulta);
        DadesObertesConsulta dadesObertesConsulta = dadesObertesConsultaRepository.findOne(consulta.getId());
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
}
