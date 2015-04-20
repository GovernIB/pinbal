/**
 * 
 */
package es.caib.pinbal.webapp.controller;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import es.caib.pinbal.core.dto.ConsultaDto;
import es.caib.pinbal.core.dto.EntitatDto;
import es.caib.pinbal.core.dto.OrdreDto;
import es.caib.pinbal.core.dto.OrdreDto.OrdreDireccio;
import es.caib.pinbal.core.dto.PaginaLlistatDto;
import es.caib.pinbal.core.dto.PaginacioAmbOrdreDto;
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
import es.caib.pinbal.webapp.jmesa.JMesaGridHelper;
import es.caib.pinbal.webapp.jmesa.JMesaGridHelper.ConsultaPagina;

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

	@Autowired
	private EntitatService entitatService;
	@Autowired
	private ProcedimentService procedimentService;
	@Autowired
	private ServeiService serveiService;
	@Autowired
	private ConsultaService consultaService;

	@RequestMapping(method = RequestMethod.GET)
	public String get(
			HttpServletRequest request,
			Model model) throws Exception {
		if (!EntitatHelper.isAuditorEntitatActual(request))
			return "auditorNoAutoritzat";
		EntitatDto entitat = EntitatHelper.getEntitatActual(request, entitatService);
		if (entitat != null) {
			omplirModelPerMostrarLlistat(request, entitat, model);
		}
		return "auditorConsultes";
	}

	@RequestMapping(method = RequestMethod.POST)
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

	/*@RequestMapping(value = "/{consultaId}/xmlPeticio", method = RequestMethod.GET)
	public String xmlPeticio(
			HttpServletRequest request,
			HttpServletResponse response,
			@PathVariable Long consultaId,
			Model model) throws ConsultaNotFoundException, ScspException {
		if (!EntitatHelper.isAuditorEntitatActual(request))
			return "auditorNoAutoritzat";
		EntitatDto entitat = EntitatHelper.getEntitatActual(request, entitatService);
		if (entitat != null) {
			ConsultaDto consulta = consultaService.findOneAuditor(consultaId);
			model.addAttribute("consulta", consulta);
			model.addAttribute("mostrarPeticio", new Boolean(true));
			model.addAttribute("mostrarResposta", new Boolean(false));
			return "consultaXml";
		}
		return "auditorConsultes";
	}*/

	@RequestMapping(value = "/serveisPerProcediment/{procedimentId}", method = RequestMethod.GET)
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
	@RequestMapping(value = "/generar", method = RequestMethod.POST)
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

	@RequestMapping(value = "/generarExcel", method = RequestMethod.GET)
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
		if (command == null)
			command = new ConsultaFiltreCommand();
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
			return consultaService.findByFiltrePaginatPerAuditor(
					entitat.getId(),
					ConsultaFiltreCommand.asDto(command),
					paginacioAmbOrdre);
		}
	}

}
