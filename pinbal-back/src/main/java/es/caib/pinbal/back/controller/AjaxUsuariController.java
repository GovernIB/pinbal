package es.caib.pinbal.back.controller;

import es.caib.pinbal.logic.intf.dto.UsuariDto;
import es.caib.pinbal.logic.intf.service.UsuariService;
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

import static org.apache.commons.lang3.StringUtils.isBlank;

/**
 * Controlador per a les consultes ajax dels serveis.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/usuariajax")
public class AjaxUsuariController extends BaseController{

	@Autowired
	private UsuariService usuariService;

	@GetMapping("/usuari")
	@ResponseBody
	public List<UsuariDto> get(HttpServletRequest request, Model model) throws EntitatNotFoundException, ProcedimentNotFoundException {
		return get(request, null, model);
	}
	
	@GetMapping("/usuari/{text}")
	@ResponseBody
	public List<UsuariDto> get(HttpServletRequest request, @PathVariable String text, Model model) throws EntitatNotFoundException, ProcedimentNotFoundException {

		return getWithParam(request, text, model);
	}
	
	@GetMapping("/usuari/item/{codi}")
	@ResponseBody
	public UsuariDto getItem(HttpServletRequest request, @PathVariable String codi, Model model) throws ServeiNotFoundException {
		return usuariService.getDades(codi);
	}

	@GetMapping("/usuari/externs/{text}")
	@ResponseBody
	public List<UsuariDto> getUsuarisExterns(HttpServletRequest request, @PathVariable String text, Model model) throws Exception {
		try {
			text = URLDecoder.decode(request.getRequestURI().split("/")[5], StandardCharsets.UTF_8.name());
		} catch (UnsupportedEncodingException e) { }

		return usuariService.getUsuarisExterns(text);
	}

	@GetMapping("/usuari/extern/{codi}")
	@ResponseBody
	public UsuariDto getUsuariExtern(HttpServletRequest request, @PathVariable String codi, Model model) throws Exception {
		return usuariService.getUsuariExtern(codi);
	}
	
	private List<UsuariDto> getWithParam(HttpServletRequest request, String text, Model model) {
		
		try {
			text = URLDecoder.decode(request.getRequestURI().split("/")[4], StandardCharsets.UTF_8.name());
		} catch (UnsupportedEncodingException e) { }
		
		List<UsuariDto> usuariList = new ArrayList<UsuariDto>();

		if (!isBlank(text)) {
			usuariList = usuariService.findLikeCodiONomONif(text);
		}
		
		return usuariList;
	}


	@GetMapping("/entitat/{entitatId}/usuari/item/{codi}")
	@ResponseBody
	public UsuariDto getItem(
			HttpServletRequest request,
			@PathVariable Long entitatId,
			@PathVariable String codi)  {
		return usuariService.getUsuariEntitat(entitatId, codi);
	}

	@GetMapping("/entitat/{entitatId}/usuari/{codi}")
	@ResponseBody
	public UsuariDto getUsuariEntitat(
			HttpServletRequest request,
			@PathVariable Long entitatId,
			@PathVariable String codi) throws Exception {
		return usuariService.getUsuariEntitat(entitatId, codi);
	}

	@GetMapping("/entitat/{entitatId}/usuaris/{text}")
	@ResponseBody
	public List<UsuariDto> getUsuarisExterns(
			HttpServletRequest request,
			@PathVariable Long entitatId,
			@PathVariable String text) throws Exception {
		try {
			text = URLDecoder.decode(request.getRequestURI().split("/")[6], StandardCharsets.UTF_8.name());
		} catch (UnsupportedEncodingException e) { }

		return usuariService.getUsuarisEntitat(entitatId, text);
	}
}
