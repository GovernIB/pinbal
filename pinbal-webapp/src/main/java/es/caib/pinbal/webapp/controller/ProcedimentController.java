/**
 * 
 */
package es.caib.pinbal.webapp.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import es.caib.pinbal.core.dto.EntitatDto;
import es.caib.pinbal.core.dto.EntitatUsuariDto;
import es.caib.pinbal.core.dto.OrdreDto;
import es.caib.pinbal.core.dto.OrdreDto.OrdreDireccio;
import es.caib.pinbal.core.dto.PaginaLlistatDto;
import es.caib.pinbal.core.dto.PaginacioAmbOrdreDto;
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
import es.caib.pinbal.webapp.common.AlertHelper;
import es.caib.pinbal.webapp.common.EntitatHelper;
import es.caib.pinbal.webapp.common.RequestSessionHelper;
import es.caib.pinbal.webapp.jmesa.JMesaGridHelper;
import es.caib.pinbal.webapp.jmesa.JMesaGridHelper.ConsultaPagina;

/**
 * Controlador per al manteniment de procediments.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/procediment")
public class ProcedimentController extends BaseController {

	public static final String SESSION_ATTRIBUTE_FILTRE = "ProcedimentController.session.filtre";

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
		if (entitat != null) {
			omplirModelPerMostrarLlistat(request, entitat, model);
			return "procedimentList";
		} else {
			AlertHelper.error(
					request,
					getMessage(
							request, 
							"procediment.controller.no.entitat.seleccionada"));
			return "redirect:../index";
		}
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
		if (entitat != null) {
			if (bindingResult.hasErrors()) {
				omplirModelPerMostrarLlistat(request, entitat, model);
				return "procedimentList";
			} else {
				RequestSessionHelper.actualitzarObjecteSessio(
						request,
						SESSION_ATTRIBUTE_FILTRE,
						command);
				return "redirect:procediment";
			}
		} else {
			AlertHelper.error(
					request,
					getMessage(
							request, 
							"procediment.controller.no.entitat.seleccionada"));
			return "redirect:../index";
		}
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
			BindingResult bindingResult) throws EntitatNotFoundException, ProcedimentNotFoundException {
		if (!EntitatHelper.isRepresentantEntitatActual(request))
			return "representantNoAutoritzat";
		EntitatDto entitat = EntitatHelper.getEntitatActual(request, entitatService);
		if (entitat != null) {
			if (bindingResult.hasErrors()) {
				return "procedimentForm";
			}
			ProcedimentDto procediment = ProcedimentCommand.asDto(command);
			procediment.setEntitatId(entitat.getId());
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
		} else {
			AlertHelper.error(
					request, 
					getMessage(
							request,
							"procediment.controller.no.entitat.seleccionada"));
			return "redirect:../index";
		}
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

	@RequestMapping(value = "/{procedimentId}/servei", method = RequestMethod.GET)
	public String servei(
			HttpServletRequest request,
			@PathVariable Long procedimentId,
			Model model) throws EntitatNotFoundException {
		if (!EntitatHelper.isRepresentantEntitatActual(request))
			return "representantNoAutoritzat";
		EntitatDto entitat = EntitatHelper.getEntitatActual(request, entitatService);
		if (entitat != null) {
			ProcedimentDto procediment = null;
			if (procedimentId != null)
				procediment = procedimentService.findById(procedimentId);
			if (procediment != null) {
				model.addAttribute("entitat", entitat);
				model.addAttribute("procediment", procediment);
				
				List<ServeiDto> serveisActius = serveiService.findAmbEntitat(entitat.getId());
				for (ServeiDto servei: serveisActius) {
					for (String serveiActiu: procediment.getServeisActius()) {
						if (servei.getCodi().equals(serveiActiu)) {
							servei.setActiu(true);
							break;
						}
					}
				}
				model.addAttribute(
						"serveisActius",
						serveisActius);
				return "procedimentServeis";
			} else {
				AlertHelper.error(
						request, 
						getMessage(
								request,
								"procediment.controller.procediment.no.existeix"));
				return "redirect:../../procediment";
			}
			
		} else {
			AlertHelper.error(
					request,
					getMessage(
							request,
							"procediment.controller.no.entitat.seleccionada"));
			return "redirect:../../../index";
		}
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
		if (entitat != null) {
			ProcedimentDto procediment = null;
			if (procedimentId != null)
				procediment = procedimentService.findById(procedimentId);
			if (procediment != null) {
				if (procediment.getServeisActius().contains(serveiCodi)) {
					model.addAttribute("entitat", entitat);
					model.addAttribute("procediment", procediment);
					model.addAttribute("servei", serveiService.findAmbCodiPerAdminORepresentant(serveiCodi));
					List<String> usuarisAmbPermis = procedimentService.findUsuarisAmbPermisPerServei(procedimentId, serveiCodi);
					model.addAttribute("usuarisAmbPermis", usuarisAmbPermis);
					
					for (EntitatUsuariDto usuari: entitat.getUsuarisRepresentant()) {
						for (String usuariAmbPermis: usuarisAmbPermis) {
							if (usuari.getUsuari().getCodi().equals(usuariAmbPermis)) {
								usuari.setAcces(true);
								break;
							}
						}
					}
					
					return "procedimentServeiPermisos";
				} else {
					AlertHelper.error(
							request,
							getMessage(
									request,
									"procediment.controller.servei.no.pertany.procediment"));
					return "redirect:../../../../procediment";
				}
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



	private void omplirModelPerMostrarLlistat(
			HttpServletRequest request,
			EntitatDto entitat,
			Model model) throws Exception {
		ProcedimentFiltreCommand command = (ProcedimentFiltreCommand)RequestSessionHelper.obtenirObjecteSessio(
				request,
				SESSION_ATTRIBUTE_FILTRE);
		if (command == null)
			command = new ProcedimentFiltreCommand();
		model.addAttribute(command);
		List<?> paginaProcediments = JMesaGridHelper.consultarPaginaIActualitzarLimit(
				"procediments",
				request,
				new ConsultaPaginaProcediment(
						procedimentService,
						entitat,
						command),
				new OrdreDto("nom", OrdreDireccio.DESCENDENT));
		model.addAttribute("procediments", paginaProcediments);
		/*List<ProcedimentDto> procediments = procedimentService.findAmbEntitat(entitat.getId());
		model.addAttribute("procediments", procediments);*/
		model.addAttribute(
				"propertyEsborrar",
				propertyService.get(
						"es.caib.pinbal.procediment.accio.esborrar.activa"));
	}

	public class ConsultaPaginaProcediment implements ConsultaPagina<ProcedimentDto> {
		ProcedimentService procedimentService;
		EntitatDto entitat;
		ProcedimentFiltreCommand command;
		public ConsultaPaginaProcediment(
				ProcedimentService procedimentService,
				EntitatDto entitat,
				ProcedimentFiltreCommand command) {
			this.procedimentService = procedimentService;
			this.entitat = entitat;
			this.command = command;
		}
		public PaginaLlistatDto<ProcedimentDto> consultar(
				PaginacioAmbOrdreDto paginacioAmbOrdre) throws Exception {
			return procedimentService.findAmbFiltrePaginat(
					entitat.getId(),
					command.getCodi(),
					command.getNom(),
					command.getDepartament(),
					paginacioAmbOrdre);
		}
	}

}
