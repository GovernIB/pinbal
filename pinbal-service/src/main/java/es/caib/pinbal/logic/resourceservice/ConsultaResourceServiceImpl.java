package es.caib.pinbal.logic.resourceservice;

import es.caib.pinbal.logic.base.helper.AuthenticationHelper;
import es.caib.pinbal.logic.base.service.BaseNoDatabaseReadonlyResourceService;
import es.caib.pinbal.logic.base.service.BaseReadonlyResourceService.ReportGenerator;
import es.caib.pinbal.logic.intf.base.config.BaseConfig;
import es.caib.pinbal.logic.intf.base.exception.AnswerRequiredException;
import es.caib.pinbal.logic.intf.base.exception.ReportGenerationException;
import es.caib.pinbal.logic.intf.base.exception.ResourceNotFoundException;
import es.caib.pinbal.logic.intf.base.model.DownloadableFile;
import es.caib.pinbal.logic.intf.base.model.ReportFileType;
import es.caib.pinbal.logic.intf.base.model.ResourceReference;
import es.caib.pinbal.logic.intf.dto.ConsultaDto;
import es.caib.pinbal.logic.intf.dto.ConsultaFiltreDto;
import es.caib.pinbal.logic.intf.dto.JustificantDto;
import es.caib.pinbal.logic.intf.model.ConsultaResource;
import es.caib.pinbal.logic.intf.model.EntitatResource;
import es.caib.pinbal.logic.intf.resourceservice.ConsultaResourceService;
import es.caib.pinbal.logic.intf.service.ConsultaService;
import es.caib.pinbal.persist.base.entity.NoDatabaseResourceEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Implementació del servei de consulta de consultes SCSP recents.
 * <p>
 * Una mateixa pantalla serveix per a administrador i delegat (i auditor/superauditor):
 * l'àmbit de dades es resol despatxant a la consulta de domini adequada segons el
 * rol de l'usuari autenticat. Per a delegat, el selector simples/múltiples
 * (camp {@code multiple} del filtre) tria el mètode corresponent.
 *
 * @author Límit Tecnologies
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ConsultaResourceServiceImpl
        extends BaseNoDatabaseReadonlyResourceService<ConsultaResource, Long>
        implements ConsultaResourceService {

    private final ConsultaService consultaService;
    private final AuthenticationHelper authenticationHelper;

    @PostConstruct
    public void init() {
        register("justificant",
                new ReportGenerator<NoDatabaseResourceEntity<ConsultaResource, Long>, Serializable, JustificantDto>() {
                    @Override
                    public List<JustificantDto> generateData(
                            String code,
                            NoDatabaseResourceEntity<ConsultaResource, Long> entity,
                            Serializable params) throws ReportGenerationException {
                        try {
                            boolean isAdmin = authenticationHelper.isCurrentUserInRole(BaseConfig.ROLE_ADMIN);
                            JustificantDto justificant = consultaService.obtenirJustificant(entity.getId(), isAdmin);
                            return (justificant != null && !justificant.isError() && justificant.getContingut() != null)
                                    ? Collections.singletonList(justificant)
                                    : Collections.emptyList();
                        } catch (Exception ex) {
                            throw new ReportGenerationException(ConsultaResource.class, entity.getId(), code, ex.getMessage(), ex);
                        }
                    }

                    @Override
                    public DownloadableFile generateFile(
                            String code,
                            List<?> data,
                            ReportFileType fileType,
                            OutputStream out) {
                        if (data.isEmpty()) {
                            return DownloadableFile.builder().build();
                        }
                        JustificantDto justificant = (JustificantDto) data.get(0);
                        return DownloadableFile.builder()
                                .name(justificant.getNom() != null ? justificant.getNom() : "justificant.pdf")
                                .contentType(justificant.getContentType() != null ? justificant.getContentType() : "application/pdf")
                                .content(justificant.getContingut())
                                .build();
                    }

                    @Override
                    public void onChange(
                            Serializable id,
                            Serializable previous,
                            String fieldName,
                            Object fieldValue,
                            Map<String, AnswerRequiredException.AnswerValue> answers,
                            String[] previousFieldNames,
                            Serializable target) {
                    }
                });
    }

    @Override
    protected Optional<NoDatabaseResourceEntity<ConsultaResource, Long>> entityRepositoryFindOne(Long id) {
        return Optional.of(NoDatabaseResourceEntity.<ConsultaResource, Long>builder().id(id).build());
    }

    @Override
    @Transactional(readOnly = true)
    public ConsultaResource getOne(Long id, String[] perspectives) throws ResourceNotFoundException {
        try {
            ConsultaDto dto;
            if (authenticationHelper.isCurrentUserInRole(BaseConfig.ROLE_ADMIN)) {
                dto = consultaService.findOneAdmin(id);
            } else if (authenticationHelper.isCurrentUserInRole(BaseConfig.ROLE_SUPERAUDIT)) {
                dto = consultaService.findOneSuperauditor(id);
            } else if (authenticationHelper.isCurrentUserInRole(BaseConfig.ROLE_AUDIT)) {
                dto = consultaService.findOneAuditor(id);
            } else {
                dto = consultaService.findOneDelegat(id);
            }
            return toResource(dto);
        } catch (Exception e) {
            throw new ResourceNotFoundException(ConsultaResource.class, String.valueOf(id));
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ConsultaResource> findPage(
            String quickFilter,
            String filter,
            String[] namedQueries,
            String[] perspectives,
            Pageable pageable) {
        ConsultaFiltreDto filtreDto = ConsultaFiltreSpringFilterParser.parse(filter);
        try {
            Page<ConsultaDto> consultaPage = findByRol(filtreDto, pageable);
            List<ConsultaResource> resources = consultaPage.getContent().stream()
                    .map(this::toResource)
                    .collect(Collectors.toList());
            return new PageImpl<>(resources, pageable, consultaPage.getTotalElements());
        } catch (Exception e) {
            log.error("Error consultant consultes recents", e);
            return Page.empty(pageable);
        }
    }

    private Page<ConsultaDto> findByRol(ConsultaFiltreDto filtreDto, Pageable pageableOriginal) throws Exception {
        // L'ordenació de la graella usa els camps del recurs; cal traduir-los als camps de la
        // vista LlistatConsulta sobre la qual operen les consultes de domini.
        Pageable pageable = ConsultaFiltreSpringFilterParser.translateSort(pageableOriginal);
        Long entitatId = filtreDto.getEntitatId();
        if (authenticationHelper.isCurrentUserInRole(BaseConfig.ROLE_ADMIN)) {
            return consultaService.findByFiltrePaginatPerAdmin(filtreDto, pageable);
        }
        if (authenticationHelper.isCurrentUserInRole(BaseConfig.ROLE_SUPERAUDIT)) {
            return consultaService.findByFiltrePaginatPerSuperauditor(entitatId, filtreDto, pageable);
        }
        if (authenticationHelper.isCurrentUserInRole(BaseConfig.ROLE_AUDIT)) {
            return consultaService.findByFiltrePaginatPerAuditor(entitatId, filtreDto, pageable);
        }
        // Delegat: el selector simples/múltiples (camp 'multiple') tria el mètode
        if (Boolean.TRUE.equals(filtreDto.getMultiple())) {
            return consultaService.findMultiplesByFiltrePaginatPerDelegat(entitatId, filtreDto, pageable);
        }
        return consultaService.findSimplesByFiltrePaginatPerDelegat(entitatId, filtreDto, pageable);
    }

    private ConsultaResource toResource(ConsultaDto dto) {
        ConsultaResource resource = new ConsultaResource();
        resource.setId(dto.getId());
        resource.setScspPeticionId(dto.getScspPeticionId());
        resource.setScspSolicitudId(dto.getScspSolicitudId());
        resource.setCreacioData(dto.getCreacioData());
        if (dto.getCreacioUsuari() != null) {
            resource.setCreacioUsuariNomCodi(dto.getCreacioUsuari().getNomCodi());
        }
        resource.setFuncionariNomAmbDocument(dto.getFuncionariNomAmbDocument());
        resource.setProcedimentCodi(dto.getProcedimentCodi());
        resource.setProcedimentNom(dto.getProcedimentNom());
        resource.setProcedimentCodiNom(dto.getProcedimentCodiNom());
        resource.setServeiCodiNom(dto.getServeiCodiNom());
        resource.setEstat(dto.getEstat());
        resource.setDataEsperadaResposta(dto.getDataEsperadaResposta());
        if (dto.getJustificantEstat() != null) {
            resource.setJustificantEstat(dto.getJustificantEstat().name());
        }
        resource.setJustificantEstatError(dto.isJustificantEstatError());
        resource.setJustificantError(dto.getJustificantError());
        resource.setRecobriment(dto.isRecobriment());
        resource.setMultiple(dto.isMultiple());
        resource.setPareId(dto.getPareId());
        resource.setTitularDocumentTipus(dto.getTitularDocumentTipus() != null ? dto.getTitularDocumentTipus().name() : null);
        resource.setTitularDocumentNum(dto.getTitularDocumentNum());
        resource.setTitularNomComplet(dto.getTitularNomComplet());
        resource.setDepartamentNom(dto.getDepartamentNom());
        resource.setFinalitat(dto.getFinalitat());
        resource.setConsentiment(dto.getConsentiment() != null ? dto.getConsentiment().name() : null);
        resource.setExpedientId(dto.getExpedientId());
        resource.setEntitatNom(dto.getEntitatNom());
        resource.setEntitatCif(dto.getEntitatCif());
        resource.setRespostaData(dto.getRespostaData());
        if (dto.getEntitatId() != null) {
            ResourceReference<EntitatResource, Long> entitatRef = ResourceReference.toResourceReference(
                    dto.getEntitatId(),
                    dto.getEntitatNom());
            resource.setEntitat(entitatRef);
        }
        return resource;
    }

}
