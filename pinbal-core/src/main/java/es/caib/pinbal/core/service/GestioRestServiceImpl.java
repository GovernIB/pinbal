package es.caib.pinbal.core.service;

import es.caib.pinbal.client.procediments.ClaseTramite;
import es.caib.pinbal.client.procediments.Procediment;
import es.caib.pinbal.client.procediments.ProcedimentPatch;
import es.caib.pinbal.client.serveis.Servei;
import es.caib.pinbal.client.usuaris.FiltreUsuaris;
import es.caib.pinbal.client.usuaris.PermisosServei;
import es.caib.pinbal.client.usuaris.ProcedimentServei;
import es.caib.pinbal.client.usuaris.UsuariEntitat;
import es.caib.pinbal.core.dto.EntitatDto;
import es.caib.pinbal.core.dto.EntitatUsuariDto;
import es.caib.pinbal.core.dto.FiltreActiuEnumDto;
import es.caib.pinbal.core.dto.OrganGestorDto;
import es.caib.pinbal.core.dto.PaginacioAmbOrdreDto;
import es.caib.pinbal.core.dto.ProcedimentClaseTramiteEnumDto;
import es.caib.pinbal.core.dto.ProcedimentDto;
import es.caib.pinbal.core.dto.ProcedimentServeiDto;
import es.caib.pinbal.core.dto.ProcedimentServeiSimpleDto;
import es.caib.pinbal.core.dto.ServeiDto;
import es.caib.pinbal.core.dto.UsuariDto;
import es.caib.pinbal.core.model.Entitat;
import es.caib.pinbal.core.model.OrganGestor;
import es.caib.pinbal.core.model.Usuari;
import es.caib.pinbal.core.repository.EntitatRepository;
import es.caib.pinbal.core.repository.OrganGestorRepository;
import es.caib.pinbal.core.repository.ProcedimentRepository;
import es.caib.pinbal.core.repository.UsuariRepository;
import es.caib.pinbal.core.service.exception.EntitatNotFoundException;
import es.caib.pinbal.core.service.exception.EntitatUsuariNotFoundException;
import es.caib.pinbal.core.service.exception.MultiplesUsuarisExternsException;
import es.caib.pinbal.core.service.exception.OrganNotFoundException;
import es.caib.pinbal.core.service.exception.ProcedimentNotFoundException;
import es.caib.pinbal.core.service.exception.ProcedimentServeiNotFoundException;
import es.caib.pinbal.core.service.exception.ServeiNotFoundException;
import es.caib.pinbal.core.service.exception.UsuariExternNotFoundException;
import es.caib.pinbal.core.service.exception.UsuariNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class GestioRestServiceImpl implements GestioRestService {

    @Resource
    private ProcedimentRepository procedimentRepository;
    @Resource
    private EntitatRepository entitatRepository;
    @Resource
    private OrganGestorRepository organGestorRepository;
    @Resource
    private UsuariRepository usuariRepository;

    @Resource
    private ProcedimentService procedimentService;
    @Resource
    private ServeiService serveiService;
    @Resource
    private UsuariService usuariService;


    @Override
    public Procediment create(Procediment procediment) throws EntitatNotFoundException, OrganNotFoundException {

        try {
            ProcedimentDto creat = procedimentService.create(toProcedimentDto(procediment));
            return toProcediment(creat);
        } catch (Exception e) {
            log.error("S'ha produït un error al crear el procediment " + procediment.toString() + " : " + e.getMessage());
            throw e;
        }
    }

    @Override
    public Procediment update(Procediment procediment) throws EntitatNotFoundException, ProcedimentNotFoundException, OrganNotFoundException {
        
        ProcedimentDto creat = procedimentService.update(toProcedimentDto(procediment));
        return toProcediment(creat);
    }

    @Override
    @Transactional //(rollbackFor = {EntitatNotFoundException.class, ProcedimentNotFoundException.class})
    public Procediment updateParcial(Long procedimentId, ProcedimentPatch procedimentPatch) throws EntitatNotFoundException, ProcedimentNotFoundException, OrganNotFoundException {
        es.caib.pinbal.core.model.Procediment procediment = procedimentRepository.findOne(procedimentId);
        if (procediment == null) {
            throw new ProcedimentNotFoundException(procedimentId.toString()); // "Procediment no trobat amb ID: " + procedimentId);
        }

        if (procedimentPatch.getCodi() != null && procedimentPatch.getCodi().isShouldUpdate()) {
            procediment.setCodi(procedimentPatch.getCodi().getValue());
        }
        if (procedimentPatch.getNom() != null && procedimentPatch.getNom().isShouldUpdate()) {
            procediment.setNom(procedimentPatch.getNom().getValue());
        }
        if (procedimentPatch.getDepartament() != null && procedimentPatch.getDepartament().isShouldUpdate()) {
            procediment.setDepartament(procedimentPatch.getDepartament().getValue());
        }
        if (procedimentPatch.getCodiSia() != null && procedimentPatch.getCodiSia().isShouldUpdate()) {
            procediment.setCodiSia(procedimentPatch.getCodiSia().getValue());
        }
        if (procedimentPatch.getValorCampAutomatizado() != null && procedimentPatch.getValorCampAutomatizado().isShouldUpdate()) {
            procediment.setValorCampAutomatizado(procedimentPatch.getValorCampAutomatizado().getValue());
        }
        if (procedimentPatch.getValorCampClaseTramite() != null && procedimentPatch.getValorCampClaseTramite().isShouldUpdate()) {
            procediment.setValorCampClaseTramite(
                    procedimentPatch.getValorCampClaseTramite().getValue() != null ? ProcedimentClaseTramiteEnumDto.valueOf(procedimentPatch.getValorCampClaseTramite().getValue().name()) : null);
        }
        if (procedimentPatch.getActiu() != null && procedimentPatch.getActiu().isShouldUpdate() && procedimentPatch.getActiu().getValue() != null) {
            procediment.setActiu(procedimentPatch.getActiu().getValue());
        }
        if (procedimentPatch.getOrganGestorDir3() != null && procedimentPatch.getOrganGestorDir3().isShouldUpdate()) {
            OrganGestor organGestor = null;
            if (procedimentPatch.getOrganGestorDir3().getValue() != null) {
                organGestor = organGestorRepository.findByCodiAndEntitat(procedimentPatch.getOrganGestorDir3().getValue(), procediment.getEntitat());
                if (organGestor == null) {
                    throw new OrganNotFoundException(procedimentPatch.getOrganGestorDir3().getValue());
                }
            }
            procediment.setOrganGestor(organGestor);
        }

        procediment = procedimentRepository.save(procediment);

        return Procediment.builder()
                .id(procediment.getId())
                .codi(procediment.getCodi())
                .nom(procediment.getNom())
                .departament(procediment.getDepartament())
                .entitatCodi(procediment.getEntitat().getCodi())
                .organGestorDir3(procediment.getOrganGestor().getCodi())
                .codiSia(procediment.getCodiSia())
                .valorCampAutomatizado(procediment.getValorCampAutomatizado())
                .valorCampClaseTramite(procediment.getValorCampClaseTramite() != null ? ClaseTramite.valueOf(procediment.getValorCampClaseTramite().name()) : null)
                .actiu(procediment.isActiu())
                .build();
    }

    @Override
    public void serveiEnable(Long procedimentId, String serveiCodi) throws ProcedimentNotFoundException, ServeiNotFoundException {
        try {
            procedimentService.serveiEnable(procedimentId, serveiCodi);
        } catch (ProcedimentNotFoundException e) {
            throw new ProcedimentNotFoundException(procedimentId.toString());
        } catch (ServeiNotFoundException e) {
            throw new ServeiNotFoundException(serveiCodi);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Procediment> findProcedimentsPaginat(String entitatCodi, String codi, String nom, String organGestor, Pageable pageable) throws EntitatNotFoundException, OrganNotFoundException {
        Entitat entitat = entitatRepository.findByCodi(entitatCodi);
        if (entitat == null)
            throw new EntitatNotFoundException(entitatCodi);
        Long organGestorId = null;
        if (organGestor != null && !organGestor.isEmpty()) {
            OrganGestor organ = organGestorRepository.findByCodiAndEntitat(organGestor, entitat);
            if (organ == null) {
                throw new OrganNotFoundException(organGestor);
            }
            organGestorId = organ.getId();
        }

        PaginacioAmbOrdreDto paginacio = new PaginacioAmbOrdreDto();
        paginacio.setPaginaNum(pageable.getPageNumber());
        paginacio.setPaginaTamany(pageable.getPageSize());
//        paginacio.setFiltre(codi);
//        paginacio.afegirFiltre("codi", codi);
        if (pageable.getSort() != null) {
            for (Order order : pageable.getSort()) {
                paginacio.afegirOrdre(order.getProperty(), Sort.Direction.DESC.equals(order.getDirection()) ? PaginacioAmbOrdreDto.OrdreDireccioDto.DESCENDENT : PaginacioAmbOrdreDto.OrdreDireccioDto.ASCENDENT);
            }
        }
        Page<ProcedimentDto> procedimentDtoPage = procedimentService.findAmbFiltrePaginat(entitat.getId(), codi, nom, null, organGestorId, null, FiltreActiuEnumDto.ACTIU, paginacio);

        if (procedimentDtoPage == null) {
            return null;
        }
        return convertToProcediments(procedimentDtoPage);
    }

    @Override
    @Transactional(readOnly = true)
    public Procediment getProcedimentById(Long procedimentId) {
        return toProcediment(procedimentService.findById(procedimentId));
    }

    @Override
    @Transactional(readOnly = true)
    public Procediment getProcedimentAmbEntitatICodi(String entitatCodi, String procedimentCodi) throws EntitatNotFoundException {
        Entitat entitat = entitatRepository.findByCodi(entitatCodi);
        if (entitat == null)
            throw new EntitatNotFoundException(entitatCodi);
        return toProcediment(procedimentService.findAmbEntitatICodi(entitat.getId(), procedimentCodi));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Servei> findServeisByProcedimentPaginat(Long procedimentId, Pageable pageable) throws ProcedimentNotFoundException {

        es.caib.pinbal.core.model.Procediment procediment = procedimentRepository.getOne(procedimentId);
        if (procediment == null) {
            throw new ProcedimentNotFoundException(procedimentId.toString());
        }
        ProcedimentDto procedimentDto = ProcedimentDto.builder().id(procediment.getId()).build();
        Entitat entitat = procediment.getEntitat();
        EntitatDto entitatDto = EntitatDto.builder().id(entitat.getId()).codi(entitat.getCodi()).build();

        Page<ServeiDto> serveiDtoPage = serveiService.findAmbFiltrePaginat(null, null, null, true, entitatDto, procedimentDto, pageable);

        if (serveiDtoPage == null) {
            return null;
        }
        return convertToServeis(serveiDtoPage);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Servei> findServeisPaginat(String codi, String descripcio, Pageable pageable) {

        Page<ServeiDto> serveiDtoPage = serveiService.findAmbFiltrePaginat(codi, descripcio, null, true, null, pageable);

        if (serveiDtoPage == null) {
            return null;
        }
        return convertToServeis(serveiDtoPage);
    }

    @Override
    @Transactional(readOnly = true)
    public Servei getServeiByCodi(String serveiCodi) {
        ServeiDto serveiDto = null;
        try {
            serveiDto = serveiService.findAmbCodiPerAdminORepresentant(serveiCodi);
        } catch (ServeiNotFoundException e) {
            return null;
        }
        return toServei(serveiDto);
    }

    @Override
    @Transactional
    public void createOrUpdateUsuari(UsuariEntitat usuariEntitat) throws Exception {

        Entitat entitat = entitatRepository.findByCodi(usuariEntitat.getEntitatCodi());
        if (entitat == null)
            throw new EntitatNotFoundException(usuariEntitat.getEntitatCodi());

        UsuariDto usuariDto = null;
        if (usuariEntitat.getCodi() != null && !usuariEntitat.getCodi().trim().isEmpty()) {
            usuariDto = usuariService.getUsuariExtern(usuariEntitat.getCodi());
        }

        if (usuariDto == null) {
            String text = usuariEntitat.getNom();
            if (usuariEntitat.getNif() != null && !usuariEntitat.getNif().trim().isEmpty()) {
                text = usuariEntitat.getNif();
            }
            if (usuariEntitat.getCodi() != null && !usuariEntitat.getCodi().trim().isEmpty()) {
                text = usuariEntitat.getCodi();
            }
            List<UsuariDto> usuarisExterns = usuariService.getUsuarisExterns(text);

            if (usuarisExterns == null || usuarisExterns.size() == 0) {
                throw new UsuariExternNotFoundException(text);
            }
            if (usuarisExterns.size() > 1) {
                throw new MultiplesUsuarisExternsException();
            }
            usuariDto = usuarisExterns.get(0);
        }

        usuariService.actualitzarDadesAdmin(
                entitat.getId(),
                usuariDto.getCodi(),
                null,
                usuariEntitat.getDepartament(),
                usuariEntitat.isRepresentant(),
                usuariEntitat.isDelegat(),
                usuariEntitat.isAuditor(),
                usuariEntitat.isAplicacio(),
                false,
                usuariEntitat.isActiu());
    }

    @Override
    @Transactional(readOnly = true)
    public UsuariEntitat getUsuariAmbEntitatICodi(String entitatCodi, String usuariCodi) throws EntitatNotFoundException {

        Entitat entitat = entitatRepository.findByCodi(entitatCodi);
        if (entitat == null)
            throw new EntitatNotFoundException(entitatCodi);

        EntitatUsuariDto entitatUsuari = usuariService.getEntitatUsuari(entitat.getId(), usuariCodi);

        return toUsuari(entitatCodi, entitatUsuari);
    }

    @Override
//    @Transactional(noRollbackFor = {EntitatNotFoundException.class, EntitatUsuariNotFoundException.class, ProcedimentServeiNotFoundException.class})
    public void serveiGrantPermis(PermisosServei permisosServei) throws EntitatNotFoundException, EntitatUsuariNotFoundException, ProcedimentServeiNotFoundException {

        Entitat entitat = entitatRepository.findByCodi(permisosServei.getEntitatCodi());
        if (entitat == null)
            throw new EntitatNotFoundException(permisosServei.getEntitatCodi());

        List<ProcedimentServeiSimpleDto> procedimentServeiSimpleDtos = new ArrayList<>();
        for (es.caib.pinbal.client.usuaris.ProcedimentServei procedimentServei: permisosServei.getProcedimentServei()) {
            procedimentServeiSimpleDtos.add(ProcedimentServeiSimpleDto.builder()
                    .procedimentCodi(procedimentServei.getProcedimentCodi())
                    .serveiCodi(procedimentServei.getServeiCodi())
                    .build());
        }

        try {
            procedimentService.serveiPermisAllowSelected(
                    permisosServei.getUsuariCodi(),
                    procedimentServeiSimpleDtos,
                    entitat.getId());
        } catch (EntitatUsuariNotFoundException | ProcedimentServeiNotFoundException e) {
            throw e;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public PermisosServei permisosPerUsuariEntitat(String entitatCodi, String usuariCodi) throws EntitatNotFoundException, UsuariNotFoundException {

        Entitat entitat = entitatRepository.findByCodi(entitatCodi);
        if (entitat == null)
            throw new EntitatNotFoundException(entitatCodi);
        Usuari usuari = usuariRepository.findByCodi(usuariCodi);
        if (usuari == null)
            throw new UsuariNotFoundException(usuariCodi);

        List<ProcedimentServeiDto> permesosAmbEntitatIUsuari = serveiService.findPermesosAmbEntitatIUsuari(entitat.getId(), usuariCodi);

        return toPermisosServei(entitatCodi, usuariCodi, permesosAmbEntitatIUsuari);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UsuariEntitat> findUsuarisPaginat(String entitatCodi, FiltreUsuaris filtreUsuaris, Pageable pageable) throws EntitatNotFoundException {

        Entitat entitat = entitatRepository.findByCodi(entitatCodi);
        if (entitat == null)
            throw new EntitatNotFoundException(entitatCodi);

        Page<EntitatUsuariDto> usuariPage = usuariService.findAmbFiltrePaginat(
                entitat.getId(),
                filtreUsuaris.getIsRepresentant(),
                filtreUsuaris.getIsDelegat(),
                filtreUsuaris.getIsAuditor(),
                filtreUsuaris.getIsAplicacio(),
                filtreUsuaris.getCodi(),
                filtreUsuaris.getNom(),
                filtreUsuaris.getNif(),
                filtreUsuaris.getDepartament(),
                pageable);
        return convertToUsuaris(entitatCodi, usuariPage);
    }

    private ProcedimentDto toProcedimentDto(Procediment procediment) throws EntitatNotFoundException, OrganNotFoundException {
        if (procediment == null)
            return null;

        Entitat entitat = entitatRepository.findByCodi(procediment.getEntitatCodi());
        if (entitat == null)
            throw new EntitatNotFoundException(procediment.getEntitatCodi());
        OrganGestor organGestor = null;
//        if (procediment.getOrganGestorDir3() != null && !procediment.getOrganGestorDir3().isEmpty()) {
        organGestor = organGestorRepository.findByCodiAndEntitat(procediment.getOrganGestorDir3(), entitat);
        if (organGestor == null)
            throw new OrganNotFoundException(procediment.getOrganGestorDir3());
//        }
        ProcedimentClaseTramiteEnumDto claseTramite = procediment.getValorCampClaseTramite() != null ? ProcedimentClaseTramiteEnumDto.valueOf(procediment.getValorCampClaseTramite().name()) : null;
        ProcedimentDto procedimentDto = ProcedimentDto.builder()
                .id( procediment.getId() )
                .entitatId(entitat.getId())
                .codi( procediment.getCodi() )
                .nom( procediment.getNom() )
                .departament( procediment.getDepartament() )
                .organGestor( OrganGestorDto.builder().id(organGestor.getId()).build() )
                .codiSia( procediment.getCodiSia() )
                .valorCampAutomatizado( procediment.getValorCampAutomatizado() )
                .valorCampClaseTramite( claseTramite )
                .actiu(procediment.isActiu())
                .build();
        return procedimentDto;
    }

    private Procediment toProcediment(ProcedimentDto procedimentDto) {
        if (procedimentDto == null)
            return null;

        Entitat entitat = entitatRepository.findOne(procedimentDto.getEntitatId());

        return Procediment.builder()
                .id(procedimentDto.getId())
                .codi(procedimentDto.getCodi())
                .nom(procedimentDto.getNom())
                .departament(procedimentDto.getDepartament())
                .entitatCodi(entitat.getCodi())
                .organGestorDir3(procedimentDto.getOrganGestor().getCodi())
                .codiSia(procedimentDto.getCodiSia())
                .valorCampAutomatizado(procedimentDto.getValorCampAutomatizado())
                .valorCampClaseTramite(procedimentDto.getValorCampClaseTramite() != null ? ClaseTramite.valueOf(procedimentDto.getValorCampClaseTramite().name()) : null)
                .actiu(procedimentDto.isActiu())
                .build();
    }

    private Page<Procediment> convertToProcediments(Page<ProcedimentDto> procedimentDtos) {
        List<Procediment> procedimentsList = new ArrayList<Procediment>();

        for (ProcedimentDto dto : procedimentDtos.getContent()) {
            procedimentsList.add(toProcediment(dto));
        }

        Pageable pageable = new PageRequest(procedimentDtos.getNumber(), procedimentDtos.getSize(), procedimentDtos.getSort());
        return new PageImpl<Procediment>(procedimentsList, pageable, procedimentDtos.getTotalElements());
    }

    private Servei toServei(ServeiDto serveiDto) {
        if (serveiDto == null)
            return null;

        return Servei.builder()
                .codi(serveiDto.getCodi())
                .descripcio(serveiDto.getDescripcio())
                .emisor(serveiDto.getScspEmisorNom())
                .actiu(serveiDto.getActiu())
                .build();
    }

    private Page<Servei> convertToServeis(Page<ServeiDto> serveiDtos) {
        List<Servei> serveisList = new ArrayList<Servei>();

        for (ServeiDto dto : serveiDtos.getContent()) {
            serveisList.add(toServei(dto));
        }

        Pageable pageable = new PageRequest(serveiDtos.getNumber(), serveiDtos.getSize(), serveiDtos.getSort());
        return new PageImpl<Servei>(serveisList, pageable, serveiDtos.getTotalElements());
    }

    private UsuariEntitat toUsuari(String entitatCodi, EntitatUsuariDto entitatUsuariDto) {
        if (entitatUsuariDto == null)
            return null;

        return UsuariEntitat.builder()
                .entitatCodi(entitatCodi)
                .codi(entitatUsuariDto.getUsuari().getCodi())
                .nif(entitatUsuariDto.getUsuari().getNif())
                .nom(entitatUsuariDto.getUsuari().getNom())
                .departament(entitatUsuariDto.getDepartament())
                .representant(entitatUsuariDto.isRepresentant())
                .delegat(entitatUsuariDto.isDelegat())
                .auditor(entitatUsuariDto.isAuditor())
                .aplicacio(entitatUsuariDto.isAplicacio())
                .actiu(entitatUsuariDto.isActiu())
                .build();
    }

    private Page<UsuariEntitat> convertToUsuaris(String entitatCodi, Page<EntitatUsuariDto> usuariPage) {
        List<UsuariEntitat> usuarisList = new ArrayList<>();

        for (EntitatUsuariDto dto : usuariPage.getContent()) {
            usuarisList.add(toUsuari(entitatCodi, dto));
        }

        Pageable pageable = new PageRequest(usuariPage.getNumber(), usuariPage.getSize(), usuariPage.getSort());
        return new PageImpl<UsuariEntitat>(usuarisList, pageable, usuariPage.getTotalElements());
        
    }

    private static PermisosServei toPermisosServei(String entitatCodi, String usuariCodi, List<ProcedimentServeiDto> permesosAmbEntitatIUsuari) {
        List<ProcedimentServei> procedimentServeis = new ArrayList<>();
        for(ProcedimentServeiDto procedimentServeiDto: permesosAmbEntitatIUsuari) {
            procedimentServeis.add(ProcedimentServei.builder()
                    .procedimentCodi(procedimentServeiDto.getProcediment().getCodi())
                    .serveiCodi(procedimentServeiDto.getServei().getCodi())
                    .build());
        }
        PermisosServei permisosServei = PermisosServei.builder()
                .entitatCodi(entitatCodi)
                .usuariCodi(usuariCodi)
                .procedimentServei(procedimentServeis)
                .build();
        return permisosServei;
    }
}
