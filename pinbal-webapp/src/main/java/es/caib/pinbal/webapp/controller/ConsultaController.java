package es.caib.pinbal.webapp.controller;

import static es.caib.pinbal.webapp.view.SpreadSheetReader.SEPARADOR;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.naming.NamingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import es.caib.pinbal.core.dto.CodiValor;
import es.caib.pinbal.core.dto.ConsultaDto;
import es.caib.pinbal.core.dto.ConsultaDto.DocumentTipus;
import es.caib.pinbal.core.dto.DadaEspecificaDto;
import es.caib.pinbal.core.dto.EntitatDto;
import es.caib.pinbal.core.dto.EntitatUsuariDto;
import es.caib.pinbal.core.dto.FitxerDto;
import es.caib.pinbal.core.dto.JsonResponse;
import es.caib.pinbal.core.dto.JustificantDto;
import es.caib.pinbal.core.dto.NodeDto;
import es.caib.pinbal.core.dto.ServeiCampDto;
import es.caib.pinbal.core.dto.ServeiCampDto.ServeiCampDtoTipus;
import es.caib.pinbal.core.dto.ServeiCampGrupDto;
import es.caib.pinbal.core.dto.ServeiDto;
import es.caib.pinbal.core.dto.UsuariDto;
import es.caib.pinbal.core.dto.regles.CampFormProperties;
import es.caib.pinbal.core.service.ConsultaService;
import es.caib.pinbal.core.service.EntitatService;
import es.caib.pinbal.core.service.HistoricConsultaService;
import es.caib.pinbal.core.service.ProcedimentService;
import es.caib.pinbal.core.service.ServeiService;
import es.caib.pinbal.core.service.UsuariService;
import es.caib.pinbal.core.service.exception.AccesExternException;
import es.caib.pinbal.core.service.exception.ConsultaNotFoundException;
import es.caib.pinbal.core.service.exception.ConsultaScspException;
import es.caib.pinbal.core.service.exception.EntitatNotFoundException;
import es.caib.pinbal.core.service.exception.FileTypeException;
import es.caib.pinbal.core.service.exception.ProcedimentNotFoundException;
import es.caib.pinbal.core.service.exception.ProcedimentServeiNotFoundException;
import es.caib.pinbal.core.service.exception.ScspException;
import es.caib.pinbal.core.service.exception.ServeiNotAllowedException;
import es.caib.pinbal.core.service.exception.ServeiNotFoundException;
import es.caib.pinbal.core.service.exception.ValidacioDadesPeticioException;
import es.caib.pinbal.webapp.command.ConsultaCommand;
import es.caib.pinbal.webapp.command.ConsultaCommand.ConsultaCommandAmbDocumentObligatori;
import es.caib.pinbal.webapp.command.ConsultaCommand.ConsultaCommandAmbDocumentTipusCif;
import es.caib.pinbal.webapp.command.ConsultaCommand.ConsultaCommandAmbDocumentTipusDni;
import es.caib.pinbal.webapp.command.ConsultaCommand.ConsultaCommandAmbDocumentTipusNie;
import es.caib.pinbal.webapp.command.ConsultaCommand.ConsultaCommandAmbDocumentTipusNif;
import es.caib.pinbal.webapp.command.ConsultaCommand.ConsultaCommandAmbDocumentTipusPass;
import es.caib.pinbal.webapp.command.ConsultaCommand.ConsultaCommandMultiple;
import es.caib.pinbal.webapp.command.ConsultaFiltreCommand;
import es.caib.pinbal.webapp.common.AlertHelper;
import es.caib.pinbal.webapp.common.EntitatHelper;
import es.caib.pinbal.webapp.common.RequestSessionHelper;
import es.caib.pinbal.webapp.common.RolHelper;
import es.caib.pinbal.webapp.common.ValidationHelper;
import es.caib.pinbal.webapp.datatables.ServerSideColumn;
import es.caib.pinbal.webapp.datatables.ServerSideRequest;
import es.caib.pinbal.webapp.datatables.ServerSideResponse;
import es.caib.pinbal.webapp.validation.consultes.DadesConsultaMultipleValidator;
import es.caib.pinbal.webapp.validation.consultes.DadesConsultaSimpleValidator;
import es.caib.pinbal.webapp.view.SpreadSheetReader;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * Controlador per a la pàgina de consultes.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Slf4j
@Controller
@RequestMapping("/consulta")
public class ConsultaController extends BaseController {

	private static final String PREFIX_CAMP_DADES_ESPECIFIQUES = "camp_";
	public static final String SESSION_ATTRIBUTE_FILTRE = "ConsultaController.session.filtre";
	public static final String SESSION_CONSULTA_HISTORIC = "consulta_delegat";
	public static final String FORMAT_DATA_DADES_ESPECIFIQUES = "dd/MM/yyyy";

	private static Map<String, FitxerErrors> fitxersAmbErrors = new HashMap<>();

	@Autowired
	private EntitatService entitatService;
	@Autowired
	private ProcedimentService procedimentService;
	@Autowired
	private ServeiService serveiService;
	@Autowired
	private ConsultaService consultaService;
	@Autowired
	private HistoricConsultaService historicConsultaService;
	@Autowired
	private UsuariService usuariService;

	@Autowired(required = true)
	private javax.validation.Validator validator;

	@RequestMapping(method = RequestMethod.GET)
	public String get(HttpServletRequest request, Model model) throws Exception {
		if (!EntitatHelper.isDelegatEntitatActual(request))
			return "delegatNoAutoritzat";
		EntitatDto entitat = EntitatHelper.getEntitatActual(request, entitatService);
		if (entitat != null) {
			omplirModelPerFiltreTaula(request, entitat, model);
		}
		getOrigens(model);
		return "consulta";
	}

	private void getOrigens(Model model) {
		List<CodiValor> origens = new ArrayList<>();
		origens.add(new CodiValor("true", "admin.consulta.list.filtre.origen.recobriment"));
		origens.add(new CodiValor("false", "admin.consulta.list.filtre.origen.web"));
		model.addAttribute("origens", origens);
	}

	@RequestMapping(method = RequestMethod.POST)
	public String post(HttpServletRequest request, @Valid ConsultaFiltreCommand command, BindingResult bindingResult,
			@RequestParam(value = "accio", required = false) String accio, Model model) throws Exception {
		if (!EntitatHelper.isDelegatEntitatActual(request))
			return "delegatNoAutoritzat";
		if ("netejar".equals(accio)) {
			RequestSessionHelper.esborrarObjecteSessio(request, SESSION_ATTRIBUTE_FILTRE);
			return "redirect:.";
		} else {
			EntitatDto entitat = EntitatHelper.getEntitatActual(request, entitatService);
			if (entitat != null) {
				if (bindingResult.hasErrors()) {
					omplirModelPerFiltreTaula(request, entitat, model);
				} else {
					RequestSessionHelper.actualitzarObjecteSessio(request, SESSION_ATTRIBUTE_FILTRE, command);
					return "redirect:.";
				}
			}
		}
		return "consulta";
	}

	@RequestMapping(value = "/datatable", produces = "application/json", method = RequestMethod.GET)
	@ResponseBody
	public ServerSideResponse<ConsultaDto, Long> datatable(HttpServletRequest request, Model model)
			throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, NamingException,
			SQLException, EntitatNotFoundException {
		long t0 = System.currentTimeMillis();
		String error = null;
		ServerSideRequest serverSideRequest = new ServerSideRequest(request);
		boolean isDelegat = EntitatHelper.isDelegatEntitatActual(request);
		log.debug("[C_CONS_DT] Consulta si usuari es delegat (" + (System.currentTimeMillis() - t0) + "ms)");
		t0 = System.currentTimeMillis();
		if (!isDelegat) {
			error = "Delegat no autoritzat";
		}
		EntitatDto entitat = EntitatHelper.getEntitatActual(request, entitatService);
		if (entitat == null) {
			throw new EntitatNotFoundException();
		}
		log.debug("[C_CONS_DT] Consulta de l'entitat actual (" + (System.currentTimeMillis() - t0) + "ms)");
		t0 = System.currentTimeMillis();
		ConsultaFiltreCommand command = (ConsultaFiltreCommand) RequestSessionHelper.obtenirObjecteSessio(request,
				SESSION_ATTRIBUTE_FILTRE);
		if (command == null) {
			command = new ConsultaFiltreCommand();
			command.filtrarDarrersMesos(isHistoric(request) ? 9 : 3);
		} else {
			command.updateDefaultDataInici(isHistoric(request));
		}
		log.debug("[C_CONS_DT] Obtenció del filtre (" + (System.currentTimeMillis() - t0) + "ms)");
		t0 = System.currentTimeMillis();
		Page<ConsultaDto> page;
		ServerSideResponse<ConsultaDto, Long> response = null;
		if (error != null) {
			List<ConsultaDto> lista = new ArrayList<ConsultaDto>();
			page = new PageImpl<ConsultaDto>(lista, serverSideRequest.toPageable(), lista.size());
			response = new ServerSideResponse<ConsultaDto, Long>(serverSideRequest, page);
			response.setError(error);
			log.debug("[C_CONS_DT] Retornar resposta error (" + (System.currentTimeMillis() - t0) + "ms)");
		} else {
			List<ServerSideColumn> cols = serverSideRequest.getColumns();
			cols.get(0).setData("peticioId");
			cols.get(1).setData("data");
			cols.get(2).setData("procedimentCodi");
			cols.get(3).setData("serveiCodi");
			if (isHistoric(request)) {
				page = historicConsultaService.findSimplesByFiltrePaginatPerDelegat(entitat.getId(),
						ConsultaFiltreCommand.asDto(command), serverSideRequest.toPageable());
			} else {
				page = consultaService.findSimplesByFiltrePaginatPerDelegat(entitat.getId(),
						ConsultaFiltreCommand.asDto(command), serverSideRequest.toPageable());
			}
			cols.get(0).setData("scspPeticionId");
			cols.get(1).setData("creacioData");
			cols.get(2).setData("procedimentCodiNom");
			cols.get(3).setData("serveiCodiNom");
			response = new ServerSideResponse<ConsultaDto, Long>(serverSideRequest, page);
			log.debug("[C_CONS_DT] Retornar resposta amb consultes (" + (System.currentTimeMillis() - t0) + "ms)");
		}
		return response;
	}

	@RequestMapping(value = "/{serveiCodi}/new", method = RequestMethod.GET)
	public String newGet(HttpServletRequest request, @PathVariable String serveiCodi, Model model)
			throws AccesExternException, ServeiNotFoundException, ScspException, EntitatNotFoundException, IOException,
			ParserConfigurationException, SAXException {
		EntitatDto entitat = EntitatHelper.getEntitatActual(request, entitatService);
		if (!EntitatHelper.isDelegatEntitatActual(request) || !usuariService
				.getEntitatUsuari(entitat.getId(), SecurityContextHolder.getContext().getAuthentication().getName())
				.isActiu())
			return "delegatNoAutoritzat";

		if (entitat == null) {
			AlertHelper.error(request, getMessage(request, "comu.error.no.entitat"));
			return "redirect:../../../index";
		}
		omplirModelPerMostrarFormulari(entitat.getId(), serveiCodi, model);
		ConsultaCommand command = new ConsultaCommand(serveiCodi);
		emplenarCommand(request, command, serveiCodi, entitat, true);
		model.addAttribute(command);
		return "consultaForm";

	}

	@RequestMapping(value = "/{serveiCodi}/new", method = RequestMethod.POST)
	public String newPost(HttpServletRequest request, @PathVariable String serveiCodi, @Valid ConsultaCommand command,
			BindingResult bindingResult, Model model)
			throws AccesExternException, ProcedimentServeiNotFoundException, ServeiNotFoundException,
			ConsultaNotFoundException, ServeiNotAllowedException, ScspException, EntitatNotFoundException,
			ValidacioDadesPeticioException, IOException, ParserConfigurationException, SAXException {
		if (!EntitatHelper.isDelegatEntitatActual(request))
			return "delegatNoAutoritzat";
		EntitatDto entitat = EntitatHelper.getEntitatActual(request, entitatService);
		List<String[]> liniesFitxer = null;
		if (entitat == null) {
			AlertHelper.error(request, getMessage(request, "comu.error.no.entitat"));
			return "redirect:../../index";
		}
		if (bindingResult.hasErrors()) {
			// El command te errors d'anotacions sense grups
			omplirModelPerMostrarFormulari(entitat.getId(), serveiCodi, model);
			emplenarCommand(request, command, serveiCodi, entitat, false);
			return "consultaForm";
		}
		List<Class<?>> grups = new ArrayList<Class<?>>();
		ServeiDto servei = serveiService.findAmbCodiPerDelegat(entitat.getId(), serveiCodi);
		List<ServeiCampDto> camps = serveiService.findServeiCamps(serveiCodi);
		if (!command.isMultiple()) {
			// Comprova les anotacions amb grups
			if (servei.isPinbalDocumentObligatori())
				grups.add(ConsultaCommandAmbDocumentObligatori.class);
			if (servei.isPinbalComprovarDocument()) {
				if (DocumentTipus.NIF.equals(command.getTitularDocumentTipus()))
					grups.add(ConsultaCommandAmbDocumentTipusNif.class);
				if (DocumentTipus.DNI.equals(command.getTitularDocumentTipus()))
					grups.add(ConsultaCommandAmbDocumentTipusDni.class);
				if (DocumentTipus.CIF.equals(command.getTitularDocumentTipus()))
					grups.add(ConsultaCommandAmbDocumentTipusCif.class);
				if (DocumentTipus.NIE.equals(command.getTitularDocumentTipus()))
					grups.add(ConsultaCommandAmbDocumentTipusNie.class);
				if (DocumentTipus.Passaport.equals(command.getTitularDocumentTipus()))
					grups.add(ConsultaCommandAmbDocumentTipusPass.class);
			}
			new ValidationHelper(validator).isValid(command, bindingResult, grups.toArray(new Class[grups.size()]));
			emplenarCommand(request, command, serveiCodi, entitat, false);
			new DadesConsultaSimpleValidator(serveiService, serveiCodi).validate(command, bindingResult);
		} else {
			MultipartFile fitxer = command.getMultipleFitxer();
			grups.add(ConsultaCommandMultiple.class);
			new ValidationHelper(validator).isValid(command, bindingResult, grups.toArray(new Class[grups.size()]));
			// Validar fitxer
			if (!bindingResult.hasErrors()) {
				try {
					FitxerDto fitxerDto = FitxerDto.builder().nom(fitxer.getOriginalFilename())
							.contentType(fitxer.getContentType()).contingut(fitxer.getBytes()).build();
					liniesFitxer = readFile(fitxerDto, bindingResult);
					int numPeticions = liniesFitxer.size() - 1;
					if (servei.getScspMaxSolicitudesPeticion() > 0
							&& numPeticions > servei.getScspMaxSolicitudesPeticion()) {
						LOGGER.error("Error al processar dades de la petició múltiple",
								"El fitxer excedeix el màxim de sol·licituds permeses pel servei");
						bindingResult.rejectValue("multipleFitxer", "PeticioMultiple.fitxer.massa.peticions",
								"El fitxer excedeix el màxim de sol·licituds permeses pel servei");
					}

					if (!bindingResult.hasErrors()) {
						DadesConsultaMultipleValidator dadesConsultaMultipleValidator = new DadesConsultaMultipleValidator(
								serveiService, liniesFitxer, camps, servei, bindingResult, request.getLocale());
						dadesConsultaMultipleValidator.validate();
						command.setMultipleErrorsValidacio(dadesConsultaMultipleValidator.getErrorsValidacio());

						if (dadesConsultaMultipleValidator.hasErrors()) {
							// Afegir els errors al fitxer, i que es pugui descarregar
							try {
								FitxerDto fitxerErrors = SpreadSheetReader.addColumnaToSpreadSheat(fitxerDto,
										getErrorsPerFila(dadesConsultaMultipleValidator.getErrorsValidacioPerLinia()));
								Path tempFile = Files.createTempFile(null, fitxerErrors.getExtensio());
								Files.write(tempFile, fitxerErrors.getContingut());
								String fitxerUuid = UUID.randomUUID().toString();
								fitxersAmbErrors.put(fitxerUuid,
										FitxerErrors.builder().nom(fitxerErrors.getNomSenseExtensio())
												.extensio(fitxerErrors.getExtensio()).path(tempFile)
												.contentType(fitxerErrors.getContentType()).build());
								model.addAttribute("fitxerAmbErrors", fitxerUuid);
							} catch (Exception ex) {
								log.error("No ha estat possible generar el fitxer de consulta multiple amb els errors",
										ex);
							}
						}
					}
				} catch (Exception ex) {
					LOGGER.error("Error al processar dades de la petició múltiple", ex);
					bindingResult.rejectValue("multipleFitxer", "PeticioMultiple.fitxer.format",
							"Errors en el contingut del fitxer");
				}
			}
			emplenarCommand(request, command, serveiCodi, entitat, false);
		}
		if (bindingResult.hasErrors()) {
			omplirModelPerMostrarFormulari(entitat.getId(), serveiCodi, model);
			return "consultaForm";
		}

		boolean error = false;
		try {
			ConsultaDto consulta = null;
			if (!command.isMultiple()) {
				consulta = novaConsulta(command);
			} else {
				consulta = novaConsultaMultiple(command, liniesFitxer.get(0),
						liniesFitxer.subList(1, liniesFitxer.size()));
			}
			if (consulta.isEstatError()) {
				AlertHelper.error(request,
						getMessage(request, "consulta.controller.recepcio.error") + ": " + consulta.getError());
				error = true;
			} else {
				AlertHelper.success(request, getMessage(request, "consulta.controller.recepcio.ok"));
			}
		} catch (ConsultaScspException ex) {
			AlertHelper.error(request,
					getMessage(request, "consulta.controller.enviament.error") + ": " + ex.getMessage());
			error = true;
		}

		if (error) {
			omplirModelPerMostrarFormulari(entitat.getId(), serveiCodi, model);
			model.addAttribute("reintentar", true);
			return "consultaForm";
		}
		if (!command.isMultiple()) {
			return "redirect:../../consulta";
		} else
			return "redirect:../../consulta/multiple";
	}

	@RequestMapping(value = "/errors/{uuid}/download", method = RequestMethod.GET)
	@ResponseBody
	public JsonResponse errorsDownload(HttpServletRequest request, HttpServletResponse response,
			@PathVariable String uuid) throws Exception {
		FitxerErrors fitxerErrors = fitxersAmbErrors.get(uuid);
		fitxersAmbErrors.remove(uuid);

		if (fitxerErrors == null) {
			return new JsonResponse(true, "Fitxer no trobat");
		}

		try {
			FitxerDto fitxer = FitxerDto.builder().nom(fitxerErrors.getNom() + "_errors." + fitxerErrors.getExtensio())
					.contentType(fitxerErrors.getContentType()).contingut(Files.readAllBytes(fitxerErrors.getPath()))
					.build();
			Files.deleteIfExists(fitxerErrors.getPath());
			return new JsonResponse(fitxer);
		} catch (Exception ex) {
			return new JsonResponse(true, "Error llegint el fitxer amb els errors");
		}
	}

	private List<String> getErrorsPerFila(List<List<String>> errors) {
		List<String> errorsFilaUnificats = new ArrayList<>();
		// Les 3 primeres files no son consultes
		errorsFilaUnificats.add(null);
		errorsFilaUnificats.add(null);
		errorsFilaUnificats.add(null);
		for (List<String> errorsFila : errors) {
			errorsFilaUnificats.add(unificaErrors(errorsFila));
		}
		return errorsFilaUnificats;
	}

	private String unificaErrors(List<String> errorsFila) {
		String errors = "";
		if (errorsFila != null && !errorsFila.isEmpty()) {
			for (String error : errorsFila) {
				errors += error + SEPARADOR;
			}
			errors = errors.substring(0, errors.length() - SEPARADOR.length());
		}
		return errors;
	}

	@RequestMapping(value = "/{consultaId}", method = RequestMethod.GET)
	public String info(HttpServletRequest request, @PathVariable Long consultaId, Model model)
			throws ConsultaNotFoundException, ScspException, ServeiNotFoundException {
		if (!EntitatHelper.isDelegatEntitatActual(request))
			return "delegatNoAutoritzat";
		EntitatDto entitat = EntitatHelper.getEntitatActual(request, entitatService);
		if (entitat != null) {
			ConsultaDto consulta = getConsultaDelegate(consultaId, isHistoric(request));
			model.addAttribute("consulta", consulta);
			model.addAttribute("servei",
					serveiService.findAmbCodiPerDelegat(entitat.getId(), consulta.getServeiCodi()));

			omplirModelAmbDadesEspecifiques(consulta.getServeiCodi(), model);
			return "consultaInfo";
		} else {
			AlertHelper.error(request, getMessage(request, "comu.error.no.entitat"));
			return "redirect:../../../index";
		}
	}

	@RequestMapping(value = "/{consultaId}/justificant/arxiu/detall", method = RequestMethod.GET)
	public String justificantArxiuDetall(HttpServletRequest request, HttpServletResponse response,
			@PathVariable Long consultaId, Model model) {

		model.addAttribute("arxiuDetall", isHistoric(request) ? historicConsultaService.obtenirArxiuInfo(consultaId)
				: consultaService.obtenirArxiuInfo(consultaId));
		model.addAttribute("mostrarArxiuInfo", true);
		return "contingutArxiu";
	}

	@RequestMapping(value = "/{consultaId}/justificant", method = RequestMethod.GET)
	public String justificant(HttpServletRequest request, HttpServletResponse response, @PathVariable Long consultaId,
			Model model) throws ConsultaNotFoundException {
		if (!EntitatHelper.isDelegatEntitatActual(request))
			return "delegatNoAutoritzat";
		EntitatDto entitat = EntitatHelper.getEntitatActual(request, entitatService);
		if (entitat != null) {
			try {
				JustificantDto justificant = getJustificant(consultaId, isHistoric(request));
				if (!justificant.isError()) {
					writeFileToResponse(justificant.getNom(), justificant.getContingut(), response);
					return null;
				} else {
					AlertHelper.error(request, getMessage(request, "consulta.controller.justificant.error"));
					return "redirect:../../consulta";
				}
			} catch (ConsultaNotFoundException ex) {
				throw ex;
			} catch (Exception ex) {
				ex.printStackTrace();
				AlertHelper.error(request, getMessage(request, "consulta.controller.justificant.error"));
				return "redirect:../../consulta";
			}
		} else {
			AlertHelper.error(request, getMessage(request, "comu.error.no.entitat"));
			return "redirect:../../index";
		}
	}

	@RequestMapping(value = "/{consultaId}/justificant/previsualitzacio", method = RequestMethod.GET)
	@ResponseBody
	public JsonResponse justificantPrevisualitzacio(HttpServletRequest request, @PathVariable Long consultaId) {

		if (!EntitatHelper.isDelegatEntitatActual(request))
			return new JsonResponse(true, "delegatNoAutoritzat");
		EntitatDto entitat = EntitatHelper.getEntitatActual(request, entitatService);
		if (entitat == null)
			return new JsonResponse(true, getMessage(request, "comu.error.no.entitat"));

		try {
			JustificantDto justificant = getJustificant(consultaId, isHistoric(request));
			if (justificant.isError())
				return new JsonResponse(true, getMessage(request, "consulta.controller.justificant.error"));
			return new JsonResponse(justificant);
		} catch (ConsultaNotFoundException ex) {
			return new JsonResponse(true, ex.getMessage());
		} catch (Exception ex) {
			return new JsonResponse(true, getMessage(request, "consulta.controller.justificant.error"));
		}
	}

	@RequestMapping(value = "/{consultaId}/justificantReintentar", method = RequestMethod.GET)
	public String justificantReintentar(HttpServletRequest request, HttpServletResponse response,
			@PathVariable Long consultaId, @RequestParam(value = "info", required = false) Boolean info)
			throws ConsultaNotFoundException {
		if (!EntitatHelper.isDelegatEntitatActual(request))
			return "delegatNoAutoritzat";
		EntitatDto entitat = EntitatHelper.getEntitatActual(request, entitatService);
		if (entitat != null) {
			try {
				JustificantDto justificant = consultaService.reintentarGeneracioJustificant(consultaId, false, false);
				if (!justificant.isError()) {
					AlertHelper.success(request, getMessage(request, "consulta.controller.justificant.regenerat"));
				} else {
					AlertHelper.error(request, getMessage(request, "consulta.controller.justificant.error"));
				}
			} catch (ConsultaNotFoundException ex) {
				throw ex;
			} catch (Exception ex) {
				AlertHelper.error(request, getMessage(request, "consulta.controller.justificant.error"));
			}
			if (info != null && info.booleanValue()) {
				return "redirect:../../consulta/" + consultaId;
			} else {
				return "redirect:../../consulta";
			}
		} else {
			AlertHelper.error(request, getMessage(request, "comu.error.no.entitat"));
			return "redirect:../../index";
		}
	}

	@RequestMapping(value = "/{consultaId}/xmlPeticio", method = RequestMethod.GET)
	public String xmlPeticio(HttpServletRequest request, HttpServletResponse response, @PathVariable Long consultaId,
			Model model) throws ConsultaNotFoundException, ScspException {
		if (!EntitatHelper.isDelegatEntitatActual(request))
			return "delegatNoAutoritzat";
		EntitatDto entitat = EntitatHelper.getEntitatActual(request, entitatService);
		if (entitat != null) {
			ConsultaDto consulta = getConsultaDelegate(consultaId, isHistoric(request));
			model.addAttribute("consulta", consulta);
			model.addAttribute("mostrarPeticio", new Boolean(true));
			model.addAttribute("mostrarResposta", new Boolean(false));
			return "consultaXml";
		} else {
			AlertHelper.error(request, getMessage(request, "comu.error.no.entitat"));
			return "redirect:../../index";
		}
	}

	@RequestMapping(value = "/{consultaId}/xmlResposta", method = RequestMethod.GET)
	public String xmlResposta(HttpServletRequest request, HttpServletResponse response, @PathVariable Long consultaId,
			Model model) throws ConsultaNotFoundException, ScspException {
		if (!EntitatHelper.isDelegatEntitatActual(request))
			return "delegatNoAutoritzat";
		EntitatDto entitat = EntitatHelper.getEntitatActual(request, entitatService);
		if (entitat != null) {
			ConsultaDto consulta = getConsultaDelegate(consultaId, isHistoric(request));
			model.addAttribute("consulta", consulta);
			model.addAttribute("mostrarPeticio", new Boolean(false));
			model.addAttribute("mostrarResposta", new Boolean(true));
			return "consultaXml";
		} else {
			AlertHelper.error(request, getMessage(request, "comu.error.no.entitat"));
			return "redirect:../../index";
		}
	}

	@RequestMapping(value = "/serveisPermesosPerProcediment/{procedimentId}", method = RequestMethod.GET)
	public String serveisPermesosPerProcedimentAmbId(HttpServletRequest request, HttpServletResponse response,
			@PathVariable Long procedimentId, Model model)
			throws IOException, EntitatNotFoundException, ProcedimentNotFoundException {
		if (EntitatHelper.isDelegatEntitatActual(request)) {
			EntitatDto entitat = EntitatHelper.getEntitatActual(request, entitatService);
			model.addAttribute("serveis",
					serveiService.findPermesosAmbProcedimentPerDelegat(entitat.getId(), procedimentId));
		}
		return "serveiSelectJson";
	}

	@RequestMapping(value = "/serveisPermesosPerProcediment", method = RequestMethod.GET)
	public String serveisPermesosPerProcedimentSenseId(HttpServletRequest request, HttpServletResponse response,
			Model model) throws IOException, EntitatNotFoundException, ProcedimentNotFoundException {
		return serveisPermesosPerProcedimentAmbId(request, response, null, model);
	}

	@RequestMapping(value = "/{serveiCodi}/plantilla/{tipusPlantilla}", method = RequestMethod.GET)
	public String plantillaCsvGet(HttpServletRequest request, @PathVariable String serveiCodi,
			@PathVariable String tipusPlantilla, Model model)
			throws ScspException, ServeiNotFoundException, EntitatNotFoundException {

		if (!EntitatHelper.isDelegatEntitatActual(request))
			return "delegatNoAutoritzat";
		EntitatDto entitat = EntitatHelper.getEntitatActual(request, entitatService);
		if (entitat != null) {
			model.addAttribute("servei", serveiService.findAmbCodiPerDelegat(entitat.getId(), serveiCodi));
			model.addAttribute("campsDadesEspecifiques", serveiService.findServeiCamps(serveiCodi));
			if ("CSV".equals(tipusPlantilla)) {
				return "peticioMultiplePlantillaCsvView";
			} else if ("ODS".equals(tipusPlantilla)) {
				return "peticioMultiplePlantillaOdsView";
			}
			return "peticioMultiplePlantillaExcelView";
		} else {
			AlertHelper.error(request, getMessage(request, "comu.error.no.entitat"));
			return "redirect:../../../index";
		}
	}

	@RequestMapping(value = "/{serveiCodi}/downloadAjuda", method = RequestMethod.GET)
	public String downloadAjuda(HttpServletRequest request, HttpServletResponse response,
			@PathVariable String serveiCodi) throws ServeiNotFoundException {
		try {
			ServeiDto servei = null;
			if (serveiCodi != null)
				servei = serveiService.findAmbCodiPerDelegat(
						EntitatHelper.getEntitatActual(request, entitatService).getId(), serveiCodi);

			response.setHeader("Pragma", "");
			response.setHeader("Expires", "");
			response.setHeader("Cache-Control", "");
			response.setHeader("Content-Disposition", "attachment; filename=\"" + servei.getFitxerAjudaNom() + "\"");
			response.setContentType(servei.getFitxerAjudaMimeType());
			response.getOutputStream().write(servei.getFitxerAjudaContingut());

			return null;
		} catch (Exception e) {
			AlertHelper.error(request, getMessage(request, "servei.controller.servei.download.ajuda"));
		}
		return "redirect:servei";
	}

	// Regles

	@ResponseBody
	@RequestMapping(value = "/{serveiCodi}/camps/regles", method = RequestMethod.POST)
	public List<CampFormProperties> campsRegles(HttpServletRequest request, @PathVariable String serveiCodi,
			@RequestParam(value = "campsModificats[]", required = false) String[] campsModificats)
			throws ServeiNotFoundException {
		return serveiService.getCampsByserveiRegla(serveiCodi, campsModificats);
	}

	@ResponseBody
	@RequestMapping(value = "/{serveiCodi}/grups/regles", method = RequestMethod.POST)
	public List<CampFormProperties> grupsRegles(HttpServletRequest request, @PathVariable String serveiCodi,
			@RequestParam(value = "grupsModificats[]", required = false) String[] grupsModificats)
			throws ServeiNotFoundException {
		return serveiService.getGrupsByserveiRegla(serveiCodi, grupsModificats);
	}

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		SimpleDateFormat dateFormat = new SimpleDateFormat(FORMAT_DATA_DADES_ESPECIFIQUES);
		dateFormat.setLenient(false);
		binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
	}

	@RequestMapping(value = "/excel")
	public String excel(HttpServletRequest request, Model model) throws Exception {
		if (!RolHelper.isRolActualAdministrador(request) && !EntitatHelper.isRepresentantEntitatActual(request))
			return "representantNoAutoritzat";
		ConsultaFiltreCommand command = (ConsultaFiltreCommand) RequestSessionHelper.obtenirObjecteSessio(request,
				SESSION_ATTRIBUTE_FILTRE);
		EntitatDto entitat = EntitatHelper.getEntitatActual(request, entitatService);
		if (command == null) {
			command = new ConsultaFiltreCommand();
			command.filtrarDarrersMesos(isHistoric(request) ? 9 : 3);
		} else {
			command.updateDefaultDataInici(isHistoric(request));
		}
		model.addAttribute(command);

		Page<ConsultaDto> page;
		if (isHistoric(request)) {
			page = historicConsultaService.findSimplesByFiltrePaginatPerDelegat(entitat.getId(),
					ConsultaFiltreCommand.asDto(command), null);
		} else {
			page = consultaService.findSimplesByFiltrePaginatPerDelegat(entitat.getId(),
					ConsultaFiltreCommand.asDto(command), null);
		}
		model.addAttribute("consultaList", page.getContent());

		return "consultaExcelView";
	}

	private void emplenarCommand(HttpServletRequest request, ConsultaCommand command, String serveiCodi,
			EntitatDto entitat, boolean inicialitzacioCommand) throws AccesExternException, ServeiNotFoundException,
			ScspException, IOException, ParserConfigurationException, SAXException {
		command.setServeiCodi(serveiCodi);
		UsuariDto dadesUsuari = usuariService.getDades();
		if (dadesUsuari != null) {
			if (inicialitzacioCommand) {
				command.setFuncionariNom(dadesUsuari.getNom());
				command.setDepartamentNom(dadesUsuari.getDepartament());
				command.setFinalitat(dadesUsuari.getFinalitat());
			}
			command.setFuncionariNif(dadesUsuari.getNif());
		}
		command.setEntitatNom(entitat.getNom());
		command.setEntitatCif(entitat.getCif());
		if (command.getDepartamentNom() == null || command.getDepartamentNom().isEmpty()) {
			String usuariActual = request.getUserPrincipal().getName();
			for (EntitatUsuariDto entitatUsuari : entitat.getUsuarisDelegat()) {
				if (entitatUsuari.getUsuari().getCodi().equals(usuariActual)) {
					command.setDepartamentNom(entitatUsuari.getDepartament());
					break;
				}
			}
		}
		command.setDadesEspecifiques(getDadesEspecifiques(request, serveiCodi));
		command.setMultipleFitxer(null);
		ServeiDto servei = serveiService.findAmbCodiPerDelegat(entitat.getId(), serveiCodi);
		if (servei.isConsultaMultiplePermesa() && !servei.isConsultaSimplePermesa()) {
			command.setMultiple(true);
		}
	}

	@SuppressWarnings("unchecked")
	private Map<String, Object> getDadesEspecifiques(HttpServletRequest request, String serveiCodi)
			throws ScspException, ServeiNotFoundException, IOException, ParserConfigurationException, SAXException {
		List<ServeiCampDto> serveiCamps = serveiService.findServeiCamps(serveiCodi);
		Map<String, Object> resposta = new HashMap<String, Object>();
		Enumeration<String> paramNames = request.getParameterNames();
		while (paramNames.hasMoreElements()) {
			String paramName = paramNames.nextElement();
			if (paramName.startsWith(PREFIX_CAMP_DADES_ESPECIFIQUES)) {
				String campId = paramName.substring(PREFIX_CAMP_DADES_ESPECIFIQUES.length());
				for (ServeiCampDto serveiCamp : serveiCamps) {
					if (serveiCamp.getId().equals(new Long(campId))) {
						// Si es un BOOLEA hi pot haver més d'un valor
						String[] valors = request.getParameterValues(paramName);
						if (valors.length != 1) {
							for (String valor : valors) {
								if (valor.equalsIgnoreCase("on")) {
									resposta.put(serveiCamp.getPath(), request.getParameter(paramName));
									break;
								}
							}
						} else {
							resposta.put(serveiCamp.getPath(), request.getParameter(paramName));
						}
						break;
					}
				}
			}
		}
		if (request instanceof MultipartHttpServletRequest) {
			MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
			Iterator<String> fileNamesIt = multipartRequest.getFileNames();
			while (fileNamesIt.hasNext()) {
				String fileName = fileNamesIt.next();
				if (fileName.startsWith(PREFIX_CAMP_DADES_ESPECIFIQUES)) {
					String campId = fileName.substring(PREFIX_CAMP_DADES_ESPECIFIQUES.length());
					for (ServeiCampDto serveiCamp : serveiCamps) {
						// ARXIU BINARI
						if (ServeiCampDtoTipus.ADJUNT_BINARI.equals(serveiCamp.getTipus())
								&& serveiCamp.getId().equals(new Long(campId))) {
							MultipartFile multipartFile = multipartRequest.getFile(fileName);
							resposta.put(serveiCamp.getPath(),
									new String(Base64.encodeBase64(multipartFile.getBytes())));
						}

						// ARXIU XML
						if (ServeiCampDtoTipus.ADJUNT_XML.equals(serveiCamp.getTipus())
								&& serveiCamp.getId().equals(new Long(campId))) {
							MultipartFile multipartFile = multipartRequest.getFile(fileName);

							String contentType = multipartFile.getContentType();

							if (!"text/xml".equalsIgnoreCase(contentType)
									&& !"application/xml".equalsIgnoreCase(contentType))
								throw new IOException(getMessage(request, "consulta.fitxer.camp.document.xml"));

							////////////
							DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
							DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
							InputStream inputStream = multipartFile.getInputStream();
							Document docXml = dBuilder.parse(inputStream);
							////////////

							resposta.put(serveiCamp.getPath(), docXml);
						}
					}
				}
			}
		}
		return resposta;
	}

	private void omplirModelPerFiltreTaula(HttpServletRequest request, EntitatDto entitat, Model model)
			throws Exception {
		ConsultaFiltreCommand command = (ConsultaFiltreCommand) RequestSessionHelper.obtenirObjecteSessio(request,
				SESSION_ATTRIBUTE_FILTRE);
		if (command == null) {
			command = new ConsultaFiltreCommand();
			command.filtrarDarrersMesos(isHistoric(request) ? 9 : 3);
			UsuariDto usuari = usuariService.getDades();
			command.setProcediment(usuari.getProcedimentId());
			command.setServei(usuari.getServeiCodi());
			RequestSessionHelper.actualitzarObjecteSessio(request, SESSION_ATTRIBUTE_FILTRE, command);
		} else {
			command.updateDefaultDataInici(isHistoric(request));
		}
		command.eliminarEspaisCampsCerca();
		model.addAttribute("filtreCommand", command);
		long t0 = System.currentTimeMillis();
		model.addAttribute("procediments", procedimentService.findAmbEntitatPerDelegat(entitat.getId()));
		log.debug("[C_CONS] Consulta de procediments (" + (System.currentTimeMillis() - t0) + "ms)");
		t0 = System.currentTimeMillis();
		model.addAttribute("serveis",
				serveiService.findPermesosAmbProcedimentPerDelegat(entitat.getId(), command.getProcediment()));
		model.addAttribute("historic", isHistoric(request));
		log.debug("[C_CONS] Consulta de serveis (" + (System.currentTimeMillis() - t0) + "ms)");
	}

	private void omplirModelPerMostrarFormulari(Long entitatId, String serveiCodi, Model model)
			throws ScspException, ServeiNotFoundException, EntitatNotFoundException {
		model.addAttribute("procediments", procedimentService.findActiusAmbEntitatIServeiCodi(entitatId, serveiCodi));
		model.addAttribute("servei", serveiService.findAmbCodiPerDelegat(entitatId, serveiCodi));
		omplirModelAmbDadesEspecifiques(serveiCodi, model);
	}

	private void omplirModelAmbDadesEspecifiques(String serveiCodi, Model model)
			throws ScspException, ServeiNotFoundException {
		List<NodeDto<DadaEspecificaDto>> llistaArbreDadesEspecifiques = serveiService
				.generarArbreDadesEspecifiques(serveiCodi).toList();
		model.addAttribute("llistaArbreDadesEspecifiques", llistaArbreDadesEspecifiques);
		List<ServeiCampDto> camps = serveiService.findServeiCamps(serveiCodi);
		model.addAttribute("campsDadesEspecifiques", camps);
		Map<Long, List<ServeiCampDto>> campsAgrupats = new HashMap<Long, List<ServeiCampDto>>();
		for (ServeiCampDto camp : camps) {
			Long clau = (camp.getGrup() != null) ? camp.getGrup().getId() : null;
			if (campsAgrupats.get(clau) == null) {
				campsAgrupats.put(clau, new ArrayList<ServeiCampDto>());
			}
			campsAgrupats.get(clau).add(camp);
		}
		List<ServeiCampGrupDto> grups = serveiService.findServeiCampGrups(serveiCodi);
		model.addAttribute("campsDadesEspecifiquesAgrupats", campsAgrupats);
		model.addAttribute("grups", grups);
		boolean mostraDadesEspecifiques = false;
		for (ServeiCampDto camp : camps) {
			if (camp.isVisible()) {
				mostraDadesEspecifiques = true;
				break;
			}
		}
		model.addAttribute("mostrarDadesEspecifiques", mostraDadesEspecifiques);
		if (mostraDadesEspecifiques) {
			List<Long> campsRegles = serveiService.findCampIdsByReglesServei(serveiCodi);
			List<Long> grupsRegles = serveiService.findGrupIdsByReglesServei(serveiCodi);

			if (campsRegles != null && !campsRegles.isEmpty()) {
				for (ServeiCampDto camp : camps) {
					if (campsRegles.contains(camp.getId()))
						camp.setCampRegla(true);
				}
			}
			if (grupsRegles != null && !grupsRegles.isEmpty()) {
				for (ServeiCampGrupDto grup : grups) {
					if (grupsRegles.contains(grup.getId()))
						grup.setGrupRegla(true);
					if (grup.getFills() != null && !grup.getFills().isEmpty()) {
						for (ServeiCampGrupDto subgrup : grup.getFills()) {
							if (grupsRegles.contains(subgrup.getId()))
								subgrup.setGrupRegla(true);
						}
					}
				}
			}
			// List<ServeiReglaDto> serveiReglaDtos =
			// serveiService.serveiReglesFindAll(serveiCodi);
			// if (serveiReglaDtos != null && !serveiReglaDtos.isEmpty()) {
			//
			// model.addAttribute("campsRegles", );
			model.addAttribute("grupsRegles_", "");
			// }
		}
	}

	private ConsultaDto novaConsulta(ConsultaCommand command) throws ProcedimentServeiNotFoundException,
			ConsultaNotFoundException, ServeiNotAllowedException, ConsultaScspException {
		ConsultaDto consulta = ConsultaCommand.asDto(command);
		if (consultaService.isOptimitzarTransaccionsNovaConsulta()) {
			ConsultaDto consultaInit = consultaService.novaConsultaInit(consulta);
			consultaService.novaConsultaEnviament(consultaInit.getId(), consulta);
			return consultaService.novaConsultaEstat(consultaInit.getId());
		} else {
			return consultaService.novaConsulta(ConsultaCommand.asDto(command));
		}
	}

	private ConsultaDto novaConsultaMultiple(ConsultaCommand command, String[] campsPeticioMultiple,
			List<String[]> dadesPeticioMultiple) throws ProcedimentServeiNotFoundException, ConsultaNotFoundException,
			ServeiNotAllowedException, ConsultaScspException, ValidacioDadesPeticioException {
		ConsultaDto consulta = ConsultaCommand.asDto(command);
		consulta.setCampsPeticioMultiple(campsPeticioMultiple);
		consulta.setDadesPeticioMultiple(
				dadesPeticioMultiple.toArray(new String[dadesPeticioMultiple.size()][campsPeticioMultiple.length]));
		return consultaService.novaConsultaMultiple(consulta);
	}

	private List<String[]> readFile(FitxerDto fitxer, Errors errors) throws Exception {
		List<String[]> linies = new ArrayList<String[]>();
		// Obtenir dades del fitxer
		try {
			linies = SpreadSheetReader.getLinesFromSpreadSheat(fitxer);
			// 1. Mínim 1 registre de dades (fila 4)
			if (linies.size() < 4) {
				errors.rejectValue("multipleFitxer", "PeticioMultiple.fitxer.buit",
						"El fitxer és buit. No te dades de peticions.");
			} else {
				linies = linies.subList(2, linies.size());
			}
		} catch (FileTypeException fte) {
			errors.rejectValue("multipleFitxer", "PeticioMultiple.fitxer.tipus",
					"Tipus de fitxer no suportat. Tipus acceptats: Excel i CSV.");
		}
		return linies;
	}

	// private List<ServeiCampDto> getCampsObligatoris(
	// HttpServletRequest request,
	// ServeiDto servei,
	// List<ServeiCampDto> camps) {
	// List<ServeiCampDto> campsObligatoris = getCampsGenericsObligatoris(request,
	// servei);
	// for (ServeiCampDto camp: camps) {
	// if (camp.isObligatori())
	// campsObligatoris.add(camp);
	// }
	// return campsObligatoris;
	// }

	private List<ServeiCampDto> getCampsGenericsObligatoris(HttpServletRequest request, ServeiDto servei) {
		List<ServeiCampDto> campsObligatoris = new ArrayList<ServeiCampDto>();
		if (servei.isPinbalDocumentObligatori()) {
			ServeiCampDto documentTipus = new ServeiCampDto();
			documentTipus.setEtiqueta(getMessage(request, "consulta.form.camp.document.tipus"));
			documentTipus.setPath("DatosGenericos/Titular/TipoDocumentacion");
			campsObligatoris.add(documentTipus);
			ServeiCampDto documentNum = new ServeiCampDto();
			documentNum.setEtiqueta(getMessage(request, "consulta.form.camp.document.num"));
			documentNum.setPath("DatosGenericos/Titular/Documentacion");
			campsObligatoris.add(documentNum);
		}
		return campsObligatoris;
	}

	private boolean isHistoric(HttpServletRequest request) {
		Object historic = request.getSession().getAttribute(SESSION_CONSULTA_HISTORIC);
		if (historic == null)
			return false;
		else
			return ((Boolean) historic).booleanValue();
	}

	private ConsultaDto getConsultaDelegate(Long consultaId, boolean historic)
			throws ConsultaNotFoundException, ScspException {
		ConsultaDto consulta;
		if (historic) {
			try {
				consulta = historicConsultaService.findOneDelegat(consultaId);
			} catch (Exception nfe) {
				consulta = consultaService.findOneDelegat(consultaId);
			}
		} else {
			try {
				consulta = consultaService.findOneDelegat(consultaId);
			} catch (Exception nfe) {
				consulta = historicConsultaService.findOneDelegat(consultaId);
			}
		}
		return consulta;
	}

	private JustificantDto getJustificant(Long consultaId, boolean historic) throws Exception {
		JustificantDto justificant;
		if (historic) {
			try {
				justificant = historicConsultaService.obtenirJustificant(consultaId, false);
			} catch (Exception nfe) {
				justificant = consultaService.obtenirJustificant(consultaId, false);
			}
		} else {
			try {
				justificant = consultaService.obtenirJustificant(consultaId, false);
			} catch (Exception nfe) {
				justificant = historicConsultaService.obtenirJustificant(consultaId, false);
			}
		}
		return justificant;
	}

	@Builder
	@Getter
	public static class FitxerErrors {
		private Path path;
		private String nom;
		private String extensio;
		private String contentType;
	}

	private static final Logger LOGGER = LoggerFactory.getLogger(ConsultaController.class);
}
