/**
 * 
 */
package es.caib.pinbal.webapp.controller;

import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import es.caib.pinbal.core.dto.EntitatDto;
import es.caib.pinbal.core.dto.EntitatUsuariDto;
import es.caib.pinbal.core.dto.OrdreDto;
import es.caib.pinbal.core.dto.PaginaLlistatDto;
import es.caib.pinbal.core.dto.PaginacioAmbOrdreDto;
import es.caib.pinbal.core.dto.UsuariDto;
import es.caib.pinbal.core.dto.OrdreDto.OrdreDireccio;
import es.caib.pinbal.core.service.EntitatService;
import es.caib.pinbal.core.service.UsuariService;
import es.caib.pinbal.core.service.exception.EntitatNotFoundException;
import es.caib.pinbal.core.service.exception.EntitatUsuariNotFoundException;
import es.caib.pinbal.core.service.exception.UsuariExternNotFoundException;
import es.caib.pinbal.webapp.command.EntitatFiltreCommand;
import es.caib.pinbal.webapp.command.EntitatUsuariCommand;
import es.caib.pinbal.webapp.command.EntitatUsuariCommand.Existent;
import es.caib.pinbal.webapp.command.EntitatUsuariCommand.TipusCodi;
import es.caib.pinbal.webapp.command.EntitatUsuariCommand.TipusNif;
import es.caib.pinbal.webapp.command.UsuariFiltreCommand;
import es.caib.pinbal.webapp.common.AlertHelper;
import es.caib.pinbal.webapp.common.RequestSessionHelper;
import es.caib.pinbal.webapp.common.ValidationHelper;
import es.caib.pinbal.webapp.controller.EntitatController.ConsultaPaginaEntitat;
import es.caib.pinbal.webapp.jmesa.JMesaGridHelper;
import es.caib.pinbal.webapp.jmesa.JMesaGridHelper.ConsultaPagina;

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

	@RequestMapping(value = "/{entitatId}/usuari/save", method = RequestMethod.POST)
	public String usuariSave(
			HttpServletRequest request,
			@PathVariable Long entitatId,
			@Valid EntitatUsuariCommand command,
			BindingResult bindingResult,
			Model model) throws Exception {
		EntitatDto entitat = entitatService.findById(entitatId);
		if (entitat != null) {
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
				return "entitatUsuaris";
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
								"entitat.controller.usuari.actualitzat",
								new Object[] {nomUsuari}));
			} catch (UsuariExternNotFoundException ex) {
				AlertHelper.error(
						request,
						getMessage(
								request, 
								"entitat.controller.usuari.extern.no.existeix"));
			}
			return "redirect:../usuari";
		} else {
			AlertHelper.error(
					request, 
					getMessage(
							request, 
							"entitat.controller.entitat.no.existeix"));
			return "redirect:../../../entitat";
		}
	}

	@RequestMapping(value = "/{entitatId}/usuari/{usuariCodi}/principal", method = RequestMethod.GET)
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
			return "redirect:../../usuari";
		} else {
			AlertHelper.error(
					request, 
					getMessage(
							request, 
							"entitat.controller.entitat.no.existeix"));
			return "redirect:../../../../entitat";
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
		
		List<?> paginaUsuaris = JMesaGridHelper.consultarPaginaIActualitzarLimit(
				"usuaris",
				request,
				new ConsultaPaginaEntitat(
						usuariService,
						command),
				new OrdreDto("usuari.nom", OrdreDireccio.DESCENDENT));
		
		model.addAttribute("entitat", entitat);
		model.addAttribute(new EntitatUsuariCommand(entitat.getId()));
		model.addAttribute("usuaris", paginaUsuaris);
	}
	public class ConsultaPaginaEntitat implements ConsultaPagina<EntitatUsuariDto> {
		UsuariService usuariService;
		UsuariFiltreCommand command;
		public ConsultaPaginaEntitat(
				UsuariService usuariService,
				UsuariFiltreCommand command) {
			this.usuariService = usuariService;
			this.command = command;
		}
		public PaginaLlistatDto<EntitatUsuariDto> consultar(
				PaginacioAmbOrdreDto paginacioAmbOrdre) throws Exception {
			return usuariService.findAmbFiltrePaginat(
					command.getEntitat().getId(),
					command.getIsRepresentant(),
					command.getIsDelegat(),
					command.getIsAuditor(),
					command.getIsAplicacio(),
					command.getCodi(),
					command.getNom(),
					command.getNif(),
					command.getDepartament(),					
					paginacioAmbOrdre);
		}
	}
}
