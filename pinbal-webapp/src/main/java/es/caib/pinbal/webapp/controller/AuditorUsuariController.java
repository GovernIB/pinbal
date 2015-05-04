/**
 * 
 */
package es.caib.pinbal.webapp.controller;

import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import es.caib.pinbal.core.dto.EntitatDto;
import es.caib.pinbal.core.dto.EntitatUsuariDto;
import es.caib.pinbal.core.dto.UsuariDto;
import es.caib.pinbal.core.service.EntitatService;
import es.caib.pinbal.core.service.UsuariService;
import es.caib.pinbal.core.service.exception.EntitatNotFoundException;
import es.caib.pinbal.core.service.exception.EntitatUsuariProtegitException;
import es.caib.pinbal.webapp.command.EntitatUsuariCommand;
import es.caib.pinbal.webapp.command.EntitatUsuariCommand.Existent;
import es.caib.pinbal.webapp.command.EntitatUsuariCommand.TipusCodi;
import es.caib.pinbal.webapp.command.EntitatUsuariCommand.TipusNif;
import es.caib.pinbal.webapp.command.UsuariFiltreCommand;
import es.caib.pinbal.webapp.common.AlertHelper;
import es.caib.pinbal.webapp.common.EntitatHelper;
import es.caib.pinbal.webapp.common.RequestSessionHelper;
import es.caib.pinbal.webapp.common.ValidationHelper;

/**
 * Controlador per a la configuració d'usuaris per a l'auditor.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/auditor")
public class AuditorUsuariController extends BaseController {

	public static final String SESSION_ATTRIBUTE_FILTRE = "AuditorUsuariController.session.filtre";

	@Autowired
	private EntitatService entitatService;
	@Autowired
	private UsuariService usuariService;
	@Autowired(required = true)
	private javax.validation.Validator validator;



	@RequestMapping(value = "/usuari", method = RequestMethod.GET)
	public String get(
			HttpServletRequest request,
			Model model) throws Exception {
		if (!EntitatHelper.isAuditorEntitatActual(request))
			return "auditorNoAutoritzat";
		EntitatDto entitat = EntitatHelper.getEntitatActual(
				request,
				entitatService);
		if (entitat != null) {
			omplirModelPerMostrarLlistat(request, entitat, model);
			return "auditorUsuaris";
		} else {
			AlertHelper.error(
					request,
					getMessage(
							request, 
							"auditor.controller.entitat.no.existeix"));
			return "redirect:../index";
		}
	}
	@RequestMapping(value = "/usuari", method = RequestMethod.POST)
	public String post(
			HttpServletRequest request,
			@Valid UsuariFiltreCommand command,
			BindingResult bindingResult,
			Model model) throws Exception {
		EntitatDto entitat = EntitatHelper.getEntitatActual(request);
		if (entitat != null) {
			if (!EntitatHelper.isAuditorEntitatActual(request))
				return "auditorNoAutoritzat";
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
							"representant.controller.entitat.no.existeix"));
			return "redirect:../index";
		}
	}

	@RequestMapping(value = "/usuari/save", method = RequestMethod.POST)
	public String add(
			HttpServletRequest request,
			@Valid EntitatUsuariCommand command,
			BindingResult bindingResult,
			Model model) throws EntitatNotFoundException, EntitatUsuariProtegitException {
		EntitatDto entitat = EntitatHelper.getEntitatActual(request);
		if (entitat != null) {
			if (!EntitatHelper.isAuditorEntitatActual(request))
				return "auditorNoAutoritzat";
			Class<?> grup = null;
			if (command.isTipusCodi()) {
				grup = TipusCodi.class;
			} else if (command.isTipusNif()) {
				grup = TipusNif.class;
			} else {
				grup = Existent.class;
			}
			new ValidationHelper(validator).isValid(
					command,
					bindingResult,
					grup);
			if (bindingResult.hasErrors()) {
				for (FieldError error: bindingResult.getFieldErrors()) {
					AlertHelper.error(
							request,
							getMessage(
									request, 
									error.getCode(),
									error.getArguments()));
					break;
				}
				model.addAttribute(
						"entitat",
						entitatService.findById(command.getId()));
				return "auditorUsuaris";
			}
			usuariService.actualitzarDadesAuditor(
					command.getId(),
					command.getCodi(),
					command.getNif(),
					command.isRolAuditor(),
					command.isAfegir());
			String nomUsuari = command.getNif();
			for (EntitatUsuariDto usuari: entitat.getUsuaris()) {
				if (usuari.getUsuari().getNif() != null && usuari.getUsuari().getNif().equalsIgnoreCase(command.getNif())) {
					nomUsuari = usuari.getUsuari().getDescripcio();
					break;
				}
			}
			AlertHelper.success(
					request,
					getMessage(
							request, 
							"auditor.controller.usuari.actualitzat",
							new Object[] {nomUsuari}));
			return "redirect:../usuari";
		} else {
			AlertHelper.error(
					request,
					getMessage(
							request, 
							"auditor.controller.entitat.no.existeix"));
			return "redirect:../../index";
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
		model.addAttribute(command);
		Iterator<EntitatUsuariDto> it = entitat.getUsuaris().iterator();
		while (it.hasNext()) {
			EntitatUsuariDto entitatUsuari = it.next();
			UsuariDto usuari = entitatUsuari.getUsuari();
			boolean eliminar = false;
			if (command.getCodi() != null && command.getCodi().length() > 0) {
				if (usuari.getCodi() == null || usuari.getCodi().length() == 0) {
					eliminar = true;
				} else if (!usuari.getCodi().toLowerCase().contains(command.getCodi().toLowerCase())) {
					eliminar = true;
				}
			}
			if (command.getNif() != null && command.getNif().length() > 0) {
				if (usuari.getNif() == null || usuari.getNif().length() == 0) {
					eliminar = true;
				} else if (!usuari.getNif().toLowerCase().contains(command.getNif().toLowerCase())) {
					eliminar = true;
				}
			}
			if (command.getNom() != null && command.getNom().length() > 0) {
				if (usuari.getNom() == null || usuari.getNom().length() == 0) {
					eliminar = true;
				} else if (!usuari.getNom().toLowerCase().contains(command.getNom().toLowerCase())) {
					eliminar = true;
				}
			}
			if (command.getDepartament() != null && command.getDepartament().length() > 0) {
				if (entitatUsuari.getDepartament() == null || entitatUsuari.getDepartament().length() == 0) {
					eliminar = true;
				} else if (!entitatUsuari.getDepartament().toLowerCase().contains(command.getDepartament().toLowerCase())) {
					eliminar = true;
				}
			}
			if (eliminar) {
				it.remove();
			}
		}
		model.addAttribute("entitat", entitat);
		model.addAttribute(new EntitatUsuariCommand(entitat.getId()));
	}

}