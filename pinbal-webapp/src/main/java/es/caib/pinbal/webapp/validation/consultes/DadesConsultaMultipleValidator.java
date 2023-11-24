package es.caib.pinbal.webapp.validation.consultes;

import es.caib.pinbal.core.dto.ConsultaDto;
import es.caib.pinbal.core.dto.DadaEspecificaDto;
import es.caib.pinbal.core.dto.NodeDto;
import es.caib.pinbal.core.dto.ServeiCampDto;
import es.caib.pinbal.core.dto.ServeiDto;
import es.caib.pinbal.core.service.ServeiService;
import es.caib.pinbal.webapp.command.ConsultaCommand;
import es.caib.pinbal.webapp.helper.MessageHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.ObjectError;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Slf4j
public class DadesConsultaMultipleValidator {
    private final ServeiService serveiService;
    private final ServeiDto servei;
    private final List<ServeiCampDto> camps;
    private final List<CampPathInfo> campsPaths;
    private final List<String> documentsPermesos;
    private final Locale locale;
    private final int numLines;
    private final PosicionsDadesGeneriques posicionsDadesGeneriques;

    private List<ConsultaLinia> consultaLinies = new ArrayList<>();
    private Errors errorsCommand;
    private boolean hasErrorsLinies = false;


    public DadesConsultaMultipleValidator(ServeiService serveiService,
                                          List<String[]> linies,
                                          List<ServeiCampDto> camps,
                                          ServeiDto servei,
                                          Errors errorsCommand,
                                          Locale locale) {
        this.serveiService = serveiService;
        this.servei = servei;
        this.camps = camps;
        this.locale = locale;
        this.errorsCommand = errorsCommand;
        this.campsPaths = getCampsPaths(linies.get(0), errorsCommand, servei.getCodi());
        this.posicionsDadesGeneriques = getPosicioDadesGeneriques();
        this.documentsPermesos = getDocumentsPermesos(servei);
        this.numLines = linies.size() - 1;
        this.consultaLinies = getConsultaLinees(linies);
    }

    private List<CampPathInfo> getCampsPaths(String[] pathsFitxer, Errors errorsCommand, String serveiCodi) {
        List<CampPathInfo> campsPaths = new ArrayList<>();

        List<NodeDto<DadaEspecificaDto>> llistaArbreDadesEspecifiques = new ArrayList<>();

        for (String path : pathsFitxer) {
            ServeiCampDto camp = getCampByPath(path);
            List<String> valorsPermesos = null;
            if (camp == null) {
                if (path.toLowerCase().startsWith("datosespecificos")) {
                    errorsCommand.reject(
                            "consulta.fitxer.camp.path.invalid",
                            new Object[]{path},
                            "El path " + path + " no està definit en aquest servei");
                }
            } else {
                if (ServeiCampDto.ServeiCampDtoTipus.ENUM.equals(camp.getTipus())) {
                    DadaEspecificaDto dadaEspecifica = getDadaEspecifica(path, llistaArbreDadesEspecifiques, serveiCodi);
//                    String[] enums = camp.getEnumDescripcions();
                    if (dadaEspecifica != null) {
                        if (dadaEspecifica.getEnumeracioValors() != null && dadaEspecifica.getEnumeracioValors().length > 0) {
                            valorsPermesos = new ArrayList<>(Arrays.asList(dadaEspecifica.getEnumeracioValors()));
                        }
                    } else {
                        valorsPermesos = new ArrayList<>();
                    }
                }
            }
            campsPaths.add(CampPathInfo.builder()
                    .etiqueta(camp != null ? camp.getEtiqueta() != null ? camp.getEtiqueta() : camp.getCampNom() : null)
                    .path(path)
                    .tipus(camp != null ? camp.getTipus() : null)
                    .valorsPermesos(valorsPermesos)
                    .validPath(camp != null)
                    .build());
        }

        return campsPaths;
    }

    private DadaEspecificaDto getDadaEspecifica(String path, List<NodeDto<DadaEspecificaDto>> llistaArbreDadesEspecifiques, String serveiCodi) {
        if (llistaArbreDadesEspecifiques.isEmpty()) {
            try {
                llistaArbreDadesEspecifiques = serveiService.generarArbreDadesEspecifiques(serveiCodi).toList();
            } catch (Exception ex) {
                log.error("No s'ha pogut obtenir l'arbre de dades específiques");
            }
        }
        for (NodeDto<DadaEspecificaDto> nodeDadesEspecifiques : llistaArbreDadesEspecifiques) {
            if (path.equals(nodeDadesEspecifiques.getDades().getPathAmbSeparadorDefault())) {
                return nodeDadesEspecifiques.getDades();
            }
        }
        return null;
    }

    private ServeiCampDto getCampByPath(String path) {
        for (ServeiCampDto camp : camps) {
            if (path.equals(camp.getPath())) {
                return camp;
            }
        }
        return null;
    }

//    private Integer[] getPosicioCampsObligatoris(List<ServeiCampDto> campsObligatoris, Errors errors) {
//        Integer[] posicioCampsObligatoris = new Integer[campsObligatoris.size()];
//
//        int posOb = 0;
//        for (ServeiCampDto campObligatori : campsObligatoris) {
//            // Comprovam si el camp obligatori existeix en el fitxer
//            int pos = 0;
//            for (CampPathInfo campPathInfo : campsPaths) {
//                if (campObligatori.getPath().equals(campPathInfo)) {
//                    posicioCampsObligatoris[posOb] = pos;
//                    break;
//                }
//                pos++;
//            }
//            // Es comprova que tots els camps obligatoris del servei s'han inclòs al fitxer
//            // Si no hi es es genera un missatge d'error
//            if (posicioCampsObligatoris[posOb] == null) {
//                errors.reject(
//                        MessageHelper.getInstance().getMessage(
//                                "consulta.fitxer.camp.obligatori",
//                                new Object[]{campObligatori.getEtiqueta()},
//                                locale));
//            }
//            posOb++;
//        }
//
//        return posicioCampsObligatoris;
//    }

    private PosicionsDadesGeneriques getPosicioDadesGeneriques() {
        PosicionsDadesGeneriques posicions = new PosicionsDadesGeneriques();
        int pos = 0;
        for (CampPathInfo campPathInfo : campsPaths) {
            switch (campPathInfo.getPath()) {
                case "DatosGenericos/Solicitante/IdExpediente":
                    posicions.setPosicioExpedient(pos);
                    break;
                case "DatosGenericos/Titular/TipoDocumentacion":
                    posicions.setPosicioTitularDocumentTipus(pos);
                    break;
                case "DatosGenericos/Titular/Documentacion":
                    posicions.setPosiciotitularDocumentNumero(pos);
                    break;
                case "DatosGenericos/Titular/NombreCompleto":
                    posicions.setPosicioTitularNomComplet(pos);
                    break;
                case "DatosGenericos/Titular/Nombre":
                    posicions.setPosicioTitularNom(pos);
                    break;
                case "DatosGenericos/Titular/Apellido1":
                    posicions.setPosicioTitularLlinatge1(pos);
                    break;
                case "DatosGenericos/Titular/Apellido2":
                    posicions.setPosicioTitularLlinatge2(pos);
                    break;
            }
            pos++;
        }
        return posicions;
    }

    private List<String> getDocumentsPermesos(ServeiDto servei) {
        List<String> documentsPermesos = new ArrayList<>();
        if (servei.isPinbalActiuCampDocument()) {
            if (servei.isPinbalPermesDocumentTipusCif()) documentsPermesos.add("CIF");
            if (servei.isPinbalPermesDocumentTipusDni()) documentsPermesos.add("DNI");
            if (servei.isPinbalPermesDocumentTipusNie()) documentsPermesos.add("NIE");
            if (servei.isPinbalPermesDocumentTipusNif()) documentsPermesos.add("NIF");
            if (servei.isPinbalPermesDocumentTipusPas()) documentsPermesos.add("PASSAPORT");
        }
        return documentsPermesos;
    }


    private List<ConsultaLinia> getConsultaLinees(List<String[]> linies) {
        List<ConsultaLinia> consultaLinies = new ArrayList<>();
        int posicioLinia = 4;
        for (String[] linia : linies.subList(1, linies.size())) {
            consultaLinies.add(getConsultaLinea(linia, posicioLinia++));
        }
        return consultaLinies;
    }

    private ConsultaLinia getConsultaLinea(String[] linia, int posicioLinia) {
        ConsultaCommand commandLinia = new ConsultaCommand();
        List<String> errorsLinia = new ArrayList<>();
        addDadesGeneriques(linia, posicioLinia, commandLinia, errorsLinia);
        addDadesEspecifiques(linia, posicioLinia, commandLinia, errorsLinia);
        return ConsultaLinia.builder().linia(linia).commandLinia(commandLinia).errorsLinia(errorsLinia).build();
    }

    private void addDadesEspecifiques(String[] linia, int posicioLinia, ConsultaCommand commandLinia, List<String> errorsLinia) {
        Map<String, Object> dadesEspecifiques = new HashMap<>();
        int pos = 0;
        for (CampPathInfo campPath : campsPaths) {
            if (campPath.isDadaEspecifica() && linia[pos] != null && !linia[pos].trim().isEmpty()) {
                dadesEspecifiques.put(campPath.getPath(), getValorDadaEspecifica(linia[pos].trim(), campPath, posicioLinia, errorsLinia));
            }
            pos++;
        }
        commandLinia.setDadesEspecifiques(dadesEspecifiques);
    }

    private Object getValorDadaEspecifica(String valor, CampPathInfo campPath, int posicioLinia, List<String> errorsLinia) {
        if (!campPath.isValidValue(valor)) {
            errorsLinia.add(MessageHelper.getInstance().getMessage("consulta.fitxer.camp.valor.invalid", new Object[]{posicioLinia, valor, campPath.getEtiqueta()}, locale));
            hasErrorsLinies = true;
        }
        return campPath.getValue(valor);
    }

    private void addDadesGeneriques(String[] linia, int posicioLinia, ConsultaCommand commandLinia, List<String> errorsLinia) {
        if (isDadaGenericaNullOrEmpty(posicionsDadesGeneriques.getPosicioExpedient(), linia)) {
            commandLinia.setExpedientId(linia[posicionsDadesGeneriques.getPosicioExpedient()].trim());
        }
        if (isDadaGenericaNullOrEmpty(posicionsDadesGeneriques.getPosicioTitularDocumentTipus(), linia)) {
            ConsultaDto.DocumentTipus docTipus = documentTipusByName(linia[posicionsDadesGeneriques.getPosicioTitularDocumentTipus()].trim());
            if (docTipus == null) {
                errorsLinia.add(MessageHelper.getInstance().getMessage("consulta.fitxer.camp.document.tipus.invalid", new Object[]{posicioLinia}, locale));
                hasErrorsLinies = true;
            }
            commandLinia.setTitularDocumentTipus(docTipus);
        }
        if (isDadaGenericaNullOrEmpty(posicionsDadesGeneriques.getPosiciotitularDocumentNumero(), linia)) {
            commandLinia.setTitularDocumentNum(linia[posicionsDadesGeneriques.getPosiciotitularDocumentNumero()].trim());
        }
        if (isDadaGenericaNullOrEmpty(posicionsDadesGeneriques.getPosicioTitularNom(), linia)) {
            commandLinia.setTitularNom(linia[posicionsDadesGeneriques.getPosicioTitularNom()].trim());
        }
        if (isDadaGenericaNullOrEmpty(posicionsDadesGeneriques.getPosicioTitularLlinatge1(), linia)) {
            commandLinia.setTitularLlinatge1(linia[posicionsDadesGeneriques.getPosicioTitularLlinatge1()].trim());
        }
        if (isDadaGenericaNullOrEmpty(posicionsDadesGeneriques.getPosicioTitularLlinatge2(), linia)) {
            commandLinia.setTitularLlinatge2(linia[posicionsDadesGeneriques.getPosicioTitularLlinatge2()].trim());
        }
        if (isDadaGenericaNullOrEmpty(posicionsDadesGeneriques.getPosicioTitularNomComplet(), linia)) {
            commandLinia.setTitularNomComplet(linia[posicionsDadesGeneriques.getPosicioTitularNomComplet()].trim());
        }
    }

    private boolean isDadaGenericaNullOrEmpty(int posicio, String[] linia) {
        return posicio >= 0 && linia[posicio] != null && !linia[posicio].trim().isEmpty();
    }

    public ConsultaDto.DocumentTipus documentTipusByName(String name) {
        ConsultaDto.DocumentTipus result = null;
        for (ConsultaDto.DocumentTipus tipus : ConsultaDto.DocumentTipus.values()) {
            if (tipus.name().equalsIgnoreCase(name)) {
                result = tipus;
                break;
            }
        }
        return result;
    }

    public void validate() {
        // 1. Tots els camps genèrics obligatoris s'han emplenat
//        if (!campsObligatoris.isEmpty()) {
//            for (int i = 0; i < campsObligatoris.size(); i++) {
//                ServeiCampDto campObligatori = campsObligatoris.get(i);
//                Integer posicioCampObligatori = posicioCampsObligatoris[i];
//                if (posicioCampObligatori != null) {
//                    int numLinia = 4;
//                    for (ConsultaLinia consultaLinia : consultaLinies) {
//                        if (consultaLinia.getLinia()[posicioCampObligatori] == null || "".equals(consultaLinia.getLinia()[posicioCampObligatori])) {
//                            String campEtiqueta = (campObligatori.getEtiqueta() != null) ? campObligatori.getEtiqueta() : campObligatori.getCampNom();
//                            consultaLinia.getErrorsLinia().add(
//                                    MessageHelper.getInstance().getMessage("consulta.fitxer.camp.obligatori.null", new Object[]{numLinia, campEtiqueta}, locale));
//                            hasError = true;
//                        }
//                        numLinia++;
//                    }
//                }
//            }
//        }
        if (servei.isPinbalDocumentObligatori()) {
            int numLinia = 4;
            for (ConsultaLinia consultaLinia : consultaLinies) {
                if (consultaLinia.getCommandLinia().getTitularDocumentTipus() == null) {
                    consultaLinia.getErrorsLinia().add(
                            MessageHelper.getInstance().getMessage(
                                    "consulta.fitxer.camp.obligatori.null",
                                    new Object[]{
                                            numLinia,
                                            MessageHelper.getInstance().getMessage("consulta.form.camp.document.tipus", null, locale)},
                                    locale));
                    hasErrorsLinies = true;
                }
                if (consultaLinia.getCommandLinia().getTitularDocumentNum() == null || consultaLinia.getCommandLinia().getTitularDocumentNum().isEmpty()) {
                    consultaLinia.getErrorsLinia().add(
                            MessageHelper.getInstance().getMessage(
                                    "consulta.fitxer.camp.obligatori.null",
                                    new Object[]{
                                            numLinia,
                                            MessageHelper.getInstance().getMessage("consulta.form.camp.document.num", null, locale)},
                                    locale));
                    hasErrorsLinies = true;
                }
                numLinia++;
            }
        }
        // 2. Tipus documents és un dels acceptats pel servei
        if (servei.isPinbalActiuCampDocument()) {
            if (posicionsDadesGeneriques.getPosicioTitularDocumentTipus() != -1) {
                int numLinia = 4;
                for (ConsultaLinia consultaLinia : consultaLinies) {
                    if (consultaLinia.getCommandLinia().getTitularDocumentTipus() != null) {
                        if (!documentsPermesos.contains(consultaLinia.getCommandLinia().getTitularDocumentTipus().name())) {
                            consultaLinia.getErrorsLinia().add(
                                    MessageHelper.getInstance().getMessage("consulta.fitxer.camp.document.tipus", new Object[] {numLinia}, locale));
                            hasErrorsLinies = true;
                        }
                    }
                    numLinia++;
                }

            }
        }
        // 3. Validació de dades especifiques
        DadesConsultaSimpleValidator validadorDadesEspecifiques = new DadesConsultaSimpleValidator(serveiService, servei.getCodi());
        int numLinia = 4;
        for (ConsultaLinia consultaLinia : consultaLinies) {
            Errors errors = new BeanPropertyBindingResult(consultaLinia.getCommandLinia(), "command");
            validadorDadesEspecifiques.validate(consultaLinia.getCommandLinia(), errors);
            if (errors.hasErrors()) {
                consultaLinia.getErrorsLinia().addAll(getErrorList(errors, numLinia));
                hasErrorsLinies = true;
            }
            numLinia++;
        }


        if (hasErrorsLinies) {
            errorsCommand.rejectValue(
                    "multipleFitxer",
                    "PeticioMultiple.fitxer",
                    "Errors en el contingut del fitxer");
        }
    }

    private List<String> getErrorList(Errors errors, int numLinia) {
        List<String> errorList = new ArrayList<>();
        if (errors.hasErrors()) {
            String prefix = MessageHelper.getInstance().getMessage("consulta.fitxer.linea", new Object[]{numLinia}, locale);
            for(ObjectError error: errors.getAllErrors()) {
                errorList.add(prefix + MessageHelper.getInstance().getMessage(error.getCode(), error.getArguments(), locale));
            }
        }
        return errorList;
    }

    public List<String> getErrorsValidacio() {
        List<String> errorsValidacio = new ArrayList<>();
        if (consultaLinies != null && !consultaLinies.isEmpty())
        for (ConsultaLinia consultaLinia : consultaLinies) {
            errorsValidacio.addAll(consultaLinia.getErrorsLinia());
        }
        return errorsValidacio;
    }

    public List<List<String>> getErrorsValidacioPerLinia() {
        List<List<String>> errorsValidacio = new ArrayList<>();
        if (consultaLinies != null && !consultaLinies.isEmpty())
            for (ConsultaLinia consultaLinia : consultaLinies) {
                errorsValidacio.add(consultaLinia.getErrorsLinia());
            }
        return errorsValidacio;
    }

    public List<String> getErrorValidacioConsulta(int numConsulta) {
        return consultaLinies.get(numConsulta).getErrorsLinia();
    }

    public boolean hasErrors() {
        return hasErrorsLinies;
    }
}
