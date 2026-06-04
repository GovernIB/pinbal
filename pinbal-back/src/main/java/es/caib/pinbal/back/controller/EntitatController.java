/**
 * 
 */
package es.caib.pinbal.back.controller;

import es.caib.pinbal.back.command.EntitatCommand;
import es.caib.pinbal.back.command.EntitatFiltreCommand;
import es.caib.pinbal.back.datatables.ServerSideRequest;
import es.caib.pinbal.back.datatables.ServerSideResponse;
import es.caib.pinbal.back.helper.AlertHelper;
import es.caib.pinbal.back.helper.RequestSessionHelper;
import es.caib.pinbal.logic.intf.dto.EntitatDto;
import es.caib.pinbal.logic.intf.service.EntitatService;
import es.caib.pinbal.logic.intf.service.PropertyService;
import es.caib.pinbal.logic.intf.service.exception.EntitatNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.naming.NamingException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;

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

	@GetMapping
	public String get(
			HttpServletRequest request,
			Model model) throws Exception {
		omplirModelPerMostrarLlistat(request, model);
		return "entitatList";
	}
	
	@PostMapping
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

	@GetMapping("/new")
	public String get(Model model) {
		return get((Long)null, model);
	}
	@GetMapping("/{entitatId}")
	public String get(
			@PathVariable Long entitatId,
			Model model) {
		EntitatDto entitat = null;
		if (entitatId != null) {
			entitat = entitatService.findById(entitatId);
		}
		EntitatCommand command;
		if (entitat != null) {
			command = EntitatCommand.asCommand(entitat);
		} else {
			command = new EntitatCommand();
		}
		model.addAttribute(command);
		return "entitatForm";
	}

	@GetMapping(value = "/datatable", produces="application/json")
	@ResponseBody
	public ServerSideResponse<EntitatDto, Long> datatable(HttpServletRequest request, Model model)
	      throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, NamingException,
	      SQLException {
		ServerSideRequest serverSideRequest = new ServerSideRequest(request);
		
		EntitatFiltreCommand command = (EntitatFiltreCommand)RequestSessionHelper.obtenirObjecteSessio(
											request,
											SESSION_ATTRIBUTE_FILTRE);
		if (command == null)
			command = new EntitatFiltreCommand();
		
		Page<EntitatDto> page = entitatService.findAmbFiltrePaginat(
				command.getCodi(),
				command.getNom(),
				command.getCif(),
				command.getActiva(),
				command.getTipus(),					
				serverSideRequest.toPageable(), 
				command.getUnitatArrel());
//		Page<EntitatDto> page = entitatService.findAll(serverSideRequest.toPageable());
		return new ServerSideResponse<EntitatDto, Long>(serverSideRequest, page);
	}
	
	@PostMapping("/save")
	public String save(
			HttpServletRequest request,
			@Valid EntitatCommand command,
			BindingResult bindingResult) throws EntitatNotFoundException {
		if (bindingResult.hasErrors()) {
			return "entitatForm";
		}
		if (command.getId() != null) {
			entitatService.update(EntitatCommand.asDto(command));
			return getModalControllerReturnValueSuccess(
					request,
					"redirect:../entitat",
					"entitat.controller.entitat.modificada.ok");
		} else {
			entitatService.create(EntitatCommand.asDto(command));
			return getModalControllerReturnValueSuccess(
					request,
					"redirect:../entitat",
					"entitat.controller.entitat.creada.ok");
		}
	}

	@GetMapping("/{entitatId}/enable")
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
	@GetMapping("/{entitatId}/disable")
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

	@GetMapping("/{entitatId}/delete")
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
		if (command == null) {
			command = new EntitatFiltreCommand();
			command.setActiva(true);
		}
		command.eliminarEspaisCampsCerca();
		model.addAttribute(command);
		model.addAttribute(
				"propertyEsborrar",
				propertyService.get("es.caib.pinbal.entitat.accio.esborrar.activa", "false"));
	}

}
