/**
 * 
 */
package es.caib.pinbal.webapp.controller;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

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

import es.caib.pinbal.core.dto.EntitatDto;
import es.caib.pinbal.core.dto.EstadisticaDto;
import es.caib.pinbal.core.dto.EstadistiquesFiltreDto;
import es.caib.pinbal.core.service.ConsultaService;
import es.caib.pinbal.core.service.EntitatService;
import es.caib.pinbal.core.service.ProcedimentService;
import es.caib.pinbal.core.service.ServeiService;
import es.caib.pinbal.core.service.exception.EntitatNotFoundException;
import es.caib.pinbal.core.service.exception.ProcedimentNotFoundException;
import es.caib.pinbal.webapp.command.EstadistiquesFiltreCommand;
import es.caib.pinbal.webapp.common.EntitatHelper;
import es.caib.pinbal.webapp.common.RequestSessionHelper;
import es.caib.pinbal.webapp.common.RolHelper;

/**
 * Controlador per les estadístiques.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/estadistiques")
public class EstadistiquesController {

	private static final String SESSION_ATTRIBUTE_ENTITAT_ID = "EstadistiquesController.session.entitat.id";
	private static final String SESSION_ATTRIBUTE_FILTRE = "EstadistiquesController.session.filtre";

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
			Model model) throws EntitatNotFoundException, ProcedimentNotFoundException {
		if (	!RolHelper.isRolActualAdministrador(request) && 
				!EntitatHelper.isRepresentantEntitatActual(request))
			return "representantNoAutoritzat";
		EstadistiquesFiltreCommand command = (EstadistiquesFiltreCommand)RequestSessionHelper.obtenirObjecteSessio(
				request,
				SESSION_ATTRIBUTE_FILTRE);
		if (command == null)
			command = new EstadistiquesFiltreCommand();
		model.addAttribute(command);
		omplirModel(request, command, model);
		return "estadistiques";
	}

	@RequestMapping(value = "/excel")
	public String excel(
			HttpServletRequest request,
			Model model) throws EntitatNotFoundException, ProcedimentNotFoundException {
		if (	!RolHelper.isRolActualAdministrador(request) && 
				!EntitatHelper.isRepresentantEntitatActual(request))
			return "representantNoAutoritzat";
		EstadistiquesFiltreCommand command = (EstadistiquesFiltreCommand)RequestSessionHelper.obtenirObjecteSessio(
				request,
				SESSION_ATTRIBUTE_FILTRE);
		if (command == null)
			command = new EstadistiquesFiltreCommand();
		model.addAttribute(command);
		omplirModel(request, command, model);
		return "estadistiquesExcelView";
	}

	@RequestMapping(method = RequestMethod.POST)
	public String post(
			HttpServletRequest request,
			@Valid EstadistiquesFiltreCommand command,
			BindingResult bindingResult,
			Model model) throws Exception {
		if (	!RolHelper.isRolActualAdministrador(request) && 
				!EntitatHelper.isRepresentantEntitatActual(request))
				return "representantNoAutoritzat";
		if (bindingResult.hasErrors()) {
			omplirModel(request, command, model);
			return "estadistiques";
		} else {
			RequestSessionHelper.actualitzarObjecteSessio(
					request,
					SESSION_ATTRIBUTE_FILTRE,
					command);
			return "redirect:estadistiques";
		}
	}

	@RequestMapping(value = "/canviEntitat")
	public String canviEntitat(
			HttpServletRequest request,
			@RequestParam(value = "entitatId", required = false) Long entitatId) {
		RequestSessionHelper.actualitzarObjecteSessio(
				request,
				SESSION_ATTRIBUTE_ENTITAT_ID,
				entitatId);
		return "redirect:../estadistiques";
	}

	@RequestMapping(value = "/serveisPerProcediment/{procedimentId}", method = RequestMethod.GET)
	public String serveisPerProcediment(
			HttpServletRequest request,
			HttpServletResponse response,
			@PathVariable Long procedimentId,
			Model model) throws IOException, EntitatNotFoundException, ProcedimentNotFoundException {
		Long entitatId = (Long)RequestSessionHelper.obtenirObjecteSessio(
				request,
				SESSION_ATTRIBUTE_ENTITAT_ID);
		if (entitatId == null && EntitatHelper.isRepresentantEntitatActual(request)) {
			EntitatDto entitat = EntitatHelper.getEntitatActual(request);
			entitatId = entitat.getId();
		}
		if (entitatId != null) {
			if (procedimentId != null)
				model.addAttribute(
						"serveis",
						serveiService.findAmbEntitatIProcediment(
								entitatId,
								procedimentId));
			else
				model.addAttribute(
						"serveis",
						serveiService.findAmbEntitat(
								entitatId));
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

	@InitBinder
	public void initBinder(WebDataBinder binder) {
	    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
	    dateFormat.setLenient(false);
	    binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
	}



	private void omplirModel(
			HttpServletRequest request,
			EstadistiquesFiltreCommand command,
			Model model) throws EntitatNotFoundException, ProcedimentNotFoundException {
		if (RolHelper.isRolActualAdministrador(request)) {
			model.addAttribute(
					"entitats",
					entitatService.findAll());
			Long entitatId = (Long)RequestSessionHelper.obtenirObjecteSessio(
					request,
					SESSION_ATTRIBUTE_ENTITAT_ID);
			command.setEntitatId(entitatId);
			if (entitatId != null) {
				model.addAttribute(
						"entitatSeleccionada",
						entitatService.findById(entitatId));
			}
		} else {
			EntitatDto entitat = EntitatHelper.getEntitatActual(request, entitatService);
			command.setEntitatId(entitat.getId());
			model.addAttribute(
					"entitatSeleccionada",
					entitat);
		}
		if (command.getEntitatId() != null) {
			EstadistiquesFiltreDto filtre = EstadistiquesFiltreCommand.asDto(command);
			if (command.getEntitatId() == -1) {
				filtre.setEntitatId(null);
			}
			if (filtre.getEntitatId() != null) {
				List<EstadisticaDto> estadistiques = consultaService.findEstadistiquesByFiltre(
						filtre);
				model.addAttribute("estadistiques", estadistiques);
			} else {
				Map<EntitatDto, List<EstadisticaDto>> estadistiquesPerEntitat = consultaService.findEstadistiquesGlobalsByFiltre(
						filtre);
				model.addAttribute("estadistiquesPerEntitat", estadistiquesPerEntitat);
			}
		}
	}

}
