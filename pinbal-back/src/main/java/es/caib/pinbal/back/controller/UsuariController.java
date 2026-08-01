package es.caib.pinbal.back.controller;

import es.caib.pinbal.back.command.UsuariCodiCommand;
import es.caib.pinbal.back.command.UsuariCommand;
import es.caib.pinbal.back.config.WebSecurityConfig;
import es.caib.pinbal.back.helper.AlertHelper;
import es.caib.pinbal.back.helper.EntitatHelper;
import es.caib.pinbal.back.helper.EnumHelper;
import es.caib.pinbal.back.helper.EnumHelper.HtmlOption;
import es.caib.pinbal.back.helper.RolHelper;
import es.caib.pinbal.back.helper.UsuariHelper;
import es.caib.pinbal.logic.intf.dto.EntitatDto;
import es.caib.pinbal.logic.intf.dto.IdiomaEnumDto;
import es.caib.pinbal.logic.intf.dto.NumElementsPaginaEnum;
import es.caib.pinbal.logic.intf.dto.UsuariDto;
import es.caib.pinbal.logic.intf.service.EntitatService;
import es.caib.pinbal.logic.intf.service.ProcedimentService;
import es.caib.pinbal.logic.intf.service.ServeiService;
import es.caib.pinbal.logic.intf.service.UsuariService;
import es.caib.pinbal.logic.intf.service.exception.EntitatNotFoundException;
import es.caib.pinbal.logic.intf.service.exception.ProcedimentNotFoundException;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.KeycloakSecurityContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static es.caib.pinbal.back.controller.UsuariController.ResultatEstatEnum.ERROR;
import static es.caib.pinbal.back.controller.UsuariController.ResultatEstatEnum.OK;

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

	@Value("${jboss.home.dir:#{null}}")
	private String jbossHomeDir;
	@Value("${es.caib.pinbal.auth.url:#{null}}")
	private String authUrl;
	@Value("${es.caib.pinbal.auth.realm:#{null}}")
	private String authRealm;
	@Value("${es.caib.pinbal.auth.clientid:#{null}}")
	private String authClientId;

	@GetMapping("/logout")
	public String logout(HttpServletRequest request) {
		if (jbossHomeDir == null) {
			// Spring Boot: delega en el logout de Spring Security (WebSecurityConfig.LOGOUT_URL), que ja fa
			// el flux OIDC complet (end_session_endpoint de Keycloak) i neteja les cookies corresponents.
			return "redirect:" + WebSecurityConfig.LOGOUT_URL;
		}
		// JBoss: request.logout() tanca la sessió local a l'adaptador servlet de Keycloak, però NO tanca la
		// sessió SSO al propi Keycloak. Sense el redirect explícit de sota a l'"end session endpoint", la
		// següent visita hi torna a entrar silenciosament amb l'usuari anterior (sessió SSO encara viva) en
		// lloc de mostrar el formulari de login, i a més l'"state" OAuth de la petició anterior ja no és
		// vàlid per aquest nou intent, provocant un "Bad Request" a Keycloak.
		// Cal obtenir l'id_token ABANS de request.logout() (que invalida el KeycloakSecurityContext), perquè
		// Keycloak >= 18 exigeix "id_token_hint" per fer el logout sense demanar confirmació a l'usuari.
		String idTokenHint = null;
		Object keycloakSecurityContext = request.getAttribute(KeycloakSecurityContext.class.getName());
		if (keycloakSecurityContext instanceof KeycloakSecurityContext) {
			idTokenHint = ((KeycloakSecurityContext) keycloakSecurityContext).getIdTokenString();
		}
		try {
			request.logout();
		} catch (ServletException e) {
			log.warn("Error fent logout", e);
		}
		// NO facis un bucle genèric que reenviï totes les cookies de la petició amb valor buit:
		// request.getCookies() no exposa el path/domain amb què cada cookie es va crear
		// originalment (només ho sap el navegador), així que reenviar-les totes amb
		// path=contextPath ("/pinbalback") no esborra les que l'adaptador de Keycloak crea a
		// path "/" (p.ex. OAuth_Token_Request_State) ni les del domini de Keycloak -- en lloc
		// d'esborrar-les, crea una SEGONA cookie fantasma amb el mateix nom i valor buit a
		// "/pinbalback". El navegador envia totes dues al següent login; l'adaptador llegeix la
		// fantasma (buida) en lloc de la real que ell mateix acaba de crear a "/", i rebutja el
		// callback amb "state parameter invalid" -> "Bad Request" (vegeu e2e/BUGS_APLICACIO.md).
		// Només cal invalidar la sessió HTTP local (per forçar un JSESSIONID nou al pròxim
		// login); el tancament de la sessió SSO de Keycloak ja el fa el redirect de sota.
		HttpSession session = request.getSession(false);
		if (session != null) {
			try {
				session.invalidate();
			} catch (IllegalStateException e) {
				// Ja invalidada (p.ex. per request.logout()); res a fer.
			}
		}
		if (authUrl != null && authRealm != null) {
			String baseUrl = request.getScheme() + "://" + request.getServerName() +
					((request.getServerPort() == 80 || request.getServerPort() == 443) ? "" : ":" + request.getServerPort()) +
					request.getContextPath() + "/";
			String authUrlSensePrefix = authUrl.endsWith("/") ? authUrl.substring(0, authUrl.length() - 1) : authUrl;
			String logoutUrl = authUrlSensePrefix + "/realms/" + authRealm + "/protocol/openid-connect/logout" +
					"?post_logout_redirect_uri=" + URLEncoder.encode(baseUrl, StandardCharsets.UTF_8) +
					(idTokenHint != null ?
							"&id_token_hint=" + URLEncoder.encode(idTokenHint, StandardCharsets.UTF_8) :
							(authClientId != null ? "&client_id=" + URLEncoder.encode(authClientId, StandardCharsets.UTF_8) : ""));
			return "redirect:" + logoutUrl;
		}
		return "redirect:/";
	}

	@GetMapping("/configuracio")
	public String getConfiguracio(
			HttpServletRequest request,
			Model model) {
		UsuariDto usuari = usuariService.getUsuariActual();
		UsuariCommand command = UsuariCommand.asCommand(usuari);
		model.addAttribute(command);
		emplenaModel(request, model, usuari.getEntitatId());
		return "usuariForm";
	}

	@PostMapping("/configuracio")
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
			boolean procedimentIdChanged = hasChanged(usuari.getProcedimentId(), command.getProcedimentId());
			boolean serveiCodiChanged = hasChanged(usuari.getServeiCodi(), command.getServeiCodi());
			updateEntitat = procedimentIdChanged || serveiCodiChanged;

			if (updateEntitat) {
				EntitatDto entitatActual = EntitatHelper.getEntitatActual(request, entitatService);
				command.setEntitatId(entitatActual != null ? entitatActual.getId() : null);
			}
		}
		usuariService.updateUsuariActual(UsuariCommand.asDto(command), updateEntitat);
		UsuariHelper.resetUsuariActual(request);
		return getModalControllerReturnValueSuccess(
				request,
				"redirect:/",
				"usuari.controller.modificat.ok");
	}

	private boolean hasChanged(Object oldValue, Object newValue) {
		return (oldValue == null && newValue != null)
				|| (oldValue != null && newValue == null)
				|| (oldValue != null && !oldValue.equals(newValue));
	}


	@GetMapping("/num/elements/pagina/defecte")
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
				model.addAttribute("procediments", entitatId != null ? procedimentService.findAmbEntitat(entitatId) : procedimentService.findAll());
				model.addAttribute("serveis", entitatId != null ? serveiService.findAmbEntitat(entitatId) : serveiService.findActius());
			} else if (RolHelper.isRolActualDelegat(request)) {
				model.addAttribute("entitats", EntitatHelper.getEntitats(request, entitatService, true));
				model.addAttribute("procediments", entitatActual != null ? procedimentService.findAmbEntitatPerDelegat(entitatActual.getId()) : new ArrayList<>());
				model.addAttribute("serveis", entitatActual != null ? serveiService.findPermesosAmbProcedimentPerDelegat(entitatActual.getId(), null) : new ArrayList<>());
			} else {
				model.addAttribute("entitats", EntitatHelper.getEntitats(request, entitatService, true));
				model.addAttribute("procediments", entitatActual != null ? procedimentService.findAmbEntitat(entitatActual.getId()) : new ArrayList<>());
				model.addAttribute("serveis", entitatActual != null ? serveiService.findAmbEntitat(entitatActual.getId()) : new ArrayList<>());
			}
		} catch (EntitatNotFoundException | ProcedimentNotFoundException e) {
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


	///////////////////////////////////////

	@GetMapping("/username")
	public String getCanviCodi(
			HttpServletRequest request,
			Model model) {
		UsuariCodiCommand command = new UsuariCodiCommand();
		model.addAttribute(command);
		model.addAttribute("idiomaEnumOptions", EnumHelper.getOptionsForEnum(IdiomaEnumDto.class, "usuari.form.camp.idioma.enum."));
		return "usuariCodiForm";
	}

	@PostMapping("/username")
	public String setCanviCodi(
			HttpServletRequest request,
			HttpServletResponse response,
			@Valid UsuariCodiCommand command,
			BindingResult bindingResult,
			Model model) {

		model.addAttribute("idiomaEnumOptions", EnumHelper.getOptionsForEnum(IdiomaEnumDto.class, "usuari.form.camp.idioma.enum."));

		if (bindingResult.hasErrors()) {
			return "usuariCodiForm";
		}

		try {
			usuariService.updateUsuariCodi(
					command.getCodiAntic(),
					command.getCodiNou(),
					command.getNom(),
					command.getNif(),
					command.getEmail(),
					command.getIdioma());
			AlertHelper.success(
					request,
					getMessage(request, "usuari.controller.codi.modificat.ok", null));
		} catch (Exception e) {
			AlertHelper.error(
					request,
					getMessage(request, "usuari.controller.codi.modificat.error", null) + ". " + e.getMessage());
			log.error("Error modificant el codi de l'usuari", e);
		}
		return "usuariCodiForm";
	}

	@GetMapping("/usernames/change")
	public String getCanviCodis(
			HttpServletRequest request,
			Model model) {
		return "usuarisCanviCodi";
	}

	@PostMapping(value = "/usernames/{codiAntic}/validateTo/{codiNou}", produces = "application/json")
	@ResponseBody
	public UsuariChangeValidation validaCanviCodis(
			HttpServletRequest request,
			@PathVariable("codiAntic") String codiAntic,
			@PathVariable("codiNou") String codiNou) {

		UsuariDto usuariAntic = usuariService.getDades(codiAntic);
		UsuariDto usuariNou = usuariService.getDades(codiNou);
		String codiActual = SecurityContextHolder.getContext().getAuthentication() != null ? SecurityContextHolder.getContext().getAuthentication().getName() : null;

		return UsuariChangeValidation.builder()
				.usuariActual(codiActual != null && codiActual.equals(codiAntic))
				.usuariAnticExists(usuariAntic != null)
				.usuariNouExists(usuariNou != null)
				.build();
	}

	@PostMapping(value = "/usernames/{codiAntic}/changeTo/{codiNou}", produces = "application/json")
	@ResponseBody
	public UsuariChangeResponse setCanviCodis(
			HttpServletRequest request,
			@PathVariable("codiAntic") String codiAntic,
			@PathVariable("codiNou") String codiNou) {

		Long t0 = System.currentTimeMillis();
		try {
			Long registresModificats = usuariService.updateUsuariCodi(codiAntic, codiNou);
			return UsuariChangeResponse.builder()
					.estat(OK)
					.registresModificats(registresModificats)
					.duracio(System.currentTimeMillis() - t0)
					.build();
		} catch (Exception e) {
			log.error("Error modificant el codi de l'usuari", e);
			return UsuariChangeResponse.builder()
					.estat(ERROR)
					.errorMessage(getMessage(request, "usuari.controller.codi.modificat.error", null) + ": " + e.getMessage())
					.duracio(System.currentTimeMillis() - t0)
					.build();
		}
	}

	@Data
	@Builder
	public static class UsuariChangeValidation {
		private boolean usuariActual;
		private boolean usuariAnticExists;
		private boolean usuariNouExists;
	}

	@Data
	@Builder
	public static class UsuariChangeResponse {
		private ResultatEstatEnum estat;
		private String errorMessage;
		private Long registresModificats;
		private Long duracio;
	}

	public enum ResultatEstatEnum { OK, ERROR }

}
