/**
 * 
 */
package es.caib.pinbal.webapp.controller;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.naming.NamingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import es.caib.pinbal.core.dto.CodiValor;
import es.caib.pinbal.core.service.HistoricConsultaService;
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
import es.caib.pinbal.core.service.ConsultaService;
import es.caib.pinbal.core.service.EntitatService;
import es.caib.pinbal.core.service.ProcedimentService;
import es.caib.pinbal.core.service.ServeiService;
import es.caib.pinbal.core.service.exception.EntitatNotFoundException;
import es.caib.pinbal.core.service.exception.ProcedimentNotFoundException;
import es.caib.pinbal.core.service.exception.ScspException;
import es.caib.pinbal.webapp.command.AuditoriaGenerarCommand;
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

	@RequestMapping(method = RequestMethod.GET)
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

	@RequestMapping(method = RequestMethod.POST)
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

	@RequestMapping(value = "/datatable", produces="application/json", method = RequestMethod.GET)
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
		cols.get(1).setData("createdDate");
		cols.get(2).setData("createdBy.nom");
		cols.get(4).setData("procedimentServei.procediment.nom");

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
		
		cols.get(1).setData("creacioData");
		cols.get(2).setData("creacioUsuari.nom");
		cols.get(3).setData("funcionariNomAmbDocument");
		cols.get(4).setData("procedimentNom");
		cols.get(5).setData("serveiDescripcio");

		return new ServerSideResponse<ConsultaDto, Long>(serverSideRequest, page);
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

	@RequestMapping(value = "/entitat/deseleccionar", method = RequestMethod.GET)
	public String entitatDeseleccionar(
			HttpServletRequest request) {
		RequestSessionHelper.esborrarObjecteSessio(
				request,
				SESSION_ATTRIBUTE_ENTITAT);
		return "redirect:../../superauditor";
	}

	@RequestMapping(value = "/serveisPerProcediment/{procedimentId}", method = RequestMethod.GET)
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
	@RequestMapping(value = "/serveisPerProcediment", method = RequestMethod.GET)
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

	@RequestMapping(value = "/generar", method = RequestMethod.GET)
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
	@RequestMapping(value = "/generar", method = RequestMethod.POST)
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

	@RequestMapping(value = "/generarExcel", method = RequestMethod.GET)
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
}
