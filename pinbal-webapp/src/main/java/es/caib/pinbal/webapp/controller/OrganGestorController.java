/**
 * 
 */
package es.caib.pinbal.webapp.controller;

import java.lang.reflect.InvocationTargetException;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import es.caib.pinbal.core.dto.OrganGestorEstatEnum;
import es.caib.pinbal.core.dto.PaginacioAmbOrdreDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import es.caib.pinbal.core.dto.EntitatDto;
import es.caib.pinbal.core.dto.EntitatDto.EntitatTipusDto;
import es.caib.pinbal.core.dto.OrganGestorDto;
import es.caib.pinbal.core.service.EntitatService;
import es.caib.pinbal.core.service.OrganGestorService;
import es.caib.pinbal.webapp.command.OrganGestorFiltreCommand;
import es.caib.pinbal.webapp.common.EntitatHelper;
import es.caib.pinbal.webapp.common.RequestSessionHelper;
import es.caib.pinbal.webapp.common.RolHelper;
import es.caib.pinbal.webapp.datatables.ServerSideRequest;
import es.caib.pinbal.webapp.datatables.ServerSideResponse;
import lombok.extern.slf4j.Slf4j;

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

	@RequestMapping(method = RequestMethod.GET)
	public String get(HttpServletRequest request, Model model) throws Exception {
		omplirModelPerMostrarLlistat(request, EntitatHelper.getEntitatActual(request), model);
		return "organGestor";
	}

	@RequestMapping(method = RequestMethod.POST)
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
				command.setEntitatId(entitatService.findTopByTipus(EntitatTipusDto.GOVERN).getId());
			}
			RequestSessionHelper.actualitzarObjecteSessio(
					request,
					SESSION_ATTRIBUTE_FILTRE,
					command);
			return "redirect:organgestor";
		}
	}

	@RequestMapping(value = "/datatable", method = RequestMethod.GET)
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

	@RequestMapping(value = "/sync/dir3", method = RequestMethod.GET)
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
