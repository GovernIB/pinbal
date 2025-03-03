/**
 * 
 */
package es.caib.pinbal.webapp.controller;

import es.caib.pinbal.core.dto.IntegracioAccioDto;
import es.caib.pinbal.core.dto.IntegracioDto;
import es.caib.pinbal.core.dto.IntegracioEnumDto;
import es.caib.pinbal.core.service.ConfigService;
import es.caib.pinbal.core.service.IntegracioAccioService;
import es.caib.pinbal.webapp.command.IntegracioFiltreCommand;
import es.caib.pinbal.webapp.common.AlertHelper;
import es.caib.pinbal.webapp.common.RequestSessionHelper;
import es.caib.pinbal.webapp.datatables.DatatablesHelper;
import es.caib.pinbal.webapp.datatables.DatatablesHelper.DatatablesResponse;
import es.caib.pinbal.webapp.helper.EnumHelper;
import es.caib.pinbal.webapp.helper.MissatgesHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Controlador per a la consulta d'accions de les integracions.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/integracio")
public class IntegracioController extends BaseController {

	private static final String SESSION_ATTRIBUTE_FILTRE = "IntegracioController.session.filtre";
	public static final String FORMAT_DATA_DADES_ESPECIFIQUES = "dd/MM/yyyy";

	@InitBinder
	public void initBinder(WebDataBinder binder) {
	    SimpleDateFormat dateFormat = new SimpleDateFormat(FORMAT_DATA_DADES_ESPECIFIQUES);
	    dateFormat.setLenient(false);
	    binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
	}
	
//	@Autowired
//	private AplicacioService aplicacioService;
	
	@Autowired
	private ConfigService configService;
	
	@Autowired
	private IntegracioAccioService integracioAccioService;

	@RequestMapping(method = RequestMethod.GET)
	public String get(
			HttpServletRequest request,
			Model model) {
		return getAmbCodi(request, null, model);
	}

	@RequestMapping(value = "/{codi}", method = RequestMethod.GET)
	public String getAmbCodi(
			HttpServletRequest request,
			@PathVariable String codi,
			Model model) {
		
		IntegracioFiltreCommand filtreCommand = getFiltreCommand(request);
		
		List<IntegracioDto> integracions = integracioAccioService.getAll();
		for (IntegracioDto integracio: integracions) {
			for (IntegracioEnumDto integracioEnum: IntegracioEnumDto.values()) {
				if (integracio.getCodi() == integracioEnum.name()) {
					integracio.setNom(
							EnumHelper.getOneOptionForEnum(
									IntegracioEnumDto.class,
									"integracio.list.pipella." + integracio.getCodi()).getText());
				}
			}
		}
		model.addAttribute("integracions", integracions);

		if (codi != null) {
			filtreCommand.setCodi(codi);
			RequestSessionHelper.actualitzarObjecteSessio(
					request,
					SESSION_ATTRIBUTE_FILTRE,
					filtreCommand);
		} else if (integracions.size() > 0) {
			filtreCommand.setCodi(integracions.get(0).getCodi());
			RequestSessionHelper.actualitzarObjecteSessio(
					request,
					SESSION_ATTRIBUTE_FILTRE,
					filtreCommand);
		}
		model.addAttribute("integracioFiltreCommand",filtreCommand);
		model.addAttribute(
				"codiActual",
				RequestSessionHelper.obtenirObjecteSessio(
						request,
						SESSION_ATTRIBUTE_FILTRE));
		return "integracioList";
	}
	
	@RequestMapping(value = "/{codi}", method = RequestMethod.POST)
	public String getAmbCodiPost(
			HttpServletRequest request, 
			@PathVariable String codi,
			@Valid IntegracioFiltreCommand integracioFiltreCommand, 
			BindingResult bindingResult, 
			@RequestParam(value = "accio", required = false) String accio, 
			Model model) {
		if ("netejar".equals(accio)) {
			RequestSessionHelper.esborrarObjecteSessio(
					request, 
					SESSION_ATTRIBUTE_FILTRE);
		} else {
			if (!bindingResult.hasErrors()) {
				RequestSessionHelper.actualitzarObjecteSessio(
						request, 
						SESSION_ATTRIBUTE_FILTRE, 
						integracioFiltreCommand);				
			}
		}
		return "redirect:/integracio/" + codi;
	}

	@RequestMapping(value = "/datatable", method = RequestMethod.GET)
	@ResponseBody
	public DatatablesResponse datatable(
			HttpServletRequest request) {		
		IntegracioFiltreCommand filtreCommand = getFiltreCommand(request);
		DatatablesResponse dtr = DatatablesHelper.getDatatableResponse(
				request,
				integracioAccioService.findPaginat(
						DatatablesHelper.getPaginacioDtoFromRequest(request),	
						IntegracioFiltreCommand.asDto(filtreCommand)
				)				
		);
		return dtr;
	}
	
	private IntegracioFiltreCommand getFiltreCommand(
			HttpServletRequest request) {
		IntegracioFiltreCommand filtreCommand = new IntegracioFiltreCommand();
		try {
			filtreCommand = (IntegracioFiltreCommand) RequestSessionHelper.obtenirObjecteSessio(
					request,
					SESSION_ATTRIBUTE_FILTRE);
		}catch (Exception e) {
			RequestSessionHelper.actualitzarObjecteSessio(
					request,
					SESSION_ATTRIBUTE_FILTRE,
					filtreCommand);
		}
		
		if (filtreCommand == null) {
			filtreCommand = new IntegracioFiltreCommand();
			RequestSessionHelper.actualitzarObjecteSessio(
					request,
					SESSION_ATTRIBUTE_FILTRE,
					filtreCommand);
		}
		return filtreCommand;
	}

	@RequestMapping(value = "/{codi}/{id}", method = RequestMethod.GET)
	public String detall(
			HttpServletRequest request,
			@PathVariable String codi,
			@PathVariable Long id,
			Model model) {
		
		// TODO: Obtenir les integracions per codi a nivell de bbdd
		IntegracioAccioDto integracio = integracioAccioService.findById(id);
		if (integracio != null) {
			model.addAttribute(
					"integracio",
					integracio);
			model.addAttribute(
					"codiActual", 
					codi);
			return "integracioDetall";
			
		} else {
			AlertHelper.error(
					request,
					getMessage(
							request, 
							"integracio.list.no.existeix"));
			
			return "redirect:../../integracio/" + codi;
		}
	}
	
	@ResponseBody
	@RequestMapping(value = "/{codi}/esborrar", method = RequestMethod.GET)
	public String esborrar(
			HttpServletRequest request,
			@PathVariable String codi,
			Model model) {
		int n = 0;
		try {
			n = integracioAccioService.delete(codi);			
			MissatgesHelper.success(
					request, 
					getMessage(
							request, 
							"integracio.esborrar.success",
							new Object[] {n, codi}));
		} catch (Exception e) {
			String errMsg = getMessage(
					request,
					"integracio.esborrar.error",
					new Object[] {codi, e.getMessage()});
			logger.error(errMsg, e);
			MissatgesHelper.error(request, errMsg);
		}
		
		return "redirect:/integracio/" + codi;
	}
	
//	/** Mètode per consultar les integracions i els errors.*/
//	@ResponseBody
//	@RequestMapping(value = "integracions", method = RequestMethod.GET)
//	public List<IntegracioDto> getIntegracionsIErrors(String numeroHoresPropietat) {
//		List<IntegracioDto> integracions = integracioAccioService.integracioFindAll();
//		if (numeroHoresPropietat == null) {
//			numeroHoresPropietat = configService.getTempsErrorsMonitorIntegracio();
//		}
//		int numeroHores = Integer.parseInt(numeroHoresPropietat != null ? numeroHoresPropietat : "48");
//
//		// Consulta el número d'errors per codi d'integracio
//		Map<String, Integer> errors = integracioAccioService.countErrors(numeroHores);
//
//		for (IntegracioDto integracio: integracions) {
//			for (IntegracioEnumDto integracioEnum: IntegracioEnumDto.values()) {
//				if (integracio.getCodi() == integracioEnum.name()) {
//					integracio.setNom(
//							EnumHelper.getOneOptionForEnum(
//									IntegracioEnumDto.class,
//									"integracio.list.pipella." + integracio.getCodi()).getText());
//				}
//			}
//			if (errors.containsKey(integracio.getCodi())) {
//				integracio.setNumErrors(errors.get(integracio.getCodi()).intValue());
//			}
//		}
//		return integracions;
//	}
	
	private static final Logger logger = LoggerFactory.getLogger(IntegracioController.class);

}
