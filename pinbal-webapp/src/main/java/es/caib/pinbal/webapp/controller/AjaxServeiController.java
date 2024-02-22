package es.caib.pinbal.webapp.controller;

import es.caib.pinbal.core.dto.ServeiDto;
import es.caib.pinbal.core.service.ServeiService;
import es.caib.pinbal.core.service.exception.EntitatNotFoundException;
import es.caib.pinbal.core.service.exception.ProcedimentNotFoundException;
import es.caib.pinbal.core.service.exception.ServeiNotFoundException;
import es.caib.pinbal.webapp.common.EntitatHelper;
import es.caib.pinbal.webapp.common.RequestSessionHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Controlador per a les consultes ajax dels serveis.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/serveiajax")
public class AjaxServeiController extends BaseController{
	
	private static final String SESSION_ATTRIBUTE_ENTITAT_ID = "EstadistiquesController.session.entitat.id";

	@Autowired
	private ServeiService serveiService;

	@RequestMapping(value = "/servei", method = RequestMethod.GET)
	@ResponseBody
	public List<ServeiDto> get(HttpServletRequest request, Model model) throws EntitatNotFoundException, ProcedimentNotFoundException {
		return get(request, null, null, model);
	}
	
	@RequestMapping(value = "/servei/{text}", method = RequestMethod.GET)
	@ResponseBody
	public List<ServeiDto> get(HttpServletRequest request, @PathVariable String text, Model model) throws EntitatNotFoundException, ProcedimentNotFoundException {

		return getWithParam(request, text, null, model, false);
	}
	
	@RequestMapping(value = "/servei/item/{codi}", method = RequestMethod.GET)
	@ResponseBody
	public ServeiDto getItem(HttpServletRequest request, @PathVariable String codi, Model model) throws ServeiNotFoundException {
		return serveiService.findAmbCodiPerAdminORepresentant(codi);
	}
	
	@RequestMapping(value = "/servei/{text}/{procediment}", method = RequestMethod.GET)
	@ResponseBody
	public List<ServeiDto> get(HttpServletRequest request, @PathVariable String text, @PathVariable Long procediment, Model model) throws EntitatNotFoundException, ProcedimentNotFoundException {

		return getWithParam(request, text, procediment, model, false);
	}

	@RequestMapping(value = "/procediment/{procedimentId}/servei", method = RequestMethod.GET)
	@ResponseBody
	public List<ServeiDto> getByProcediment(HttpServletRequest request, @PathVariable Long procedimentId, Model model) throws EntitatNotFoundException, ProcedimentNotFoundException {

		return serveiService.findAmbProcediment(procedimentId);
	}

	private List<ServeiDto> getWithParam(HttpServletRequest request, String text, Long procedimentId, Model model, boolean directOrganPermisRequired) throws EntitatNotFoundException, ProcedimentNotFoundException {
		
		Long entitatId = (Long)RequestSessionHelper.obtenirObjecteSessio(
				request,
				SESSION_ATTRIBUTE_ENTITAT_ID);
		
		if (entitatId == null) //Per al representant no es guarda en sessió l'entitat
			entitatId = EntitatHelper.getEntitatActual(request).getId();
		
		try {
			String[] requestSegments = request.getRequestURI().split("/");
			text = requestSegments.length > 4 ? URLDecoder.decode(requestSegments[4], StandardCharsets.UTF_8.name()) : null;
		} catch (UnsupportedEncodingException e) { }
		
		List<ServeiDto> serveisList = new ArrayList<ServeiDto>();
		
		if (entitatId != null) {
			if (entitatId != -1) {
				if (procedimentId != null)
					serveisList = serveiService.findAmbEntitatIProcediment(
							entitatId,
							procedimentId,
							text);
				else
					serveisList = serveiService.findAmbEntitat(
							entitatId,
							text);
			} else {
				serveisList = serveiService.findActius(text);
			}
		}
		
		if (text == null) {
			return serveisList.subList(0, 5);
		}

		return serveisList;
	}
	
	
}
