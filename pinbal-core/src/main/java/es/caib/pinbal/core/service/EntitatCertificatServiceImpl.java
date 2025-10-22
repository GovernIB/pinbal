package es.caib.pinbal.core.service;

import es.caib.pinbal.core.dto.EntitatCertificatDto;
import es.caib.pinbal.core.helper.DtoMappingHelper;
import es.caib.pinbal.core.model.ClauPrivada;
import es.caib.pinbal.core.model.Entitat;
import es.caib.pinbal.core.model.EntitatCertificat;
import es.caib.pinbal.core.model.ServeiConfig;
import es.caib.pinbal.core.repository.ClauPrivadaRepository;
import es.caib.pinbal.core.repository.EntitatCertificatRepository;
import es.caib.pinbal.core.repository.EntitatRepository;
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

import java.util.Collections;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class EntitatCertificatServiceImpl implements EntitatCertificatService, ApplicationContextAware, MessageSourceAware {

    @Autowired
    private EntitatCertificatRepository entitatCertificatRepository;
    @Autowired
    private EntitatRepository entitatRepository;
    @Autowired
    private ServeiConfigRepository serveiConfigRepository;
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

    @Override
    public EntitatCertificatDto create(String entitatCodi, String certificatAlias) throws EntitatNotFoundException {

        Entitat entitat = entitatRepository.findByCodi(entitatCodi);
        if (entitat == null) {
            throw new EntitatNotFoundException(entitatCodi);
        }

        // Validar que existeix la ClavePrivada
        ClauPrivada clau = clauPrivadaRepository.findByAlies(certificatAlias);
        if (clau == null) {
            throw new IllegalArgumentException("ClauPrivada no trobada: " + certificatAlias);
        }
        if (clau.isCaducada()) {
            throw new IllegalArgumentException("La clauPrivada (" + certificatAlias + ") ha caducat el " + clau.getDataBaixa().toString());
        }

        // Desactivar certificats anteriors de la mateixa entitat
        List<EntitatCertificat> anteriors = entitatCertificatRepository.findByEntitatOrderByDataAltaDesc(entitat);
        for (EntitatCertificat anterior : anteriors) {
            if (Boolean.TRUE.equals(anterior.getActiu())) {
                anterior.setActiu(false);
                anterior.setDataBaixa(new Date());
                entitatCertificatRepository.save(anterior);
            }
        }

        // Crear nou certificat
        EntitatCertificat entitatCertificat = EntitatCertificat.builder()
                .entitat(entitat)
                .clau(clau)
                .actiu(true)
                .build();
        EntitatCertificat desat = entitatCertificatRepository.save(entitatCertificat);

        // Sincronitzar amb serveis que usen certificat d'entitat
        sincronitzarAmbServeis(desat);

        return dtoMappingHelper.getMapperFacade().map(
                desat,
                EntitatCertificatDto.class);
    }


    @Override
    public EntitatCertificatDto update(Long id) {
        return null;
    }

    @Override
    public void delete(Long id) {
        EntitatCertificat cert = entitatCertificatRepository.findOne(id);
        if (cert == null) {
            throw new IllegalArgumentException("Certificat no trobat");
        }

        cert.setActiu(false);
        cert.setDataBaixa(new Date());
        entitatCertificatRepository.save(cert);

        // Eliminar SOC associats
        eliminarServiciosOrganismo(cert);
    }

    @Override
    public List<EntitatCertificatDto> findByFiltrePaginat(String entitatCodi, String alias) {
        return Collections.emptyList();
    }

    @Override
    public EntitatCertificatDto findById(Long id) {
        EntitatCertificat cert = entitatCertificatRepository.findOne(id);
        if (cert != null) {
            return dtoMappingHelper.getMapperFacade().map(
                    cert,
                    EntitatCertificatDto.class);
        }
        return null;
    }

    @Override
    public EntitatCertificatDto findByEntitat(String entitatCodi) {
        return null;
    }

    @Override
    public List<EntitatCertificatDto> findByEntitatId(Long entitatId) {
        List<EntitatCertificat> certificats = entitatCertificatRepository.findByEntitatIdOrderByDataAltaDesc(entitatId);
        return dtoMappingHelper.getMapperFacade().mapAsList(
          certificats,
          EntitatCertificatDto.class);
    }

    /**
     * Sincronitza un certificat d'entitat amb tots els serveis
     * que tenen useCertificatEntitat = true
     */
    private void sincronitzarAmbServeis(EntitatCertificat certificat) {

        Entitat entitat = certificat.getEntitat();

        // Assegurar OrganismoCesionario
        scspHelper.organismoCesionarioUpdate(
                entitat.getCif(),
                entitat.getNom(),
                false
        );

        // Obtenir serveis amb useCertificatEntitat = true
        List<ServeiConfig> serveis = serveiConfigRepository.findByUseCertificatEntitatTrueAndEntitat(entitat.getId());

        for (ServeiConfig servei : serveis) {
            try {
                scspHelper.assignarCertificatAServei(
                        entitat.getCif(),
                        servei.getServei(),
                        certificat.getClau().getAlies()
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
    private void eliminarServiciosOrganismo(EntitatCertificat certificat) {

        Entitat entitat = certificat.getEntitat();

        List<ServeiConfig> serveisConfig = serveiConfigRepository
                .findByUseCertificatEntitatTrueAndEntitat(entitat.getId());

        for (ServeiConfig serveiConfig : serveisConfig) {
            try {
                scspHelper.eliminarServicioOrganismo(
                        entitat.getCif(),
                        serveiConfig.getServei()
                );
            } catch (Exception e) {
                log.error("Error eliminant SOC per servei {}: {}", serveiConfig.getServei(), e.getMessage(), e);
            }
        }
    }
}
