/**
 * 
 */
package es.caib.pinbal.webapp.controller;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.List;

import javax.naming.NamingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import es.caib.pinbal.core.dto.EntitatDto;
import es.caib.pinbal.core.dto.EntitatUsuariDto;
import es.caib.pinbal.core.dto.ProcedimentClaseTramiteEnumDto;
import es.caib.pinbal.core.dto.ProcedimentDto;
import es.caib.pinbal.core.dto.ServeiDto;
import es.caib.pinbal.core.service.EntitatService;
import es.caib.pinbal.core.service.ProcedimentService;
import es.caib.pinbal.core.service.PropertyService;
import es.caib.pinbal.core.service.ServeiService;
import es.caib.pinbal.core.service.exception.EntitatNotFoundException;
import es.caib.pinbal.core.service.exception.EntitatUsuariNotFoundException;
import es.caib.pinbal.core.service.exception.ProcedimentNotFoundException;
import es.caib.pinbal.core.service.exception.ProcedimentServeiNotFoundException;
import es.caib.pinbal.core.service.exception.ServeiNotFoundException;
import es.caib.pinbal.webapp.command.ProcedimentCommand;
import es.caib.pinbal.webapp.command.ProcedimentFiltreCommand;
import es.caib.pinbal.webapp.command.ServeiFiltreCommand;
import es.caib.pinbal.webapp.common.AlertHelper;
import es.caib.pinbal.webapp.common.EntitatHelper;
import es.caib.pinbal.webapp.common.RequestSessionHelper;
import es.caib.pinbal.webapp.datatables.ServerSideRequest;
import es.caib.pinbal.webapp.datatables.ServerSideResponse;
import es.caib.pinbal.webapp.helper.EnumHelper;

/**
 * Controlador per al manteniment de procediments.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/procediment")
public class ProcedimentController extends BaseController {

	public static final String SESSION_ATTRIBUTE_FILTRE = "ProcedimentController.session.filtre";
	private static final String SESSION_ATTRIBUTE_FILTRE_PROCEDIMENT = "ServeiController.session.filtre.procediment";

	public static String getSessionAttributeFiltreProcediment() {
		return SESSION_ATTRIBUTE_FILTRE_PROCEDIMENT;
	}

	@Autowired
	private ProcedimentService procedimentService;
	@Autowired
	private EntitatService entitatService;
	@Autowired
	private ServeiService serveiService;
	@Autowired
	private PropertyService propertyService;



	@RequestMapping(method = RequestMethod.GET)
	public String get(
			HttpServletRequest request,
			HttpServletResponse response,
			Model model) throws Exception {
		if (!EntitatHelper.isRepresentantEntitatActual(request))
			return "representantNoAutoritzat";
		
		EntitatDto entitat = EntitatHelper.getEntitatActual(request, entitatService);
		if (entitat == null) {
			AlertHelper.error(
					request,
					getMessage(
							request, 
							"procediment.controller.no.entitat.seleccionada"));
			return "redirect:../index";
			
		}
		model.addAttribute("organsGestors", entitatService.getOrgansGestors(entitat.getId()));
		omplirModelFiltreDataTable(request, entitat, model);
		return "procedimentList";
	}
	
	@RequestMapping(method = RequestMethod.POST)
	public String post(
			HttpServletRequest request,
			@Valid ProcedimentFiltreCommand command,
			BindingResult bindingResult,
			Model model) throws Exception {
		if (!EntitatHelper.isRepresentantEntitatActual(request))
			return "representantNoAutoritzat";
		EntitatDto entitat = EntitatHelper.getEntitatActual(request, entitatService);
		if (entitat == null) {
			AlertHelper.error(
					request,
					getMessage(
							request, 
							"procediment.controller.no.entitat.seleccionada"));
			return "redirect:../index";
		}
			
		if (bindingResult.hasErrors()) {
			omplirModelFiltreDataTable(request, entitat, model);
			return "procedimentList";
		} else {
			RequestSessionHelper.actualitzarObjecteSessio(
					request,
					SESSION_ATTRIBUTE_FILTRE,
					command);
			return "redirect:procediment";
		}
	}
	
	@RequestMapping(value = "/datatable", produces="application/json", method = RequestMethod.GET)
	@ResponseBody
	public ServerSideResponse<ProcedimentDto, Long> datatable(HttpServletRequest request, Model model)
	      throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, NamingException,
	      SQLException, EntitatNotFoundException {
		ServerSideRequest serverSideRequest = new ServerSideRequest(request);
		
		ProcedimentFiltreCommand command = (ProcedimentFiltreCommand)RequestSessionHelper.obtenirObjecteSessio(
				request,
				SESSION_ATTRIBUTE_FILTRE);
		if (command == null)
			command = new ProcedimentFiltreCommand();
		EntitatDto entitat = EntitatHelper.getEntitatActual(request, entitatService);
		if (entitat == null) {
			throw new EntitatNotFoundException();
		}
		Page<ProcedimentDto> page = procedimentService.findAmbFiltrePaginat(
													entitat.getId(),
													command.getCodi(),
													command.getNom(),
													command.getDepartament(),
													command.getOrganGestorId(),
													command.getCodiSia(), 
													command.getActiu(), 
													ServerSideRequest.getPaginacioDtoFromRequest(request));

		return new ServerSideResponse<ProcedimentDto, Long>(serverSideRequest, page);
	}

	@RequestMapping(value = "/new", method = RequestMethod.GET)
	public String get(
			HttpServletRequest request,
			Model model) {
		return get(request, (Long)null, model);
	}
	@RequestMapping(value = "/{procedimentId}", method = RequestMethod.GET)
	public String get(
			HttpServletRequest request,
			@PathVariable Long procedimentId,
			Model model) {
		if (!EntitatHelper.isRepresentantEntitatActual(request))
			return "representantNoAutoritzat";
		EntitatDto entitat = EntitatHelper.getEntitatActual(request, entitatService);
		if (entitat != null) {
			ProcedimentDto procediment = null;
			if (procedimentId != null)
				procediment = procedimentService.findById(procedimentId);
			ProcedimentCommand command;
			if (procediment != null) {
				command = ProcedimentCommand.asCommand(procediment);
			} else {
				command = new ProcedimentCommand();
			}
			command.setEntitatId(entitat.getId());
			model.addAttribute(command);
			fillFormModel(entitat.getId(), model);
			return "procedimentForm";
		} else {
			AlertHelper.error(
					request, 
					getMessage(
							request,
							"procediment.controller.no.entitat.seleccionada"));
			return "redirect:../../index";
		}
	}

	@RequestMapping(value = "/save", method = RequestMethod.POST)
	public String save(
			HttpServletRequest request,
			@Valid ProcedimentCommand command,
			BindingResult bindingResult,
			Model model) throws EntitatNotFoundException, ProcedimentNotFoundException {
		if (!EntitatHelper.isRepresentantEntitatActual(request))
			return "representantNoAutoritzat";
		EntitatDto entitat = EntitatHelper.getEntitatActual(request, entitatService);
		if (entitat == null) {
			AlertHelper.error(
					request, 
					getMessage(
							request,
							"procediment.controller.no.entitat.seleccionada"));
			return "redirect:../index";
		}
			
		if (bindingResult.hasErrors()) {
			fillFormModel(entitat.getId(), model);
			return "procedimentForm";
		}
		
		ProcedimentDto procediment = command.asDto();
		procediment.setEntitatId(entitat.getId());
		command.setEntitatId(entitat.getId());
		
		if (command.getId() != null) {
			procedimentService.update(procediment);
			AlertHelper.success(
					request,
					getMessage(
							request,
							"procediment.controller.procediment.modificat.ok"));
		} else {
			procedimentService.create(procediment);
			AlertHelper.success(
					request, 
					getMessage(
							request,
							"procediment.controller.procediment.creat.ok"));
		}
		return "redirect:../procediment";
	}

	@RequestMapping(value = "/{procedimentId}/delete", method = RequestMethod.GET)
	public String delete(
			HttpServletRequest request,
			@PathVariable Long procedimentId) throws ProcedimentNotFoundException {
		if (!EntitatHelper.isRepresentantEntitatActual(request))
			return "representantNoAutoritzat";
		EntitatDto entitat = EntitatHelper.getEntitatActual(request, entitatService);
		if (entitat != null) {
			procedimentService.delete(procedimentId);
			AlertHelper.success(
					request, 
					getMessage(
							request,
							"procediment.controller.procediment.esborrat.ok"));
			return "redirect:../../procediment";
		} else {
			return "redirect:../../../index";
		}
	}

	@RequestMapping(value = "/{procedimentId}/enable", method = RequestMethod.GET)
	public String enable(
			HttpServletRequest request,
			@PathVariable Long procedimentId) throws ProcedimentNotFoundException {
		if (!EntitatHelper.isRepresentantEntitatActual(request))
			return "representantNoAutoritzat";
		EntitatDto entitat = EntitatHelper.getEntitatActual(request, entitatService);
		if (entitat != null) {
			procedimentService.updateActiu(procedimentId, true);
			AlertHelper.success(
					request, 
					getMessage(
							request,
							"procediment.controller.procediment.activat.ok"));
			return "redirect:../../procediment";
		} else {
			return "redirect:../../../index";
		}
	}

	@RequestMapping(value = "/{procedimentId}/disable", method = RequestMethod.GET)
	public String disable(
			HttpServletRequest request,
			@PathVariable Long procedimentId) throws ProcedimentNotFoundException {
		if (!EntitatHelper.isRepresentantEntitatActual(request))
			return "representantNoAutoritzat";
		EntitatDto entitat = EntitatHelper.getEntitatActual(request, entitatService);
		if (entitat != null) {
			procedimentService.updateActiu(procedimentId, false);
			AlertHelper.success(
					request, 
					getMessage(
							request,
							"procediment.controller.procediment.desactivat.ok"));
			return "redirect:../../procediment";
		} else {
			return "redirect:../../../index";
		}
	}

	@RequestMapping(value = "/{procedimentId}/servei/datatable", produces="application/json", method = RequestMethod.GET)
	@ResponseBody
	public ServerSideResponse<ServeiDto, Long> datatable(
			HttpServletRequest request,
			@PathVariable Long procedimentId,
			Model model)
	      throws Exception {
		ServerSideRequest serverSideRequest = new ServerSideRequest(request);
		ServeiFiltreCommand command = (ServeiFiltreCommand) RequestSessionHelper.obtenirObjecteSessio( 
				request, 
				SESSION_ATTRIBUTE_FILTRE_PROCEDIMENT);
		if (command == null) {
			command = new ServeiFiltreCommand();
			command.setActiva(true);
		}
		if (!EntitatHelper.isRepresentantEntitatActual(request))
			throw new Exception("Representant no autoritzat");
		EntitatDto entitat = EntitatHelper.getEntitatActual(request, entitatService);
		if (entitat == null) {
			throw new Exception("Entitat actual incorrecte");
		}
		ProcedimentDto procediment = procedimentService.findById(procedimentId);
		if (procediment == null) {
			throw new Exception("Incorrect procediment id");
		}
		Page<ServeiDto> page = serveiService.findAmbFiltrePaginat(
													command.getCodi(),
													command.getDescripcio(),
													command.getEmissor(),
													command.getActiva(),
													entitat, 
													procediment,			
													serverSideRequest.toPageable());
		return new ServerSideResponse<ServeiDto, Long>(serverSideRequest, page);
	}

	@RequestMapping(value = "/{procedimentId}/servei", method = RequestMethod.GET)
	public String servei(
			HttpServletRequest request,
			@PathVariable Long procedimentId,
			Model model) throws EntitatNotFoundException, Exception {
		if (!EntitatHelper.isRepresentantEntitatActual(request))
			return "representantNoAutoritzat";
		EntitatDto entitat = EntitatHelper.getEntitatActual(request, entitatService);
		if (entitat == null) {
			AlertHelper.error(
					request,
					getMessage(
							request,
							"procediment.controller.no.entitat.seleccionada"));
			return "redirect:../../../index";
		}
		
		ProcedimentDto procediment = procedimentService.findById(procedimentId);
		if (procediment == null) {
			AlertHelper.error(
					request, 
					getMessage(
							request,
							"procediment.controller.procediment.no.existeix"));
			return "redirect:../../procediment";	
		}
		model.addAttribute("entitat", entitat);
		model.addAttribute("procediment", procediment);
				
		setCommandFiltreServeis(request, model);
		return "procedimentServeis";		
	}

	@RequestMapping(value = "/{procedimentId}/servei", method = RequestMethod.POST)
	public String serveiPost(
			HttpServletRequest request,
			@PathVariable Long procedimentId,
			@Valid ServeiFiltreCommand command,
			BindingResult bindingResult,
			Model model) throws Exception {
		if (!EntitatHelper.isRepresentantEntitatActual(request))
			return "representantNoAutoritzat";
		EntitatDto entitat = EntitatHelper.getEntitatActual(request, entitatService);
		if (entitat == null) {
			AlertHelper.error(
					request,
					getMessage(
							request,
							"procediment.controller.no.entitat.seleccionada"));
			return "redirect:../../../index";
		}
		ProcedimentDto procediment = null;
		if (procedimentId != null)
			procediment = procedimentService.findById(procedimentId);
		if (procediment == null) {
			AlertHelper.error(
					request, 
					getMessage(
							request,
							"procediment.controller.procediment.no.existeix"));
			return "redirect:../../procediment";	
		}
		model.addAttribute("entitat", entitat);
		model.addAttribute("procediment", procediment);
		if (bindingResult.hasErrors()) {
			setCommandFiltreServeis(request, model);
			
		} else {
			RequestSessionHelper.actualitzarObjecteSessio(
					request,
					getSessionAttributeFiltreProcediment(),
					command);
		}
		return "procedimentServeis";
	}
	
	@RequestMapping(value = "/{procedimentId}/servei/{serveiCodi}/enable", method = RequestMethod.GET)
	public String serveiEnable(
			HttpServletRequest request,
			@PathVariable Long procedimentId,
			@PathVariable String serveiCodi,
			Model model) throws ProcedimentNotFoundException, ServeiNotFoundException {
		if (!EntitatHelper.isRepresentantEntitatActual(request))
			return "representantNoAutoritzat";
		EntitatDto entitat = EntitatHelper.getEntitatActual(request, entitatService);
		if (entitat != null) {
			ProcedimentDto procediment = null;
			if (procedimentId != null)
				procediment = procedimentService.findById(procedimentId);
			if (procediment != null) {
				procedimentService.serveiEnable(procedimentId, serveiCodi);
				ServeiDto servei = serveiService.findAmbCodiPerAdminORepresentant(serveiCodi);
				AlertHelper.success(
						request, 
						getMessage(
								request,
								"procediment.controller.servei.activat",
								new Object[] {servei.getDescripcio()}));
				return "redirect:../../servei";
			} else {
				AlertHelper.error(
						request, 
						getMessage(
								request,
								"procediment.controller.procediment.no.existeix"));
				return "redirect:../../../../procediment";
			}
		} else {
			AlertHelper.error(
					request, 
					getMessage(
							request,
							"procediment.controller.no.entitat.seleccionada"));
			return "redirect:../../../../../index";
		}
	}

	@RequestMapping(value = "/{procedimentId}/servei/{serveiCodi}/disable", method = RequestMethod.GET)
	public String serveiDisable(
			HttpServletRequest request,
			@PathVariable Long procedimentId,
			@PathVariable String serveiCodi,
			Model model) throws ProcedimentNotFoundException, ProcedimentServeiNotFoundException, ServeiNotFoundException {
		if (!EntitatHelper.isRepresentantEntitatActual(request))
			return "representantNoAutoritzat";
		EntitatDto entitat = EntitatHelper.getEntitatActual(request, entitatService);
		if (entitat != null) {
			ProcedimentDto procediment = null;
			if (procedimentId != null)
				procediment = procedimentService.findById(procedimentId);
			if (procediment != null) {
				procedimentService.serveiDisable(procedimentId, serveiCodi);
				ServeiDto servei = serveiService.findAmbCodiPerAdminORepresentant(serveiCodi);
				AlertHelper.success(
						request, 
						getMessage(
								request,
								"procediment.controller.servei.desactivat",
								new Object[] {servei.getDescripcio()}));
				return "redirect:../../servei";
			} else {
				AlertHelper.error(
						request, 
						getMessage(
								request,
								"procediment.controller.procediment.no.existeix"));
				return "redirect:../../../../entitat";
			}
		} else {
			AlertHelper.error(
					request, 
					getMessage(
							request,
							"procediment.controller.no.entitat.seleccionada"));
			return "redirect:../../../../../index";
		}
	}

	@RequestMapping(value = "/{procedimentId}/servei/{serveiCodi}/permis", method = RequestMethod.GET)
	public String serveiPermis(
			HttpServletRequest request,
			@PathVariable Long procedimentId,
			@PathVariable String serveiCodi,
			Model model) throws ProcedimentNotFoundException, ServeiNotFoundException, ProcedimentServeiNotFoundException {
		if (!EntitatHelper.isRepresentantEntitatActual(request))
			return "representantNoAutoritzat";
		EntitatDto entitat = EntitatHelper.getEntitatActual(request, entitatService);
		if (entitat == null) {
			AlertHelper.error(
					request, 
					getMessage(
							request,
							"procediment.controller.no.entitat.seleccionada"));
			return "redirect:../../../../../index";
		}
		ProcedimentDto procediment = null;
		if (procedimentId != null)
			procediment = procedimentService.findById(procedimentId);
		
		if (procediment == null) {
			AlertHelper.error(
					request, 
					getMessage(
							request,
							"procediment.controller.procediment.no.existeix"));
			return "redirect:../../../../procediment";
		}
		
		model.addAttribute("entitat", entitat);
		model.addAttribute("procediment", procediment);
		model.addAttribute("servei", serveiService.findAmbCodiPerAdminORepresentant(serveiCodi));
		return "procedimentServeiPermisos";

	}

	@RequestMapping(value = "/{procedimentId}/servei/{serveiCodi}/permis/datatable", produces="application/json", method = RequestMethod.GET)
	@ResponseBody
	public ServerSideResponse<EntitatUsuariDto, Long> datatableServeiPermis(HttpServletRequest request, 
																			@PathVariable Long procedimentId, 
																			@PathVariable String serveiCodi,
																			Model model)
	      throws Exception {
		if (!EntitatHelper.isRepresentantEntitatActual(request))
			throw new Exception("Representant no autoritzat");
							
		EntitatDto entitat = EntitatHelper.getEntitatActual(request, entitatService);
		if (entitat == null) {
			throw new EntitatNotFoundException();
		}
		
		
		ProcedimentDto procediment = null;
		if (procedimentId != null)
			procediment = procedimentService.findById(procedimentId);
		
		if (procediment == null) {
			throw new ProcedimentNotFoundException();
		}
	
		List<String> usuarisAmbPermis = procedimentService.findUsuarisAmbPermisPerServei(procedimentId, serveiCodi);
		
		for (EntitatUsuariDto usuari: entitat.getUsuarisRepresentant()) {
			for (String usuariAmbPermis: usuarisAmbPermis) {
				if (usuari.getUsuari().getCodi().equals(usuariAmbPermis)) {
					usuari.setAcces(true);
					break;
				}
			}
		}
		ServerSideRequest serverSideRequest = new ServerSideRequest(request);	
		
		List<EntitatUsuariDto> list =  entitat.getUsuarisRepresentant();
		Page<EntitatUsuariDto> page = new PageImpl<EntitatUsuariDto>(list, null, list.size());
		return new ServerSideResponse<EntitatUsuariDto, Long>(serverSideRequest, page);

	}
	@RequestMapping(value = "/{procedimentId}/servei/{serveiCodi}/permis/{usuariCodi}/allow", method = RequestMethod.GET)
	public String serveiPermisAllow(
			HttpServletRequest request,
			@PathVariable Long procedimentId,
			@PathVariable String serveiCodi,
			@PathVariable String usuariCodi,
			Model model) throws ProcedimentNotFoundException, ProcedimentServeiNotFoundException, EntitatUsuariNotFoundException, ServeiNotFoundException {
		if (!EntitatHelper.isRepresentantEntitatActual(request))
			return "representantNoAutoritzat";
		EntitatDto entitat = EntitatHelper.getEntitatActual(request, entitatService);
		if (entitat != null) {
			ProcedimentDto procediment = null;
			if (procedimentId != null)
				procediment = procedimentService.findById(procedimentId);
			if (procediment != null) {
				procedimentService.serveiPermisAllow(procedimentId, serveiCodi, usuariCodi);
				String usuariNom = usuariCodi;
				for (EntitatUsuariDto usuari: entitat.getUsuaris()) {
					if (usuari.getUsuari().getCodi().equalsIgnoreCase(usuariCodi)) {
						usuariNom = usuari.getUsuari().getDescripcio();
						break;
					}
				}
				ServeiDto servei = serveiService.findAmbCodiPerAdminORepresentant(serveiCodi);
				AlertHelper.success(
						request, 
						getMessage(
								request,
								"procediment.controller.permis.atorgat",
								new Object[] {
										servei.getDescripcio(),
										usuariNom}));
				return "redirect:../../permis";
			} else {
				AlertHelper.error(
						request, 
						getMessage(
								request,
								"procediment.controller.procediment.no.existeix"));
				return "redirect:../../../../procediment";
			}
		} else {
			AlertHelper.error(
					request, 
					getMessage(
							request,
							"procediment.controller.no.entitat.seleccionada"));
			return "redirect:../../../../../index";
		}
	}

	@RequestMapping(value = "/{procedimentId}/servei/{serveiCodi}/permis/{usuariCodi}/deny", method = RequestMethod.GET)
	public String serveiPermisDeny(
			HttpServletRequest request,
			@PathVariable Long procedimentId,
			@PathVariable String serveiCodi,
			@PathVariable String usuariCodi,
			Model model) throws ProcedimentNotFoundException, ProcedimentServeiNotFoundException, EntitatUsuariNotFoundException, ServeiNotFoundException {
		if (!EntitatHelper.isRepresentantEntitatActual(request))
			return "representantNoAutoritzat";
		EntitatDto entitat = EntitatHelper.getEntitatActual(request, entitatService);
		if (entitat != null) {
			ProcedimentDto procediment = null;
			if (procedimentId != null)
				procediment = procedimentService.findById(procedimentId);
			if (procediment != null) {
				procedimentService.serveiPermisDeny(procedimentId, serveiCodi, usuariCodi);
				String usuariNom = usuariCodi;
				for (EntitatUsuariDto usuari: entitat.getUsuaris()) {
					if (usuari.getUsuari().getCodi().equalsIgnoreCase(usuariCodi)) {
						usuariNom = usuari.getUsuari().getDescripcio();
						break;
					}
				}
				ServeiDto servei = serveiService.findAmbCodiPerAdminORepresentant(serveiCodi);
				AlertHelper.success(
						request, 
						getMessage(
								request,
								"procediment.controller.permis.denegat",
								new Object[] {
										servei.getDescripcio(),
										usuariNom}));
				return "redirect:../../permis";
			} else {
				AlertHelper.error(
						request, 
						getMessage(
								request,
								"procediment.controller.procediment.no.existeix"));
				return "redirect:../../../../procediment";
			}
		} else {
			AlertHelper.error(
					request, 
					getMessage(
							request,
							"procediment.controller.no.entitat.seleccionada"));
			return "redirect:../../../../../index";
		}
	}

	@ResponseBody
	@RequestMapping(value = "/{procedimentId}/servei/{serveiCodi}/procedimentCodi", method = RequestMethod.GET)
	public String putProcedimentCodi(
			HttpServletRequest request,
			HttpServletResponse response,
			@PathVariable Long procedimentId,
			@PathVariable String serveiCodi,
			@RequestParam String procedimentCodi,
			Model model) throws IOException, EntitatNotFoundException, ProcedimentNotFoundException, ProcedimentServeiNotFoundException, EntitatUsuariNotFoundException, ServeiNotFoundException {
		
		if (!EntitatHelper.isRepresentantEntitatActual(request))
			return "representantNoAutoritzat";
		EntitatDto entitat = EntitatHelper.getEntitatActual(request, entitatService);
		if (entitat == null) {
			return "false";
		}
		
		ProcedimentDto procediment = null;
		if (procedimentId != null)
			procediment = procedimentService.findById(procedimentId);
		if (procediment != null) {
			procedimentService.putProcedimentCodi(procedimentId, serveiCodi, procedimentCodi);
			return "true";
		} else {
			return "false";
		}
	}

	private void omplirModelFiltreDataTable(
			HttpServletRequest request,
			EntitatDto entitat,
			Model model) throws Exception {
		ProcedimentFiltreCommand command = (ProcedimentFiltreCommand)RequestSessionHelper.obtenirObjecteSessio(
				request,
				SESSION_ATTRIBUTE_FILTRE);
		if (command == null)
			command = new ProcedimentFiltreCommand();
		model.addAttribute(command);
		
		model.addAttribute(
				"propertyEsborrar",
				propertyService.get(
						"es.caib.pinbal.procediment.accio.esborrar.activa"));
	}

	private void setCommandFiltreServeis(
			HttpServletRequest request,
			Model model) throws Exception {
		ServeiFiltreCommand command = (ServeiFiltreCommand) RequestSessionHelper.obtenirObjecteSessio( 
				request, 
				getSessionAttributeFiltreProcediment());
		if (command == null) {
			command = new ServeiFiltreCommand();
			command.setActiva(true);
		}
		model.addAttribute(command);
	}

	private void fillFormModel(Long entitatId, Model model) {
		model.addAttribute("organsGestors", entitatService.getOrgansGestors(entitatId));
		model.addAttribute(
				"procedimentClaseTramiteOptions",
				EnumHelper.getOptionsForEnum(
						ProcedimentClaseTramiteEnumDto.class,
						"procediment.form.camp.claseTramite.enum."));
		model.addAttribute(
				"procedimentAutomatizadoOptions",
				EnumHelper.getOptionsForArray(
						new String[] {"true", "false"},
						new String[] {"comu.si", "comu.no"}));
	}

}
