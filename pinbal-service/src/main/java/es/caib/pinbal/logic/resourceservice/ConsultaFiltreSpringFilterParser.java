package es.caib.pinbal.logic.resourceservice;

import es.caib.pinbal.logic.intf.dto.ConsultaDto;
import es.caib.pinbal.logic.intf.dto.ConsultaFiltreDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Converteix el "spring filter" generat per la graella React en un
 * {@link ConsultaFiltreDto} reutilitzable pels serveis de domini de consultes.
 * <p>
 * Compartit entre {@code ConsultaResourceServiceImpl} i
 * {@code HistoricConsultaResourceServiceImpl}.
 *
 * @author Límit Tecnologies
 */
@Slf4j
public final class ConsultaFiltreSpringFilterParser {

    private static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";
    // field:'value' (string entre cometes) o field:123 (numèric)
    private static final Pattern EQ_PATTERN = Pattern.compile("([\\w.]+):(?:'([^']*)'|(\\d+))");
    private static final Pattern LIKE_PATTERN = Pattern.compile("(\\w+)~'\\*([^*]*)\\*'");
    private static final Pattern GTE_PATTERN = Pattern.compile("(\\w+)>:'([^']*)'");
    private static final Pattern LTE_PATTERN = Pattern.compile("(\\w+)<:'([^']*)'");

    // Traducció dels camps d'ordenació del recurs (noms de ConsultaResource) als camps de les
    // vistes LlistatConsulta / LlistatHistoricConsulta, que són les entitats sobre les quals
    // s'executen totes les consultes paginades (admin, delegat, auditor i superauditor).
    private static final Map<String, String> SORT_FIELD_MAP = Map.of(
            "scspPeticionId", "peticioId",
            "scspSolicitudId", "solicitudId",
            "creacioData", "data",
            "creacioUsuariNomCodi", "usuariNom",
            "funcionariNomAmbDocument", "funcionariNom",
            "procedimentCodiNom", "procedimentNom",
            "serveiCodiNom", "serveiNom",
            "estat", "estat");
    private static final String DEFAULT_SORT_FIELD = "data";

    private ConsultaFiltreSpringFilterParser() {
    }

    /**
     * Tradueix l'ordenació que arriba de la graella React (camps del recurs) als camps reals de
     * les vistes LlistatConsulta/LlistatHistoricConsulta. Els camps no mapejables es descarten;
     * si no en queda cap, s'ordena per data descendent (equivalent al llistat JSP).
     */
    public static Pageable translateSort(Pageable pageable) {
        if (pageable == null) {
            return Pageable.unpaged();
        }
        List<Sort.Order> orders = new ArrayList<>();
        for (Sort.Order order : pageable.getSort()) {
            String mapped = SORT_FIELD_MAP.get(order.getProperty());
            if (mapped != null) {
                orders.add(new Sort.Order(order.getDirection(), mapped));
            }
        }
        Sort sort = orders.isEmpty()
                ? Sort.by(Sort.Direction.DESC, DEFAULT_SORT_FIELD)
                : Sort.by(orders);
        return PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);
    }

    public static ConsultaFiltreDto parse(String filter) {
        ConsultaFiltreDto filtreDto = new ConsultaFiltreDto();
        if (filter == null || filter.isBlank()) {
            return filtreDto;
        }
        // Igualtats: field:'value' o field:123
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
                case "serveiCodiNom":
                    filtreDto.setServeiCodi(value);
                    break;
                default:
                    break;
            }
        }
        // "Like": field~'*value*'
        Matcher likeMatcher = LIKE_PATTERN.matcher(filter);
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
                case "serveiCodiNom":
                    filtreDto.setServeiCodi(value);
                    break;
                default:
                    break;
            }
        }
        // Dates GTE: field>:'value'
        Matcher gteMatcher = GTE_PATTERN.matcher(filter);
        while (gteMatcher.find()) {
            if ("creacioData".equals(gteMatcher.group(1))) {
                filtreDto.setDataInici(parseDate(gteMatcher.group(2)));
            }
        }
        // Dates LTE: field<:'value'
        Matcher lteMatcher = LTE_PATTERN.matcher(filter);
        while (lteMatcher.find()) {
            if ("creacioData".equals(lteMatcher.group(1))) {
                filtreDto.setDataFi(parseDate(lteMatcher.group(2)));
            }
        }
        return filtreDto;
    }

    private static Date parseDate(String value) {
        try {
            return new SimpleDateFormat(DATE_FORMAT).parse(value);
        } catch (ParseException e) {
            log.warn("No s'ha pogut parsejar la data: {}", value);
            return null;
        }
    }

}
