/**
 * 
 */
package es.caib.pinbal.webapp.controller;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import es.caib.pinbal.core.dto.ArxiuDto;
import es.caib.pinbal.core.dto.ConsultaDto;
import es.caib.pinbal.core.dto.EntitatDto;
import es.caib.pinbal.core.dto.OrdreDto;
import es.caib.pinbal.core.dto.OrdreDto.OrdreDireccio;
import es.caib.pinbal.core.dto.PaginaLlistatDto;
import es.caib.pinbal.core.dto.PaginacioAmbOrdreDto;
import es.caib.pinbal.core.service.ConsultaService;
import es.caib.pinbal.core.service.EntitatService;
import es.caib.pinbal.core.service.ProcedimentService;
import es.caib.pinbal.core.service.ServeiService;
import es.caib.pinbal.core.service.UsuariService;
import es.caib.pinbal.core.service.exception.ConsultaNotFoundException;
import es.caib.pinbal.core.service.exception.ScspException;
import es.caib.pinbal.core.service.exception.ServeiNotFoundException;
import es.caib.pinbal.webapp.command.ConsultaFiltreCommand;
import es.caib.pinbal.webapp.common.AlertHelper;
import es.caib.pinbal.webapp.common.EntitatHelper;
import es.caib.pinbal.webapp.common.RequestSessionHelper;
import es.caib.pinbal.webapp.jmesa.JMesaGridHelper;
import es.caib.pinbal.webapp.jmesa.JMesaGridHelper.ConsultaPagina;

/**
 * Controlador per a la pàgina de consultes múltiples.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/consulta/multiple")
public class ConsultaMultipleController extends BaseController {

	public static final String SESSION_ATTRIBUTE_FILTRE = "ConsultaMultipleController.session.filtre";

	@Autowired
	private EntitatService entitatService;
	@Autowired
	private ProcedimentService procedimentService;
	@Autowired
	private ServeiService serveiService;
	@Autowired
	private ConsultaService consultaService;
	@Autowired
	private UsuariService usuariService;

	@Autowired(required = true)
	private javax.validation.Validator validator;

	@RequestMapping(method = RequestMethod.GET)
	public String get(
			HttpServletRequest request,
			Model model) throws Exception {
		if (!EntitatHelper.isDelegatEntitatActual(request))
			return "delegatNoAutoritzat";
		EntitatDto entitat = EntitatHelper.getEntitatActual(request, entitatService);
		if (entitat != null) {
			omplirModelPerMostrarLlistat(request, entitat, model);
		}
		return "consultaMultiple";
	}

	@RequestMapping(method = RequestMethod.POST)
	public String post(
			HttpServletRequest request,
			@Valid ConsultaFiltreCommand command,
			BindingResult bindingResult,
			Model model) throws Exception {
		if (!EntitatHelper.isDelegatEntitatActual(request))
			return "delegatNoAutoritzat";
		EntitatDto entitat = EntitatHelper.getEntitatActual(request, entitatService);
		if (entitat != null) {
			if (bindingResult.hasErrors()) {
				omplirModelPerMostrarLlistat(request, entitat, model);
			} else {
				RequestSessionHelper.actualitzarObjecteSessio(
						request,
						SESSION_ATTRIBUTE_FILTRE,
						command);
				return "redirect:multiple";
			}
		}
		return "consultaMultiple";
	}

	@RequestMapping(value = "/{consultaId}", method = RequestMethod.GET)
	public String info(
			HttpServletRequest request,
			@PathVariable Long consultaId,
			Model model) throws ConsultaNotFoundException, ScspException, ServeiNotFoundException {
		if (!EntitatHelper.isDelegatEntitatActual(request))
			return "delegatNoAutoritzat";
		EntitatDto entitat = EntitatHelper.getEntitatActual(request, entitatService);
		if (entitat != null) {
			ConsultaDto consulta = consultaService.findOneDelegat(consultaId);
			model.addAttribute("consulta", consulta);
			model.addAttribute("filles", consultaService.findAmbPare(consultaId));
			model.addAttribute(
					"servei",
					serveiService.findAmbCodiPerDelegat(
							entitat.getId(),
							consulta.getServeiCodi()));
			return "consultaMultipleInfo";
		} else {
			AlertHelper.error(
					request,
					getMessage(
							request, 
							"comu.error.no.entitat"));
			return "redirect:../../../index";
		}
	}

	@RequestMapping(value = "/{consultaId}/justificantpdf", method = RequestMethod.GET)
	public String justificantPdf(
			HttpServletRequest request,
			HttpServletResponse response,
			@PathVariable Long consultaId,
			Model model) throws ConsultaNotFoundException {
		if (!EntitatHelper.isDelegatEntitatActual(request))
			return "delegatNoAutoritzat";
		EntitatDto entitat = EntitatHelper.getEntitatActual(request, entitatService);
		if (entitat != null) {
			try {
				ArxiuDto arxiu = consultaService.obtenirJustificantMultipleConcatenat(consultaId);
				writeFileToResponse(
						arxiu.getNom(),
						arxiu.getContingut(),
						response);
				return null;
			} catch (ConsultaNotFoundException ex) {
				throw ex;
			} catch (Exception ex) {
				AlertHelper.error(
						request,
						getMessage(
								request, 
								"consulta.controller.justificant.error") + ": " + ex.getMessage());
				return "redirect:../../consulta";
			}
		} else {
			AlertHelper.error(
					request,
					getMessage(
							request, 
							"comu.error.no.entitat"));
			return "redirect:../../index";
		}
	}

	@RequestMapping(value = "/{consultaId}/justificantzip", method = RequestMethod.GET)
	public String justificantZip(
			HttpServletRequest request,
			HttpServletResponse response,
			@PathVariable Long consultaId,
			Model model) throws ConsultaNotFoundException {
		if (!EntitatHelper.isDelegatEntitatActual(request))
			return "delegatNoAutoritzat";
		EntitatDto entitat = EntitatHelper.getEntitatActual(request, entitatService);
		if (entitat != null) {
			try {
				ArxiuDto arxiu = consultaService.obtenirJustificantMultipleZip(consultaId);
				writeFileToResponse(
						arxiu.getNom(),
						arxiu.getContingut(),
						response);
				return null;
			} catch (ConsultaNotFoundException ex) {
				throw ex;
			} catch (Exception ex) {
				AlertHelper.error(
						request,
						getMessage(
								request, 
								"consulta.controller.justificant.error") + ": " + ex.getMessage());
				return "redirect:../../consulta";
			}
		} else {
			AlertHelper.error(
					request,
					getMessage(
							request, 
							"comu.error.no.entitat"));
			return "redirect:../../index";
		}
	}

	@InitBinder
	public void initBinder(WebDataBinder binder) {
	    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
	    dateFormat.setLenient(false);
	    binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
	}



	private void omplirModelPerMostrarLlistat(
			HttpServletRequest request,
			EntitatDto entitat,
			Model model) throws Exception {
		ConsultaFiltreCommand command = (ConsultaFiltreCommand)RequestSessionHelper.obtenirObjecteSessio(
				request,
				SESSION_ATTRIBUTE_FILTRE);
		if (command == null)
			command = new ConsultaFiltreCommand();
		model.addAttribute(
				"filtreCommand",
				command);
		List<?> paginaConsultes = JMesaGridHelper.consultarPaginaIActualitzarLimit(
				"consultes",
				request,
				new ConsultaPaginaConsulta(
						consultaService,
						entitat,
						command),
				new OrdreDto("creacioData", OrdreDireccio.DESCENDENT));
		model.addAttribute(
				"consultes",
				paginaConsultes);
		model.addAttribute(
				"procediments",
				procedimentService.findAmbEntitatPerDelegat(entitat.getId()));
		model.addAttribute(
				"serveis",
				serveiService.findPermesosAmbProcedimentPerDelegat(
						entitat.getId(),
						command.getProcediment()));
	}

	public class ConsultaPaginaConsulta implements ConsultaPagina<ConsultaDto> {
		ConsultaService consultaService;
		EntitatDto entitat;
		ConsultaFiltreCommand command;
		public ConsultaPaginaConsulta(
				ConsultaService consultaService,
				EntitatDto entitat,
				ConsultaFiltreCommand command) {
			this.consultaService = consultaService;
			this.entitat = entitat;
			this.command = command;
		}
		public PaginaLlistatDto<ConsultaDto> consultar(
				PaginacioAmbOrdreDto paginacioAmbOrdre) throws Exception {
			return consultaService.findMultiplesByFiltrePaginatPerDelegat(
					entitat.getId(),
					ConsultaFiltreCommand.asDto(command),
					paginacioAmbOrdre);
		}
	}

}
