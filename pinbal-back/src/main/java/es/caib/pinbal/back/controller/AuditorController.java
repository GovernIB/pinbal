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
import es.caib.pinbal.back.helper.EntitatHelper;
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
import es.caib.pinbal.logic.intf.dto.UsuariDto;
import es.caib.pinbal.logic.intf.service.ConsultaService;
import es.caib.pinbal.logic.intf.service.EntitatService;
import es.caib.pinbal.logic.intf.service.HistoricConsultaService;
import es.caib.pinbal.logic.intf.service.ProcedimentService;
import es.caib.pinbal.logic.intf.service.ServeiService;
import es.caib.pinbal.logic.intf.service.UsuariService;
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
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
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
 * Controlador per a les auditories dels auditors normals.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/auditor")
public class AuditorController extends BaseController {

	public static final String SESSION_ATTRIBUTE_FILTRE = "AuditorController.session.filtre";
	public static final String SESSION_ATTRIBUTE_GENFORM = "AuditorController.session.genform";
	public static final String SESSION_ATTRIBUTE_GENIDS = "AuditorController.session.genids";
	public static final String SESSION_CONSULTA_HISTORIC = "consulta_auditor";

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

	@GetMapping
	public String get(
			HttpServletRequest request,
			Model model) throws Exception {
		if (!EntitatHelper.isAuditorEntitatActual(request))
			return "auditorNoAutoritzat";
		EntitatDto entitat = EntitatHelper.getEntitatActual(request, entitatService);
		if (entitat != null) {
			omplirModelPerMostrarLlistat(request, entitat, model);
		}
		getOrigens(model);
		return "auditorConsultes";
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
		if (!EntitatHelper.isAuditorEntitatActual(request))
			return "auditorNoAutoritzat";
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
		return "auditorConsultes";
	}

	@GetMapping(value = "/datatable", produces="application/json")
	@ResponseBody
	public ServerSideResponse<ConsultaDto, Long> datatable(HttpServletRequest request, Model model)
	      throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, NamingException,
	      SQLException, EntitatNotFoundException {
		ServerSideRequest serverSideRequest = new ServerSideRequest(request);
		EntitatDto entitat = EntitatHelper.getEntitatActual(request, entitatService);
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
			page = historicConsultaService.findByFiltrePaginatPerAuditor(
					entitat.getId(),
					ConsultaFiltreCommand.asDto(command),
					serverSideRequest.toPageable());
		} else {
			page = consultaService.findByFiltrePaginatPerAuditor(
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
		EntitatDto entitat = EntitatHelper.getEntitatActual(request, entitatService);

		Page<ConsultaDto> page;
		if (isHistoric(request)) {
			page = historicConsultaService.findByFiltrePaginatPerAuditor(
					entitat.getId(),
					ConsultaFiltreCommand.asDto(command),
					null);
		} else {
			page = consultaService.findByFiltrePaginatPerAuditor(
					entitat.getId(),
					ConsultaFiltreCommand.asDto(command),
					null);
		}
		model.addAttribute("consultaList", page.getContent());

		return "consultaAuditorExcelView";
	}

	@GetMapping("/excelConsultes")
	public String excelConsultes(
			HttpServletRequest request,
			Model model) throws EntitatNotFoundException {

		EntitatDto entitat = EntitatHelper.getEntitatActual(request, entitatService);
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

		List<ConsultaDto> llistat;
		if (isHistoric(request)) {
			llistat = historicConsultaService.findByFiltrePerAuditor(
					entitat.getId(),
					ConsultaFiltreCommand.asDto(command));
		} else {
			llistat = consultaService.findByFiltrePerAuditor(
					entitat.getId(),
					ConsultaFiltreCommand.asDto(command));
		}

		model.addAttribute("consultes", llistat);

		return "auditorGenerarExcelView";
	}

	@GetMapping("/csvConsultes")
	public String csvConsultes(
			HttpServletRequest request,
			Model model) throws EntitatNotFoundException {

		EntitatDto entitat = EntitatHelper.getEntitatActual(request, entitatService);
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

		List<ConsultaDto> llistat;
		if (isHistoric(request)) {
			llistat = historicConsultaService.findByFiltrePerAuditor(
					entitat.getId(),
					ConsultaFiltreCommand.asDto(command));
		} else {
			llistat = consultaService.findByFiltrePerAuditor(
					entitat.getId(),
					ConsultaFiltreCommand.asDto(command));
		}

		model.addAttribute("consultes", llistat);

		return "auditorGenerarCsvView";
	}

	@GetMapping("/{consultaId}")
	public String info(
			HttpServletRequest request,
			@PathVariable Long consultaId,
			@RequestParam(value = "multiple", required = false) Boolean multiple,
			Model model) throws Exception {
		ConsultaDto consulta = getConsultaAuditor(consultaId, isHistoric(request));
		model.addAttribute("consulta", consulta);
		model.addAttribute("servei", serveiService.findAmbCodiPerAdminORepresentant(consulta.getServeiCodi()));
		model.addAttribute("historic", isHistoric(request));

		if (consulta.isMultiple()) {
			model.addAttribute("filles", getConsultesFilles(consultaId, isHistoric(request)));
			return "auditorConsultaMultipleInfo";
		}

		omplirModelAmbDadesEspecifiques(consulta.getServeiCodi(), model);
		if (!consulta.isEstatError()) {
			ArbreRespostaDto dadesResposta = consultaService.generarArbreResposta(consultaId);
			model.addAttribute("dadesResposta", dadesResposta);
		}
		if (multiple != null && multiple) {
			model.addAttribute("multiple", true);
		}
		return "auditorConsultaInfo";
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
			ConsultaDto consulta = getConsultaAuditor(consultaId, isHistoric(request));
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
				return "redirect:../../auditor";
			}
		} catch (ConsultaNotFoundException ex) {
			throw ex;
		} catch (Exception ex) {
			AlertHelper.error(request, getMessage(request, "consulta.controller.justificant.error"));
			return "redirect:../../auditor";
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
			return "redirect:../../auditor";
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
			return "redirect:../../auditor";
		}
	}

	@GetMapping("/{consultaId}/xmlPeticio")
	public String xmlPeticio(
			HttpServletRequest request,
			HttpServletResponse response,
			@PathVariable Long consultaId,
			Model model) throws ConsultaNotFoundException, ScspException {
		ConsultaDto consulta = getConsultaAuditor(consultaId, isHistoric(request));
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
		ConsultaDto consulta = getConsultaAuditor(consultaId, isHistoric(request));
		model.addAttribute("consulta", consulta);
		model.addAttribute("mostrarPeticio", Boolean.FALSE);
		model.addAttribute("mostrarResposta", Boolean.TRUE);
		return "consultaXml";
	}

	@GetMapping("/serveisPerProcediment/{procedimentId}")
	public String serveisPerProcediment(
			HttpServletRequest request,
			HttpServletResponse response,
			@PathVariable Long procedimentId,
			Model model) throws IOException, EntitatNotFoundException, ProcedimentNotFoundException {
		if (EntitatHelper.isAuditorEntitatActual(request)) {
			EntitatDto entitat = EntitatHelper.getEntitatActual(request, entitatService);
			if (procedimentId != null)
				model.addAttribute(
						"serveis",
						serveiService.findAmbEntitatIProcediment(
								entitat.getId(),
								procedimentId));
			else
				model.addAttribute(
						"serveis",
						serveiService.findAmbEntitat(
								entitat.getId()));
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
		if (!EntitatHelper.isAuditorEntitatActual(request))
			return "auditorNoAutoritzat";
		if (RequestSessionHelper.existeixObjecteSessio(
				request,
				SESSION_ATTRIBUTE_GENFORM)) {
			model.addAttribute(
					RequestSessionHelper.obtenirObjecteSessio(
							request,
							SESSION_ATTRIBUTE_GENFORM));
			EntitatDto entitat = EntitatHelper.getEntitatActual(request, entitatService);
			omplirModelPerMostrarAuditoriaGenerada(
					request,
					entitat,
					model);
		} else {
			AuditoriaGenerarCommand command = new AuditoriaGenerarCommand();
			command.setNumEntitats(1);
			model.addAttribute(command);
		}
		return "auditorGenerar";
	}
	@PostMapping("/generar")
	public String generarPost(
			HttpServletRequest request,
			@Valid AuditoriaGenerarCommand command,
			BindingResult bindingResult,
			Model model) throws EntitatNotFoundException, ScspException, ParseException {
		if (!EntitatHelper.isAuditorEntitatActual(request))
			return "auditorNoAutoritzat";
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
			EntitatDto entitat = EntitatHelper.getEntitatActual(request, entitatService);
			RequestSessionHelper.actualitzarObjecteSessio(
					request,
					SESSION_ATTRIBUTE_GENFORM,
					command);
			List<Long> ids = consultaService.auditoriaGenerarAuditor(
					entitat.getId(),
					command.getDataInici(),
					command.getDataFi(),
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
					entitat,
					model);
		}
		return "auditorGenerar";
	}

	@GetMapping("/generarExcel")
	public String generarExcel(
			HttpServletRequest request,
			Model model) throws EntitatNotFoundException, ScspException, ParseException {
		if (!EntitatHelper.isAuditorEntitatActual(request))
			return "auditorNoAutoritzat";
		if (RequestSessionHelper.existeixObjecteSessio(
				request,
				SESSION_ATTRIBUTE_GENIDS)) {
			EntitatDto entitat = EntitatHelper.getEntitatActual(request, entitatService);
			@SuppressWarnings("unchecked")
			List<Long> ids = (List<Long>)RequestSessionHelper.obtenirObjecteSessio(
					request,
					SESSION_ATTRIBUTE_GENIDS);
			model.addAttribute(
					"consultes",
					consultaService.auditoriaConsultarAuditor(
							entitat.getId(),
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

	private void omplirModelPerMostrarLlistat(
			HttpServletRequest request,
			EntitatDto entitat,
			Model model) throws Exception {
		ConsultaFiltreCommand command = (ConsultaFiltreCommand)RequestSessionHelper.obtenirObjecteSessio(
				request,
				SESSION_ATTRIBUTE_FILTRE);
		if (command == null) {
			command = new ConsultaFiltreCommand();
			command.filtrarDarrersMesos(isHistoric(request) ? 9 : 3);
			UsuariDto usuari = usuariService.getDades();
			command.setProcediment(usuari.getProcedimentId());
			command.setServei(usuari.getServeiCodi());
			RequestSessionHelper.actualitzarObjecteSessio(
					request,
					SESSION_ATTRIBUTE_FILTRE,
					command);
		} else {
			command.updateDefaultDataInici(isHistoric(request));
		}
		model.addAttribute(
				"filtreCommand",
				command);
		model.addAttribute(
				"procediments",
				procedimentService.findAmbEntitat(entitat.getId()));
		if (command.getProcediment() != null)
			model.addAttribute(
					"serveis",
					serveiService.findAmbEntitatIProcediment(
							entitat.getId(),
							command.getProcediment()));
		else
			model.addAttribute(
					"serveis",
					serveiService.findAmbEntitat(entitat.getId()));
		model.addAttribute("historic", isHistoric(request));
	}

	private void omplirModelPerMostrarAuditoriaGenerada(
			HttpServletRequest request,
			EntitatDto entitat,
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
					consultaService.auditoriaConsultarAuditor(
							entitat.getId(),
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

	private ConsultaDto getConsultaAuditor(Long consultaId, boolean historic) throws ConsultaNotFoundException, ScspException {
		ConsultaDto consulta;
		if (historic) {
			try {
				consulta = historicConsultaService.findOneAuditor(consultaId);
			} catch (ConsultaNotFoundException nfe) {
				consulta = consultaService.findOneAuditor(consultaId);
			}
		} else {
			try {
				consulta = consultaService.findOneAuditor(consultaId);
			} catch (ConsultaNotFoundException nfe) {
				consulta = historicConsultaService.findOneAuditor(consultaId);
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
