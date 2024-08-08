/**
 * 
 */
package es.caib.pinbal.webapp.controller;

import es.caib.pinbal.core.dto.EntitatDto;
import es.caib.pinbal.core.dto.EntitatUsuariDto;
import es.caib.pinbal.core.service.EntitatService;
import es.caib.pinbal.core.service.UsuariService;
import es.caib.pinbal.core.service.exception.EntitatNotFoundException;
import es.caib.pinbal.core.service.exception.EntitatUsuariNotFoundException;
import es.caib.pinbal.core.service.exception.NotFoundException;
import es.caib.pinbal.core.service.exception.UsuariExternNotFoundException;
import es.caib.pinbal.webapp.command.EntitatUsuariCommand;
import es.caib.pinbal.webapp.command.UsuariFiltreCommand;
import es.caib.pinbal.webapp.common.AlertHelper;
import es.caib.pinbal.webapp.common.RequestSessionHelper;
import es.caib.pinbal.webapp.datatables.ServerSideRequest;
import es.caib.pinbal.webapp.datatables.ServerSideResponse;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.naming.NamingException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;

/**
 * Controlador per al manteniment dels usuaris d'una entitat.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/entitat")
public class EntitatUsuariController extends BaseController {

	private static final String SESSION_ATTRIBUTE_FILTRE = "EntitatUsuariController.session.filtre";

	@Autowired
	private EntitatService entitatService;
	@Autowired
	private UsuariService usuariService;
	@Autowired(required = true)
	private javax.validation.Validator validator;



	@RequestMapping(value = "/{entitatId}/usuari", method = RequestMethod.GET)
	public String usuariGet(
			HttpServletRequest request,
			@PathVariable Long entitatId,
			Model model) throws Exception {
		EntitatDto entitat = null;
		if (entitatId != null)
			entitat = entitatService.findById(entitatId);
		if (entitat != null) {
			omplirModelPerMostrarLlistat(request, entitat, model);
			return "entitatUsuaris";
		} else {
			AlertHelper.error(
					request, 
					getMessage(
							request, 
							"entitat.controller.entitat.no.existeix"));
			return "redirect:../../entitat";
		}
	}
	@RequestMapping(value = "/{entitatId}/usuari", method = RequestMethod.POST)
	public String usuariPost(
			HttpServletRequest request,
			@PathVariable Long entitatId,
			@Valid UsuariFiltreCommand command,
			BindingResult bindingResult,
			Model model) throws Exception {
		EntitatDto entitat = null;
		if (entitatId != null)
			entitat = entitatService.findById(entitatId);
		if (entitat != null) {
			if (bindingResult.hasErrors()) {
				omplirModelPerMostrarLlistat(request, entitat, model);
				return "entitatList";
			} else {
				RequestSessionHelper.actualitzarObjecteSessio(
						request,
						SESSION_ATTRIBUTE_FILTRE,
						command);
				return "redirect:usuari";
			}
		} else {
			AlertHelper.error(
					request, 
					getMessage(
							request, 
							"entitat.controller.entitat.no.existeix"));
			return "redirect:../../entitat";
		}
	}

	@RequestMapping(value = "/{entitatId}/usuari/datatable", produces="application/json", method = RequestMethod.GET)
	@ResponseBody
	public ServerSideResponse<EntitatUsuariDto, Long> datatable(HttpServletRequest request, 
																@PathVariable Long entitatId,
																Model model)
	      throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, NamingException,
	      SQLException, EntitatNotFoundException {
		ServerSideRequest serverSideRequest = new ServerSideRequest(request);
		
		EntitatDto entitat = null;
		entitat = entitatService.findById(entitatId);
		if (entitat == null) {
			throw new EntitatNotFoundException();
		}
		
		UsuariFiltreCommand command = (UsuariFiltreCommand)RequestSessionHelper.obtenirObjecteSessio(
				request,
				SESSION_ATTRIBUTE_FILTRE);
		if (command == null)
			command = new UsuariFiltreCommand();
		
		
		Page<EntitatUsuariDto> page = usuariService.findAmbFiltrePaginat(
				entitat.getId(),
				command.getIsRepresentant(),
				command.getIsDelegat(),
				command.getIsAuditor(),
				command.getIsAplicacio(),
				command.getCodi(),
				command.getNom(),
				command.getNif(),
				command.getDepartament(),		
				serverSideRequest.toPageable());

		return new ServerSideResponse<EntitatUsuariDto, Long>(serverSideRequest, page);
	}


	@RequestMapping(value = "/{entitatId}/usuari/new", method = RequestMethod.GET)
	public String usuariNew(
			HttpServletRequest request,
			@PathVariable Long entitatId,
			Model model) {
		return usuariGet(request, entitatId, null, model);
	}

	@RequestMapping(value = "/{entitatId}/usuari/{codi}", method = RequestMethod.GET)
	public String usuariGet(
			HttpServletRequest request,
			@PathVariable Long entitatId,
			@PathVariable String codi,
			Model model) {
		EntitatDto entitat = entitatService.findById(entitatId);
		if (entitat == null) {
			AlertHelper.error(request, getMessage(request, "representant.controller.entitat.no.existeix"));
			return "redirect:usuari";
		}

		EntitatUsuariCommand entitatUsuariCommand;
		if (StringUtils.isBlank(codi)) {
			entitatUsuariCommand = new EntitatUsuariCommand(entitatId);
		} else {
			EntitatUsuariDto entitatUsuari = usuariService.getEntitatUsuari(entitatId, codi);
			entitatUsuariCommand = EntitatUsuariCommand.asCommand(entitatUsuari, entitatId);
		}
		model.addAttribute(entitatUsuariCommand);
		return "entitatUsuariForm";
	}

	@RequestMapping(value = "/{entitatId}/usuari/save", method = RequestMethod.POST)
	public String usuariPost(
			HttpServletRequest request,
			@PathVariable Long entitatId,
			@Valid EntitatUsuariCommand command,
			BindingResult bindingResult,
			Model model) throws Exception {

		if (bindingResult.hasErrors()) {
			for (FieldError error: bindingResult.getFieldErrors()) {
				AlertHelper.error(request, getMessage(request, error.getCode(), error.getArguments()));
				break;
			}
			return "entitatUsuariForm";
		}

		EntitatDto entitat = entitatService.findById(entitatId);
		if (entitat == null) {
			return getModalControllerReturnValueError(request, "redirect:../usuari", "representant.controller.entitat.no.existeix");
		}

		try {
			usuariService.actualitzarDadesAdmin(
					command.getId(),
					command.getCodi(),
					command.getNif(),
					command.getDepartament(),
					command.isRolRepresentant(),
					command.isRolDelegat(),
					command.isRolAuditor(),
					command.isRolAplicacio(),
					command.isAfegir(),
					command.isActiu());

			return getModalControllerReturnValueSuccess(request, "redirect:../usuari", "representant.controller.usuari.actualitzat", new Object[] {command.getNom()});
		} catch (UsuariExternNotFoundException ex) {
			return getModalControllerReturnValueError(request, "redirect:../usuari", "representant.controller.usuari.extern.no.existeix");
		}

	}

	@RequestMapping(value = "/{entitatId}/usuari/{usuariCodi}/principal", method = RequestMethod.POST)
	@ResponseBody
	public String usuariPrincipal(
			HttpServletRequest request,
			@PathVariable Long entitatId,
			@PathVariable String usuariCodi,
			Model model) throws EntitatNotFoundException, EntitatUsuariNotFoundException {
		EntitatDto entitat = null;
		if (entitatId != null)
			entitat = entitatService.findById(entitatId);
		if (entitat != null) {
			boolean protegit = usuariService.establirPrincipal(
					entitatId,
					usuariCodi);
			if (protegit)
				AlertHelper.success(
						request,
						getMessage(
								request, 
								"entitat.controller.usuari.principal.marcat", new Object[] {usuariCodi}));
			else
				AlertHelper.success(
						request,
						getMessage(
								request, 
								"entitat.controller.usuari.principal.desmarcat", new Object[] {usuariCodi}));
			return "OK";
		} else {
			AlertHelper.error(
					request, 
					getMessage(
							request, 
							"entitat.controller.entitat.no.existeix"));
			throw new NotFoundException(entitatId, EntitatDto.class);
		}
	}

	@RequestMapping(value = "/{entitatId}/usuari/{usuariCodi}/activar", method = RequestMethod.POST)
	@ResponseBody
	public String usuariActivar(
			HttpServletRequest request,
			@PathVariable Long entitatId,
			@PathVariable String usuariCodi,
			Model model) throws EntitatNotFoundException, EntitatUsuariNotFoundException {
		EntitatDto entitat = null;
		if (entitatId != null)
			entitat = entitatService.findById(entitatId);
		if (entitat != null) {
			boolean actiu = usuariService.canviActiu(
					entitatId,
					usuariCodi);
			if (actiu)
				AlertHelper.success(
						request,
						getMessage(
								request,
								"entitat.controller.usuari.activat", new Object[] {usuariCodi}));
			else
				AlertHelper.success(
						request,
						getMessage(
								request,
								"entitat.controller.usuari.desactivat", new Object[] {usuariCodi}));
			return "OK";
		} else {
			AlertHelper.error(
					request,
					getMessage(
							request,
							"entitat.controller.entitat.no.existeix"));
			throw new NotFoundException(entitatId, EntitatDto.class);
		}
	}



	private void omplirModelPerMostrarLlistat(
			HttpServletRequest request,
			EntitatDto entitat,
			Model model) throws Exception {
		UsuariFiltreCommand command = (UsuariFiltreCommand)RequestSessionHelper.obtenirObjecteSessio(
				request,
				SESSION_ATTRIBUTE_FILTRE);
		if (command == null)
			command = new UsuariFiltreCommand();
		
		command.setEntitat(entitat);
		model.addAttribute(command);
		
		model.addAttribute("entitat", entitat);
		model.addAttribute(new EntitatUsuariCommand(entitat.getId()));
	}
	
}
