/**
 * 
 */
package es.caib.pinbal.webapp.controller;

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

import es.caib.pinbal.core.dto.CodiValor;
import es.caib.pinbal.core.dto.DadaEspecificaDto;
import es.caib.pinbal.core.dto.NodeDto;
import es.caib.pinbal.core.dto.ServeiCampDto;
import es.caib.pinbal.core.service.HistoricConsultaService;
import es.caib.pinbal.core.service.exception.ServeiNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import es.caib.pinbal.core.dto.ConsultaDto;
import es.caib.pinbal.core.dto.EntitatDto;
import es.caib.pinbal.core.dto.JustificantDto;
import es.caib.pinbal.core.dto.EntitatDto.EntitatTipusDto;
import es.caib.pinbal.core.service.ConsultaService;
import es.caib.pinbal.core.service.EntitatService;
import es.caib.pinbal.core.service.ProcedimentService;
import es.caib.pinbal.core.service.ServeiService;
import es.caib.pinbal.core.service.exception.ConsultaNotFoundException;
import es.caib.pinbal.core.service.exception.EntitatNotFoundException;
import es.caib.pinbal.core.service.exception.ScspException;
import es.caib.pinbal.webapp.command.ConsultaFiltreCommand;
import es.caib.pinbal.webapp.common.AlertHelper;
import es.caib.pinbal.webapp.common.EntitatHelper;
import es.caib.pinbal.webapp.common.RequestSessionHelper;
import es.caib.pinbal.webapp.datatables.ServerSideColumn;
import es.caib.pinbal.webapp.datatables.ServerSideRequest;
import es.caib.pinbal.webapp.datatables.ServerSideResponse;

/**
 * Controlador per a les auditories dels superauditors.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/admin/consulta")
public class ConsultaAdminController extends BaseController {

	private static final String SESSION_ATTRIBUTE_ENTITAT = "ConsultaAdminController.session.entitat";
	private static final String SESSION_ATTRIBUTE_FILTRE = "ConsultaAdminController.session.filtre";
	public static final String SESSION_ATTRIBUTE_GENFORM = "ConsultaAdminController.session.genform";
	public static final String SESSION_ATTRIBUTE_GENIDS = "ConsultaAdminController.session.genids";
	public static final String SESSION_CONSULTA_HISTORIC = "consulta_admin";

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

	@RequestMapping(method = RequestMethod.GET)
	public String get(
			HttpServletRequest request,
			Model model) throws Exception {
		omplirModelPerFiltreTaula(request, model);
		return "adminConsultes";
	}

	@RequestMapping(method = RequestMethod.POST)
	public String post(
			HttpServletRequest request,
			@Valid ConsultaFiltreCommand command,
			BindingResult bindingResult,
			@RequestParam(value = "accio", required = false) String accio,
			Model model) throws Exception {
		if ("netejar".equals(accio)) {
			RequestSessionHelper.esborrarObjecteSessio(
					request,
					SESSION_ATTRIBUTE_FILTRE);
		} else {
			if (bindingResult.hasErrors()) {
				omplirModelPerFiltreTaula(request, model);
				return "adminConsultes";
			} else {
				RequestSessionHelper.actualitzarObjecteSessio(
						request,
						SESSION_ATTRIBUTE_FILTRE,
						command);
				return "redirect:consulta";
			}
		}
		return "redirect:consulta";
	}

	@RequestMapping(value = "/datatable", produces="application/json", method = RequestMethod.GET)
	@ResponseBody
	public ServerSideResponse<ConsultaDto, Long> datatable(
			HttpServletRequest request,
			Model model)throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, NamingException, SQLException, EntitatNotFoundException {
		ServerSideRequest serverSideRequest = new ServerSideRequest(request);
		ConsultaFiltreCommand command = getCommandInstance(request);
		List<ServerSideColumn> cols = serverSideRequest.getColumns();
		cols.get(1).setData("createdDate");
		cols.get(2).setData("createdBy.nom");
		cols.get(4).setData("procedimentServei.procediment.nom");
		Page<ConsultaDto> page;
		if (isHistoric(request)) {
			page = historicConsultaService.findByFiltrePaginatPerAdmin(
					ConsultaFiltreCommand.asDto(command),
					serverSideRequest.toPageable());
		} else {
			page = consultaService.findByFiltrePaginatPerAdmin(
					ConsultaFiltreCommand.asDto(command),
					serverSideRequest.toPageable());
		}
		cols.get(1).setData("creacioData");
		cols.get(2).setData("creacioUsuari.nom");
		cols.get(3).setData("funcionariNomAmbDocument");
		cols.get(4).setData("procedimentNom");
		cols.get(5).setData("serveiDescripcio");
		return new ServerSideResponse<ConsultaDto, Long>(serverSideRequest, page);
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
				JustificantDto justificant = getJustificant(consultaId, isHistoric(request));
				if (!justificant.isError()) {
					writeFileToResponse(
							justificant.getNom(),
							justificant.getContingut(),
							response);
					return null;
				} else {
					AlertHelper.error(
							request,
							getMessage(
									request, 
									"consulta.controller.justificant.error"));
					return "redirect:../../consulta";
				}
			} catch (ConsultaNotFoundException ex) {
				throw ex;
			} catch (Exception ex) {
				ex.printStackTrace();
				AlertHelper.error(
						request,
						getMessage(
								request, 
								"consulta.controller.justificant.error"));
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

	@RequestMapping(value = "/entitat/seleccionar", method = RequestMethod.POST)
	public String entitatSeleccionar(
			HttpServletRequest request,
			@RequestParam(value = "entitatId", required = false) Long entitatId) {
		if (entitatId == null) {
			AlertHelper.error(
					request, 
					getMessage(
							request, 
							"admin.consulta.controller.entitat.no.especificada"));
		} else {
			EntitatDto entitat = entitatService.findById(entitatId);
			if (entitat != null) {
				RequestSessionHelper.actualitzarObjecteSessio(
						request,
						SESSION_ATTRIBUTE_ENTITAT,
						entitat);
			}
		}
		return "redirect:../../consulta";
	}

	@RequestMapping(value = "/entitat/deseleccionar", method = RequestMethod.GET)
	public String entitatDeseleccionar(
			HttpServletRequest request) {
		RequestSessionHelper.esborrarObjecteSessio(
				request,
				SESSION_ATTRIBUTE_ENTITAT);
		return "redirect:../../consulta";
	}

	@RequestMapping(value = "/{consultaId}", method = RequestMethod.GET)
	public String get(
			HttpServletRequest request,
			@PathVariable Long consultaId,
			Model model) throws Exception {
		ConsultaDto consulta = getConsultaAdmin(consultaId, isHistoric(request));
		model.addAttribute("consulta", consulta);
		model.addAttribute(
				"servei",
				serveiService.findAmbCodiPerAdminORepresentant(
						consulta.getServeiCodi()));
		omplirModelAmbDadesEspecifiques(
				consulta.getServeiCodi(),
				model);
		return "adminConsultaInfo";
	}

	private void omplirModelAmbDadesEspecifiques(
			String serveiCodi,
			Model model) throws ScspException, ServeiNotFoundException {
		List<NodeDto<DadaEspecificaDto>> llistaArbreDadesEspecifiques = serveiService.generarArbreDadesEspecifiques(serveiCodi).toList();
		model.addAttribute(
				"llistaArbreDadesEspecifiques",
				llistaArbreDadesEspecifiques);
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
		boolean mostraDadesEspecifiques = false;
		for (ServeiCampDto camp: camps) {
			if (camp.isVisible()) {
				mostraDadesEspecifiques = true;
				break;
			}
		}
		model.addAttribute("mostrarDadesEspecifiques", mostraDadesEspecifiques);
	}

	@RequestMapping(value = "/{consultaId}/xmlPeticio", method = RequestMethod.GET)
	public String xmlPeticio(
			HttpServletRequest request,
			HttpServletResponse response,
			@PathVariable Long consultaId,
			Model model) throws ConsultaNotFoundException, ScspException {
		ConsultaDto consulta = getConsultaAdmin(consultaId, isHistoric(request));
		model.addAttribute("consulta", consulta);
		model.addAttribute("mostrarPeticio", new Boolean(true));
		model.addAttribute("mostrarResposta", new Boolean(false));
		return "consultaXml";
	}

	@RequestMapping(value = "/{consultaId}/xmlResposta", method = RequestMethod.GET)
	public String xmlResposta(
			HttpServletRequest request,
			HttpServletResponse response,
			@PathVariable Long consultaId,
			Model model) throws ConsultaNotFoundException, ScspException {
		ConsultaDto consulta = getConsultaAdmin(consultaId, isHistoric(request));
		model.addAttribute("consulta", consulta);
		model.addAttribute("mostrarPeticio", new Boolean(false));
		model.addAttribute("mostrarResposta", new Boolean(true));
		return "consultaXml";
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
		model.addAttribute("entitats", entitatService.findAll());
		ConsultaFiltreCommand command = getCommandInstance(request);
		if (command.getEntitatId() != null) {
			model.addAttribute(
					"filtreCommand",
					command);
			model.addAttribute(
					"procediments",
					procedimentService.findAmbEntitat(command.getEntitatId()));
			if (command.getProcediment() != null)
				model.addAttribute(
						"serveis",
						serveiService.findAmbEntitatIProcediment(
								command.getEntitatId(),
								command.getProcediment()));
			else
				model.addAttribute(
						"serveis",
						serveiService.findAmbEntitat(command.getEntitatId()));
		} else {
			model.addAttribute(
					"filtreCommand",
					command);
			model.addAttribute(
					"procediments",
					procedimentService.findAll());
			if (command.getProcediment() != null)
				model.addAttribute(
						"serveis",
						serveiService.findAmbProcediment(command.getProcediment()));
			else
				model.addAttribute(
						"serveis",
						serveiService.findAll());
		}
		model.addAttribute("historic", isHistoric(request));
		getOrigens(model);
	}

	private void getOrigens(Model model) {
		List<CodiValor> origens = new ArrayList<>();
		origens.add(new CodiValor("true", "admin.consulta.list.filtre.origen.recobriment"));
		origens.add(new CodiValor("false", "admin.consulta.list.filtre.origen.web"));
		model.addAttribute("origens", origens);
	}

	private boolean isHistoric(HttpServletRequest request) {
		Object historic = request.getSession().getAttribute(SESSION_CONSULTA_HISTORIC);
		if (historic == null)
			return false;
		else
			return ((Boolean) historic).booleanValue();
	}

	private ConsultaDto getConsultaAdmin(Long consultaId, boolean historic) throws ConsultaNotFoundException, ScspException {
		ConsultaDto consulta;
		if (historic) {
			try {
				consulta = historicConsultaService.findOneAdmin(consultaId);
			} catch (Exception nfe) {
				consulta = consultaService.findOneAdmin(consultaId);
			}
		} else {
			try {
				consulta = consultaService.findOneAdmin(consultaId);
			} catch (Exception nfe) {
				consulta = historicConsultaService.findOneAdmin(consultaId);
			}
		}
		return consulta;
	}

	private ConsultaFiltreCommand getCommandInstance(HttpServletRequest request) {
		ConsultaFiltreCommand command = (ConsultaFiltreCommand)RequestSessionHelper.obtenirObjecteSessio(
				request,
				SESSION_ATTRIBUTE_FILTRE);
		if (command == null) {
			command = new ConsultaFiltreCommand(
					entitatService.findTopByTipus(EntitatTipusDto.GOVERN).getId());
		} else {
			command.eliminarEspaisCampsCerca();
		}
		command.updateDefaultDataInici(isHistoric(request));
		return command;
	}

	private JustificantDto getJustificant(Long consultaId, boolean historic) throws Exception {
		JustificantDto justificant;
		if (historic) {
			try {
				justificant = historicConsultaService.obtenirJustificant(consultaId, true);
			} catch (Exception nfe) {
				justificant = consultaService.obtenirJustificant(consultaId, true);
			}
		} else {
			try {
				justificant = consultaService.obtenirJustificant(consultaId, true);
			} catch (Exception nfe) {
				justificant = historicConsultaService.obtenirJustificant(consultaId, true);
			}
		}
		return justificant;
	}

}
