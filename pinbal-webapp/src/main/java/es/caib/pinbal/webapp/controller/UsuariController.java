package es.caib.pinbal.webapp.controller;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import es.caib.pinbal.core.dto.IdiomaEnumDto;
import es.caib.pinbal.core.dto.UsuariDto;
import es.caib.pinbal.core.service.UsuariService;
import es.caib.pinbal.webapp.command.UsuariCommand;
import es.caib.pinbal.webapp.helper.EnumHelper;
/**
 * Controlador per al manteniment de usuaris.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/usuari")
public class UsuariController extends BaseController{

	@Autowired
	private UsuariService usuariService;

	@RequestMapping(value = "/logout", method = RequestMethod.GET)
	public String logout(HttpServletRequest request, HttpServletResponse response) {
		// Només per Jboss
		// Es itera sobre totes les cookies
		for (Cookie c: request.getCookies()) {
			// Es sobre escriu el valor de cada cookie a NULL
			Cookie ck = new Cookie(c.getName(), null);
			ck.setPath(request.getContextPath());
			response.addCookie(ck);
		}
		return "redirect:/";
	}

	@RequestMapping(value = "/configuracio", method = RequestMethod.GET)
	public String getConfiguracio(
			HttpServletRequest request,
			Model model) {
		UsuariDto usuari = usuariService.getUsuariActual();
		UsuariCommand command = UsuariCommand.asCommand(usuari);
		model.addAttribute(command);
		model.addAttribute(
				"idiomaEnumOptions",
				EnumHelper.getOptionsForEnum(
						IdiomaEnumDto.class,
						"usuari.form.camp.idioma.enum."));
		return "usuariForm";
	}

	@RequestMapping(value = "/configuracio", method = RequestMethod.POST)
	public String save(
			HttpServletRequest request,
			HttpServletResponse response,
			@Valid UsuariCommand command,
			BindingResult bindingResult,
			Model model) {
		if (bindingResult.hasErrors()) {
			return "usuariForm";
		}
		usuariService.updateUsuariActual(UsuariCommand.asDto(command));
		return getModalControllerReturnValueSuccess(
				request,
				"redirect:/",
				"usuari.controller.modificat.ok");
	}

}
