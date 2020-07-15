/**
 * 
 */
package es.caib.pinbal.webapp.controller;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;

import javax.naming.NamingException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import es.caib.pinbal.core.dto.EntitatDto;
import es.caib.pinbal.core.dto.EntitatUsuariDto;
import es.caib.pinbal.core.dto.ServeiDto;
import es.caib.pinbal.core.dto.UsuariDto;
import es.caib.pinbal.core.service.EntitatService;
import es.caib.pinbal.core.service.ProcedimentService;
import es.caib.pinbal.core.service.ServeiService;
import es.caib.pinbal.core.service.UsuariService;
import es.caib.pinbal.core.service.exception.EntitatNotFoundException;
import es.caib.pinbal.core.service.exception.UsuariExternNotFoundException;
import es.caib.pinbal.webapp.command.EntitatUsuariCommand;
import es.caib.pinbal.webapp.command.EntitatUsuariCommand.Existent;
import es.caib.pinbal.webapp.command.EntitatUsuariCommand.TipusCodi;
import es.caib.pinbal.webapp.command.EntitatUsuariCommand.TipusNif;
import es.caib.pinbal.webapp.command.UsuariFiltreCommand;
import es.caib.pinbal.webapp.common.AlertHelper;
import es.caib.pinbal.webapp.common.EntitatHelper;
import es.caib.pinbal.webapp.common.RequestSessionHelper;
import es.caib.pinbal.webapp.common.ValidationHelper;
import es.caib.pinbal.webapp.datatables.ServerSideRequest;
import es.caib.pinbal.webapp.datatables.ServerSideResponse;

/**
 * Controlador per a la configuració d'usuaris per al representant.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/representant/usuari")
public class RepresentantUsuariController extends BaseController {

	public static final String SESSION_ATTRIBUTE_FILTRE = "EntitatUsuariController.session.filtre";

	@Autowired
	private EntitatService entitatService;
	@Autowired
	private ProcedimentService procedimentService;
	@Autowired
	private ServeiService serveiService;
	@Autowired
	private UsuariService usuariService;
	@Autowired(required = true)
	private javax.validation.Validator validator;



	@RequestMapping(method = RequestMethod.GET)
	public String get(
			HttpServletRequest request,
			Model model) throws Exception {
		if (!EntitatHelper.isRepresentantEntitatActual(request))
			return "representantNoAutoritzat";
		EntitatDto entitat = EntitatHelper.getEntitatActual(
				request,
				entitatService);
		if (entitat != null) {
			omplirModelPerMostrarLlistat(request, entitat, model);
			return "representantUsuaris";
		} else {
			AlertHelper.error(
					request,
					getMessage(
							request, 
							"representant.controller.entitat.no.existeix"));
			return "redirect:../index";
		}
	}
	@RequestMapping(method = RequestMethod.POST)
	public String post(
			HttpServletRequest request,
			@Valid UsuariFiltreCommand command,
			BindingResult bindingResult,
			Model model) throws Exception {
		EntitatDto entitat = EntitatHelper.getEntitatActual(request);
		if (entitat == null) {
			AlertHelper.error(
					request, 
					getMessage(
							request, 
							"representant.controller.entitat.no.existeix"));
			return "redirect:../index";
			
		}
		if (!EntitatHelper.isRepresentantEntitatActual(request))
			return "representantNoAutoritzat";
		
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
	}

	@RequestMapping(value = "/datatable", produces="application/json", method = RequestMethod.GET)
	@ResponseBody
	public ServerSideResponse<EntitatUsuariDto, Long> datatable(HttpServletRequest request, Model model)
	      throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, NamingException,
	      SQLException, EntitatNotFoundException {
		
		EntitatDto entitat = EntitatHelper.getEntitatActual(request);
		if (entitat == null) {
			throw new EntitatNotFoundException();			
		}
		
		ServerSideRequest serverSideRequest = new ServerSideRequest(request);
		
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
		List<EntitatUsuariDto> listUsers = entitat.getUsuarisRepresentant();
		Page<EntitatUsuariDto> page = new PageImpl<EntitatUsuariDto>(listUsers, null, listUsers.size());
		
		return new ServerSideResponse<EntitatUsuariDto, Long>(serverSideRequest, page);
	}
	
	@RequestMapping(value = "/save", method = RequestMethod.POST)
	public String save(
			HttpServletRequest request,
			@Valid EntitatUsuariCommand command,
			BindingResult bindingResult,
			Model model) throws Exception {
		EntitatDto entitat = EntitatHelper.getEntitatActual(request);
		if (entitat != null) {
			if (!EntitatHelper.isRepresentantEntitatActual(request))
				return "representantNoAutoritzat";
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
				omplirModelPerMostrarLlistat(
						request,
						entitat,
						model);
				return "representantUsuaris";
			}
			try {
				usuariService.actualitzarDadesRepresentant(
						command.getId(),
						command.getCodi(),
						command.getNif(),
						command.getDepartament(),
						command.isRolRepresentant(),
						command.isRolDelegat(),
						command.isRolAplicacio(),
						command.isAfegir());
				String nomUsuari = command.getNif();
				for (EntitatUsuariDto usuari: entitat.getUsuaris()) {
					if (usuari.getUsuari().getNif() != null && usuari.getUsuari().getNif() != null && usuari.getUsuari().getNif().equalsIgnoreCase(command.getNif())) {
						nomUsuari = usuari.getUsuari().getDescripcio();
						break;
					}
				}
				AlertHelper.success(
						request,
						getMessage(
								request, 
								"representant.controller.usuari.actualitzat",
								new Object[] {nomUsuari}));
			} catch (UsuariExternNotFoundException ex) {
				AlertHelper.error(
						request,
						getMessage(
								request, 
								"representant.controller.usuari.extern.no.existeix"));
			}
			return "redirect:../usuari";
		} else {
			AlertHelper.error(
					request,
					getMessage(
							request, 
							"representant.controller.entitat.no.existeix"));
			return "redirect:../index";
		}
	}

	@RequestMapping(value = "/{usuariCodi}/permis", method = RequestMethod.GET)
	public String permisGet(
			HttpServletRequest request,
			@PathVariable String usuariCodi,
			Model model) throws EntitatNotFoundException {
		EntitatDto entitat = EntitatHelper.getEntitatActual(request);
		if (entitat != null) {
			if (!EntitatHelper.isRepresentantEntitatActual(request))
				return "representantNoAutoritzat";
			model.addAttribute(
					"usuari",
					usuariService.getDades(usuariCodi));
			model.addAttribute(
					"procediments",
					procedimentService.findAmbEntitat(entitat.getId()));
			model.addAttribute(
					"permisos",
					serveiService.findPermesosAmbEntitatIUsuari(
							entitat.getId(),
							usuariCodi));
			return "representantUsuariPermis";
		} else {
			AlertHelper.error(
					request,
					getMessage(
							request, 
							"representant.controller.entitat.no.existeix"));
			return "redirect:../index";
		}
	}

	@RequestMapping(value = "/{usuariCodi}/permis/allow", method = RequestMethod.POST)
	public String permisAtorgar(
			HttpServletRequest request,
			@PathVariable String usuariCodi,
			@RequestParam("procedimentId") Long procedimentId,
			@RequestParam("serveiCodi") String serveiCodi,
			Model model) throws Exception {
		EntitatDto entitat = EntitatHelper.getEntitatActual(request);
		if (entitat != null) {
			if (!EntitatHelper.isRepresentantEntitatActual(request))
				return "representantNoAutoritzat";
			if (procedimentId != null && serveiCodi != null) {
				procedimentService.serveiPermisAllow(
						procedimentId,
						serveiCodi,
						usuariCodi);
				UsuariDto usuari = usuariService.getDades(usuariCodi);
				ServeiDto servei = serveiService.findAmbCodiPerAdminORepresentant(serveiCodi);
				AlertHelper.success(
						request, 
						getMessage(
								request,
								"representant.controller.permis.atorgat",
								new Object[] {
										servei.getDescripcio(),
										usuari.getDescripcio()}));
			} else {
				AlertHelper.error(
						request,
						getMessage(
								request, 
								"representant.controller.permis.falten.dades"));
			}
			return "redirect:../permis";
		} else {
			AlertHelper.error(
					request,
					getMessage(
							request, 
							"representant.controller.entitat.no.existeix"));
			return "redirect:../index";
		}
	}

	@RequestMapping(value = "/{usuariCodi}/permis/deny", method = RequestMethod.POST)
	public String permisDenegar(
			HttpServletRequest request,
			@PathVariable String usuariCodi,
			@RequestParam("procedimentId") Long procedimentId,
			@RequestParam("serveiCodi") String serveiCodi,
			Model model) throws Exception {
		EntitatDto entitat = EntitatHelper.getEntitatActual(request);
		if (entitat != null) {
			if (!EntitatHelper.isRepresentantEntitatActual(request))
				return "representantNoAutoritzat";
			if (procedimentId != null && serveiCodi != null) {
				procedimentService.serveiPermisDeny(
						procedimentId,
						serveiCodi,
						usuariCodi);
				UsuariDto usuari = usuariService.getDades(usuariCodi);
				ServeiDto servei = serveiService.findAmbCodiPerAdminORepresentant(serveiCodi);
				AlertHelper.success(
						request, 
						getMessage(
								request,
								"representant.controller.permis.denegat",
								new Object[] {
										servei.getDescripcio(),
										usuari.getDescripcio()}));
			} else {
				AlertHelper.error(
						request,
						getMessage(
								request, 
								"representant.controller.permis.falten.dades"));
			}
			return "redirect:../permis";
		} else {
			AlertHelper.error(
					request,
					getMessage(
							request, 
							"representant.controller.entitat.no.existeix"));
			return "redirect:../index";
		}
	}
	
	@RequestMapping(value = "/{usuariCodi}/permis/deny/all", method = RequestMethod.GET)
	public String permisDenegarTots(
			HttpServletRequest request,
			@PathVariable String usuariCodi,
			Model model) throws Exception {
		EntitatDto entitat = EntitatHelper.getEntitatActual(request);
		if (entitat != null) {
			if (!EntitatHelper.isRepresentantEntitatActual(request))
				return "representantNoAutoritzat";
			procedimentService.serveiPermisDenyAll(
					usuariCodi,
					entitat.getId());
			UsuariDto usuari = usuariService.getDades(usuariCodi);
			AlertHelper.success(
					request, 
					getMessage(
							request,
							"representant.controller.permis.all.denegat",
							new Object[] {usuari.getDescripcio()}));
			return "redirect:../../permis";
		} else {
			AlertHelper.error(
					request,
					getMessage(
							request, 
							"representant.controller.entitat.no.existeix"));
			return "redirect:/index";
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
