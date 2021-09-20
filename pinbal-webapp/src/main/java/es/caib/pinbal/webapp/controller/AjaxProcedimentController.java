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

import es.caib.pinbal.core.dto.ProcedimentDto;
import es.caib.pinbal.core.service.ProcedimentService;
import es.caib.pinbal.core.service.exception.EntitatNotFoundException;
import es.caib.pinbal.webapp.common.RequestSessionHelper;

/**
 * Controlador per a les consultes ajax dels procediments.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/procedimentajax")
public class AjaxProcedimentController extends BaseController{
	
	private static final String SESSION_ATTRIBUTE_ENTITAT_ID = "EstadistiquesController.session.entitat.id";

	@Autowired
	private ProcedimentService procedimentService;

	@RequestMapping(value = "/procediment", method = RequestMethod.GET)
	@ResponseBody
	public List<ProcedimentDto> get(HttpServletRequest request, Model model) throws EntitatNotFoundException {
		return get(request, null, model);
	}
	
	@RequestMapping(value = "/procediment/{text}", method = RequestMethod.GET)
	@ResponseBody
	public List<ProcedimentDto> get(HttpServletRequest request, @PathVariable String text, Model model) throws EntitatNotFoundException {

		return getWithParam(request, text, model, false);
	}
	
	private List<ProcedimentDto> getWithParam(HttpServletRequest request, String text, Model model, boolean directOrganPermisRequired) throws EntitatNotFoundException {
		
		Long entitatId = (Long)RequestSessionHelper.obtenirObjecteSessio(
				request,
				SESSION_ATTRIBUTE_ENTITAT_ID);
		
		try {
			text = URLDecoder.decode(request.getRequestURI().split("/")[4], StandardCharsets.UTF_8.name());
		} catch (UnsupportedEncodingException e) { }
		
		List<ProcedimentDto> procedimentsList = new ArrayList<ProcedimentDto>();
		
		if (entitatId != null && entitatId != -1) {
					procedimentsList = procedimentService.findAmbEntitat(entitatId, text);
		}
		
		if (text == null) {
			return procedimentsList.subList(0, 5);
		}

		return procedimentsList;
	}
	
	
}
