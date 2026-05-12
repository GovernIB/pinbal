package es.caib.pinbal.webapp.controller;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import es.caib.pinbal.core.dto.EntitatDto;
import es.caib.pinbal.core.dto.OrganGestorDto;
import es.caib.pinbal.core.service.exception.NotFoundException;
import es.caib.pinbal.core.service.OrganGestorService;
import es.caib.pinbal.webapp.common.EntitatHelper;

/**
 * Controlador per a les consultes ajax dels usuaris normals.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/organgestorajax") // No podem posar "/ajaxuser" per mor del AjaxInterceptor
public class AjaxOrganGestorController extends BaseController{

	@Autowired
	private OrganGestorService organGestorService;

	@RequestMapping(value = "/organgestor", method = RequestMethod.GET)
	@ResponseBody
	public List<OrganGestorDto> get(HttpServletRequest request, Model model) {
		return get(request, null, model);
	}
	
	@RequestMapping(value = "/organgestor/{text}", method = RequestMethod.GET)
	@ResponseBody
	public List<OrganGestorDto> get(HttpServletRequest request, @PathVariable String text, Model model) {

		return getWithParam(request, text, model, false);
	}
		
	@RequestMapping(value = "/organgestor/item/{id}", method = RequestMethod.GET)
	@ResponseBody
	public OrganGestorDto getItem(HttpServletRequest request, @PathVariable Long id, Model model) {		
		try {
			return organGestorService.findItem(id);
		} catch (NotFoundException e) {
			return null;
		} 
	}

	private List<OrganGestorDto> getWithParam(HttpServletRequest request, String text, Model model, boolean directOrganPermisRequired) {
		EntitatDto entitatActual = EntitatHelper.getEntitatActual(request);
		
		try {
			text = URLDecoder.decode(request.getRequestURI().split("/")[4], StandardCharsets.UTF_8.name());
		} catch (UnsupportedEncodingException e) { }
		
		List<OrganGestorDto> organGestorsList = new ArrayList<OrganGestorDto>();
 		
		organGestorsList = organGestorService.findByEntitatAmbFiltre(
				entitatActual.getId(),
				text);
		
		if (text == null) {
			return organGestorsList.subList(0, 5);
		}

		return organGestorsList;
	}
	
	
}
