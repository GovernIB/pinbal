/**
 * 
 */
package es.caib.pinbal.webapp.controller;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;
import org.supercsv.io.CsvListReader;
import org.supercsv.io.ICsvListReader;
import org.supercsv.prefs.CsvPreference;

import es.caib.pinbal.core.dto.ArxiuDto;
import es.caib.pinbal.core.dto.ConsultaDto;
import es.caib.pinbal.core.dto.ConsultaDto.DocumentTipus;
import es.caib.pinbal.core.dto.EntitatDto;
import es.caib.pinbal.core.dto.EntitatUsuariDto;
import es.caib.pinbal.core.dto.OrdreDto;
import es.caib.pinbal.core.dto.OrdreDto.OrdreDireccio;
import es.caib.pinbal.core.dto.PaginaLlistatDto;
import es.caib.pinbal.core.dto.PaginacioAmbOrdreDto;
import es.caib.pinbal.core.dto.ServeiCampDto;
import es.caib.pinbal.core.dto.ServeiCampDto.ServeiCampDtoTipus;
import es.caib.pinbal.core.dto.ServeiDto;
import es.caib.pinbal.core.dto.UsuariDto;
import es.caib.pinbal.core.service.ConsultaService;
import es.caib.pinbal.core.service.EntitatService;
import es.caib.pinbal.core.service.ProcedimentService;
import es.caib.pinbal.core.service.ServeiService;
import es.caib.pinbal.core.service.UsuariService;
import es.caib.pinbal.core.service.exception.AccesExternException;
import es.caib.pinbal.core.service.exception.ConsultaNotFoundException;
import es.caib.pinbal.core.service.exception.EntitatNotFoundException;
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
import es.caib.pinbal.webapp.common.ValidationHelper;
import es.caib.pinbal.webapp.jmesa.JMesaGridHelper;
import es.caib.pinbal.webapp.jmesa.JMesaGridHelper.ConsultaPagina;
import es.caib.pinbal.webapp.view.ExcelHssfGet;

/**
 * Controlador per a la pàgina de consultes.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/consulta")
public class ConsultaController extends BaseController {

	private static final String PREFIX_CAMP_DADES_ESPECIFIQUES = "camp_";
	public static final String SESSION_ATTRIBUTE_FILTRE = "ConsultaController.session.filtre";
	private static final String FORMAT_DATA_DADES_ESPECIFIQUES = "dd/MM/yyyy";

	@Autowired
	private EntitatService entitatService;
	@Autowired
	private ProcedimentService procedimentService;
	@Autowired
	private ServeiService serveiService;
	@Autowired
	private ConsultaService consultaService;
	@Autowired
	private UsuariService usuariService;

	@Autowired(required = true)
	private javax.validation.Validator validator;

	@RequestMapping(method = RequestMethod.GET)
	public String get(
			HttpServletRequest request,
			Model model) throws Exception {
		if (!EntitatHelper.isDelegatEntitatActual(request))
			return "delegatNoAutoritzat";
		EntitatDto entitat = EntitatHelper.getEntitatActual(request, entitatService);
		if (entitat != null) {
			omplirModelPerMostrarLlistat(request, entitat, model);
		}
		return "consulta";
	}

	@RequestMapping(method = RequestMethod.POST)
	public String post(
			HttpServletRequest request,
			@Valid ConsultaFiltreCommand command,
			BindingResult bindingResult,
			Model model) throws Exception {
		if (!EntitatHelper.isDelegatEntitatActual(request))
			return "delegatNoAutoritzat";
		EntitatDto entitat = EntitatHelper.getEntitatActual(request, entitatService);
		if (entitat != null) {
			if (bindingResult.hasErrors()) {
				omplirModelPerMostrarLlistat(request, entitat, model);
			} else {
				RequestSessionHelper.actualitzarObjecteSessio(
						request,
						SESSION_ATTRIBUTE_FILTRE,
						command);
				return "redirect:.";
			}
		}
		return "consulta";
	}

	@RequestMapping(value = "/{serveiCodi}/new", method = RequestMethod.GET)
	public String newGet(
			HttpServletRequest request,
			@PathVariable String serveiCodi,
			Model model) throws AccesExternException, ServeiNotFoundException, ScspException, EntitatNotFoundException {
		if (!EntitatHelper.isDelegatEntitatActual(request))
			return "delegatNoAutoritzat";
		EntitatDto entitat = EntitatHelper.getEntitatActual(request, entitatService);
		if (entitat != null) {
			omplirModelPerMostrarFormulari(
					entitat.getId(),
					serveiCodi,
					model);
			ConsultaCommand command = new ConsultaCommand(serveiCodi);
			emplenarCommand(
					request,
					command,
					serveiCodi,
					entitat);
			model.addAttribute(command);
			return "consultaForm";
		} else {
			AlertHelper.error(
					request,
					getMessage(
							request, 
							"comu.error.no.entitat"));
			return "redirect:../../../index";
		}
	}

	@RequestMapping(value = "/{serveiCodi}/new", method = RequestMethod.POST)
	public String newPost(
			HttpServletRequest request,
			@PathVariable String serveiCodi,
			@Valid ConsultaCommand command,
			BindingResult bindingResult,
			Model model) throws AccesExternException, ProcedimentServeiNotFoundException, ServeiNotFoundException, ConsultaNotFoundException, ServeiNotAllowedException, ScspException, EntitatNotFoundException, ValidacioDadesPeticioException {
		if (!EntitatHelper.isDelegatEntitatActual(request))
			return "delegatNoAutoritzat";
		EntitatDto entitat = EntitatHelper.getEntitatActual(request, entitatService);
		List<String[]> liniesFitxer = null;
		if (entitat != null) {
			if (bindingResult.hasErrors()) {
				// El command te errors d'anotacions sense grups
				omplirModelPerMostrarFormulari(
						entitat.getId(),
						serveiCodi,
						model);
				emplenarCommand(
						request,
						command,
						serveiCodi,
						entitat);
				return "consultaForm";
			} else {
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
					new ValidationHelper(validator).isValid(
							command,
							bindingResult,
							grups.toArray(new Class[grups.size()]));
					emplenarCommand(
							request,
							command,
							serveiCodi,
							entitat);
					new DadesEspecifiquesValidator(
							serveiService.findServeiCamps(serveiCodi)).validate(
									command,
									bindingResult);
				} else {
					MultipartFile fitxer = command.getMultipleFitxer();
					grups.add(ConsultaCommandMultiple.class);
					new ValidationHelper(validator).isValid(
							command,
							bindingResult,
							grups.toArray(new Class[grups.size()]));
					// Validar fitxer					
					if (!bindingResult.hasErrors()) {
						try {
							liniesFitxer = readFile(fitxer, bindingResult);
							if (!bindingResult.hasErrors()) {
								List<String> errorsValidacio = new ArrayList<String>();
								validatePeticioMultipleFile(request, liniesFitxer, servei, camps, bindingResult, errorsValidacio);
								command.setMultipleErrorsValidacio(errorsValidacio);
							}
						} catch (Exception ex) {
							LOGGER.error(
									"Error al processar dades de la petició múltiple",
									ex);
							bindingResult.rejectValue(
									"multipleFitxer", 
									"PeticioMultiple.fitxer.format", 
									"Errors en el contingut del fitxer");
						}
					}
					emplenarCommand(
							request,
							command,
							serveiCodi,
							entitat);
				}
				if (bindingResult.hasErrors()) {
					omplirModelPerMostrarFormulari(
							entitat.getId(),
							serveiCodi,
							model);
					return "consultaForm";
				}
			}
			try {
				ConsultaDto consulta = null;
				if (!command.isMultiple()) {
					consulta = novaConsulta(command);
				} else {
					consulta = novaConsultaMultiple(
							command, 
							liniesFitxer.get(0),
							liniesFitxer.subList(1, liniesFitxer.size()));
				}
				if (consulta.isEstatError()) {
					AlertHelper.error(
							request,
							getMessage(
									request, 
									"consulta.controller.recepcio.error") + ": " + consulta.getError());
				} else {
					AlertHelper.success(
							request,
							getMessage(
									request, 
									"consulta.controller.recepcio.ok"));
				}
			} catch (ScspException ex) {
				AlertHelper.error(
						request,
						getMessage(
								request, 
								"consulta.controller.enviament.error") + ": " + ex.getMessage());
			}
			if (!command.isMultiple())
				return "redirect:../../consulta";
			else
				return "redirect:../../consulta/multiple";
		} else {
			AlertHelper.error(
					request,
					getMessage(
							request, 
							"comu.error.no.entitat"));
			return "redirect:../../index";
		}
	}

	@RequestMapping(value = "/{consultaId}", method = RequestMethod.GET)
	public String info(
			HttpServletRequest request,
			@PathVariable Long consultaId,
			Model model) throws ConsultaNotFoundException, ScspException, ServeiNotFoundException {
		if (!EntitatHelper.isDelegatEntitatActual(request))
			return "delegatNoAutoritzat";
		EntitatDto entitat = EntitatHelper.getEntitatActual(request, entitatService);
		if (entitat != null) {
			ConsultaDto consulta = consultaService.findOneDelegat(consultaId);
			model.addAttribute("consulta", consulta);
			model.addAttribute(
					"servei",
					serveiService.findAmbCodiPerDelegat(
							entitat.getId(),
							consulta.getServeiCodi()));
			omplirModelAmbDadesEspecifiques(
					consulta.getServeiCodi(),
					model);
			return "consultaInfo";
		} else {
			AlertHelper.error(
					request,
					getMessage(
							request, 
							"comu.error.no.entitat"));
			return "redirect:../../../index";
		}
	}

	@RequestMapping(value = "/{consultaId}/justificant", method = RequestMethod.GET)
	public String justificant(
			HttpServletRequest request,
			HttpServletResponse response,
			@PathVariable Long consultaId,
			Model model) throws ConsultaNotFoundException {
		if (!EntitatHelper.isDelegatEntitatActual(request))
			return "delegatNoAutoritzat";
		EntitatDto entitat = EntitatHelper.getEntitatActual(request, entitatService);
		if (entitat != null) {
			try {
				ArxiuDto arxiu = consultaService.obtenirJustificant(consultaId);
				writeFileToResponse(
						arxiu.getNom(),
						arxiu.getContingut(),
						response);
				return null;
			} catch (ConsultaNotFoundException ex) {
				throw ex;
			} catch (Exception ex) {
				AlertHelper.error(
						request,
						getMessage(
								request, 
								"consulta.controller.justificant.error") + ": " + ex.getMessage());
				return "redirect:../../consulta";
			}
		} else {
			AlertHelper.error(
					request,
					getMessage(
							request, 
							"comu.error.no.entitat"));
			return "redirect:../../index";
		}
	}

	@RequestMapping(value = "/{consultaId}/xmlPeticio", method = RequestMethod.GET)
	public String xmlPeticio(
			HttpServletRequest request,
			HttpServletResponse response,
			@PathVariable Long consultaId,
			Model model) throws ConsultaNotFoundException, ScspException {
		if (!EntitatHelper.isDelegatEntitatActual(request))
			return "delegatNoAutoritzat";
		EntitatDto entitat = EntitatHelper.getEntitatActual(request, entitatService);
		if (entitat != null) {
			ConsultaDto consulta = consultaService.findOneDelegat(consultaId);
			model.addAttribute("consulta", consulta);
			model.addAttribute("mostrarPeticio", new Boolean(true));
			model.addAttribute("mostrarResposta", new Boolean(false));
			return "consultaXml";
		} else {
			AlertHelper.error(
					request,
					getMessage(
							request, 
							"comu.error.no.entitat"));
			return "redirect:../../index";
		}
	}

	@RequestMapping(value = "/{consultaId}/xmlResposta", method = RequestMethod.GET)
	public String xmlResposta(
			HttpServletRequest request,
			HttpServletResponse response,
			@PathVariable Long consultaId,
			Model model) throws ConsultaNotFoundException, ScspException {
		if (!EntitatHelper.isDelegatEntitatActual(request))
			return "delegatNoAutoritzat";
		EntitatDto entitat = EntitatHelper.getEntitatActual(request, entitatService);
		if (entitat != null) {
			ConsultaDto consulta = consultaService.findOneDelegat(consultaId);
			model.addAttribute("consulta", consulta);
			model.addAttribute("mostrarPeticio", new Boolean(false));
			model.addAttribute("mostrarResposta", new Boolean(true));
			return "consultaXml";
		} else {
			AlertHelper.error(
					request,
					getMessage(
							request, 
							"comu.error.no.entitat"));
			return "redirect:../../index";
		}
	}

	@RequestMapping(value = "/serveisPermesosPerProcediment/{procedimentId}", method = RequestMethod.GET)
	public String serveisPermesosPerProcedimentAmbId(
			HttpServletRequest request,
			HttpServletResponse response,
			@PathVariable Long procedimentId,
			Model model) throws IOException, EntitatNotFoundException, ProcedimentNotFoundException {
		if (EntitatHelper.isDelegatEntitatActual(request)) {
			EntitatDto entitat = EntitatHelper.getEntitatActual(request, entitatService);
			model.addAttribute(
					"serveis",
					serveiService.findPermesosAmbProcedimentPerDelegat(
							entitat.getId(),
							procedimentId));
		}
		return "serveiSelectJson";
	}
	@RequestMapping(value = "/serveisPermesosPerProcediment", method = RequestMethod.GET)
	public String serveisPermesosPerProcedimentSenseId(
			HttpServletRequest request,
			HttpServletResponse response,
			Model model) throws IOException, EntitatNotFoundException, ProcedimentNotFoundException {
		return serveisPermesosPerProcedimentAmbId(
				request,
				response,
				null,
				model);
	}
	
	@RequestMapping(value = "/{serveiCodi}/plantilla/{tipusPlantilla}", method = RequestMethod.GET)
	public String plantillaCsvGet(
			HttpServletRequest request,
			@PathVariable String serveiCodi,
			@PathVariable String tipusPlantilla,
			Model model) throws ScspException, ServeiNotFoundException, EntitatNotFoundException {
		if (!EntitatHelper.isDelegatEntitatActual(request))
			return "delegatNoAutoritzat";
		EntitatDto entitat = EntitatHelper.getEntitatActual(request, entitatService);
		if (entitat != null) {
			model.addAttribute("servei", serveiService.findAmbCodiPerDelegat(entitat.getId(), serveiCodi));
			model.addAttribute("campsDadesEspecifiques", serveiService.findServeiCamps(serveiCodi));
			if ("CSV".equals(tipusPlantilla)) {
				return "peticioMultiplePlantillaCsvView";
			}
			return "peticioMultiplePlantillaExcelView";
		} else {
			AlertHelper.error(
					request,
					getMessage(
							request, 
							"comu.error.no.entitat"));
			return "redirect:../../../index";
		}
	}
	
	@RequestMapping(value = "/{serveiCodi}/downloadAjuda", method = RequestMethod.GET)
	public String downloadAjuda(
			HttpServletRequest request,
			HttpServletResponse response,
			@PathVariable String serveiCodi) throws ServeiNotFoundException {
		try {
			ServeiDto servei = null;
			if (serveiCodi != null)
				servei = serveiService.findAmbCodiPerDelegat(
						EntitatHelper.getEntitatActual(request, entitatService).getId(),
						serveiCodi);
			
			response.setHeader("Pragma", "");
			response.setHeader("Expires", "");
			response.setHeader("Cache-Control", "");
			response.setHeader("Content-Disposition", "attachment; filename=\"" + servei.getFitxerAjudaNom() + "\"");
			response.setContentType(servei.getFitxerAjudaMimeType());
			response.getOutputStream().write(servei.getFitxerAjudaContingut());
			
			return null;
		} catch (Exception e) {
			AlertHelper.error(
					request, 
					getMessage(
							request, 
							"servei.controller.servei.download.ajuda"));
		}
		return "redirect:servei";
	}

	@InitBinder
	public void initBinder(WebDataBinder binder) {
	    SimpleDateFormat dateFormat = new SimpleDateFormat(FORMAT_DATA_DADES_ESPECIFIQUES);
	    dateFormat.setLenient(false);
	    binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
	}



	private void emplenarCommand(
			HttpServletRequest request,
			ConsultaCommand command,
			String serveiCodi,
			EntitatDto entitat) throws AccesExternException, ServeiNotFoundException, ScspException {
		command.setServeiCodi(serveiCodi);
		UsuariDto dadesUsuari = usuariService.getDades();
		if (dadesUsuari != null) {
			command.setFuncionariNom(dadesUsuari.getNom());
			command.setFuncionariNif(dadesUsuari.getNif());
		}
		command.setEntitatNom(entitat.getNom());
		command.setEntitatCif(entitat.getCif());
		if (command.getDepartamentNom() == null || command.getDepartamentNom().isEmpty()) {
			String usuariActual = request.getUserPrincipal().getName();
			for (EntitatUsuariDto entitatUsuari: entitat.getUsuarisDelegat()) {
				if (entitatUsuari.getUsuari().getCodi().equals(usuariActual)) {
					command.setDepartamentNom(entitatUsuari.getDepartament());
					break;
				}
			}
		}
		command.setDadesEspecifiques(
				getDadesEspecifiques(
						request,
						serveiCodi));
		command.setMultipleFitxer(null);
	}

	@SuppressWarnings("unchecked")
	private Map<String, String> getDadesEspecifiques(
			HttpServletRequest request,
			String serveiCodi) throws ScspException, ServeiNotFoundException {
		//List<NodeDto<DadaEspecificaDto>> arbreDadesEspecifiques = serveiService.generarArbreDadesEspecifiques(serveiCodi).toList();
		List<ServeiCampDto> serveiCamps = serveiService.findServeiCamps(serveiCodi);
		Map<String, String> resposta = new HashMap<String, String>();
		Enumeration<String> paramNames = request.getParameterNames();
		while (paramNames.hasMoreElements()) {
			String paramName = paramNames.nextElement();
			if (paramName.startsWith(PREFIX_CAMP_DADES_ESPECIFIQUES)) {
				String campId = paramName.substring(PREFIX_CAMP_DADES_ESPECIFIQUES.length());
				for (ServeiCampDto serveiCamp: serveiCamps) {
					if (serveiCamp.getId().equals(new Long(campId))) {
						// Si es un BOOLEA hi pot haver més d'un valor
						String[] valors = request.getParameterValues(paramName);
						if (valors.length != 1) {
							for (String valor: valors) {
								if (valor.equalsIgnoreCase("on")) {
									resposta.put(
											serveiCamp.getPath(),
											request.getParameter(paramName));
									break;
								}
							}
						} else {
							resposta.put(
									serveiCamp.getPath(),
									request.getParameter(paramName));
						}
						break;
					}
				}
			}
		}
		return resposta;
	}

	private void omplirModelPerMostrarLlistat(
			HttpServletRequest request,
			EntitatDto entitat,
			Model model) throws Exception {
		ConsultaFiltreCommand command = (ConsultaFiltreCommand)RequestSessionHelper.obtenirObjecteSessio(
				request,
				SESSION_ATTRIBUTE_FILTRE);
		if (command == null)
			command = new ConsultaFiltreCommand();
		model.addAttribute(
				"filtreCommand",
				command);
		List<?> paginaConsultes = JMesaGridHelper.consultarPaginaIActualitzarLimit(
				"consultes",
				request,
				new ConsultaPaginaConsulta(
						consultaService,
						entitat,
						command),
				new OrdreDto("creacioData", OrdreDireccio.DESCENDENT));
		model.addAttribute(
				"consultes",
				paginaConsultes);
		model.addAttribute(
				"procediments",
				procedimentService.findAmbEntitatPerDelegat(entitat.getId()));
		model.addAttribute(
				"serveis",
				serveiService.findPermesosAmbProcedimentPerDelegat(
						entitat.getId(),
						command.getProcediment()));
	}

	private void omplirModelPerMostrarFormulari(
			Long entitatId,
			String serveiCodi,
			Model model) throws ScspException, ServeiNotFoundException, EntitatNotFoundException {
		model.addAttribute(
				"procediments",
				procedimentService.findActiusAmbEntitatIServeiCodi(
						entitatId,
						serveiCodi));
		model.addAttribute(
				"servei",
				serveiService.findAmbCodiPerDelegat(
						entitatId,
						serveiCodi));
		omplirModelAmbDadesEspecifiques(
				serveiCodi,
				model);
	}

	private void omplirModelAmbDadesEspecifiques(
			String serveiCodi,
			Model model) throws ScspException, ServeiNotFoundException {
		model.addAttribute(
				"llistaArbreDadesEspecifiques",
				serveiService.generarArbreDadesEspecifiques(serveiCodi).toList());
		List<ServeiCampDto> camps = serveiService.findServeiCamps(serveiCodi);
		model.addAttribute("campsDadesEspecifiques", camps);
		Map<Long, List<ServeiCampDto>> campsAgrupats = new HashMap<Long, List<ServeiCampDto>>();
		for (ServeiCampDto camp: camps) {
			Long clau = (camp.getGrup() != null) ? camp.getGrup().getId() : null;
			if (campsAgrupats.get(clau) == null) {
				campsAgrupats.put(clau, new ArrayList<ServeiCampDto>());
			}
			campsAgrupats.get(clau).add(camp);
		}
		model.addAttribute("campsDadesEspecifiquesAgrupats", campsAgrupats);
		model.addAttribute(
				"grups",
				serveiService.findServeiCampGrups(serveiCodi));
	}
	
	@SuppressWarnings("rawtypes")
	private class DadesEspecifiquesValidator implements Validator {
		private List<ServeiCampDto> camps;
		public DadesEspecifiquesValidator(List<ServeiCampDto> camps) {
			this.camps = camps;
		}
		public boolean supports(Class clazz) {
			return ConsultaCommand.class.equals(clazz);
		}
		public void validate(Object obj, Errors errors) {
			ConsultaCommand command = (ConsultaCommand)obj;
			SimpleDateFormat sdf = new SimpleDateFormat(FORMAT_DATA_DADES_ESPECIFIQUES);
			Map<String, String> dadesEspecifiquesValors = command.getDadesEspecifiques();
			for (ServeiCampDto camp: camps) {
				if (dadesEspecifiquesValors.get(camp.getPath()) == null || dadesEspecifiquesValors.get(camp.getPath()).isEmpty()) {
					if (camp.isObligatori())
						errors.rejectValue(
								"dadesEspecifiques[" + camp.getPath() + "]",
								"NotEmpty",
								"Aquest camp és obligatori");
				} else {
					if (ServeiCampDtoTipus.DATA.equals(camp.getTipus())) {
						String dataText = dadesEspecifiquesValors.get(camp.getPath());
						try {
							Date dataDate = sdf.parse(dataText);
							if (!sdf.format(dataDate).equals(dataText))
								errors.rejectValue(
										"dadesEspecifiques[" + camp.getPath() + "]",
										"DataValida",
										"La data és incorrecta");
						} catch (Exception ex) {
							errors.rejectValue(
									"dadesEspecifiques[" + camp.getPath() + "]",
									"DataValida",
									"La data és incorrecta");
						}
					}
				}
			}
		}
	}

	private ConsultaDto novaConsulta(
			ConsultaCommand command) throws ProcedimentServeiNotFoundException, ConsultaNotFoundException, ServeiNotAllowedException, ScspException {
		ConsultaDto consulta = ConsultaCommand.asDto(command);
		if (consultaService.isOptimitzarTransaccionsNovaConsulta()) {
			ConsultaDto consultaInit = consultaService.novaConsultaInit(
					consulta);
			consultaService.novaConsultaEnviament(
					consultaInit.getId(),
					consulta);
			return consultaService.novaConsultaEstat(
					consultaInit.getId());
		} else {
			return consultaService.novaConsulta(
					ConsultaCommand.asDto(command));
		}
	}

	private ConsultaDto novaConsultaMultiple(
			ConsultaCommand command,
			String[] campsPeticioMultiple,
			List<String[]> dadesPeticioMultiple) throws ProcedimentServeiNotFoundException, ConsultaNotFoundException, ServeiNotAllowedException, ScspException, ValidacioDadesPeticioException {
		ConsultaDto consulta = ConsultaCommand.asDto(command);
		consulta.setCampsPeticioMultiple(campsPeticioMultiple);
		consulta.setDadesPeticioMultiple(
				dadesPeticioMultiple.toArray(
						new String[dadesPeticioMultiple.size()][campsPeticioMultiple.length]));
		return consultaService.novaConsultaMultiple(consulta);
	}

	public class ConsultaPaginaConsulta implements ConsultaPagina<ConsultaDto> {
		ConsultaService consultaService;
		EntitatDto entitat;
		ConsultaFiltreCommand command;
		public ConsultaPaginaConsulta(
				ConsultaService consultaService,
				EntitatDto entitat,
				ConsultaFiltreCommand command) {
			this.consultaService = consultaService;
			this.entitat = entitat;
			this.command = command;
		}
		public PaginaLlistatDto<ConsultaDto> consultar(
				PaginacioAmbOrdreDto paginacioAmbOrdre) throws Exception {
			return consultaService.findSimplesByFiltrePaginatPerDelegat(
					entitat.getId(),
					ConsultaFiltreCommand.asDto(command),
					paginacioAmbOrdre);
		}
	}
	
	private List<String[]> readFile(
			MultipartFile fitxer,
			Errors errors) throws Exception {
		List<String[]> linies = new ArrayList<String[]>();
		// Obtenir dades del fitxer
		if ("application/vnd.ms-excel".equals(fitxer.getContentType()) ||
				"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet".equals(fitxer.getContentType())) {
			ExcelHssfGet excelGet = new ExcelHssfGet();
			linies = excelGet.readExcelByteArray(fitxer.getBytes());
		} else if ("text/csv".equals(fitxer.getContentType())) {
			ICsvListReader listReader = null;
			try {
				Reader reader = new InputStreamReader(fitxer.getInputStream()); 
                listReader = new CsvListReader(reader, CsvPreference.STANDARD_PREFERENCE);
                List<String> linia;
                while( (linia = listReader.read()) != null ) {
                	linies.add(linia.toArray(new String[]{}));
                }
			}
	        finally {
                if( listReader != null )
                	listReader.close();
	        }
		} else {
			errors.rejectValue(
					"multipleFitxer", 
					"PeticioMultiple.fitxer.tipus", 
					"Tipus de fitxer no suportat. Tipus acceptats: Excel i CSV.");
		}
		// 1. Mínim 1 registre de dades (fila 4)
		if (linies.size() < 4) {
			errors.rejectValue(
					"multipleFitxer", 
					"PeticioMultiple.fitxer.buit", 
					"El fitxer és buit. No te dades de peticions.");
		} else {
			linies = linies.subList(2, linies.size());
		}
		return linies;
	}
	private void validatePeticioMultipleFile(
			HttpServletRequest request,
			List<String[]> linies,
			ServeiDto servei, 
			List<ServeiCampDto> camps, 
			Errors errors,
			List<String> errorsValidacio) throws Exception {
		// 2. Tots els camps obligatoris del servei s'han inclòs al fitxer
		String[] pathsFitxer = linies.get(0);
		linies = linies.subList(1, linies.size());
		List<ServeiCampDto> campsObligatoris = getCampsObligatoris(request, servei, camps);
		if (!campsObligatoris.isEmpty()) {
			Integer[] posicioCampsObligatoris = new Integer[campsObligatoris.size()];
			int posOb = 0;
			for (ServeiCampDto campObligatori: campsObligatoris) {
				// Comprovam si el camp obligatori existeix en el fitxer
				for (int pos = 0; pos < pathsFitxer.length; pos++) {
					if (campObligatori.getPath().equals(pathsFitxer[pos])) {
						posicioCampsObligatoris[posOb] = pos;
						break;
					}
				}
				// Si no hi es es genera un missatge d'error
				if (posicioCampsObligatoris[posOb] == null) {
					errorsValidacio.add(
							getMessage(
									request, 
									"consulta.fitxer.camp.obligatori",
									new Object[] {campObligatori.getEtiqueta()}));
				}
				posOb++;
			}
			// 3. Tots els camps obligatoris s'han emplenat
			for (int i = 0; i < campsObligatoris.size(); i++) {
				ServeiCampDto campObligatori = campsObligatoris.get(i);
				Integer posicioCampObligatori = posicioCampsObligatoris[i];
				if (posicioCampObligatori != null) {
					int numLinia = 4;
					for (String[] linia: linies) {
						if (linia[posicioCampObligatori] == null || "".equals(linia[posicioCampObligatori])) {
							String campEtiqueta = (campObligatori.getEtiqueta() != null) ? campObligatori.getEtiqueta() : campObligatori.getCampNom();
							errorsValidacio.add(
									getMessage(
											request, 
											"consulta.fitxer.camp.obligatori.null",
											new Object[] {numLinia, campEtiqueta}));
						}
						numLinia++;
					}
				}
			}
		}
		// 4. Tipus documents és un dels acceptats pel servei
		if (servei.isPinbalActiuCampDocument()) {
			Integer posDocumentTip = null;
			for (int pos = 0; pos < pathsFitxer.length; pos++) {
				if ("DatosGenericos/Titular/TipoDocumentacion".equals(pathsFitxer[pos])) {
					posDocumentTip = pos;
					break;
				}
			}
			if (posDocumentTip != null) {
				List<String> documentsPermesos = new ArrayList<String>();
				if (servei.isPinbalPermesDocumentTipusCif()) documentsPermesos.add("CIF");
				if (servei.isPinbalPermesDocumentTipusDni()) documentsPermesos.add("DNI");
				if (servei.isPinbalPermesDocumentTipusNie()) documentsPermesos.add("NIE");
				if (servei.isPinbalPermesDocumentTipusNif()) documentsPermesos.add("NIF");
				if (servei.isPinbalPermesDocumentTipusPas()) documentsPermesos.add("PASSAPORT");
				int numLinia = 4;
				for (String[] linia: linies) {
					if (linia[posDocumentTip] != null && !"".equals(linia[posDocumentTip])) {
						if (!documentsPermesos.contains(linia[posDocumentTip].toUpperCase())) {
							errorsValidacio.add(
									getMessage(
											request, 
											"consulta.fitxer.camp.document.tipus",
											new Object[] {numLinia}));
						}
					}
					numLinia++;
				}
				
			}
		}
		// 5. Els camps de tipus date tenen el format correcte
		List<ServeiCampDto> campsData = getCampsData(camps);
		if (!campsData.isEmpty()) {
			Integer[] posicioCampsData = new Integer[campsData.size()];
			int posData = 0;
			for (ServeiCampDto campData: campsData) {
				for (int pos = 0; pos < pathsFitxer.length; pos++) {
					if (campData.getPath().equals(pathsFitxer[pos])) {
						posicioCampsData[posData] = pos;
						break;
					}
				}
				posData++;
			}
			SimpleDateFormat sdf = new SimpleDateFormat(FORMAT_DATA_DADES_ESPECIFIQUES);
			for (int i = 0; i < campsData.size(); i++) {
				ServeiCampDto campData = campsData.get(i);
				Integer posicioCampData = posicioCampsData[i];
				if (posicioCampData != null) {
					int numLinia = 4;
					for (String[] linia: linies) {
						String data = linia[posicioCampData];
						if (data != null && !"".equals(data)) {
							try {
								Date dataDate = sdf.parse(data);
								if (!sdf.format(dataDate).equals(data)) {
									String campEtiqueta = (campData.getEtiqueta() != null) ? campData.getEtiqueta() : campData.getCampNom();
									errorsValidacio.add(
											getMessage(
													request,
													"consulta.fitxer.camp.data",
													new Object[] {numLinia, campEtiqueta}));
								}
							} catch (Exception ex) {
								String campEtiqueta = (campData.getEtiqueta() != null) ? campData.getEtiqueta() : campData.getCampNom();
								errorsValidacio.add(
										getMessage(
												request,
												"consulta.fitxer.camp.data",
												new Object[] {numLinia, campEtiqueta}));
							}
						}
						numLinia++;
					}
				}
			}
		}
		if (!errorsValidacio.isEmpty()) {
			errors.rejectValue(
					"multipleFitxer", 
					"PeticioMultiple.fitxer", 
					"Errors en el contingut del fitxer");
		}
	}

	private List<ServeiCampDto> getCampsObligatoris(
			HttpServletRequest request, 
			ServeiDto servei, 
			List<ServeiCampDto> camps) {
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
		for (ServeiCampDto camp: camps) {
			if (camp.isObligatori())
				campsObligatoris.add(camp);
		}
		return campsObligatoris;
	}

	private List<ServeiCampDto> getCampsData(
			List<ServeiCampDto> camps) {
		List<ServeiCampDto> campsData = new ArrayList<ServeiCampDto>();
		for (ServeiCampDto camp: camps) {
			if (ServeiCampDtoTipus.DATA.equals(camp.getTipus()))
				campsData.add(camp);
		}
		return campsData;
	}

	private static final Logger LOGGER = LoggerFactory.getLogger(ConsultaController.class);

}