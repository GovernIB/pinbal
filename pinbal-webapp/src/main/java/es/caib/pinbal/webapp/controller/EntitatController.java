/**
 * 
 */
package es.caib.pinbal.webapp.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import es.caib.pinbal.core.dto.EntitatDto;
import es.caib.pinbal.core.dto.OrdreDto;
import es.caib.pinbal.core.dto.OrdreDto.OrdreDireccio;
import es.caib.pinbal.core.dto.PaginaLlistatDto;
import es.caib.pinbal.core.dto.PaginacioAmbOrdreDto;
import es.caib.pinbal.core.service.EntitatService;
import es.caib.pinbal.core.service.PropertyService;
import es.caib.pinbal.core.service.exception.EntitatNotFoundException;
import es.caib.pinbal.webapp.command.EntitatCommand;
import es.caib.pinbal.webapp.command.EntitatFiltreCommand;
import es.caib.pinbal.webapp.common.AlertHelper;
import es.caib.pinbal.webapp.common.RequestSessionHelper;
import es.caib.pinbal.webapp.jmesa.JMesaGridHelper;
import es.caib.pinbal.webapp.jmesa.JMesaGridHelper.ConsultaPagina;

/**
 * Controlador per al manteniment d'entitats.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/entitat")
public class EntitatController extends BaseController {

	private static final String SESSION_ATTRIBUTE_FILTRE = "EntitatController.session.filtre";

	@Autowired
	private EntitatService entitatService;
	@Autowired
	private PropertyService propertyService;



	@RequestMapping(method = RequestMethod.GET)
	public String get(
			HttpServletRequest request,
			Model model) throws Exception {
		omplirModelPerMostrarLlistat(request, model);
		return "entitatList";
	}
	@RequestMapping(method = RequestMethod.POST)
	public String post(
			HttpServletRequest request,
			@Valid EntitatFiltreCommand command,
			BindingResult bindingResult,
			Model model) throws Exception {
		if (bindingResult.hasErrors()) {
			omplirModelPerMostrarLlistat(request, model);
			return "entitatList";
		} else {
			RequestSessionHelper.actualitzarObjecteSessio(
					request,
					SESSION_ATTRIBUTE_FILTRE,
					command);
			return "redirect:entitat";
		}
	}

	@RequestMapping(value = "/new", method = RequestMethod.GET)
	public String get(Model model) {
		return get((Long)null, model);
	}
	@RequestMapping(value = "/{entitatId}", method = RequestMethod.GET)
	public String get(
			@PathVariable Long entitatId,
			Model model) {
		EntitatDto entitat = null;
		if (entitatId != null)
			entitat = entitatService.findById(entitatId);
		if (entitat != null)
			model.addAttribute(EntitatCommand.asCommand(entitat));
		else
			model.addAttribute(new EntitatCommand());
		return "entitatForm";
	}

	@RequestMapping(value = "/save", method = RequestMethod.POST)
	public String save(
			HttpServletRequest request,
			@Valid EntitatCommand command,
			BindingResult bindingResult) throws EntitatNotFoundException {
		if (bindingResult.hasErrors()) {
			return "entitatForm";
		}
		if (command.getId() != null) {
			entitatService.update(EntitatCommand.asDto(command));
			AlertHelper.success(
					request, 
					getMessage(
							request, 
							"entitat.controller.entitat.modificada.ok"));
		} else {
			entitatService.create(EntitatCommand.asDto(command));
			AlertHelper.success(
					request, 
					getMessage(
							request, 
							"entitat.controller.entitat.creada.ok"));
		}
		return "redirect:../entitat";
	}

	@RequestMapping(value = "/{entitatId}/enable", method = RequestMethod.GET)
	public String enable(
			HttpServletRequest request,
			@PathVariable Long entitatId) throws EntitatNotFoundException {
		entitatService.updateActiva(entitatId, true);
		AlertHelper.success(
				request, 
				getMessage(
						request, 
						"entitat.controller.entitat.activat.ok"));
		return "redirect:../../entitat";
	}
	@RequestMapping(value = "/{entitatId}/disable", method = RequestMethod.GET)
	public String disable(
			HttpServletRequest request,
			@PathVariable Long entitatId) throws EntitatNotFoundException {
		entitatService.updateActiva(entitatId, false);
		AlertHelper.success(
				request, 
				getMessage(
						request, 
						"entitat.controller.entitat.desactivat.ok"));
		return "redirect:../../entitat";
	}

	@RequestMapping(value = "/{entitatId}/delete", method = RequestMethod.GET)
	public String delete(
			HttpServletRequest request,
			@PathVariable Long entitatId) throws EntitatNotFoundException {
		entitatService.delete(entitatId);
		AlertHelper.success(
				request, 
				getMessage(
						request, 
						"entitat.controller.entitat.esborrat.ok"));
		return "redirect:../../entitat";
	}



	private void omplirModelPerMostrarLlistat(
			HttpServletRequest request,
			Model model) throws Exception {
		EntitatFiltreCommand command = (EntitatFiltreCommand)RequestSessionHelper.obtenirObjecteSessio(
				request,
				SESSION_ATTRIBUTE_FILTRE);
		if (command == null)
			command = new EntitatFiltreCommand();
		model.addAttribute(command);
		List<?> paginaEntitats = JMesaGridHelper.consultarPaginaIActualitzarLimit(
				"entitats",
				request,
				new ConsultaPaginaEntitat(
						entitatService,
						command),
				new OrdreDto("nom", OrdreDireccio.DESCENDENT));
		model.addAttribute("entitats", paginaEntitats);
		model.addAttribute(
				"propertyEsborrar",
				propertyService.get(
						"es.caib.pinbal.entitat.accio.esborrar.activa"));
	}

	public class ConsultaPaginaEntitat implements ConsultaPagina<EntitatDto> {
		EntitatService entitatService;
		EntitatFiltreCommand command;
		public ConsultaPaginaEntitat(
				EntitatService entitatService,
				EntitatFiltreCommand command) {
			this.entitatService = entitatService;
			this.command = command;
		}
		public PaginaLlistatDto<EntitatDto> consultar(
				PaginacioAmbOrdreDto paginacioAmbOrdre) throws Exception {
			return entitatService.findAmbFiltrePaginat(
					command.getCodi(),
					command.getNom(),
					paginacioAmbOrdre);
		}
	}

}
