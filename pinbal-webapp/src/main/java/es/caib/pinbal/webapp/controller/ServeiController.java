package es.caib.pinbal.webapp.controller;

import es.caib.pinbal.core.dto.ArbreDto;
import es.caib.pinbal.core.dto.CodiValor;
import es.caib.pinbal.core.dto.DadaEspecificaDto;
import es.caib.pinbal.core.dto.FitxerDto;
import es.caib.pinbal.core.dto.NodeDto;
import es.caib.pinbal.core.dto.ProcedimentDto;
import es.caib.pinbal.core.dto.ServeiBusDto;
import es.caib.pinbal.core.dto.ServeiCampDto;
import es.caib.pinbal.core.dto.ServeiCampDto.ServeiCampDtoTipus;
import es.caib.pinbal.core.dto.ServeiCampGrupDto;
import es.caib.pinbal.core.dto.ServeiDto;
import es.caib.pinbal.core.dto.ServeiXsdDto;
import es.caib.pinbal.core.dto.XsdTipusEnumDto;
import es.caib.pinbal.core.dto.regles.AccioEnum;
import es.caib.pinbal.core.dto.regles.ModificatEnum;
import es.caib.pinbal.core.dto.regles.ServeiReglaDto;
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
import es.caib.pinbal.webapp.command.ServeiReglaCommand;
import es.caib.pinbal.webapp.command.ServeiXsdCommand;
import es.caib.pinbal.webapp.common.AlertHelper;
import es.caib.pinbal.webapp.common.RequestSessionHelper;
import es.caib.pinbal.webapp.datatables.ServerSideRequest;
import es.caib.pinbal.webapp.datatables.ServerSideResponse;
import es.caib.pinbal.webapp.helper.EnumHelper;
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

import javax.naming.NamingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controlador per al manteniment de serveis.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/servei")
public class ServeiController extends BaseController {
	
	private static final String SESSION_ATTRIBUTE_FILTRE = "ServeiController.session.filtre";
	private static final String SESSION_ATTRIBUTE_SERVEIS = "ServeiHelper.serveis";

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
			command.setActiu(true);
		}
		
		Page<ServeiDto> page = serveiService.findAmbFiltrePaginat(
				command.getCodi(),
				command.getDescripcio(),
				command.getEmissor(),
				command.getActiu(),
				command.getScspVersionEsquema(),
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
		if (serveiCodi != null) {
			serveiDto = serveiService.findAmbCodiPerAdminORepresentant(serveiCodi);
		}
		if (serveiDto != null) {
			serveiDto.setFitxersXsd(serveiService.xsdFindByServei(serveiCodi));
			model.addAttribute(ServeiCommand.asCommand(serveiDto));
		} else {
			model.addAttribute(new ServeiCommand(true));
		}
		model.addAttribute("serveiXsdCommand", new ServeiXsdCommand());

		if (serveiDto != null) {
			model.addAttribute("serveisBus", serveiService.findServeisBus(serveiDto.getCodi()));
		}
		
		this.fillFormModel(model);
		return "serveiForm";
	}
	
	@RequestMapping(value = "/save", method = RequestMethod.POST) 
	public String save(
			HttpServletRequest request,
			@Valid ServeiCommand command,
			BindingResult bindingResult,
			Model model) throws ServeiNotFoundException {
		if (bindingResult.hasErrors()) {
			this.fillFormModel(model);
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
		return "redirect:../servei";
	}
	


	@RequestMapping(value = "/{serveiCodi}/enable", method = RequestMethod.GET)
	public String enable(
			HttpServletRequest request,
			@PathVariable String serveiCodi) throws ServeiNotFoundException {
		try {
			serveiService.saveActiu(serveiCodi, true);
			request.getSession().removeAttribute(SESSION_ATTRIBUTE_SERVEIS);
			AlertHelper.success(
					request, 
					getMessage(
							request, 
							"servei.controller.servei.activat.ok"));
		} catch (Exception e) {
			AlertHelper.error(
					request, 
					e.getMessage());
		}
		return "redirect:../../servei";
	}
	
	@RequestMapping(value = "/{serveiCodi}/disable", method = RequestMethod.GET)
	public String disable(
			HttpServletRequest request,
			@PathVariable String serveiCodi) throws ServeiNotFoundException {
		try {
			serveiService.saveActiu(serveiCodi, false);
			request.getSession().removeAttribute(SESSION_ATTRIBUTE_SERVEIS);
			AlertHelper.success(
					request, 
					getMessage(
							request, 
							"servei.controller.servei.desactivat.ok"));
		} catch (Exception e) {
			AlertHelper.error(
					request, 
					e.getMessage());
		}
		return "redirect:../../servei";
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
		ArbreDto<DadaEspecificaDto> arbreDadesEspecifiques = serveiService.generarArbreDadesEspecifiques(serveiCodi);
		model.addAttribute("arbreDadesEspecifiques", arbreDadesEspecifiques);
		List<NodeDto<DadaEspecificaDto>> llistatDadesEspecifiques = arbreDadesEspecifiques.toList();
		List<ServeiCampDto> camps = serveiService.findServeiCamps(serveiCodi);
		model.addAttribute("camps", camps);
		
		// Consulta dels valors d'enumerat dels camps tipus enumerat
		List<ServeiCampDto> campsEnum = new ArrayList<ServeiCampDto>();
		List<String[]> valorsEnums = new ArrayList<String[]>();
		for (ServeiCampDto camp: camps) {
			if (camp.getTipus() == ServeiCampDtoTipus.ENUM) {
				for (NodeDto<DadaEspecificaDto> node: llistatDadesEspecifiques) {
					if (node.getDades().getPathAmbSeparadorDefault().equals(camp.getPath())) {
						campsEnum.add(camp);
						valorsEnums.add( node.getDades().getEnumeracioValors() );
					}
				}				
			}
		}
		model.addAttribute("campsEnumList", campsEnum);
		model.addAttribute("valorsEnumList", valorsEnums);
		
		Map<Long, List<ServeiCampDto>> campsAgrupats = new HashMap<Long, List<ServeiCampDto>>();
		for (ServeiCampDto camp: camps) {
			Long clau = (camp.getGrup() != null) ? camp.getGrup().getId() : null;
			if (campsAgrupats.get(clau) == null) {
				campsAgrupats.put(clau, new ArrayList<ServeiCampDto>());
			}
			campsAgrupats.get(clau).add(camp);
		}
		model.addAttribute("campsAgrupats", campsAgrupats);
		model.addAttribute("grups", serveiService.findServeiCampGrups(serveiCodi));
		model.addAttribute("regles", serveiService.serveiReglesFindAll(serveiCodi));
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

	@RequestMapping(value = "/{serveiCodi}/camp/{serveiCampId}/move/{posicio}", method = RequestMethod.POST)
	@ResponseBody
	public String serveiCampOrdre(
			HttpServletRequest request,
			@PathVariable String serveiCodi,
			@PathVariable Long serveiCampId,
			@PathVariable Integer posicio) throws Exception {
		try {
			serveiService.moveServeiCamp(serveiCodi, serveiCampId, posicio);
			return "ok";
		} catch (Exception ex) {
			AlertHelper.error(request, getMessage(request, "servei.controller.camp.moure.error") + ": " + ex.getMessage());
			throw ex;
		}
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
		return "redirect:../../../camp";
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
		model.addAttribute("grups", serveiService.findServeiCampGrups(serveiCodi));
		return "serveiPreview";
	}

	@RequestMapping(value = "/{serveiCodi}/campGrup/add", method = RequestMethod.POST)
	public String serveiCampGrupAdd(
			HttpServletRequest request,
			@PathVariable String serveiCodi,
			@Valid ServeiCampGrupCommand command,
			BindingResult bindingResult) throws ServeiNotFoundException {
		if (bindingResult.hasErrors()) {
			String msg = "";
			if (bindingResult.hasFieldErrors()) {
				FieldError fieldError = bindingResult.getFieldError();
				msg = " [" + fieldError.getField() + " (" + fieldError.getRejectedValue() + "): " + fieldError.getDefaultMessage() + "]";
			}
			AlertHelper.error(
					request, 
					getMessage(request, "servei.controller.camp.grup.creat.error") + msg);
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
		return xsdGet(request, serveiCodi, null, model);
	}
	
	@RequestMapping(value = "/{serveiCodi}/xsd/{tipus}/delete")
	@ResponseBody
	public void xsdDelete(
			HttpServletRequest request,
			@PathVariable XsdTipusEnumDto tipus,
			@PathVariable String serveiCodi) throws IOException {

		// Eliminar el fitxer XSD
		serveiService.xsdDelete(serveiCodi, tipus);
		// Actualitzar data al modificar xsd
		serveiService.updateVersio(serveiCodi);

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
		// Actualitzar data al modificar xsd
		serveiService.updateVersio(serveiCodi);
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

	@RequestMapping(value = "/{serveiCodi}/regla/new", method = RequestMethod.GET)
	public String reglaNewGet(
			HttpServletRequest request,
			@PathVariable String serveiCodi,
			Model model) throws ServeiNotFoundException {
		ServeiDto servei = serveiService.findAmbCodiPerAdminORepresentant(serveiCodi);
		model.addAttribute("servei", servei);
		model.addAttribute(ServeiReglaCommand.builder().serveiId(servei.getId()).build());
		modelRegles(model, servei.getCodi(), null);
		return "serveiReglaForm";
	}

	@RequestMapping(value = "/{serveiCodi}/regla/new", method = RequestMethod.POST)
	public String reglaNewPost(
			HttpServletRequest request,
			@PathVariable String serveiCodi,
			@Valid ServeiReglaCommand command,
			BindingResult bindingResult,
			Model model) throws ServeiNotFoundException {
		return reglaUpdatePost(
				request,
				serveiCodi,
				null,
				command,
				bindingResult,
				model);
	}

	@RequestMapping(value = "/{serveiCodi}/regla/{reglaId}", method = RequestMethod.GET)
	public String reglaUpdateGet(
			HttpServletRequest request,
			@PathVariable String serveiCodi,
			@PathVariable Long reglaId,
			Model model) throws ServeiNotFoundException {
		ServeiDto servei = serveiService.findAmbCodiPerAdminORepresentant(serveiCodi);
		model.addAttribute("servei", servei);
		ServeiReglaDto regla = serveiService.serveiReglaFindById(reglaId);
		model.addAttribute(ServeiReglaCommand.asCommand(regla));
		modelRegles(model, serveiCodi, regla.getModificat());
		return "serveiReglaForm";
	}

	@RequestMapping(value = "/{serveiCodi}/regla/{reglaId}", method = RequestMethod.POST)
	public String reglaUpdatePost(
			HttpServletRequest request,
			@PathVariable String serveiCodi,
			@PathVariable Long reglaId,
			@Valid ServeiReglaCommand command,
			BindingResult bindingResult,
			Model model) throws ServeiNotFoundException {
		ServeiDto servei = serveiService.findAmbCodiPerAdminORepresentant(serveiCodi);
		if (bindingResult.hasErrors()) {
			model.addAttribute("servei", servei);
			modelRegles(model, serveiCodi, command.getModificat());
			return "serveiReglaForm";
		} else {
			if (reglaId == null) {
				serveiService.serveiReglaCreate(serveiCodi, ServeiReglaCommand.asDto(command));
//				AlertHelper.success(request, getMessage(request, "servei.regla.controller.regla.creat"));
				return getModalControllerReturnValueSuccess(
						request,
						"redirect:../..",
						"servei.regla.controller.regla.creat");
			} else {
				serveiService.serveiReglaUpdate(serveiCodi, ServeiReglaCommand.asDto(command));
//				AlertHelper.success(request, getMessage(request, "servei.regla.controller.regla.actualitzat"));
				return getModalControllerReturnValueSuccess(
						request,
						"redirect:../..",
						"servei.regla.controller.regla.actualitzat");
			}
//			return modalUrlTancar();
		}
	}

	@RequestMapping(value = "/{serveiCodi}/regla/{reglaId}/delete")
	public String reglaDelete(
			HttpServletRequest request,
			@PathVariable String serveiCodi,
			@PathVariable Long reglaId,
			Model model) throws ServeiNotFoundException {
		ServeiDto servei = serveiService.findAmbCodiPerAdminORepresentant(serveiCodi);

		serveiService.serveiReglaDelete(serveiCodi, reglaId);

		model.addAttribute("servei", servei);
		return getAjaxControllerReturnValueSuccess(
				request,
				"redirect:/servei/" + serveiCodi + "/camp",
				"servei.regla.controller.regla.borrat");
//		return "redirect:/servei/" + serveiCodi + "/camp";
	}

	@RequestMapping(value = "/{serveiCodi}/regla/{reglaId}/move/{posicio}", method = RequestMethod.GET)
	@ResponseBody
	public boolean moureRegla(
			HttpServletRequest request,
			@PathVariable String serveiCodi,
			@PathVariable Long reglaId,
			@PathVariable int posicio,
			Model model) throws ServeiNotFoundException {
		boolean ret = false;

		ServeiDto servei = serveiService.findAmbCodiPerAdminORepresentant(serveiCodi);

		return serveiService.serveiReglaMoure(reglaId, posicio);
	}

	@RequestMapping(value = "/{serveiCodi}/regla/camp/select", method = RequestMethod.GET)
	@ResponseBody
	public List<CodiValor> reglaGetCamps(
			HttpServletRequest request,
			@PathVariable String serveiCodi,
			Model model) throws ServeiNotFoundException {

		List<ServeiCampDto> campsDto = serveiService.findServeiCamps(serveiCodi);
		// Crea les parelles de codi i valor
		List<CodiValor> dades = new ArrayList<CodiValor>();
		for (ServeiCampDto camp : campsDto) {
			dades.add(CodiValor.builder().codi(camp.getEtiqueta() != null ? camp.getEtiqueta() : camp.getCampNom()).valor(camp.getPath()).build());
		}
		return dades;
	}

	@RequestMapping(value = "/{serveiCodi}/regla/grup/select", method = RequestMethod.GET)
	@ResponseBody
	public List<CodiValor> reglaGetGrups(
			HttpServletRequest request,
			@PathVariable String serveiCodi,
			Model model) throws ServeiNotFoundException {
		List<ServeiCampGrupDto> grupsDto = serveiService.findServeiCampGrups(serveiCodi);
		// Crea les parelles de codi i valor
		List<CodiValor> dades = new ArrayList<CodiValor>();
		for (ServeiCampGrupDto grup : grupsDto) {
			dades.add(CodiValor.builder().codi(grup.getId().toString()).valor(grup.getNom()).build());
			if (grup.getFills() != null) {
				for (ServeiCampGrupDto fill: grup.getFills()) {
					dades.add(CodiValor.builder().codi(fill.getId().toString()).valor(fill.getNom()).build());
				}
			}
		}
		return dades;
	}

	private void modelRegles(Model model, String serveiCodi, ModificatEnum tipus) throws ServeiNotFoundException {
		model.addAttribute("modificatOptions", EnumHelper.getOptionsForEnum(ModificatEnum.class, "servei.regla.enum.modificat."));
		model.addAttribute("accioOptions", EnumHelper.getOptionsForEnum(AccioEnum.class, "servei.regla.enum.accio."));

		List<CodiValor> valors = new ArrayList<CodiValor>();

		if (tipus != null) {
			if (ModificatEnum.CAMPS.equals(tipus) || ModificatEnum.ALGUN_CAMP.equals(tipus)) {
				List<ServeiCampDto> camps = serveiService.findServeiCamps(serveiCodi);
				if (camps != null && !camps.isEmpty())
					for(ServeiCampDto camp: camps)
						valors.add(CodiValor.builder().valor((camp.getEtiqueta() != null ? camp.getEtiqueta() : camp.getCampNom()) + " | " + camp.getPath()).build());
			} else {
				List<ServeiCampGrupDto> grups = serveiService.findServeiCampGrups(serveiCodi);
				if (grups != null && !grups.isEmpty())
					for(ServeiCampGrupDto grup: grups) {
						valors.add(CodiValor.builder().valor(grup.getNom()).build());
						if (grup.getFills() != null) {
							for (ServeiCampGrupDto fill: grup.getFills()) {
								valors.add(CodiValor.builder().valor(fill.getNom()).build());
							}
						}
					}
			}
		}
		model.addAttribute("valors", valors);
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
			command.setActiu(true);
		}
		command.eliminarEspaisCampsCerca();
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


	private void fillFormModel(Model model) {
		model.addAttribute("emisors", serveiService.findEmisorAll());
		model.addAttribute("clausPubliques", serveiService.findClauPublicaAll());
		model.addAttribute("clausPrivades", serveiService.findClauPrivadaAll());
	
		List<String> tipusSeguretat = new ArrayList<String>(Arrays.asList("XMLSignature", "WS-Security"));
		model.addAttribute("tipusSeguretat", tipusSeguretat);
	}
	
	private void omplirCampEnumDescripcions(
			HttpServletRequest request,
			ServeiCampCommand command) {
		String parametreDescripcio = "descripcio-" + command.getId();
		command.setEnumDescripcions(request.getParameterValues(parametreDescripcio));
	}
}
