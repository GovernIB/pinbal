/**
 * 
 */
package es.caib.pinbal.webapp.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import es.caib.pinbal.core.dto.EntitatDto;
import es.caib.pinbal.core.dto.EntitatUsuariDto;
import es.caib.pinbal.core.dto.ProcedimentServeiNomDto;
import es.caib.pinbal.core.dto.ProcedimentServeiSimpleDto;
import es.caib.pinbal.core.dto.RolEnumDto;
import es.caib.pinbal.core.dto.ServeiDto;
import es.caib.pinbal.core.dto.UsuariDto;
import es.caib.pinbal.core.dto.UsuariEstatEnum;
import es.caib.pinbal.core.service.EntitatService;
import es.caib.pinbal.core.service.ProcedimentService;
import es.caib.pinbal.core.service.ServeiService;
import es.caib.pinbal.core.service.UsuariService;
import es.caib.pinbal.core.service.exception.EntitatNotFoundException;
import es.caib.pinbal.core.service.exception.UsuariExternNotFoundException;
import es.caib.pinbal.webapp.command.EntitatUsuariCommand;
import es.caib.pinbal.webapp.command.EntitatUsuariCommand.Existent;
import es.caib.pinbal.webapp.command.EntitatUsuariCommand.TipusCodi;
import es.caib.pinbal.webapp.command.EntitatUsuariCommand.TipusNie;
import es.caib.pinbal.webapp.command.EntitatUsuariCommand.TipusNif;
import es.caib.pinbal.webapp.command.UsuariFiltreCommand;
import es.caib.pinbal.webapp.common.AlertHelper;
import es.caib.pinbal.webapp.common.EntitatHelper;
import es.caib.pinbal.webapp.common.RequestSessionHelper;
import es.caib.pinbal.webapp.common.ValidationHelper;
import es.caib.pinbal.webapp.datatables.ServerSideRequest;
import es.caib.pinbal.webapp.datatables.ServerSideResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.naming.NamingException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

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
		
		// carregam un objecte nou per no modificar l'objecte de la sessió
		entitat = entitatService.findById(entitat.getId());
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
			
			if (command.getRol() != null) {
				if (command.getRol() == RolEnumDto.REPRESENTANT && !entitatUsuari.isRepresentant()) {
					eliminar = true;
				} else if (command.getRol() == RolEnumDto.DELEGAT && !entitatUsuari.isDelegat()) {
					eliminar = true;
				} else if (command.getRol() == RolEnumDto.APLICACIO && !entitatUsuari.isAplicacio()) {
					eliminar = true;
				}
			}

			if (command.getActiu() != null) {
				if (UsuariEstatEnum.ACTIU.equals(command.getActiu()) && !entitatUsuari.isActiu()) {
					eliminar = true;
				} else if (UsuariEstatEnum.INACTIU.equals(command.getActiu()) && entitatUsuari.isActiu()) {
					eliminar = true;
				}
			}
			
			if (eliminar) {
				it.remove();
			}
		}
		
//		List<EntitatUsuariDto> listUsers = entitat.getUsuarisRepresentant();
		List<EntitatUsuariDto> listUsers = entitat.getUsuaris();

		PageRequest pageable = serverSideRequest.toPageable();
		Sort sort = pageable.getSort();
		if (sort.iterator().hasNext()) {
			Order ordre = sort.iterator().next();
			// Vemos por qué columna se ha filtrado
			final String propietat = ordre.getProperty();
			final Direction direccio = ordre.getDirection();
			//if (ordre.toString().equals("usuari.codi: DESC")) {
			if ("usuari.codi".equals(propietat)) {
				
				Comparator<EntitatUsuariDto> compareByCodi = new Comparator<EntitatUsuariDto>() {
					@Override
					public int compare(EntitatUsuariDto o1, EntitatUsuariDto o2) {
						int result = o1.getUsuari().getCodi().compareTo(o2.getUsuari().getCodi());
						return Direction.DESC.equals(direccio) ? result : -result;
					}
				};
				
				Collections.sort(listUsers, compareByCodi);
				
			} else if ("usuari.nom".equals(propietat)) {
				
				Comparator<EntitatUsuariDto> compareByNom = new Comparator<EntitatUsuariDto>() {
					@Override
					public int compare(EntitatUsuariDto o1, EntitatUsuariDto o2) {
						int result = o1.getUsuari().getNom().toUpperCase().compareTo(o2.getUsuari().getNom().toUpperCase());
						return Direction.DESC.equals(direccio) ? result : -result;
					}
				};
				
				Collections.sort(listUsers, compareByNom);
				
			} else if ("usuari.nif".equals(propietat)) {
				
				Comparator<EntitatUsuariDto> compareByNif = new Comparator<EntitatUsuariDto>() {
					@Override
					public int compare(EntitatUsuariDto o1, EntitatUsuariDto o2) {
						int result = o1.getUsuari().getNif().compareTo(o2.getUsuari().getNif());
						return Direction.DESC.equals(direccio) ? result : -result;
					}
				};
				
				Collections.sort(listUsers, compareByNif);
			} else if ("departament".equals(propietat)) {
				Comparator<EntitatUsuariDto> compareByDepartament = new Comparator<EntitatUsuariDto>() {
					@Override
					public int compare(EntitatUsuariDto o1, EntitatUsuariDto o2) {
						int result = 0;
						if(o1.getDepartament() == null || o1.getDepartament() == "") {
							result = 1;
						}
						else if(o2.getDepartament() == null || o2.getDepartament() == "") {
							result = -1;
						} else {
							result = o1.getDepartament().compareTo(o2.getDepartament());
						}
						return Direction.DESC.equals(direccio) ? result : -result;
					}
				};
				
				Collections.sort(listUsers, compareByDepartament);
				
			}
		}
		
		final int start = (int)pageable.getOffset();
		final int end = Math.min((start + pageable.getPageSize()), listUsers.size());
		List<EntitatUsuariDto> paginaUsuaris = listUsers.subList(start, end);
		for (EntitatUsuariDto usuari: paginaUsuaris) {
			try {
				usuari.setPermisosCount(serveiService.countPermesosAmbEntitatIUsuari(
						entitat.getId(),
						usuari.getUsuari().getCodi()));
			} catch (Exception ex) {}
		}
		final Page<EntitatUsuariDto> page = new PageImpl<>(paginaUsuaris, pageable, listUsers.size());

		return new ServerSideResponse<EntitatUsuariDto, Long>(serverSideRequest, page);
	}

	@RequestMapping(value = "/{codi}", method = RequestMethod.GET)
	@ResponseBody
	public EntitatUsuariDto usuariGet(
			HttpServletRequest request,
			@PathVariable String codi,
			Model model) {
		EntitatDto entitat = EntitatHelper.getEntitatActual(request);
		if (entitat == null) {
			AlertHelper.error(
					request,
					getMessage(
							request,
							"representant.controller.entitat.no.existeix"));
			return null;
		}
		return usuariService.getEntitatUsuari(entitat.getId(), codi);
	}

	@RequestMapping(value = "/save", method = RequestMethod.POST)
	@ResponseBody
	public String save(
			HttpServletRequest request,
			@Valid EntitatUsuariCommand command,
			BindingResult bindingResult,
			Model model) throws Exception {
		EntitatDto entitat = EntitatHelper.getEntitatActual(request);
		if (entitat == null) {
			AlertHelper.error(
					request,
					getMessage(
							request, 
							"representant.controller.entitat.no.existeix"));
			return "NO_ENTITAT";
		}
		
		if (!EntitatHelper.isRepresentantEntitatActual(request)) {
			AlertHelper.error(
					request,
					getMessage(
							request,
							"representant.no.autoritzat.alert.error"));
			return "KO";
		}

		Class<?> grup = null;
		if (command.getTipus().equals(EntitatUsuariCommand.CARACTER_CODI) || !Strings.isNullOrEmpty(command.getCodi())) {
			grup = TipusCodi.class;
		} else if (command.getTipus().equals(EntitatUsuariCommand.CARACTER_NIF) || !Strings.isNullOrEmpty(command.getNif())) {
			if (command.getNif() != null && command.getNif().toUpperCase().matches("[XYZ][0-9]{7}[A-Z]")) {
				grup = TipusNie.class;
			} else {
				grup = TipusNif.class;
			}
		} else {
			if (!Strings.isNullOrEmpty(command.getCodi())) {
				grup = TipusCodi.class;
			} else if (!Strings.isNullOrEmpty(command.getNif())) {
				if (command.getNif() != null && command.getNif().toUpperCase().matches("[XYZ][0-9]{7}[A-Z]")) {
					grup = TipusNie.class;
				} else {
					grup = TipusNif.class;
				}
			} else {
				grup = Existent.class;
			}
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
			return "KO";
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
					command.isAfegir(),
					command.isActiu());
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
			return "OK";
		} catch (UsuariExternNotFoundException ex) {
			AlertHelper.error(
					request,
					getMessage(
							request, 
							"representant.controller.usuari.extern.no.existeix"));
			return "KO";
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

	@RequestMapping(value = "/{usuariCodi}/permis/afegir/selected", method = RequestMethod.POST)
	public String permisAfegirSeleccionats(
			HttpServletRequest request,
			@PathVariable String usuariCodi,
			@RequestParam("persisosSeleccionats") String procedimentsServeisSeleccionatsJson,
			Model model) throws Exception {
		EntitatDto entitat = EntitatHelper.getEntitatActual(request);
		if (entitat != null) {
			if (!EntitatHelper.isRepresentantEntitatActual(request))
				return "representantNoAutoritzat";

			ObjectMapper mapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			List<ProcedimentServeiSimpleDto> procedimentsServeis = mapper.readValue(procedimentsServeisSeleccionatsJson, new TypeReference<List<ProcedimentServeiSimpleDto>>(){});
			procedimentService.serveiPermisAllowSelected(
					usuariCodi,
					procedimentsServeis,
					entitat.getId());
			UsuariDto usuari = usuariService.getDades(usuariCodi);
			return getModalControllerReturnValueSuccess(
					request,
					"redirect:../../permis",
					"representant.controller.permis.seleccionats.afegit",
					new Object[] {usuari.getDescripcio()});
		} else {
			return getModalControllerReturnValueError(
					request,
					"redirect:/index",
					"representant.controller.entitat.no.existeix");
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

	@RequestMapping(value = "/{usuariCodi}/permis/deny/selected", method = RequestMethod.POST)
	public String permisDenegarSeleccionats(
			HttpServletRequest request,
			@PathVariable String usuariCodi,
			@RequestParam("persisosSeleccionats") String procedimentsServeisSeleccionatsJson,
			Model model) throws Exception {
		EntitatDto entitat = EntitatHelper.getEntitatActual(request);
		if (entitat != null) {
			if (!EntitatHelper.isRepresentantEntitatActual(request))
				return "representantNoAutoritzat";

			ObjectMapper mapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			List<ProcedimentServeiSimpleDto> procedimentsServeis = mapper.readValue(procedimentsServeisSeleccionatsJson, new TypeReference<List<ProcedimentServeiSimpleDto>>(){});
			procedimentService.serveiPermisDenySelected(
					usuariCodi,
					procedimentsServeis,
					entitat.getId());
			UsuariDto usuari = usuariService.getDades(usuariCodi);
			AlertHelper.success(
					request,
					getMessage(
							request,
							"representant.controller.permis.seleccionats.denegat",
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

	@RequestMapping(value = "/{usuariCodi}/permis/afegir", method = RequestMethod.GET)
	public String permisAfegirGet(
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
			return "representantUsuariPermisForm";
		} else {
			AlertHelper.error(
					request,
					getMessage(
							request,
							"representant.controller.entitat.no.existeix"));
			return "redirect:../index";
		}
	}

	@RequestMapping(value = "/{usuariCodi}/permis/{procedimentId}/serveis/disponibles", method = RequestMethod.GET)
	@ResponseBody
	public List<ProcedimentServeiNomDto> serveisPerProcediment(
			HttpServletRequest request,
			@PathVariable String usuariCodi,
			@PathVariable Long procedimentId,
			Model model) throws Exception {
		EntitatDto entitat = EntitatHelper.getEntitatActual(request);
		if (entitat != null) {
			if (!EntitatHelper.isRepresentantEntitatActual(request))
				return null;
		}
		return procedimentService.serveiDisponibles(
					usuariCodi,
					procedimentId,
					entitat.getId());
	}

	private void omplirModelPerMostrarLlistat(
			HttpServletRequest request,
			EntitatDto entitat,
			Model model) throws Exception {
		UsuariFiltreCommand command = (UsuariFiltreCommand)RequestSessionHelper.obtenirObjecteSessio(
				request,
				SESSION_ATTRIBUTE_FILTRE);
		if (command == null) {
			command = new UsuariFiltreCommand();
		}
		command.eliminarEspaisCampsCerca();
		model.addAttribute(command);
		model.addAttribute("entitat", entitat);
		model.addAttribute(new EntitatUsuariCommand(entitat.getId()));
//		String[] values = { EntitatUsuariCommand.CARACTER_NIF, EntitatUsuariCommand.CARACTER_CODI };
//		String[] texts = { "representant.usuaris.tipus.nif", "representant.usuaris.tipus.codi" };
//		model.addAttribute(
//				"identificatPerOptions",
//				EnumHelper.getOptionsForArray(
//						values ,
//						texts));
	}
	
}
