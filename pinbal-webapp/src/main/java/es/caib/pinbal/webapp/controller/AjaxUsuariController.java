package es.caib.pinbal.webapp.controller;

import es.caib.pinbal.core.dto.ServeiDto;
import es.caib.pinbal.core.dto.UsuariDto;
import es.caib.pinbal.core.service.ServeiService;
import es.caib.pinbal.core.service.UsuariService;
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

import static org.apache.commons.lang.StringUtils.isBlank;

/**
 * Controlador per a les consultes ajax dels serveis.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/usuariajax")
public class AjaxUsuariController extends BaseController{
	
	private static final String SESSION_ATTRIBUTE_ENTITAT_ID = "EstadistiquesController.session.entitat.id";

	@Autowired
	private UsuariService usuariService;

	@RequestMapping(value = "/usuari", method = RequestMethod.GET)
	@ResponseBody
	public List<UsuariDto> get(HttpServletRequest request, Model model) throws EntitatNotFoundException, ProcedimentNotFoundException {
		return get(request, null, model);
	}
	
	@RequestMapping(value = "/usuari/{text}", method = RequestMethod.GET)
	@ResponseBody
	public List<UsuariDto> get(HttpServletRequest request, @PathVariable String text, Model model) throws EntitatNotFoundException, ProcedimentNotFoundException {

		return getWithParam(request, text, model);
	}
	
	@RequestMapping(value = "/usuari/item/{codi}", method = RequestMethod.GET)
	@ResponseBody
	public UsuariDto getItem(HttpServletRequest request, @PathVariable String codi, Model model) throws ServeiNotFoundException {
		return usuariService.getDades(codi);
	}
	
	private List<UsuariDto> getWithParam(HttpServletRequest request, String text, Model model) {
		
		try {
			text = URLDecoder.decode(request.getRequestURI().split("/")[4], StandardCharsets.UTF_8.name());
		} catch (UnsupportedEncodingException e) { }
		
		List<UsuariDto> usuariList = new ArrayList<UsuariDto>();

		if (!isBlank(text)) {
			usuariList = usuariService.findLikeCodiONom(text);
		}
		
		return usuariList;
	}

}
