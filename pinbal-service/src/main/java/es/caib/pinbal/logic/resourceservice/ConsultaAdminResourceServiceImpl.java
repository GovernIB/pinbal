package es.caib.pinbal.logic.resourceservice;

import es.caib.pinbal.logic.base.service.BaseNoDatabaseReadonlyResourceService;
import es.caib.pinbal.logic.intf.dto.ConsultaDto;
import es.caib.pinbal.logic.intf.dto.ConsultaFiltreDto;
import es.caib.pinbal.logic.intf.model.ConsultaAdminResource;
import es.caib.pinbal.logic.intf.model.EntitatResource;
import es.caib.pinbal.logic.intf.base.model.ResourceReference;
import es.caib.pinbal.logic.intf.base.exception.ResourceNotFoundException;
import es.caib.pinbal.logic.intf.resourceservice.ConsultaAdminResourceService;
import es.caib.pinbal.logic.intf.service.ConsultaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Implementació del servei de consulta de consultes SCSP per part de l'administrador.
 *
 * @author Límit Tecnologies
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ConsultaAdminResourceServiceImpl
        extends BaseNoDatabaseReadonlyResourceService<ConsultaAdminResource, Long>
        implements ConsultaAdminResourceService {

    private static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";
    // Matches field:'value' (quoted string) or field:123 (numeric)
    private static final Pattern EQ_PATTERN = Pattern.compile("([\\w.]+):(?:'([^']*)'|(\\d+))");
    private static final Pattern GTE_PATTERN = Pattern.compile("(\\w+)>:'([^']*)'");
    private static final Pattern LTE_PATTERN = Pattern.compile("(\\w+)<:'([^']*)'");

    private final ConsultaService consultaService;

    @Override
    @Transactional(readOnly = true)
    public ConsultaAdminResource getOne(Long id, String[] perspectives) throws ResourceNotFoundException {
        try {
            ConsultaDto dto = consultaService.findOneAdmin(id);
            return toResource(dto);
        } catch (Exception e) {
            throw new ResourceNotFoundException(ConsultaAdminResource.class, String.valueOf(id));
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ConsultaAdminResource> findPage(
            String quickFilter,
            String filter,
            String[] namedQueries,
            String[] perspectives,
            Pageable pageable) {
        ConsultaFiltreDto filtreDto = parseFilterToFiltreDto(filter);
        try {
            Page<ConsultaDto> consultaPage = consultaService.findByFiltrePaginatPerAdmin(filtreDto, pageable);
            List<ConsultaAdminResource> resources = consultaPage.getContent().stream()
                    .map(this::toResource)
                    .collect(Collectors.toList());
            return new PageImpl<>(resources, pageable, consultaPage.getTotalElements());
        } catch (Exception e) {
            log.error("Error consultant consultes admin", e);
            return Page.empty(pageable);
        }
    }

    private ConsultaAdminResource toResource(ConsultaDto dto) {
        ConsultaAdminResource resource = new ConsultaAdminResource();
        resource.setId(dto.getId());
        resource.setScspPeticionId(dto.getScspPeticionId());
        resource.setCreacioData(dto.getCreacioData());
        if (dto.getCreacioUsuari() != null) {
            resource.setCreacioUsuariNomCodi(dto.getCreacioUsuari().getNomCodi());
        }
        resource.setFuncionariNomAmbDocument(dto.getFuncionariNomAmbDocument());
        resource.setProcedimentCodiNom(dto.getProcedimentCodiNom());
        resource.setServeiCodiNom(dto.getServeiCodiNom());
        resource.setEstat(dto.getEstat());
        resource.setDataEsperadaResposta(dto.getDataEsperadaResposta());
        if (dto.getJustificantEstat() != null) {
            resource.setJustificantEstat(dto.getJustificantEstat().name());
        }
        resource.setRecobriment(dto.isRecobriment());
        resource.setMultiple(dto.isMultiple());
        if (dto.getEntitatId() != null) {
            ResourceReference<EntitatResource, Long> entitatRef = ResourceReference.toResourceReference(
                    dto.getEntitatId(),
                    dto.getEntitatNom());
            resource.setEntitat(entitatRef);
        }
        return resource;
    }

    private ConsultaFiltreDto parseFilterToFiltreDto(String filter) {
        ConsultaFiltreDto filtreDto = new ConsultaFiltreDto();
        if (filter == null || filter.isBlank()) {
            return filtreDto;
        }
        // Parse equality fields: field:'value' or field:123
        Matcher eqMatcher = EQ_PATTERN.matcher(filter);
        while (eqMatcher.find()) {
            String field = eqMatcher.group(1);
            String value = eqMatcher.group(2) != null ? eqMatcher.group(2) : eqMatcher.group(3);
            if (value == null || value.isEmpty()) continue;
            switch (field) {
                case "entitat.id":
                case "entitatId":
                    try { filtreDto.setEntitatId(Long.parseLong(value)); } catch (NumberFormatException ignored) {}
                    break;
                case "scspPeticionId":
                    filtreDto.setScspPeticionId(value);
                    break;
                case "estat":
                    try { filtreDto.setEstat(ConsultaDto.EstatTipus.valueOf(value)); } catch (IllegalArgumentException ignored) {}
                    break;
                case "recobriment":
                    filtreDto.setRecobriment("true".equalsIgnoreCase(value) ? Boolean.TRUE : Boolean.FALSE);
                    break;
                case "multiple":
                    filtreDto.setMultiple("true".equalsIgnoreCase(value) ? Boolean.TRUE : Boolean.FALSE);
                    break;
                case "funcionariNomAmbDocument":
                    filtreDto.setFuncionari(value);
                    break;
                case "procedimentCodiNom":
                    // Filter by text on procedimentCodiNom - map to funcionari field for now
                    // Note: ConsultaFiltreDto doesn't have a procedimentCodiNom filter, skip
                    break;
                case "serveiCodiNom":
                    filtreDto.setServeiCodi(value);
                    break;
                default:
                    break;
            }
        }
        // Parse like fields: field~'*value*'
        Pattern likePattern = Pattern.compile("(\\w+)~'\\*([^*]*)\\*'");
        Matcher likeMatcher = likePattern.matcher(filter);
        while (likeMatcher.find()) {
            String field = likeMatcher.group(1);
            String value = likeMatcher.group(2);
            if (value.isEmpty()) continue;
            switch (field) {
                case "scspPeticionId":
                    filtreDto.setScspPeticionId(value);
                    break;
                case "funcionariNomAmbDocument":
                    filtreDto.setFuncionari(value);
                    break;
                case "procedimentCodiNom":
                    // no direct mapping
                    break;
                case "serveiCodiNom":
                    filtreDto.setServeiCodi(value);
                    break;
                default:
                    break;
            }
        }
        // Parse GTE date fields: field>:'value'
        Matcher gteMatcher = GTE_PATTERN.matcher(filter);
        while (gteMatcher.find()) {
            String field = gteMatcher.group(1);
            String value = gteMatcher.group(2);
            if ("creacioData".equals(field)) {
                filtreDto.setDataInici(parseDate(value));
            }
        }
        // Parse LTE date fields: field<:'value'
        Matcher lteMatcher = LTE_PATTERN.matcher(filter);
        while (lteMatcher.find()) {
            String field = lteMatcher.group(1);
            String value = lteMatcher.group(2);
            if ("creacioData".equals(field)) {
                filtreDto.setDataFi(parseDate(value));
            }
        }
        return filtreDto;
    }

    private Date parseDate(String value) {
        try {
            return new SimpleDateFormat(DATE_FORMAT).parse(value);
        } catch (ParseException e) {
            log.warn("No s'ha pogut parsejar la data: {}", value);
            return null;
        }
    }

}
