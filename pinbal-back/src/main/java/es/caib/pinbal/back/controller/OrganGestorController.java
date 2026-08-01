/**
 * 
 */
package es.caib.pinbal.back.controller;

import es.caib.pinbal.back.command.OrganGestorFiltreCommand;
import es.caib.pinbal.back.datatables.ServerSideRequest;
import es.caib.pinbal.back.datatables.ServerSideResponse;
import es.caib.pinbal.back.helper.EntitatHelper;
import es.caib.pinbal.back.helper.RequestSessionHelper;
import es.caib.pinbal.back.helper.RolHelper;
import es.caib.pinbal.logic.intf.dto.EntitatDto;
import es.caib.pinbal.logic.intf.dto.EntitatDto.EntitatTipusDto;
import es.caib.pinbal.logic.intf.dto.OrganGestorDto;
import es.caib.pinbal.logic.intf.dto.OrganGestorEstatEnum;
import es.caib.pinbal.logic.intf.service.EntitatService;
import es.caib.pinbal.logic.intf.service.OrganGestorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.lang.reflect.InvocationTargetException;

/**
 * Controlador per al manteniment d'entitats.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Slf4j
@Controller
@RequestMapping("/organgestor")
public class OrganGestorController extends BaseController {

	private static final String SESSION_ATTRIBUTE_FILTRE = "OrganGestorController.session.filtre";

	@Autowired
	private OrganGestorService organGestorService;
	@Autowired
	private EntitatService entitatService;

	@GetMapping
	public String get(HttpServletRequest request, Model model) throws Exception {
		omplirModelPerMostrarLlistat(request, EntitatHelper.getEntitatActual(request), model);
		return "organGestor";
	}

	@PostMapping
	public String post(
			HttpServletRequest request,
			@Valid OrganGestorFiltreCommand command,
			BindingResult bindingResult,
			Model model) throws Exception {
		if (bindingResult.hasErrors()) {
			omplirModelPerMostrarLlistat(request, EntitatHelper.getEntitatActual(request), model);
			return "organGestor";
		} else {
			if (command.getEntitatId() == null) {
				if (RolHelper.isRolActualAdministrador(request)) {
					command.setEntitatId(entitatService.findTopByTipus(EntitatTipusDto.GOVERN).getId());
				} else {
					EntitatDto entitatActual = EntitatHelper.getEntitatActual(request);
					command.setEntitatId(entitatActual != null ? entitatActual.getId() : null);
				}
			}
			RequestSessionHelper.actualitzarObjecteSessio(
					request,
					SESSION_ATTRIBUTE_FILTRE,
					command);
			return "redirect:organgestor";
		}
	}

	@GetMapping("/datatable")
	@ResponseBody
	public ServerSideResponse<OrganGestorDto, Long> datatable(
			HttpServletRequest request) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		EntitatDto entitat = EntitatHelper.getEntitatActual(request);
		ServerSideRequest serverSideRequest = new ServerSideRequest(request);
		OrganGestorFiltreCommand command = getCommandInstance(request);
		boolean isAdmin = RolHelper.isRolActualAdministrador(request);
		Long entitatId = (isAdmin) ? command.getEntitatId() : entitat.getId();
		Page<OrganGestorDto> page = organGestorService.findPageOrgansGestorsAmbFiltrePaginat(
				entitatId,
				command.getCodi(), 
				command.getNom(),
				command.getPareCodi(),
				command.getEstat(),
				ServerSideRequest.getPaginacioDtoFromRequest(request));
		return new ServerSideResponse<OrganGestorDto, Long>(serverSideRequest, page);
	}

	@GetMapping("/sync/dir3")
	public String syncDir3(HttpServletRequest request) throws Exception {
		OrganGestorFiltreCommand command = getCommandInstance(request);
		boolean isAdmin = RolHelper.isRolActualAdministrador(request);
		EntitatDto entitat = isAdmin ?
			entitatService.findById(command.getEntitatId()) :
			EntitatHelper.getEntitatActual(request);

		if (entitat.getUnitatArrel() == null || entitat.getUnitatArrel().isEmpty()) {
			return getAjaxControllerReturnValueError(
					request,
					"redirect:../../organgestor",
					"organgestor.controller.sync.dir3.asociat.error");
		}
		try {
			organGestorService.syncDir3OrgansGestors(entitat.getId());
		} catch (Exception e) {
			log.error("Error actualitzant els òrgans gestors.", e);
			return getAjaxControllerReturnValueError(
					request,
					"redirect:../../organgestor",
					e.getMessage());
		}
		return getAjaxControllerReturnValueSuccess(
				request,
				"redirect:../../organgestor",
				"organgestor.controller.update.nom.tots.ok");
	}

	private void omplirModelPerMostrarLlistat(
			HttpServletRequest request,
			EntitatDto entitat,
			Model model) throws Exception {
		OrganGestorFiltreCommand filtre = getCommandInstance(request);
		Long entitatId = filtre.getEntitatId();
		if (entitatId == null && entitat != null) {
			entitatId = entitat.getId();
		}
		model.addAttribute(filtre);
		model.addAttribute("organsEntitat", organGestorService.findByEntitat(entitatId));
		if (RolHelper.isRolActualAdministrador(request))
			model.addAttribute("entitats", entitatService.findAll());
	}

	private OrganGestorFiltreCommand getCommandInstance(HttpServletRequest request) {
		OrganGestorFiltreCommand command = (OrganGestorFiltreCommand)RequestSessionHelper.obtenirObjecteSessio(
				request,
				SESSION_ATTRIBUTE_FILTRE);
		if (command == null) {
			command = RolHelper.isRolActualAdministrador(request) ?
					new OrganGestorFiltreCommand(entitatService.findTopByTipus(EntitatTipusDto.GOVERN).getId()) :
					new OrganGestorFiltreCommand(null);
			command.setEstat(OrganGestorEstatEnum.V);
		} else {
			command.eliminarEspaisCampsCerca();
		}
		return command;
	}

}
