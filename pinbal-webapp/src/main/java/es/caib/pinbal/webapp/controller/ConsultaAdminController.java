/**
 * 
 */
package es.caib.pinbal.webapp.controller;

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
import org.springframework.web.bind.annotation.RequestParam;

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
import es.caib.pinbal.core.service.exception.ConsultaNotFoundException;
import es.caib.pinbal.core.service.exception.ScspException;
import es.caib.pinbal.webapp.command.ConsultaFiltreCommand;
import es.caib.pinbal.webapp.common.AlertHelper;
import es.caib.pinbal.webapp.common.RequestSessionHelper;
import es.caib.pinbal.webapp.jmesa.JMesaGridHelper;
import es.caib.pinbal.webapp.jmesa.JMesaGridHelper.ConsultaPagina;

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
		if (!RequestSessionHelper.existeixObjecteSessio(
				request,
				SESSION_ATTRIBUTE_ENTITAT)) {
			model.addAttribute("entitats", entitatService.findAll());
		} else {
			omplirModelPerMostrarLlistat(request, model);
		}
		return "adminConsultes";
	}

	@RequestMapping(method = RequestMethod.POST)
	public String post(
			HttpServletRequest request,
			@Valid ConsultaFiltreCommand command,
			BindingResult bindingResult,
			Model model) throws Exception {
		if (bindingResult.hasErrors()) {
			omplirModelPerMostrarLlistat(request, model);
			return "adminConsultes";
		} else {
			RequestSessionHelper.actualitzarObjecteSessio(
					request,
					SESSION_ATTRIBUTE_FILTRE,
					command);
			return "redirect:consulta";
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
		ConsultaDto consulta = consultaService.findOneAdmin(consultaId);
		model.addAttribute("consulta", consulta);
		model.addAttribute(
				"servei",
				serveiService.findAmbCodiPerAdminORepresentant(
						consulta.getServeiCodi()));
		return "adminConsultaInfo";
	}

	@RequestMapping(value = "/{consultaId}/xmlPeticio", method = RequestMethod.GET)
	public String xmlPeticio(
			HttpServletRequest request,
			HttpServletResponse response,
			@PathVariable Long consultaId,
			Model model) throws ConsultaNotFoundException, ScspException {
		ConsultaDto consulta = consultaService.findOneAdmin(consultaId);
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
		ConsultaDto consulta = consultaService.findOneAdmin(consultaId);
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



	private void omplirModelPerMostrarLlistat(
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
			if (command == null)
				command = new ConsultaFiltreCommand();
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
			List<?> paginaConsultes = JMesaGridHelper.consultarPaginaIActualitzarLimit(
					"consultes",
					request,
					new ConsultaPaginaConsulta(
							consultaService,
							entitatActual,
							command),
					new OrdreDto("creacioData", OrdreDireccio.DESCENDENT));
			model.addAttribute(
					"consultes",
					paginaConsultes);
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
			return consultaService.findByFiltrePaginatPerAdmin(
					entitat.getId(),
					ConsultaFiltreCommand.asDto(command),
					paginacioAmbOrdre);
		}
	}

}
