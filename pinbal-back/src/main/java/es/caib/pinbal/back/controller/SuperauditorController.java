/**
 * 
 */
package es.caib.pinbal.back.controller;

import es.caib.pinbal.back.command.AuditoriaGenerarCommand;
import es.caib.pinbal.back.command.ConsultaFiltreCommand;
import es.caib.pinbal.back.datatables.ServerSideColumn;
import es.caib.pinbal.back.datatables.ServerSideRequest;
import es.caib.pinbal.back.datatables.ServerSideResponse;
import es.caib.pinbal.back.helper.AlertHelper;
import es.caib.pinbal.back.helper.RequestSessionHelper;
import es.caib.pinbal.logic.intf.dto.ArbreRespostaDto;
import es.caib.pinbal.logic.intf.dto.CodiValor;
import es.caib.pinbal.logic.intf.dto.ConsultaDto;
import es.caib.pinbal.logic.intf.dto.DadaEspecificaDto;
import es.caib.pinbal.logic.intf.dto.EntitatDto;
import es.caib.pinbal.logic.intf.dto.FitxerDto;
import es.caib.pinbal.logic.intf.dto.JustificantDto;
import es.caib.pinbal.logic.intf.dto.NodeDto;
import es.caib.pinbal.logic.intf.dto.ServeiCampDto;
import es.caib.pinbal.logic.intf.service.ConsultaService;
import es.caib.pinbal.logic.intf.service.EntitatService;
import es.caib.pinbal.logic.intf.service.HistoricConsultaService;
import es.caib.pinbal.logic.intf.service.ProcedimentService;
import es.caib.pinbal.logic.intf.service.ServeiService;
import es.caib.pinbal.logic.intf.service.exception.ConsultaNotFoundException;
import es.caib.pinbal.logic.intf.service.exception.EntitatNotFoundException;
import es.caib.pinbal.logic.intf.service.exception.ProcedimentNotFoundException;
import es.caib.pinbal.logic.intf.service.exception.ScspException;
import es.caib.pinbal.logic.intf.service.exception.ServeiNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.naming.NamingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controlador per a les auditories dels superauditors.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/superauditor")
public class SuperauditorController extends BaseController {

	private static final String SESSION_ATTRIBUTE_ENTITAT = "SuperauditorController.session.entitat";
	private static final String SESSION_ATTRIBUTE_FILTRE = "SuperauditorController.session.filtre";
	public static final String SESSION_ATTRIBUTE_GENFORM = "SuperauditorController.session.genform";
	public static final String SESSION_ATTRIBUTE_GENIDS = "SuperauditorController.session.genids";
	public static final String SESSION_CONSULTA_HISTORIC = "consulta_superauditor";

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

	@GetMapping
	public String get(
			HttpServletRequest request,
			Model model) throws Exception {
		if (!RequestSessionHelper.existeixObjecteSessio(
				request,
				SESSION_ATTRIBUTE_ENTITAT)) {
			model.addAttribute("entitats", entitatService.findAll());
		} else {
			omplirModelPerFiltreTaula(request, model);
		}
		getOrigens(model);
		return "superauditorConsultes";
	}
	private void getOrigens(Model model) {
		List<CodiValor> origens = new ArrayList<>();
		origens.add(new CodiValor("true", "admin.consulta.list.filtre.origen.recobriment"));
		origens.add(new CodiValor("false", "admin.consulta.list.filtre.origen.web"));
		model.addAttribute("origens", origens);
	}

	@PostMapping
	public String post(
			HttpServletRequest request,
			@Valid ConsultaFiltreCommand command,
			BindingResult bindingResult,
			Model model) throws Exception {
		if (bindingResult.hasErrors()) {
			omplirModelPerFiltreTaula(request, model);
			return "superauditorConsultes";
		} else {
			RequestSessionHelper.actualitzarObjecteSessio(
					request,
					SESSION_ATTRIBUTE_FILTRE,
					command);
			return "redirect:.";
		}
	}

	@GetMapping(value = "/datatable", produces="application/json")
	@ResponseBody
	public ServerSideResponse<ConsultaDto, Long> datatable(HttpServletRequest request, Model model)
	      throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, NamingException,
	      SQLException, EntitatNotFoundException {
		ServerSideRequest serverSideRequest = new ServerSideRequest(request);
		EntitatDto entitat = (EntitatDto)RequestSessionHelper.obtenirObjecteSessio(
				request,
				SESSION_ATTRIBUTE_ENTITAT);
		if (entitat == null) {
			throw new EntitatNotFoundException();
		}

		ConsultaFiltreCommand command = (ConsultaFiltreCommand)RequestSessionHelper.obtenirObjecteSessio(
				request,
				SESSION_ATTRIBUTE_FILTRE);
		if (command == null) {
			command = new ConsultaFiltreCommand();
			command.filtrarDarrersMesos(isHistoric(request) ? 9 : 3);
		} else {
			command.updateDefaultDataInici(isHistoric(request));
		}
		List<ServerSideColumn> cols = serverSideRequest.getColumns();
		cols.get(0).setData("peticioId");
		cols.get(1).setData("data");
		cols.get(2).setData("usuariNom");
		cols.get(3).setData("funcionariNom");
		cols.get(4).setData("procedimentCodi");
		cols.get(5).setData("serveiCodi");

		Page<ConsultaDto> page;
		if (isHistoric(request)) {
			page = historicConsultaService.findByFiltrePaginatPerSuperauditor(
					entitat.getId(),
					ConsultaFiltreCommand.asDto(command),
					serverSideRequest.toPageable());
		} else {
			page = consultaService.findByFiltrePaginatPerSuperauditor(
					entitat.getId(),
					ConsultaFiltreCommand.asDto(command),
					serverSideRequest.toPageable());
		}
		cols.get(0).setData("scspPeticionId");
		cols.get(1).setData("creacioData");
		cols.get(2).setData("creacioUsuari.nom");
		cols.get(3).setData("funcionariNomAmbDocument");
		cols.get(4).setData("procedimentCodiNom");
		cols.get(5).setData("serveiCodiNom");

		return new ServerSideResponse<ConsultaDto, Long>(serverSideRequest, page);
	}
	
	@RequestMapping(value = "/excel")
	public String excel(HttpServletRequest request, Model model) throws Exception {
		ConsultaFiltreCommand command = (ConsultaFiltreCommand) RequestSessionHelper.obtenirObjecteSessio(request,
				SESSION_ATTRIBUTE_FILTRE);
		if (command == null) {
			command = new ConsultaFiltreCommand();
			command.filtrarDarrersMesos(isHistoric(request) ? 9 : 3);
		} else {
			command.updateDefaultDataInici(isHistoric(request));
		}
		model.addAttribute(command);
		EntitatDto entitat = (EntitatDto)RequestSessionHelper.obtenirObjecteSessio(
				request,
				SESSION_ATTRIBUTE_ENTITAT);
		if (entitat == null) {
			throw new EntitatNotFoundException();
		}

		Page<ConsultaDto> page;
		if (isHistoric(request)) {
			page = historicConsultaService.findByFiltrePaginatPerSuperauditor(
					entitat.getId(),
					ConsultaFiltreCommand.asDto(command),
					null);
		} else {
			page = consultaService.findByFiltrePaginatPerSuperauditor(
					entitat.getId(),
					ConsultaFiltreCommand.asDto(command),
					null);
		}
		model.addAttribute("consultaList", page.getContent());

		return "consultaSuperauditorExcelView";
	}

	@GetMapping("/{consultaId}")
	public String info(
			HttpServletRequest request,
			@PathVariable Long consultaId,
			@RequestParam(value = "multiple", required = false) Boolean multiple,
			Model model) throws Exception {
		ConsultaDto consulta = getConsultaSuperauditor(consultaId, isHistoric(request));
		model.addAttribute("consulta", consulta);
		model.addAttribute("servei", serveiService.findAmbCodiPerAdminORepresentant(consulta.getServeiCodi()));
		model.addAttribute("historic", isHistoric(request));

		if (consulta.isMultiple()) {
			model.addAttribute("filles", getConsultesFilles(consultaId, isHistoric(request)));
			return "superauditorConsultaMultipleInfo";
		}

		omplirModelAmbDadesEspecifiques(consulta.getServeiCodi(), model);
		if (!consulta.isEstatError()) {
			ArbreRespostaDto dadesResposta = consultaService.generarArbreResposta(consultaId);
			model.addAttribute("dadesResposta", dadesResposta);
		}
		if (multiple != null && multiple) {
			model.addAttribute("multiple", true);
		}
		return "superauditorConsultaInfo";
	}

	@GetMapping("/{consultaId}/justificant/arxiu/detall")
	public String justificantArxiuDetall(
			HttpServletRequest request,
			HttpServletResponse response,
			@PathVariable Long consultaId,
			Model model) {
		model.addAttribute("arxiuDetall", isHistoric(request)
				? historicConsultaService.obtenirArxiuInfo(consultaId)
				: consultaService.obtenirArxiuInfo(consultaId));
		model.addAttribute("mostrarArxiuInfo", true);
		return "contingutArxiu";
	}

	@GetMapping("/{consultaId}/justificant")
	public String justificant(
			HttpServletRequest request,
			HttpServletResponse response,
			@PathVariable Long consultaId,
			Model model) throws ConsultaNotFoundException {
		try {
			ConsultaDto consulta = getConsultaSuperauditor(consultaId, isHistoric(request));
			if (consulta.isMultiple()) {
				FitxerDto fitxer = getJustificantMultiplePdf(consultaId, isHistoric(request));
				writeFileToResponse(fitxer.getNom(), fitxer.getContingut(), response);
				return null;
			}
			JustificantDto justificant = getJustificant(consultaId, isHistoric(request));
			if (!justificant.isError()) {
				writeFileToResponse(justificant.getNom(), justificant.getContingut(), response);
				return null;
			} else {
				AlertHelper.error(request, getMessage(request, "consulta.controller.justificant.error"));
				return "redirect:../../superauditor";
			}
		} catch (ConsultaNotFoundException ex) {
			throw ex;
		} catch (Exception ex) {
			AlertHelper.error(request, getMessage(request, "consulta.controller.justificant.error"));
			return "redirect:../../superauditor";
		}
	}

	@GetMapping("/{consultaId}/justificantpdf")
	public String justificantPdf(
			HttpServletRequest request,
			HttpServletResponse response,
			@PathVariable Long consultaId,
			Model model) throws ConsultaNotFoundException {
		try {
			FitxerDto fitxer = getJustificantMultiplePdf(consultaId, isHistoric(request));
			writeFileToResponse(fitxer.getNom(), fitxer.getContingut(), response);
			return null;
		} catch (ConsultaNotFoundException ex) {
			throw ex;
		} catch (Exception ex) {
			AlertHelper.error(request, getMessage(request, "consulta.controller.justificant.error") + ": " + ex.getMessage());
			return "redirect:../../superauditor";
		}
	}

	@GetMapping("/{consultaId}/justificantzip")
	public String justificantZip(
			HttpServletRequest request,
			HttpServletResponse response,
			@PathVariable Long consultaId,
			Model model) throws ConsultaNotFoundException {
		try {
			FitxerDto fitxer = getJustificantMultipleZip(consultaId, isHistoric(request));
			writeFileToResponse(fitxer.getNom(), fitxer.getContingut(), response);
			return null;
		} catch (ConsultaNotFoundException ex) {
			throw ex;
		} catch (Exception ex) {
			AlertHelper.error(request, getMessage(request, "consulta.controller.justificant.error") + ": " + ex.getMessage());
			return "redirect:../../superauditor";
		}
	}

	@GetMapping("/{consultaId}/xmlPeticio")
	public String xmlPeticio(
			HttpServletRequest request,
			HttpServletResponse response,
			@PathVariable Long consultaId,
			Model model) throws ConsultaNotFoundException, ScspException {
		ConsultaDto consulta = getConsultaSuperauditor(consultaId, isHistoric(request));
		model.addAttribute("consulta", consulta);
		model.addAttribute("mostrarPeticio", Boolean.TRUE);
		model.addAttribute("mostrarResposta", Boolean.FALSE);
		return "consultaXml";
	}

	@GetMapping("/{consultaId}/xmlResposta")
	public String xmlResposta(
			HttpServletRequest request,
			HttpServletResponse response,
			@PathVariable Long consultaId,
			Model model) throws ConsultaNotFoundException, ScspException {
		ConsultaDto consulta = getConsultaSuperauditor(consultaId, isHistoric(request));
		model.addAttribute("consulta", consulta);
		model.addAttribute("mostrarPeticio", Boolean.FALSE);
		model.addAttribute("mostrarResposta", Boolean.TRUE);
		return "consultaXml";
	}


	@PostMapping("/entitat/seleccionar")
	public String entitatSeleccionar(
			HttpServletRequest request,
			@RequestParam(value = "entitatId", required = false) Long entitatId) {
		if (entitatId == null) {
			AlertHelper.error(
					request, 
					getMessage(
							request, 
							"superauditor.controller.entitat.no.especificada"));
		} else {
			EntitatDto entitat = entitatService.findById(entitatId);
			if (entitat != null) {
				RequestSessionHelper.actualitzarObjecteSessio(
						request,
						SESSION_ATTRIBUTE_ENTITAT,
						entitat);
			}
		}
		return "redirect:../../superauditor";
	}

	@GetMapping("/entitat/deseleccionar")
	public String entitatDeseleccionar(
			HttpServletRequest request) {
		RequestSessionHelper.esborrarObjecteSessio(
				request,
				SESSION_ATTRIBUTE_ENTITAT);
		return "redirect:../../superauditor";
	}

	@GetMapping("/serveisPerProcediment/{procedimentId}")
	public String serveisPerProcediment(
			HttpServletRequest request,
			HttpServletResponse response,
			@PathVariable Long procedimentId,
			Model model) throws IOException, EntitatNotFoundException, ProcedimentNotFoundException {
		EntitatDto entitatActual = (EntitatDto)RequestSessionHelper.obtenirObjecteSessio(
				request,
				SESSION_ATTRIBUTE_ENTITAT);
		if (entitatActual != null) {
			if (procedimentId != null)
				model.addAttribute(
						"serveis",
						serveiService.findAmbEntitatIProcediment(
								entitatActual.getId(),
								procedimentId));
			else
				model.addAttribute(
						"serveis",
						serveiService.findAmbEntitat(
								entitatActual.getId()));
		}
		return "serveiSelectJson";
	}
	@GetMapping("/serveisPerProcediment")
	public String serveisPerProcedimentSenseId(
			HttpServletRequest request,
			HttpServletResponse response,
			Model model) throws IOException, EntitatNotFoundException, ProcedimentNotFoundException {
		return serveisPerProcediment(
				request,
				response,
				null,
				model);
	}

	@GetMapping("/generar")
	public String generarGet(
			HttpServletRequest request,
			Model model) throws EntitatNotFoundException, ScspException, ParseException {
		if (RequestSessionHelper.existeixObjecteSessio(
				request,
				SESSION_ATTRIBUTE_GENFORM)) {
			model.addAttribute(
					RequestSessionHelper.obtenirObjecteSessio(
							request,
							SESSION_ATTRIBUTE_GENFORM));
			omplirModelPerMostrarAuditoriaGenerada(
					request,
					model);
		} else {
			AuditoriaGenerarCommand command = new AuditoriaGenerarCommand();
			model.addAttribute(command);
		}
		return "superauditorGenerar";
	}
	@PostMapping("/generar")
	public String generarPost(
			HttpServletRequest request,
			@Valid AuditoriaGenerarCommand command,
			BindingResult bindingResult,
			Model model) throws EntitatNotFoundException, ScspException, ParseException {
		if (bindingResult.hasErrors()) {
			AlertHelper.error(
					request,
					getMessage(
							request, 
							"auditor.controller.generar.form.incomplet"));
			RequestSessionHelper.esborrarObjecteSessio(
					request,
					SESSION_ATTRIBUTE_GENFORM);
		} else {
			RequestSessionHelper.actualitzarObjecteSessio(
					request,
					SESSION_ATTRIBUTE_GENFORM,
					command);
			List<Long> ids = consultaService.auditoriaGenerarSuperauditor(
					command.getDataInici(),
					command.getDataFi(),
					command.getNumEntitats(),
					command.getNumConsultes());
			if (ids.size() > 0) {
				RequestSessionHelper.actualitzarObjecteSessio(
						request,
						SESSION_ATTRIBUTE_GENIDS,
						ids);
			} else {
				RequestSessionHelper.esborrarObjecteSessio(
						request,
						SESSION_ATTRIBUTE_GENIDS);
				AlertHelper.error(
						request,
						getMessage(
								request, 
								"auditor.controller.generar.no.peticions"));
			}
			omplirModelPerMostrarAuditoriaGenerada(
					request,
					model);
		}
		return "superauditorGenerar";
	}

	@GetMapping("/generarExcel")
	public String generarExcel(
			HttpServletRequest request,
			Model model) throws EntitatNotFoundException, ScspException, ParseException {
		if (RequestSessionHelper.existeixObjecteSessio(
				request,
				SESSION_ATTRIBUTE_GENIDS)) {
			@SuppressWarnings("unchecked")
			List<Long> ids = (List<Long>)RequestSessionHelper.obtenirObjecteSessio(
					request,
					SESSION_ATTRIBUTE_GENIDS);
			model.addAttribute(
					"consultesPerEntitat",
					consultaService.auditoriaConsultarSuperauditor(
							ids));
		}
		return "auditorGenerarExcelView";
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
		EntitatDto entitatActual = (EntitatDto)RequestSessionHelper.obtenirObjecteSessio(
				request,
				SESSION_ATTRIBUTE_ENTITAT);
		if (entitatActual != null) {
			model.addAttribute("entitatActual", entitatActual);
			ConsultaFiltreCommand command = (ConsultaFiltreCommand)RequestSessionHelper.obtenirObjecteSessio(
					request,
					SESSION_ATTRIBUTE_FILTRE);
			if (command == null) {
				command = new ConsultaFiltreCommand();
				command.filtrarDarrersMesos(isHistoric(request) ? 9 : 3);
			} else {
				command.updateDefaultDataInici(isHistoric(request));
			}
			model.addAttribute(
					"filtreCommand",
					command);
			model.addAttribute(
					"procediments",
					procedimentService.findAmbEntitat(entitatActual.getId()));
			if (command.getProcediment() != null)
				model.addAttribute(
						"serveis",
						serveiService.findAmbEntitatIProcediment(
								entitatActual.getId(),
								command.getProcediment()));
			else
				model.addAttribute(
						"serveis",
						serveiService.findAmbEntitat(entitatActual.getId()));
			model.addAttribute("historic", isHistoric(request));
		}
	}

	public void omplirModelPerMostrarAuditoriaGenerada(
			HttpServletRequest request,
			Model model) throws EntitatNotFoundException, ScspException {
		if (RequestSessionHelper.existeixObjecteSessio(
				request,
				SESSION_ATTRIBUTE_GENIDS)) {
			@SuppressWarnings("unchecked")
			List<Long> ids = (List<Long>)RequestSessionHelper.obtenirObjecteSessio(
					request,
					SESSION_ATTRIBUTE_GENIDS);
			model.addAttribute(
					"consultes",
					consultaService.auditoriaConsultarSuperauditor(
							ids));
		}
	}

	private boolean isHistoric(HttpServletRequest request) {
		Object historic = request.getSession().getAttribute(SESSION_CONSULTA_HISTORIC);
		if (historic == null)
			return false;
		else
			return ((Boolean) historic).booleanValue();
	}

	private ConsultaDto getConsultaSuperauditor(Long consultaId, boolean historic) throws ConsultaNotFoundException, ScspException {
		ConsultaDto consulta;
		if (historic) {
			try {
				consulta = historicConsultaService.findOneSuperauditor(consultaId);
			} catch (ConsultaNotFoundException nfe) {
				consulta = consultaService.findOneSuperauditor(consultaId);
			}
		} else {
			try {
				consulta = consultaService.findOneSuperauditor(consultaId);
			} catch (ConsultaNotFoundException nfe) {
				consulta = historicConsultaService.findOneSuperauditor(consultaId);
			}
		}
		return consulta;
	}

	private List<ConsultaDto> getConsultesFilles(Long consultaId, boolean historic) throws ConsultaNotFoundException, ScspException {
		List<ConsultaDto> filles;
		if (historic) {
			try {
				filles = historicConsultaService.findAmbPare(consultaId);
			} catch (ConsultaNotFoundException nfe) {
				filles = consultaService.findAmbPare(consultaId);
			}
		} else {
			try {
				filles = consultaService.findAmbPare(consultaId);
			} catch (ConsultaNotFoundException nfe) {
				filles = historicConsultaService.findAmbPare(consultaId);
			}
		}
		return filles;
	}

	private void omplirModelAmbDadesEspecifiques(
			String serveiCodi,
			Model model) throws ScspException, ServeiNotFoundException {
		List<NodeDto<DadaEspecificaDto>> llistaArbreDadesEspecifiques = serveiService.generarArbreDadesEspecifiques(serveiCodi).toList();
		model.addAttribute("llistaArbreDadesEspecifiques", llistaArbreDadesEspecifiques);
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
		boolean mostraDadesEspecifiques = false;
		for (ServeiCampDto camp: camps) {
			if (camp.isVisible()) {
				mostraDadesEspecifiques = true;
				break;
			}
		}
		model.addAttribute("mostrarDadesEspecifiques", mostraDadesEspecifiques);
	}

	private JustificantDto getJustificant(Long consultaId, boolean historic) throws Exception {
		JustificantDto justificant;
		if (historic) {
			try {
				justificant = historicConsultaService.obtenirJustificant(consultaId, true);
			} catch (ConsultaNotFoundException nfe) {
				justificant = consultaService.obtenirJustificant(consultaId, true);
			}
		} else {
			try {
				justificant = consultaService.obtenirJustificant(consultaId, true);
			} catch (ConsultaNotFoundException nfe) {
				justificant = historicConsultaService.obtenirJustificant(consultaId, true);
			}
		}
		return justificant;
	}

	private FitxerDto getJustificantMultiplePdf(Long consultaId, boolean historic) throws Exception {
		FitxerDto fitxer;
		if (historic) {
			try {
				fitxer = historicConsultaService.obtenirJustificantMultipleConcatenat(consultaId);
			} catch (ConsultaNotFoundException nfe) {
				fitxer = consultaService.obtenirJustificantMultipleConcatenat(consultaId);
			}
		} else {
			try {
				fitxer = consultaService.obtenirJustificantMultipleConcatenat(consultaId);
			} catch (ConsultaNotFoundException nfe) {
				fitxer = historicConsultaService.obtenirJustificantMultipleConcatenat(consultaId);
			}
		}
		return fitxer;
	}

	private FitxerDto getJustificantMultipleZip(Long consultaId, boolean historic) throws Exception {
		FitxerDto fitxer;
		if (historic) {
			try {
				fitxer = historicConsultaService.obtenirJustificantMultipleZip(consultaId);
			} catch (ConsultaNotFoundException nfe) {
				fitxer = consultaService.obtenirJustificantMultipleZip(consultaId);
			}
		} else {
			try {
				fitxer = consultaService.obtenirJustificantMultipleZip(consultaId);
			} catch (ConsultaNotFoundException nfe) {
				fitxer = historicConsultaService.obtenirJustificantMultipleZip(consultaId);
			}
		}
		return fitxer;
	}
}
