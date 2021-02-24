/**
 * 
 */
package es.caib.pinbal.webapp.controller;

import java.lang.reflect.InvocationTargetException;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import es.caib.pinbal.core.dto.EntitatDto;
import es.caib.pinbal.core.dto.OrganGestorDto;
import es.caib.pinbal.core.service.OrganGestorService;
import es.caib.pinbal.webapp.command.OrganGestorFiltreCommand;
import es.caib.pinbal.webapp.common.EntitatHelper;
import es.caib.pinbal.webapp.common.RequestSessionHelper;
import es.caib.pinbal.webapp.datatables.ServerSideRequest;
import es.caib.pinbal.webapp.datatables.ServerSideResponse;

/**
 * Controlador per al manteniment d'entitats.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/organgestor")
public class OrganGestorController extends BaseController {
	
	private static final String SESSION_ATTRIBUTE_FILTRE = "OrganGestorController.session.filtre";

	@Autowired
	private OrganGestorService organGestorService;

	@RequestMapping(method = RequestMethod.GET)
	public String get(HttpServletRequest request, Model model) throws Exception {
		omplirModelPerMostrarLlistat(request, model);
		return "organGestor";
	}

	@RequestMapping(method = RequestMethod.POST)
	public String post(
			HttpServletRequest request,
			@Valid OrganGestorFiltreCommand command,
			BindingResult bindingResult,
			Model model) throws Exception {
		if (bindingResult.hasErrors()) {
			omplirModelPerMostrarLlistat(request, model);
			return "organGestor";
		} else {
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
		
		OrganGestorFiltreCommand command = (OrganGestorFiltreCommand)RequestSessionHelper.obtenirObjecteSessio(
				request,
				SESSION_ATTRIBUTE_FILTRE);
		if (command == null)
			command = new OrganGestorFiltreCommand();

		Page<OrganGestorDto> page = organGestorService.findPageOrgansGestorsAmbFiltrePaginat(
				entitat.getId(),
				command.getCodi(), 
				command.getNom(), 
				serverSideRequest.toPageable());
		return new ServerSideResponse<OrganGestorDto, Long>(serverSideRequest, page);
	}

	@RequestMapping(value = "/sync/dir3", method = RequestMethod.GET)
	public String syncDir3(HttpServletRequest request) throws Exception {

		EntitatDto entitat = EntitatHelper.getEntitatActual(request);

		if (entitat.getUnitatArrel() == null || entitat.getUnitatArrel().isEmpty()) {
			return getAjaxControllerReturnValueError(
					request,
					"redirect:../../organgestor",
					"L'entitat actual no té cap codi DIR3 associat");
		}
		try {
			organGestorService.syncDir3OrgansGestors(entitat.getId());

		} catch (Exception e) {
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
			Model model) throws Exception {
		OrganGestorFiltreCommand command = (OrganGestorFiltreCommand)RequestSessionHelper.obtenirObjecteSessio(
				request,
				SESSION_ATTRIBUTE_FILTRE);
		if (command == null) {
			command = new OrganGestorFiltreCommand();
		}
		model.addAttribute(command);

	}
	
	
	
}
