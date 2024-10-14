/**
 * 
 */
package es.caib.pinbal.webapp.controller;

import es.caib.pinbal.core.dto.CodiValor;
import es.caib.pinbal.core.dto.ConsultaDto;
import es.caib.pinbal.core.dto.EntitatDto;
import es.caib.pinbal.core.dto.FitxerDto;
import es.caib.pinbal.core.dto.UsuariDto;
import es.caib.pinbal.core.service.ConsultaService;
import es.caib.pinbal.core.service.EntitatService;
import es.caib.pinbal.core.service.HistoricConsultaService;
import es.caib.pinbal.core.service.ProcedimentService;
import es.caib.pinbal.core.service.ServeiService;
import es.caib.pinbal.core.service.UsuariService;
import es.caib.pinbal.core.service.exception.ConsultaNotFoundException;
import es.caib.pinbal.core.service.exception.EntitatNotFoundException;
import es.caib.pinbal.core.service.exception.ScspException;
import es.caib.pinbal.core.service.exception.ServeiNotFoundException;
import es.caib.pinbal.webapp.command.ConsultaFiltreCommand;
import es.caib.pinbal.webapp.common.AlertHelper;
import es.caib.pinbal.webapp.common.EntitatHelper;
import es.caib.pinbal.webapp.common.RequestSessionHelper;
import es.caib.pinbal.webapp.datatables.ServerSideColumn;
import es.caib.pinbal.webapp.datatables.ServerSideRequest;
import es.caib.pinbal.webapp.datatables.ServerSideResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.naming.NamingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Controlador per a la pàgina de consultes múltiples.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/consulta/multiple")
public class ConsultaMultipleController extends BaseController {

	public static final String SESSION_ATTRIBUTE_FILTRE = "ConsultaMultipleController.session.filtre";
	public static final String SESSION_CONSULTA_HISTORIC = "consulta_multiple";

	@Autowired
	private EntitatService entitatService;
	@Autowired
	private ProcedimentService procedimentService;
	@Autowired
	private ServeiService serveiService;
	@Autowired
	private ConsultaService consultaService;
	@Autowired
	private HistoricConsultaService historicConsultaService;
	@Autowired
	private UsuariService usuariService;

	@RequestMapping(method = RequestMethod.GET)
	public String get(
			HttpServletRequest request,
			Model model) throws Exception {
		if (!EntitatHelper.isDelegatEntitatActual(request))
			return "delegatNoAutoritzat";
		EntitatDto entitat = EntitatHelper.getEntitatActual(request, entitatService);
		if (entitat != null) {
			omplirModelPerFiltreTaula(request, entitat, model);
		}
		getOrigens(model);
		return "consultaMultiple";
	}
	private void getOrigens(Model model) {
		List<CodiValor> origens = new ArrayList<>();
		origens.add(new CodiValor("true", "admin.consulta.list.filtre.origen.recobriment"));
		origens.add(new CodiValor("false", "admin.consulta.list.filtre.origen.web"));
		model.addAttribute("origens", origens);
	}


	@RequestMapping(method = RequestMethod.POST)
	public String post(
			HttpServletRequest request,
			@Valid ConsultaFiltreCommand command,
			BindingResult bindingResult,
			@RequestParam(value = "accio", required = false) String accio,
			Model model) throws Exception {
		if (!EntitatHelper.isDelegatEntitatActual(request))
			return "delegatNoAutoritzat";
		if ("netejar".equals(accio)) {
			RequestSessionHelper.esborrarObjecteSessio(
					request,
					SESSION_ATTRIBUTE_FILTRE);
			return "redirect:multiple";
		} else {
			EntitatDto entitat = EntitatHelper.getEntitatActual(request, entitatService);
			if (entitat != null) {
				if (bindingResult.hasErrors()) {
					omplirModelPerFiltreTaula(request, entitat, model);
				} else {
					RequestSessionHelper.actualitzarObjecteSessio(
							request,
							SESSION_ATTRIBUTE_FILTRE,
							command);
					return "redirect:multiple";
				}
			}
		}
		return "consultaMultiple";
	}

	@RequestMapping(value = "/datatable", produces="application/json", method = RequestMethod.GET)
	@ResponseBody
	public ServerSideResponse<ConsultaDto, Long> datatable(HttpServletRequest request, Model model)
	      throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, NamingException,
	      SQLException, EntitatNotFoundException {
		String error = null;
		ServerSideRequest serverSideRequest = new ServerSideRequest(request);
		if (!EntitatHelper.isDelegatEntitatActual(request)) {
			error = "Delegat no autoritzat";
		}
			
		EntitatDto entitat = EntitatHelper.getEntitatActual(request, entitatService);
		if (entitat == null) {
			throw new EntitatNotFoundException();
		}
		
		ConsultaFiltreCommand command = (ConsultaFiltreCommand)RequestSessionHelper.obtenirObjecteSessio(
				request,
				SESSION_ATTRIBUTE_FILTRE);
		if (command == null) {
			command = new ConsultaFiltreCommand();
			command.filtrarDarrersMesos(isHistoric(request) ? 9 : 3);
		} else {
			command.updateDefaultDataInici(isHistoric(request));
		}
		
		Page<ConsultaDto> page;
		ServerSideResponse<ConsultaDto, Long> response = null;
		if (error != null) {
			List<ConsultaDto> lista = new ArrayList<ConsultaDto>();
			page = new PageImpl<ConsultaDto>(lista, serverSideRequest.toPageable(), lista.size());
			response = new ServerSideResponse<ConsultaDto, Long>(serverSideRequest, page);
			response.setError(error);

		}else { 
			List<ServerSideColumn> cols = serverSideRequest.getColumns();
			cols.get(1).setData("data");
			cols.get(2).setData("procedimentCodi");
			cols.get(3).setData("serveiCodi");

			if (isHistoric(request)) {
				page = historicConsultaService.findMultiplesByFiltrePaginatPerDelegat(
						entitat.getId(),
						ConsultaFiltreCommand.asDto(command),
						serverSideRequest.toPageable());
			} else {
				page = consultaService.findMultiplesByFiltrePaginatPerDelegat(
						entitat.getId(),
						ConsultaFiltreCommand.asDto(command),
						serverSideRequest.toPageable());
			}
			cols.get(1).setData("creacioData");
			cols.get(2).setData("procedimentCodiNom");
			cols.get(3).setData("serveiCodiNom");

			response = new ServerSideResponse<ConsultaDto, Long>(serverSideRequest, page);
		}
		
		return response;
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
			ConsultaDto consulta = getConsultaDelegate(consultaId, isHistoric(request), model);
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
				FitxerDto fitxer = getJustificantMultiplePdf(consultaId, isHistoric(request));
				writeFileToResponse(
						fitxer.getNom(),
						fitxer.getContingut(),
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
				return "redirect:../../../consulta/multiple";
			}
		} else {
			AlertHelper.error(
					request,
					getMessage(
							request, 
							"comu.error.no.entitat"));
			return "redirect:../../../index";
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
				FitxerDto fitxer = getJustificantMultipleZip(consultaId, isHistoric(request));
				writeFileToResponse(
						fitxer.getNom(),
						fitxer.getContingut(),
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
				return "redirect:../../../consulta/multiple";
			}
		} else {
			AlertHelper.error(
					request,
					getMessage(
							request, 
							"comu.error.no.entitat"));
			return "redirect:../../../index";
		}
	}

	@InitBinder
	public void initBinder(WebDataBinder binder) {
	    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
	    dateFormat.setLenient(false);
	    binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
	}



	private void omplirModelPerFiltreTaula(
			HttpServletRequest request,
			EntitatDto entitat,
			Model model) throws Exception {
		ConsultaFiltreCommand command = (ConsultaFiltreCommand)RequestSessionHelper.obtenirObjecteSessio(
				request,
				SESSION_ATTRIBUTE_FILTRE);
		if (command == null) {
			command = new ConsultaFiltreCommand();
			command.filtrarDarrersMesos(isHistoric(request) ? 9 : 3);
			UsuariDto usuari = usuariService.getDades();
			command.setProcediment(usuari.getProcedimentId());
			command.setServei(usuari.getServeiCodi());
			RequestSessionHelper.actualitzarObjecteSessio(
					request,
					SESSION_ATTRIBUTE_FILTRE,
					command);
		} else {
			command.updateDefaultDataInici(isHistoric(request));
		}
		command.eliminarEspaisCampsCerca();
		model.addAttribute(
				"filtreCommand",
				command);
		model.addAttribute(
				"procediments",
				procedimentService.findAmbEntitatPerDelegat(entitat.getId()));
		model.addAttribute(
				"serveis",
				serveiService.findPermesosAmbProcedimentPerDelegat(
						entitat.getId(),
						command.getProcediment()));
		model.addAttribute("historic", isHistoric(request));
	}

	private boolean isHistoric(HttpServletRequest request) {
		Object historic = request.getSession().getAttribute(SESSION_CONSULTA_HISTORIC);
		if (historic == null)
			return false;
		else
			return ((Boolean) historic).booleanValue();
	}

	private ConsultaDto getConsultaDelegate(Long consultaId, boolean historic, Model model) throws ConsultaNotFoundException, ScspException {
		ConsultaDto consulta;
		List<ConsultaDto> filles;
		if (historic) {
			try {
				consulta = historicConsultaService.findOneDelegat(consultaId);
				filles = historicConsultaService.findAmbPare(consultaId);
			} catch (Exception nfe) {
				consulta = consultaService.findOneDelegat(consultaId);
				filles = consultaService.findAmbPare(consultaId);
			}
		} else {
			try {
				consulta = consultaService.findOneDelegat(consultaId);
				filles = consultaService.findAmbPare(consultaId);
			} catch (Exception nfe) {
				consulta = historicConsultaService.findOneDelegat(consultaId);
				filles = historicConsultaService.findAmbPare(consultaId);
			}
		}

		model.addAttribute("consulta", consulta);
		model.addAttribute("filles", filles);

		return consulta;
	}

	private FitxerDto getJustificantMultiplePdf(Long consultaId, boolean historic) throws Exception {
		FitxerDto fitxer;
		if (historic) {
			try {
				fitxer = historicConsultaService.obtenirJustificantMultipleConcatenat(consultaId);
			} catch (Exception nfe) {
				fitxer = consultaService.obtenirJustificantMultipleConcatenat(consultaId);
			}
		} else {
			try {
				fitxer = consultaService.obtenirJustificantMultipleConcatenat(consultaId);
			} catch (Exception nfe) {
				fitxer = historicConsultaService.obtenirJustificantMultipleConcatenat(consultaId);
			}
		}
		return fitxer;
	}

	private FitxerDto getJustificantMultipleZip(Long consultaId, boolean historic) throws Exception {
		FitxerDto fitxer;
		if (historic) {
			try {
				fitxer = historicConsultaService.obtenirJustificantMultipleZip(consultaId);
			} catch (Exception nfe) {
				fitxer = consultaService.obtenirJustificantMultipleZip(consultaId);
			}
		} else {
			try {
				fitxer = consultaService.obtenirJustificantMultipleZip(consultaId);
			} catch (Exception nfe) {
				fitxer = historicConsultaService.obtenirJustificantMultipleZip(consultaId);
			}
		}
		return fitxer;
	}
}
