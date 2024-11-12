package es.caib.pinbal.webapp.controller;

import es.caib.pinbal.core.dto.EntitatDto;
import es.caib.pinbal.core.dto.IdiomaEnumDto;
import es.caib.pinbal.core.dto.NumElementsPaginaEnum;
import es.caib.pinbal.core.dto.UsuariDto;
import es.caib.pinbal.core.service.EntitatService;
import es.caib.pinbal.core.service.ProcedimentService;
import es.caib.pinbal.core.service.ServeiService;
import es.caib.pinbal.core.service.UsuariService;
import es.caib.pinbal.core.service.exception.EntitatNotFoundException;
import es.caib.pinbal.webapp.command.UsuariCommand;
import es.caib.pinbal.webapp.common.EntitatHelper;
import es.caib.pinbal.webapp.common.RolHelper;
import es.caib.pinbal.webapp.helper.EnumHelper;
import es.caib.pinbal.webapp.helper.EnumHelper.HtmlOption;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

/**
 * Controlador per al manteniment de usuaris.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Slf4j
@Controller
@RequestMapping("/usuari")
public class UsuariController extends BaseController{

	@Autowired
	private UsuariService usuariService;
	@Autowired
	private EntitatService entitatService;
	@Autowired
	private ProcedimentService procedimentService;
	@Autowired
	private ServeiService serveiService;

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
		emplenaModel(request, model, usuari.getEntitatId());
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
			emplenaModel(request, model, command.getEntitatId());
			return "usuariForm";
		}
		boolean updateEntitat = false;
		UsuariDto usuari = usuariService.getUsuariActual();
		if (RolHelper.isRolActualAdministrador(request) || RolHelper.isRolActualSuperauditor(request) || usuari.isHasMultiplesEntitats()) {
			updateEntitat = true;
		} else {
			updateEntitat = !usuari.getProcedimentId().equals(command.getProcedimentId()) || !usuari.getServeiCodi().equals(command.getServeiCodi());
			if (updateEntitat) {
				EntitatDto entitatActual = EntitatHelper.getEntitatActual(request, entitatService);
				command.setEntitatId(entitatActual != null ? entitatActual.getId() : null);
			}
		}
		usuariService.updateUsuariActual(UsuariCommand.asDto(command), updateEntitat);
		return getModalControllerReturnValueSuccess(
				request,
				"redirect:/",
				"usuari.controller.modificat.ok");
	}

	@RequestMapping(value = "/num/elements/pagina/defecte", method = RequestMethod.GET)
	@ResponseBody
	public Integer getNumElementsPaginaDefecte(HttpServletRequest request, Model model) {
		return usuariService.getNumElementsPaginaDefecte();
	}

	private void emplenaModel(HttpServletRequest request, Model model, Long entitatId) {
		model.addAttribute("idiomaEnumOptions", EnumHelper.getOptionsForEnum(IdiomaEnumDto.class, "usuari.form.camp.idioma.enum."));
		model.addAttribute("numElementsPaginaEnumOptions", getOptionsForNumElementsPaginaEnum());
		EntitatDto entitatActual = EntitatHelper.getEntitatActual(request, entitatService);
		try {
			if (RolHelper.isRolActualAdministrador(request) || RolHelper.isRolActualSuperauditor(request)) {
				model.addAttribute("entitats", entitatService.findActives());
				model.addAttribute("procediments", procedimentService.findAmbEntitat(entitatId != null ? entitatId : entitatActual.getId()));
				model.addAttribute("serveis", serveiService.findAmbEntitat(entitatId != null ? entitatId : entitatActual.getId()));
			} else {
				model.addAttribute("entitats", EntitatHelper.getEntitats(request, entitatService, true));
				model.addAttribute("procediments", procedimentService.findAmbEntitat(entitatActual.getId()));
				model.addAttribute("serveis", serveiService.findAmbEntitat(entitatActual.getId()));
			}
		} catch (EntitatNotFoundException e) {
			log.error("Error recuperant procediments i serveis", e);
//			throw new RuntimeException(e);
		}
	}

	public static List<HtmlOption> getOptionsForNumElementsPaginaEnum() {
		List<HtmlOption> resposta = new ArrayList<>();
		for (NumElementsPaginaEnum e: NumElementsPaginaEnum.values()) {
			resposta.add(new HtmlOption(e.name(), String.valueOf(e.getElements())));
		}
		return resposta;
	}
}
