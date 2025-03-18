package es.caib.pinbal.core.helper;

import es.caib.pinbal.core.dto.ServeiCampDto;
import es.caib.pinbal.core.dto.ServeiCampGrupDto;
import es.caib.pinbal.core.dto.regles.CampFormProperties;
import es.caib.pinbal.core.service.ServeiService;
import es.caib.pinbal.core.service.exception.ServeiNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.joda.time.Period;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

@Slf4j
// Classes auxiliars
@SuppressWarnings("rawtypes")
public class DadesConsultaSimpleValidator implements Validator {

    private final ServeiService serveiService;
    private List<ServeiCampDto> camps;
    String serveiCodi;

    public DadesConsultaSimpleValidator(ServeiService serveiService, String serveiCodi) {
        this.serveiService = serveiService;
        this.serveiCodi = serveiCodi;
    }

    public boolean supports(Class clazz) {
        return HashMap.class.equals(clazz);
    }

    public void validate(Object obj, Errors errors) {

        Map<String, Object> dadesEspecifiquesValors = (Map<String, Object>) obj;

        List<String> campsModificats = new ArrayList<>(); // Path
        List<String> grupsModificats = new ArrayList<>(); // Nom
        Set<String> pathsValids = new HashSet<>();

        List<ServeiCampDto> camps = null;
        List<ServeiCampGrupDto> grups = null;
        try {
            camps = serveiService.findServeiCamps(serveiCodi);
            grups = serveiService.findServeiCampGrupsAndSubgrups(serveiCodi);
        } catch (ServeiNotFoundException e) {
//            throw new RuntimeException("Error obtenint els camps i grups del servei", e);
            log.error("Error obtenint els camps i grups del servei. No s'han pogut validar les dades específiques.", e);
            errors.rejectValue(
                    "dadesEspecifiques",
                    "campsNoObtinguts",
                    "Error obtenint els camps i grups del servei. No s'han pogut validar les dades específiques.");
            return;
        }
        for (ServeiCampDto camp : camps) {
            pathsValids.add(camp.getPath());
            Object valorCamp = dadesEspecifiquesValors.get(camp.getPath());

            boolean isEmptyString = valorCamp instanceof String && ((String) valorCamp).isEmpty();
            if (valorCamp == null || isEmptyString) {
                if (camp.isObligatori())
                    errors.rejectValue(
                            "dadesEspecifiques[" + camp.getPath() + "]",
                            "NotEmpty",
                            "Aquest camp és obligatori");
            } else {
                // Valors necessaris per validar regles
                campsModificats.add(camp.getPath());
                if (camp.getGrup() != null) {
                    grupsModificats.add(camp.getGrup().getNom());
                }

                // Validar expressió regular si n'hi ha
                String validacioRegexp = camp.getValidacioRegexp();
                if (ServeiCampDto.ServeiCampDtoTipus.TEXT.equals(camp.getTipus()) && validacioRegexp != null && !validacioRegexp.isEmpty()) {
                    try {
                        Matcher matcher = Pattern.compile(validacioRegexp).matcher((String) valorCamp);
                        if (!matcher.matches()) {
                            errors.rejectValue(
                                    "dadesEspecifiques[" + camp.getPath() + "]",
                                    "RegexpDontMatch",
                                    "Valor no permès");
                        }
                    } catch (PatternSyntaxException ex) {
                        // Si l'expressió no és correcta la validació també falla
                        errors.rejectValue(
                                "dadesEspecifiques[" + camp.getPath() + "]",
                                "InvalidRegexp",
                                "Expressió de validació errònia");
                    }
                }
                // Validar rang numèric
                Integer validacioMin = camp.getValidacioMin();
                Integer validacioMax = camp.getValidacioMax();
                if (ServeiCampDto.ServeiCampDtoTipus.NUMERIC.equals(camp.getTipus()) && valorCamp != null && (validacioMin != null || validacioMax != null)) {
                    Integer valorInt = null;
                    if (valorCamp instanceof Number) {
                        valorInt = ((Number) valorCamp).intValue();
                    } else if (valorCamp instanceof String) {
                        valorInt = Integer.valueOf((String) valorCamp);
                    }
                    boolean validMin = (validacioMin != null) ? validacioMin <= valorInt : true;
                    boolean validMax = (validacioMax != null) ? validacioMax >= valorInt : true;
                    if (!validMin || !validMax) {
                        errors.rejectValue(
                                "dadesEspecifiques[" + camp.getPath() + "]",
                                "NumericRangeExceeded",
                                "Valor no permès");
                    }
                }
                if (ServeiCampDto.ServeiCampDtoTipus.DATA.equals(camp.getTipus())) {
                    Object dataObj = dadesEspecifiquesValors.get(camp.getPath());
                    Date dataDate = checkDate(dataObj, camp.getDataFormat());
                    if (dataDate != null) {
                        // Si el format és vàlid comprova les demés validacions
                        ServeiCampDto validacioDataCamp2 = camp.getValidacioDataCmpCamp2();
                        if (validacioDataCamp2 != null) {
                            Object dataObj2 = dadesEspecifiquesValors.get(validacioDataCamp2.getPath());
                            Date dataDate2 = checkDate(dataObj2, validacioDataCamp2.getDataFormat());
                            if (dataDate2 != null) {
                                Integer validacioNombre = camp.getValidacioDataCmpNombre();
                                ServeiCampDto.ServeiCampDtoValidacioOperacio validacioOperacio = camp.getValidacioDataCmpOperacio();
                                if (validacioNombre != null) {
                                    // Validacio per diferencia
                                    ServeiCampDto.ServeiCampDtoValidacioDataTipus validacioTipus = camp.getValidacioDataCmpTipus();
                                    Period period = new Period(dataDate.getTime(), dataDate2.getTime());
                                    int diff = 0;
                                    // Si la comparació és < o ==, llavors hem de controlar que no hi hagi altres unitats amb valo
                                    // Exemple si comparam 2 mesos, si el període ens diu que tenim 2 mesos, necessitam comprovar que no tenim
                                    // cap any, ni cap dia 1any i 2mesos, o 2mesos i 3dies no és igual a 2 mesos!!
                                    boolean detailDiff = false;
                                    if (ServeiCampDto.ServeiCampDtoValidacioDataTipus.ANYS == validacioTipus) {
                                        diff = period.getYears();
                                        detailDiff = (period.getMonths() > 0 || period.getDays() > 0);
                                    } else if (ServeiCampDto.ServeiCampDtoValidacioDataTipus.MESOS == validacioTipus) {
                                        diff = period.getYears() * 12 + period.getMonths();
                                        detailDiff = period.getDays() > 0;
                                    } else {
//                                        DateTimeFormatter formatter = DateTimeFormat.forPattern("dd/MM/yyyy");
//                                        diff = Days.daysBetween(LocalDate.parse(dataText, formatter), LocalDate.parse(dataText2, formatter)).getDays();
                                        diff = Days.daysBetween(LocalDate.fromDateFields(dataDate), LocalDate.fromDateFields(dataDate2)).getDays();
                                    }
                                    boolean valid = true;
                                    if (ServeiCampDto.ServeiCampDtoValidacioOperacio.LT == validacioOperacio) {
                                        valid = diff < validacioNombre;
                                    } else if (ServeiCampDto.ServeiCampDtoValidacioOperacio.LTE == validacioOperacio) {
                                        valid = diff < validacioNombre || (diff == validacioNombre && !detailDiff);
                                    } else if (ServeiCampDto.ServeiCampDtoValidacioOperacio.GT == validacioOperacio) {
                                        valid = diff > validacioNombre;
                                    } else if (ServeiCampDto.ServeiCampDtoValidacioOperacio.GTE == validacioOperacio) {
                                        valid = diff >= validacioNombre;
                                    } else if (ServeiCampDto.ServeiCampDtoValidacioOperacio.EQ == validacioOperacio) {
                                        valid = diff == validacioNombre && !detailDiff;
                                    } else if (ServeiCampDto.ServeiCampDtoValidacioOperacio.NEQ == validacioOperacio) {
                                        valid = diff != validacioNombre || detailDiff;
                                    }
                                    if (!valid) {
                                        errors.rejectValue(
                                                "dadesEspecifiques[" + camp.getPath() + "]",
                                                "DataValida",
                                                "La data és surt del rang permès");
                                    }
                                } else {
                                    // Validacio per comparació
                                    boolean valid = true;
                                    if (ServeiCampDto.ServeiCampDtoValidacioOperacio.LT == validacioOperacio) {
                                        valid = dataDate.getTime() < dataDate2.getTime();
                                    } else if (ServeiCampDto.ServeiCampDtoValidacioOperacio.LTE == validacioOperacio) {
                                        valid = dataDate.getTime() <= dataDate2.getTime();
                                    } else if (ServeiCampDto.ServeiCampDtoValidacioOperacio.GT == validacioOperacio) {
                                        valid = dataDate.getTime() > dataDate2.getTime();
                                    } else if (ServeiCampDto.ServeiCampDtoValidacioOperacio.GTE == validacioOperacio) {
                                        valid = dataDate.getTime() >= dataDate2.getTime();
                                    } else if (ServeiCampDto.ServeiCampDtoValidacioOperacio.EQ == validacioOperacio) {
                                        valid = dataDate.getTime() == dataDate2.getTime();
                                    } else if (ServeiCampDto.ServeiCampDtoValidacioOperacio.NEQ == validacioOperacio) {
                                        valid = dataDate.getTime() != dataDate2.getTime();
                                    }
                                    if (!valid) {
                                        errors.rejectValue(
                                                "dadesEspecifiques[" + camp.getPath() + "]",
                                                "DataValida",
                                                "La comparació entre dates no passa la validació");
                                    }
                                }
                            } else {
                                errors.rejectValue(
                                        "dadesEspecifiques[" + camp.getPath() + "]",
                                        "DataReferencia",
                                        "El camp al que fa referència la comparació no és vàlid");
                            }
                        }
                    } else {
                        errors.rejectValue(
                                "dadesEspecifiques[" + camp.getPath() + "]",
                                "DataValida",
                                "La data és incorrecta");
                    }
                }
            }
        }

        // Validació de dades especifiques no definits com a camps
        if (dadesEspecifiquesValors != null && !dadesEspecifiquesValors.isEmpty()) {
            StringBuilder clausNoValides = new StringBuilder();
            for (String clau : dadesEspecifiquesValors.keySet()) {
                if (!pathsValids.contains(clau)) {
                    clausNoValides.append(clau).append(", ");
                }
            }
            if (clausNoValides.length() > 0) {
                clausNoValides.setLength(clausNoValides.length() - 2);
                errors.rejectValue(
                        "dadesEspecifiques",
                        "campsNoDefinits",
                        "Els següents camps no estan definits al servei: " + clausNoValides);
            }
        }

        // Validacions de regles
        List<CampFormProperties> campsRegles = null;
        List<CampFormProperties> grupsRegles = null;
        try {
            campsRegles = serveiService.getCampsByserveiRegla(serveiCodi, campsModificats.toArray(new String[]{}));
            grupsRegles = serveiService.getGrupsByserveiRegla(serveiCodi, grupsModificats.toArray(new String[]{}));
        } catch (ServeiNotFoundException e) {
            throw new RuntimeException("Error obtenint les regles del servei", e);
        }

        // Validacions de regles de camps
        int i = 0;
        if (campsRegles != null && !campsRegles.isEmpty()) {
            for (ServeiCampDto camp : camps) {
                CampFormProperties campRegla = campsRegles.get(i++);

                if (!campsModificats.contains(camp.getPath())) {
                    if (campRegla.isObligatori()) {
                        errors.rejectValue(
                                "dadesEspecifiques[" + camp.getPath() + "]",
                                "consulta.form.camp.regla.obligatori",
                                new Object[]{campRegla.getReglaObligatori()},
                                "Amb les dades actuals, aquest camp és obligatori");
                    }
                } else {
                    if (!campRegla.isVisible()) {
                        errors.rejectValue(
                                "dadesEspecifiques[" + camp.getPath() + "]",
                                "consulta.form.camp.regla.visible",
                                new Object[]{camp.getEtiqueta() != null ? camp.getEtiqueta() : camp.getCampNom(), campRegla.getReglaVisible()},
                                "Amb les dades actuals, aquest camp ha d'estar buit");
                    } else if (!campRegla.isEditable()) {
                        errors.rejectValue(
                                "dadesEspecifiques[" + camp.getPath() + "]",
                                "consulta.form.camp.regla.editable",
                                new Object[]{campRegla.getReglaEditable()},
                                "Amb les dades actuals, aquest camp ha d'estar buit");
                    }
                }
            }
        }
        // Validacions de regles de grup
        if (grupsRegles != null && !grupsRegles.isEmpty()) {
            for (CampFormProperties grupRegla : grupsRegles) {
                ServeiCampGrupDto grup = getGrupById(grups, grupRegla.getVarId());
                if (!grupsModificats.contains(grup.getNom())) {
                    if (grupRegla.isObligatori()) {
                        errors.rejectValue(
                                "dadesEspecifiques[" + grup.getId() + "]",
                                "consulta.form.grup.regla.obligatori",
                                new Object[]{grup != null ? grup.getNom() : "", grupRegla.getReglaObligatori()},
                                "El grup ha de tenir dades emplenades");
                    }

                } else {
                    if (!grupRegla.isEditable()) {
                        errors.rejectValue(
                                "dadesEspecifiques[" + grup.getId() + "]",
                                "consulta.form.grup.regla.editable",
                                new Object[]{grup != null ? grup.getNom() : "", grupRegla.getReglaEditable()},
                                "El grup no pot tenir dades emplenades");
                    } else if (!grupRegla.isVisible()) {
                        errors.reject(
                                "consulta.form.grup.regla.visible",
                                new Object[]{grup != null ? grup.getNom() : "", grupRegla.getReglaVisible()},
                                "El grup no pot tenir dades emplenades");
                    }
                }
            }
        }
    }

    private ServeiCampGrupDto getGrupById(List<ServeiCampGrupDto> grups, Long grupId) {
        if (grups == null) {
            return null;
        }
        for (ServeiCampGrupDto grup : grups) {
            if (grup.getId().equals(grupId)) {
                return grup;
            }
        }
        return null;
    }

//		private CampFormProperties getGrupRegla(ServeiCampDto camp, List<CampFormProperties> grupsRegles) {
//			if (camp.getGrup() == null) {
//				return null;
//			}
//			for (CampFormProperties grupRegles: grupsRegles) {
//				if (grupRegles.getVarId().equals(camp.getGrup().getId()) {
//					return grupRegles;
//				}
//			}
//			return null;
//		}

    public static final String FORMAT_DATA_DADES_ESPECIFIQUES = "dd/MM/yyyy";
    public static final String FORMAT2 = "dd-MM-yyyy";
    public static final String FORMAT3 = "dd/MM/yy";
    public static final String FORMAT4 = "dd-MM-yy";
    public static final String FORMAT5 = "yyyy/MM/dd";
    public static final String FORMAT6 = "yyyy-MM-dd";

    private Date checkDate(Object dateObj, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(FORMAT_DATA_DADES_ESPECIFIQUES);
        Date dataDate = null;


        if (dateObj instanceof Date) {
            dataDate = (Date) dateObj;
        } else if (dateObj instanceof String) {
            // Validar format data
            String dataText = (String) dateObj;
            try {
                String[] formats = {format, FORMAT2, FORMAT3, FORMAT4, FORMAT5, FORMAT6};
                if (format != null && !format.isEmpty()) {
                    dataDate = parseDate(dataText, FORMAT_DATA_DADES_ESPECIFIQUES, FORMAT2, FORMAT3, FORMAT4, FORMAT5, FORMAT6, format);
                } else {
                    dataDate = parseDate(dataText, FORMAT_DATA_DADES_ESPECIFIQUES, FORMAT2, FORMAT3, FORMAT4, FORMAT5, FORMAT6);
                }
            } catch (Exception ex) {
                dataDate = null;
            }
        }

        return dataDate;
    }

    public static Date parseDate(String dateString, String... formatStrings) {
        for (String formatString : formatStrings) {
            try {
                return new SimpleDateFormat(formatString).parse(dateString);
            } catch (ParseException e) {
            }
        }
        return null;
    }
}
