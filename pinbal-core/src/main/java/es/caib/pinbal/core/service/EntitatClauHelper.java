package es.caib.pinbal.core.service;

import es.caib.pinbal.core.helper.DtoMappingHelper;
import es.caib.pinbal.core.model.ClauPrivada;
import es.caib.pinbal.core.model.Entitat;
import es.caib.pinbal.core.model.EntitatServei;
import es.caib.pinbal.core.model.ServeiConfig;
import es.caib.pinbal.core.repository.ClauPrivadaRepository;
import es.caib.pinbal.core.repository.EntitatRepository;
import es.caib.pinbal.core.repository.EntitatServeiRepository;
import es.caib.pinbal.core.repository.ServeiConfigRepository;
import es.caib.pinbal.core.service.exception.EntitatNotFoundException;
import es.caib.pinbal.scsp.ScspHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
public class EntitatClauHelper implements ApplicationContextAware, MessageSourceAware {

    @Autowired
    private EntitatRepository entitatRepository;
    @Autowired
    private ServeiConfigRepository serveiConfigRepository;
    @Autowired
    private EntitatServeiRepository entitatServeiRepository;
    @Autowired
    private ClauPrivadaRepository clauPrivadaRepository;

    @Autowired
    private DtoMappingHelper dtoMappingHelper;

    private ApplicationContext applicationContext;
    private MessageSource messageSource;
    private ScspHelper scspHelper;

    @Override
    public void setApplicationContext(
            ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void setMessageSource(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    private ScspHelper getScspHelper() {
        if (scspHelper == null) {
            scspHelper = new ScspHelper(
                    applicationContext,
                    messageSource);
        }
        return scspHelper;
    }

    /**
     * Sincronitza un certificat d'entitat amb tots els serveis
     * que tenen useCertificatEntitat = true
     */
    public void sincronitzarAmbServeis(ClauPrivada clauPrivada, String organismeCif) throws EntitatNotFoundException {

        Entitat entitat = entitatRepository.findByCif(organismeCif);
        if (entitat == null) {
            throw new EntitatNotFoundException(organismeCif);
        }

        // Obtenir serveis amb useCertificatEntitat = true
        List<ServeiConfig> serveisAmbCertificatPerEntitat = serveiConfigRepository.findByUseCertificatEntitatTrueAndEntitat(entitat.getId());
        if (serveisAmbCertificatPerEntitat == null || serveisAmbCertificatPerEntitat.isEmpty()) {
            return;
        }

        for (ServeiConfig servei : serveisAmbCertificatPerEntitat) {
            try {
                scspHelper.assignarCertificatAServei(
                        entitat.getCif(),
                        servei.getServei(),
                        clauPrivada.getAlies()
                );
                log.debug("Assignat certificat al servei {}", servei.getServei());
            } catch (Exception e) {
                log.error("Error assignant certificat al servei {}: {}", servei.getServei(), e.getMessage(), e);
            }
        }
    }

    /**
     * Elimina els ServicioOrganismoCesionario associats a un certificat
     */
    public void actualitzarServiciosOrganismos(ClauPrivada clauPrivada, String entitatCif) {

        Entitat entitat = entitatRepository.findByCif(entitatCif);


        // Obtenir serveis actius
        List<EntitatServei> entitatServeis = entitatServeiRepository.findByEntitat(entitat);
        Set<String> serveisActius = new HashSet<>();
        for (int i = 0; i < entitatServeis.size(); i++) {
            serveisActius.add(entitatServeis.get(i).getServei());
        }

        // Obtenir clau privada de l'entitat
        if (clauPrivada != null) {
            clauPrivada = clauPrivadaRepository.findTopByOrganismeCifAndPerEntitatTrueOrderByDataAltaDesc(entitat.getCif());
        }
        String aliesClauFirmaEntitat = clauPrivada != null ? clauPrivada.getAlies() : null;

        // Actualitzar els serveis per òrgan SCSP
        getScspHelper().actualitzarServiciosActivosOrganismoCesionario(
                entitat.getCif(),
                serveisActius,
                aliesClauFirmaEntitat);
    }

    public void resetClauServiciosOrganismos(ClauPrivada clauPrivada) {

        String cif = clauPrivada.getOrganisme().getCif();

        // Actiualitzar la clau dels ServicioOrganismoCesionario per cada entitat que tingui assignat el servei
        // S'utilitza la claveFirma del Servicio
        Entitat entitat = entitatRepository.findByCif(cif);
        List<EntitatServei> entitatServeis = entitatServeiRepository.findByEntitat(entitat);
        for (EntitatServei entitatServei : entitatServeis) {
            scspHelper.assignarDefaultCertificatAServei(cif, entitatServei.getServei());
        }

    }
}
