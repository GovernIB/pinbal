package es.caib.pinbal.webapp.controller;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.naming.NamingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import es.caib.pinbal.core.dto.ArbreDto;
import es.caib.pinbal.core.dto.DadaEspecificaDto;
import es.caib.pinbal.core.dto.EntitatDto;
import es.caib.pinbal.core.dto.FitxerDto;
import es.caib.pinbal.core.dto.ProcedimentDto;
import es.caib.pinbal.core.dto.ServeiBusDto;
import es.caib.pinbal.core.dto.ServeiCampDto;
import es.caib.pinbal.core.dto.ServeiDto;
import es.caib.pinbal.core.dto.ServeiXsdDto;
import es.caib.pinbal.core.dto.XsdTipusEnumDto;
import es.caib.pinbal.core.service.EntitatService;
import es.caib.pinbal.core.service.ProcedimentService;
import es.caib.pinbal.core.service.ServeiService;
import es.caib.pinbal.core.service.exception.EntitatNotFoundException;
import es.caib.pinbal.core.service.exception.ScspException;
import es.caib.pinbal.core.service.exception.ServeiAmbConsultesException;
import es.caib.pinbal.core.service.exception.ServeiBusNotFoundException;
import es.caib.pinbal.core.service.exception.ServeiCampGrupNotFoundException;
import es.caib.pinbal.core.service.exception.ServeiCampNotFoundException;
import es.caib.pinbal.core.service.exception.ServeiNotFoundException;
import es.caib.pinbal.webapp.command.ServeiBusCommand;
import es.caib.pinbal.webapp.command.ServeiCampCommand;
import es.caib.pinbal.webapp.command.ServeiCampGrupCommand;
import es.caib.pinbal.webapp.command.ServeiCommand;
import es.caib.pinbal.webapp.command.ServeiFiltreCommand;
import es.caib.pinbal.webapp.command.ServeiJustificantCampCommand;
import es.caib.pinbal.webapp.command.ServeiXsdCommand;
import es.caib.pinbal.webapp.common.AlertHelper;
import es.caib.pinbal.webapp.common.EntitatHelper;
import es.caib.pinbal.webapp.common.RequestSessionHelper;
import es.caib.pinbal.webapp.datatables.ServerSideRequest;
import es.caib.pinbal.webapp.datatables.ServerSideResponse;

/**
 * Controlador per al manteniment de serveis.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/servei")
public class ServeiController extends BaseController {
	
	private static final String SESSION_ATTRIBUTE_FILTRE = "ServeiController.session.filtre"; 
	private static final String SESSION_ATTRIBUTE_FILTRE_PROCEDIMENT = "ServeiController.session.filtre.procediment";
	
	public static String getSessionAttributeFiltreProcediment() {
		return SESSION_ATTRIBUTE_FILTRE_PROCEDIMENT;
	}

	@Autowired
	private ServeiService serveiService;
	@Autowired
	private EntitatService entitatService;
	@Autowired
	private ProcedimentService procedimentService;

	@RequestMapping(method = RequestMethod.GET)
	public String get(
			HttpServletRequest request,
			HttpServletResponse response,
			Model model) throws Exception {
		omplirModelPerFiltreTaula(request, model); 
		return "serveiList";
	}

	@RequestMapping(method = RequestMethod.POST)
	public String post(
			HttpServletRequest request,
			@Valid ServeiFiltreCommand command,
			BindingResult bindingResult,
			Model model) throws Exception {
		if (bindingResult.hasErrors()) {
			omplirModelPerFiltreTaula(request, model);
			return "serveiList";
		} else {
			RequestSessionHelper.actualitzarObjecteSessio(
					request,
					SESSION_ATTRIBUTE_FILTRE,
					command);
			return "redirect:servei";
		}
	}

	@RequestMapping(value = "/datatable", produces="application/json", method = RequestMethod.GET)
	@ResponseBody
	public ServerSideResponse<ServeiDto, Long> datatable(HttpServletRequest request, Model model)
	      throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, NamingException,
	      SQLException {
		ServerSideRequest serverSideRequest = new ServerSideRequest(request);
		
		ServeiFiltreCommand command = (ServeiFiltreCommand) RequestSessionHelper.obtenirObjecteSessio( 
				request, 
				SESSION_ATTRIBUTE_FILTRE);
		
		if (command == null) {
			command = new ServeiFiltreCommand();
			command.setActiva(true);
		}
		
		Page<ServeiDto> page = serveiService.findAmbFiltrePaginat(
				command.getCodi(),
				command.getDescripcio(),
				command.getEmissor(),
				command.getActiva(),				
				serverSideRequest.toPageable());

		return new ServerSideResponse<ServeiDto, Long>(serverSideRequest, page);
	}
	
	@RequestMapping(value = "/datatable/procediment/{procedimentId}", produces="application/json", method = RequestMethod.GET)
	@ResponseBody
	public ServerSideResponse<ServeiDto, Long> datatable(
			HttpServletRequest request,
			@PathVariable Long procedimentId,
			Model model)
	      throws Exception {
		ServerSideRequest serverSideRequest = new ServerSideRequest(request);
		
		ServeiFiltreCommand command = (ServeiFiltreCommand) RequestSessionHelper.obtenirObjecteSessio( 
				request, 
				SESSION_ATTRIBUTE_FILTRE_PROCEDIMENT);
		
		if (command == null) {
			command = new ServeiFiltreCommand();
			command.setActiva(true);
		}
		
		if (!EntitatHelper.isRepresentantEntitatActual(request))
			throw new Exception("Representant no autoritzat");
		
		EntitatDto entitat = EntitatHelper.getEntitatActual(request, entitatService);
		if (entitat == null) {
			throw new Exception("Entitat actual incorrecte");
		}
		
		ProcedimentDto procediment = procedimentService.findById(procedimentId);
		if (procediment == null) {
			throw new Exception("Incorrect procediment id");
		}
		
		Page<ServeiDto> page = serveiService.findAmbFiltrePaginat(
													command.getCodi(),
													command.getDescripcio(),
													command.getEmissor(),
													command.getActiva(),
													entitat, 
													procediment,			
													serverSideRequest.toPageable());

		return new ServerSideResponse<ServeiDto, Long>(serverSideRequest, page);
	}
	
	@RequestMapping(value = "/new", method = RequestMethod.GET)
	public String get(Model model) throws ServeiNotFoundException, IOException {
		return get(null, model);
	}
	
	@RequestMapping(value = "/{serveiCodi}", method = RequestMethod.GET)
	public String get(
			@PathVariable String serveiCodi,
			Model model) throws ServeiNotFoundException, IOException {
		ServeiDto serveiDto = null;
		List<ServeiXsdDto> llistatFitxers = new ArrayList<ServeiXsdDto>();
		if (serveiCodi != null) {
			serveiDto = serveiService.findAmbCodiPerAdminORepresentant(serveiCodi);
			llistatFitxers = serveiService.xsdFindByServei(serveiCodi);
		}
		
		if (serveiDto != null) {
			serveiDto.setFitxersXsd(llistatFitxers);
			model.addAttribute(ServeiCommand.asCommand(serveiDto));
		} else {
			model.addAttribute(new ServeiCommand(true));
		}
		
		model.addAttribute("serveiXsdCommand", new ServeiXsdCommand());
		model.addAttribute("emisors", serveiService.findEmisorAll());
		model.addAttribute("clausPubliques", serveiService.findClauPublicaAll());
		model.addAttribute("clausPrivades", serveiService.findClauPrivadaAll());
		if (serveiDto != null)
			model.addAttribute("serveisBus", serveiService.findServeisBus(serveiDto.getCodi()));
		return "serveiForm";
	}
	
	@RequestMapping(value = "/save", method = RequestMethod.POST) 
	public String save(
			HttpServletRequest request,
			@Valid ServeiCommand command,
			BindingResult bindingResult,
			Model model) throws ServeiNotFoundException {
		if (bindingResult.hasErrors()) {
			model.addAttribute("emisors", serveiService.findEmisorAll());
			model.addAttribute("clausPubliques", serveiService.findClauPublicaAll());
			model.addAttribute("clausPrivades", serveiService.findClauPrivadaAll());
			return "serveiForm";
		}
		ServeiDto servei = ServeiCommand.asDto(command);
		if (command.getFitxerAjuda() != null && servei.getFitxerAjudaContingut() == null)
			AlertHelper.warning(
					request, 
					getMessage(
							request, 
							"servei.controller.servei.fitxer.ajuda.ko"));
		serveiService.save(servei);
		if (command.isCreacio()) {
			AlertHelper.success(
					request, 
					getMessage(
							request, 
							"servei.controller.servei.creat.ok"));
		} else {
			AlertHelper.success(
					request, 
					getMessage(
							request, 
							"servei.controller.servei.modificat.ok"));
		}
		return "redirect:servei";
	}
	
	@RequestMapping(value = "/{serveiCodi}/downloadAjuda", method = RequestMethod.GET)
	public String downloadAjuda(
			HttpServletRequest request,
			HttpServletResponse response,
			@PathVariable String serveiCodi) throws ServeiNotFoundException {
		try {
			ServeiDto servei = null;
			if (serveiCodi != null)
				servei = serveiService.findAmbCodiPerAdminORepresentant(serveiCodi);
			
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

	@RequestMapping(value = "/{serveiCodi}/delete", method = RequestMethod.GET)
	public String delete(
			HttpServletRequest request,
			@PathVariable String serveiCodi) throws ServeiNotFoundException {
		try {
			serveiService.delete(serveiCodi);
			AlertHelper.success(
					request, 
					getMessage(
							request, 
							"servei.controller.servei.esborrat.ok"));
		} catch (ServeiAmbConsultesException e) {
			AlertHelper.error(
					request, 
					getMessage(
							request, 
							"servei.controller.servei.amb.consultes"));
		}
		return "redirect:../../servei";
	}

	@RequestMapping(value = "/{serveiCodi}/camp", method = RequestMethod.GET)
	public String serveiCamp(
			HttpServletRequest request,
			@PathVariable String serveiCodi,
			Model model)  throws ServeiNotFoundException, ScspException {
		ServeiDto serveiDto = serveiService.findAmbCodiPerAdminORepresentant(serveiCodi);
		model.addAttribute(
				"servei",
				serveiDto);
		model.addAttribute(
				"arbreDadesEspecifiques",
				serveiService.generarArbreDadesEspecifiques(serveiCodi, serveiDto.isActivaGestioXsd()));
		
		List<ServeiCampDto> camps = serveiService.findServeiCamps(serveiCodi);
		model.addAttribute("camps", camps);
		Map<Long, List<ServeiCampDto>> campsAgrupats = new HashMap<Long, List<ServeiCampDto>>();
		for (ServeiCampDto camp: camps) {
			Long clau = (camp.getGrup() != null) ? camp.getGrup().getId() : null;
			if (campsAgrupats.get(clau) == null) {
				campsAgrupats.put(clau, new ArrayList<ServeiCampDto>());
			}
			campsAgrupats.get(clau).add(camp);
		}
		model.addAttribute("campsAgrupats", campsAgrupats);
		model.addAttribute(
				"grups",
				serveiService.findServeiCampGrups(serveiCodi));
		return "serveiCamp";
	}

	@RequestMapping(value = "/{serveiCodi}/camp/add", method = RequestMethod.POST)
	public String serveiCampAdd(
			HttpServletRequest request,
			@PathVariable String serveiCodi,
			@Valid ServeiCampCommand command,
			BindingResult bindingResult) throws ServeiNotFoundException {
		if (bindingResult.hasErrors()) {
			return "redirect:../camp";
		}
		serveiService.createServeiCamp(command.getServei(), command.getPath());
		return "redirect:../camp";
	}

	@RequestMapping(value = "/{serveiCodi}/camp/update", method = RequestMethod.POST)
	public String serveiCampUpdate(
			HttpServletRequest request,
			@PathVariable String serveiCodi,
			@Valid ServeiCampCommand command,
			BindingResult bindingResult) throws ServeiCampNotFoundException {
		if (bindingResult.hasErrors()) {
			return "redirect:../camp";
		}
		omplirCampEnumDescripcions(request, command);
		serveiService.updateServeiCamp(ServeiCampCommand.asDto(command));
		AlertHelper.success(
				request, 
				getMessage(
						request, 
						"servei.controller.camp.modificat.ok"));
		return "redirect:../camp";
	}

	@RequestMapping(value = "/{serveiCodi}/camp/{serveiCampId}/delete", method = RequestMethod.GET)
	public String serveiCampDelete(
			HttpServletRequest request,
			@PathVariable String serveiCodi,
			@PathVariable Long serveiCampId) throws ServeiCampNotFoundException {
		serveiService.deleteServeiCamp(serveiCampId);
		AlertHelper.success(
				request,
				getMessage(
						request, 
						"servei.controller.camp.esborrat.ok"));
		return "redirect:../../camp";
	}

	@RequestMapping(value = "/{serveiCodi}/camp/move/{serveiCampId}/{indexDesti}", method = RequestMethod.GET)
	public String serveiCampMove(
			HttpServletRequest request,
			@PathVariable String serveiCodi,
			@PathVariable Long serveiCampId,
			@PathVariable int indexDesti) throws ServeiCampNotFoundException {
		serveiService.moveServeiCamp(serveiCodi, serveiCampId, indexDesti);
		return "redirect:../../../camp";
	}

	@RequestMapping(value = "/{serveiCodi}/camp/{serveiCampId}/agrupar/{serveiCampGrupId}", method = RequestMethod.GET)
	public String serveiCampAgrupar(
			HttpServletRequest request,
			@PathVariable String serveiCodi,
			@PathVariable Long serveiCampId,
			@PathVariable Long serveiCampGrupId) throws ServeiCampNotFoundException, ServeiCampGrupNotFoundException {
		serveiService.agrupaServeiCamp(serveiCampId, serveiCampGrupId);
		return "redirect:../../../camp";
	}

	@RequestMapping(value = "/{serveiCodi}/camp/{serveiCampId}/desagrupar", method = RequestMethod.GET)
	public String serveiCampDesagrupar(
			HttpServletRequest request,
			@PathVariable String serveiCodi,
			@PathVariable Long serveiCampId) throws ServeiCampNotFoundException, ServeiCampGrupNotFoundException {
		serveiService.agrupaServeiCamp(serveiCampId, null);
		return "redirect:../../camp";
	}

	@RequestMapping(value = "/{serveiCodi}/preview", method = RequestMethod.GET)
	public String serveiPreview(
			HttpServletRequest request,
			@PathVariable String serveiCodi,
			Model model) throws ServeiNotFoundException, ScspException {
		model.addAttribute(
				"servei",
				serveiService.findAmbCodiPerAdminORepresentant(serveiCodi));
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
		return "serveiPreview";
	}

	@RequestMapping(value = "/{serveiCodi}/campGrup/add", method = RequestMethod.POST)
	public String serveiCampGrupAdd(
			HttpServletRequest request,
			@PathVariable String serveiCodi,
			@Valid ServeiCampGrupCommand command,
			BindingResult bindingResult) throws ServeiNotFoundException {
		if (bindingResult.hasErrors()) {
			AlertHelper.error(
					request, 
					getMessage(
							request, 
							"servei.controller.camp.grup.creat.error"));
			return "redirect:../camp";
		}
		serveiService.createServeiCampGrup(
				ServeiCampGrupCommand.asDto(command));
		AlertHelper.success(
				request, 
				getMessage(
						request, 
						"servei.controller.camp.grup.creat.ok"));
		return "redirect:../camp";
	}

	@RequestMapping(value = "/{serveiCodi}/campGrup/update", method = RequestMethod.POST)
	public String serveiCampGrupUpdate(
			HttpServletRequest request,
			@PathVariable String serveiCodi,
			@Valid ServeiCampGrupCommand command,
			BindingResult bindingResult) throws ServeiCampGrupNotFoundException {
		if (bindingResult.hasErrors()) {
			AlertHelper.error(
					request, 
					getMessage(
							request, 
							"servei.controller.camp.grup.modificat.error"));
			return "redirect:../camp";
		}
		serveiService.updateServeiCampGrup(ServeiCampGrupCommand.asDto(command));
		AlertHelper.success(
				request, 
				getMessage(
						request, 
						"servei.controller.camp.grup.modificat.ok"));
		return "redirect:../camp";
	}

	@RequestMapping(value = "/{serveiCodi}/campGrup/{serveiCampGrupId}/delete", method = RequestMethod.GET)
	public String serveiCampGrupDelete(
			HttpServletRequest request,
			@PathVariable String serveiCodi,
			@PathVariable Long serveiCampGrupId) throws ServeiCampGrupNotFoundException {
		serveiService.deleteServeiCampGrup(serveiCampGrupId);
		AlertHelper.success(
				request,
				getMessage(
						request, 
						"servei.controller.camp.grup.esborrat.ok"));
		return "redirect:../../camp";
	}

	@RequestMapping(value = "/{serveiCodi}/campGrup/{serveiCampGrupId}/up", method = RequestMethod.GET)
	public String serveiCampGrupUp(
			HttpServletRequest request,
			@PathVariable String serveiCodi,
			@PathVariable Long serveiCampGrupId) throws ServeiCampGrupNotFoundException {
		serveiService.moveServeiCampGrup(serveiCampGrupId, true);
		return "redirect:../../camp";
	}

	@RequestMapping(value = "/{serveiCodi}/campGrup/{serveiCampGrupId}/down", method = RequestMethod.GET)
	public String serveiCampGrupDown(
			HttpServletRequest request,
			@PathVariable String serveiCodi,
			@PathVariable Long serveiCampGrupId) throws ServeiCampGrupNotFoundException {
		serveiService.moveServeiCampGrup(serveiCampGrupId, false);
		return "redirect:../../camp";
	}

	@RequestMapping(value = "/{serveiCodi}/justificant", method = RequestMethod.GET)
	public String serveiJustificant(
			HttpServletRequest request,
			@PathVariable String serveiCodi,
			Model model) throws ServeiNotFoundException, ScspException {
		omplirModelTraduccio(serveiCodi, model);
		return "serveiJustificant";
	}

	@RequestMapping(value = "/{serveiCodi}/justificant", method = RequestMethod.POST)
	public String serveiJustificantPost(
			HttpServletRequest request,
			@PathVariable String serveiCodi,
			@Valid ServeiJustificantCampCommand command,
			BindingResult bindingResult,
			Model model) throws ServeiNotFoundException, ScspException {
		if (bindingResult.hasErrors()) {
			AlertHelper.error(
					request, 
					getMessage(
							request, 
							"servei.controller.traduccio.camp.error"));
			omplirModelTraduccio(serveiCodi, model);
			return "serveiJustificant";
		}
		serveiService.addServeiJustificantCamp(ServeiJustificantCampCommand.asDto(command));
		AlertHelper.success(
				request, 
				getMessage(
						request, 
						"servei.controller.traduccio.camp.ok"));
		return "redirect:justificant";
	}

	@RequestMapping(value = "/{serveiCodi}/redir/new", method = RequestMethod.GET)
	public String redirGet(
			HttpServletRequest request,
			@PathVariable String serveiCodi,
			Model model) throws ServeiNotFoundException, ServeiBusNotFoundException {
		return redirGet(request, serveiCodi, null, model);
	}
	
	

	/*Gestió de fitxers xsd*/
	
	@RequestMapping(value = "/{serveiCodi}/xsd/new", method = RequestMethod.GET)
	public String newXsd(
			HttpServletRequest request,
			@PathVariable String serveiCodi,
			Model model) throws ServeiNotFoundException {
		model.addAttribute("serveiCodi", serveiCodi);
//		model.addAttribute(
//				"xsdTipusEnumOptions",
//				HtmlSelectOptionHelper.getOptionsForEnum(
//						XsdTipusEnumDto.class,
//						"xsd.tipus.enum."));
		return xsdGet(request, serveiCodi, null, model);
	}
	
	@RequestMapping(value = "/{serveiCodi}/xsd/{tipus}/delete")
	@ResponseBody
	public void xsdDelete(
			HttpServletRequest request,
			@PathVariable XsdTipusEnumDto tipus,
			@PathVariable String serveiCodi) throws IOException {
		serveiService.xsdDelete(
				serveiCodi,
				tipus);
		AlertHelper.success(
				request, 
				getMessage(
						request, 
						"servei.controller.servei.xsd.esborrat.ok"));
	}
	
	@RequestMapping(value = "/{codi}/xsd/{tipus}/download", method = RequestMethod.GET)
	public String xsdDownload(
			HttpServletRequest request,
			HttpServletResponse response,
			@PathVariable String codi,
			@PathVariable XsdTipusEnumDto tipus) throws Exception {
		FitxerDto fitxer = serveiService.xsdDescarregar(
				codi,
				tipus);
		writeFileToResponse(
				fitxer.getNom(),
				fitxer.getContingut(),
				response);
		return null;
	}
	
	@RequestMapping(value = "/{serveiCodi}/xsd/save", method = RequestMethod.POST)
	@ResponseBody
	public String xsdPost(
			HttpServletRequest request,
			@PathVariable String serveiCodi,
			@Valid ServeiXsdCommand command,
			BindingResult bindingResult,
			Model model) throws EntitatNotFoundException, ServeiNotFoundException, ServeiBusNotFoundException, IOException {
		String jsonResponse = "";
		if (bindingResult.hasErrors()) {
			jsonResponse = "{\"error\": true ";
			if (bindingResult.hasFieldErrors()) {
				jsonResponse += ", \"errors\":[";
				int aux = 0;
				for (FieldError fieldError : bindingResult.getFieldErrors()) {
					if(aux != 0) {
						jsonResponse += ",";
					}
					jsonResponse += "{\"camp\":\"" + fieldError.getField() + "\",";
					if(fieldError.getField().equals("contingut")) {
						jsonResponse += "\"errorMsg\":\""+ getMessage(request, "servei.form.xsd.validacio.contingut") +"\"}";
					}else if(fieldError.getField().equals("nomArxiu")) {
						jsonResponse += "\"errorMsg\":\""+ getMessage(request, "servei.form.xsd.validacio.contingut") +"\"}";
					}else if(fieldError.getField().equals("tipus")) {
						jsonResponse += "\"errorMsg\":\""+ getMessage(request, "servei.form.xsd.validacio.contingut") +"\"}";
					}
					aux ++;
				}
				jsonResponse += "]}";	
			} else {
				jsonResponse += "}";
			}
			return jsonResponse;
		}
		List<ServeiXsdDto> llistatFitxers = serveiService.xsdFindByServei(serveiCodi);
		List<XsdTipusEnumDto> llistatTipusFitxersXsd = new ArrayList<XsdTipusEnumDto>();
		for(ServeiXsdDto fitxer : llistatFitxers) {
			llistatTipusFitxersXsd.add(fitxer.getTipus());
		}
		ServeiXsdDto dto = ServeiXsdCommand.asDto(command);
		serveiService.xsdCreate(command.getCodi(), dto, command.getContingut().getBytes());
		if (!llistatTipusFitxersXsd.contains(dto.getTipus())) {
			AlertHelper.success(
					request, 
					getMessage(
							request, 
							"servei.controller.servei.xsd.creat.ok"));
		} else {
			AlertHelper.success(
					request, 
					getMessage(
							request, 
							"servei.controller.servei.xsd.modificat.ok"));
		}
		return "{\"error\": false}";
	}
	
	
	@RequestMapping(value = "/{serveiCodi}/xsd/{serveiXsdId}", method = RequestMethod.GET)
	public String xsdGet(
			HttpServletRequest request,
			@PathVariable String serveiCodi,
			@PathVariable Long serveiXsdId,
			Model model) throws ServeiNotFoundException {
		ServeiDto servei = serveiService.findAmbCodiPerAdminORepresentant(serveiCodi);
		model.addAttribute("servei", servei);
		if (serveiXsdId == null) {
			model.addAttribute(new ServeiXsdCommand());
		}
		return "serveiXsd";
	}
	/*Gestió de fitxers xsd*/
	
	@RequestMapping(value = "/{serveiCodi}/redir/{serveiBusId}", method = RequestMethod.GET)
	public String redirGet(
			HttpServletRequest request,
			@PathVariable String serveiCodi,
			@PathVariable Long serveiBusId,
			Model model) throws ServeiNotFoundException, ServeiBusNotFoundException {
		ServeiDto servei = serveiService.findAmbCodiPerAdminORepresentant(serveiCodi);
		model.addAttribute("servei", servei);
		model.addAttribute(
				"entitats",
				entitatService.findDisponiblesPerRedireccionsBus(servei.getCodi()));
		if (serveiBusId == null) {
			model.addAttribute(
					new ServeiBusCommand(serveiCodi));
		} else {
			ServeiBusDto serveiBus = serveiService.findServeiBusById(serveiBusId);
			model.addAttribute(
					ServeiBusCommand.asCommand(serveiBus));
		}
		return "serveiBus";
	}
	
	@RequestMapping(value = "/{serveiCodi}/redir/save", method = RequestMethod.POST)
	public String redirPost(
			HttpServletRequest request,
			@PathVariable String serveiCodi,
			@Valid ServeiBusCommand command,
			BindingResult bindingResult,
			Model model) throws EntitatNotFoundException, ServeiNotFoundException, ServeiBusNotFoundException {
		if (bindingResult.hasErrors()) {
			ServeiDto servei = serveiService.findAmbCodiPerAdminORepresentant(serveiCodi);
			model.addAttribute("servei", servei);
			model.addAttribute(
					"entitats",
					entitatService.findDisponiblesPerRedireccionsBus(servei.getCodi()));
			return "serveiBus";
		}
		if (command.getId() == null) {
			serveiService.createServeiBus(ServeiBusCommand.asDto(command));
			AlertHelper.success(
					request, 
					getMessage(
							request, 
							"servei.controller.servei.bus.creat.ok"));
		} else {
			serveiService.updateServeiBus(ServeiBusCommand.asDto(command));
			AlertHelper.success(
					request, 
					getMessage(
							request, 
							"servei.controller.servei.bus.modificat.ok"));
		}
		model.addAttribute("reloadPage", new Boolean(true));
		return "serveiBus";
	}
	
	

	@RequestMapping(value = "/{serveiCodi}/redir/{serveiBusId}/delete", method = RequestMethod.GET)
	public String delete(
			HttpServletRequest request,
			@PathVariable String serveiCodi,
			@PathVariable Long serveiBusId) throws ServeiBusNotFoundException {
		serveiService.deleteServeiBus(serveiBusId);
		AlertHelper.success(
				request, 
				getMessage(
						request, 
						"servei.controller.servei.bus.esborrat.ok"));
		return "redirect:../../../" + serveiCodi;
	}

	@RequestMapping(value = "/{serveiCodi}/procediments", method = RequestMethod.GET)
	public String procediemnts(
			@PathVariable String serveiCodi,
			Model model) throws ServeiNotFoundException {
		List<ProcedimentDto> procediments = procedimentService.findAmbServeiCodi(serveiCodi);
		Map<String, List<ProcedimentDto>> procedimentsEntitat = new HashMap<String, List<ProcedimentDto>>();
		
		for (ProcedimentDto procediment: procediments) {
			List<ProcedimentDto> pe = null;
			if (procedimentsEntitat.containsKey(procediment.getEntitatNom())) {
				pe = procedimentsEntitat.get(procediment.getEntitatNom());
			} else {
				pe = new ArrayList<ProcedimentDto>();
			}
			pe.add(procediment);
			procedimentsEntitat.put(procediment.getEntitatNom(), pe);
		}
		model.addAttribute("procedimentsEntitat", procedimentsEntitat);
		return "serveiProcedimentList";
	}
	
	@InitBinder
	public void initBinder(WebDataBinder binder) {
	    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
	    dateFormat.setLenient(false);
	    binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
	}


	private void omplirModelPerFiltreTaula( 
			HttpServletRequest request, 
			Model model) throws Exception {
		
		ServeiFiltreCommand command = (ServeiFiltreCommand) RequestSessionHelper.obtenirObjecteSessio( 
				request, 
				SESSION_ATTRIBUTE_FILTRE);
		
		if (command == null) {
			command = new ServeiFiltreCommand();
			command.setActiva(true);
		}

		model.addAttribute(command);
		
		model.addAttribute("emisors", serveiService.findEmisorAll());
	} 
	
	private void omplirModelTraduccio(
			String serveiCodi,
			Model model) throws ServeiNotFoundException, ScspException {
		model.addAttribute(
				"servei",
				serveiService.findAmbCodiPerAdminORepresentant(serveiCodi));
		ArbreDto<DadaEspecificaDto> arbreDadesEspecifiques = serveiService.generarArbreDadesEspecifiques(serveiCodi);
		model.addAttribute(
				"arbreDadesEspecifiques",
				arbreDadesEspecifiques);
		model.addAttribute(
				"llistaDadesEspecifiques",
				arbreDadesEspecifiques.toList());
		model.addAttribute(
				"traduccions",
				serveiService.findServeiJustificantCamps(serveiCodi));
	}

	private void omplirCampEnumDescripcions(
			HttpServletRequest request,
			ServeiCampCommand command) {
		String parametreDescripcio = "descripcio-" + command.getId();
		command.setEnumDescripcions(request.getParameterValues(parametreDescripcio));
	}
}
