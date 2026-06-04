package es.caib.pinbal.back.controller;

import es.caib.pinbal.back.helper.EntitatHelper;
import es.caib.pinbal.back.helper.RequestSessionHelper;
import es.caib.pinbal.logic.intf.dto.ServeiDto;
import es.caib.pinbal.logic.intf.service.ServeiService;
import es.caib.pinbal.logic.intf.service.exception.EntitatNotFoundException;
import es.caib.pinbal.logic.intf.service.exception.ProcedimentNotFoundException;
import es.caib.pinbal.logic.intf.service.exception.ServeiNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
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

	@GetMapping("/servei")
	@ResponseBody
	public List<ServeiDto> get(HttpServletRequest request, Model model) throws EntitatNotFoundException, ProcedimentNotFoundException {
		return get(request, null, null, model);
	}
	
	@GetMapping("/servei/{text}")
	@ResponseBody
	public List<ServeiDto> get(HttpServletRequest request, @PathVariable String text, Model model) throws EntitatNotFoundException, ProcedimentNotFoundException {

		return getWithParam(request, text, null, model, false);
	}
	
	@GetMapping("/servei/item/{codi}")
	@ResponseBody
	public ServeiDto getItem(HttpServletRequest request, @PathVariable String codi, Model model) throws ServeiNotFoundException {
		return serveiService.findAmbCodiPerAdminORepresentant(codi);
	}
	
	@GetMapping("/servei/{text}/{procediment}")
	@ResponseBody
	public List<ServeiDto> get(HttpServletRequest request, @PathVariable String text, @PathVariable Long procediment, Model model) throws EntitatNotFoundException, ProcedimentNotFoundException {

		return getWithParam(request, text, procediment, model, false);
	}

	@GetMapping("/procediment/{procedimentId}/servei")
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
