/**
 * 
 */
package es.caib.pinbal.webapp.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import es.caib.pinbal.core.dto.EntitatDto;
import es.caib.pinbal.core.dto.OrdreDto;
import es.caib.pinbal.core.dto.PaginaLlistatDto;
import es.caib.pinbal.core.dto.PaginacioAmbOrdreDto;
import es.caib.pinbal.core.dto.ParamConfDto;
import es.caib.pinbal.core.dto.OrdreDto.OrdreDireccio;
import es.caib.pinbal.core.service.EntitatService;
import es.caib.pinbal.core.service.ScspService;
import es.caib.pinbal.core.service.exception.ParamConfNotFoundException;
import es.caib.pinbal.webapp.command.EntitatCommand;
import es.caib.pinbal.webapp.command.EntitatFiltreCommand;
import es.caib.pinbal.webapp.command.ParamConfCommand;
import es.caib.pinbal.webapp.common.AlertHelper;
import es.caib.pinbal.webapp.common.RequestSessionHelper;
import es.caib.pinbal.webapp.controller.EntitatController.ConsultaPaginaEntitat;
import es.caib.pinbal.webapp.jmesa.JMesaGridHelper;
import es.caib.pinbal.webapp.jmesa.JMesaGridHelper.ConsultaPagina;

/**
 * Controlador per al manteniment d'entitats.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/scsp/paramconf")
public class ParamConfController extends BaseController {
	
	@Autowired
	private ScspService scspService;
	
	
	@RequestMapping(method = RequestMethod.GET)
	public String get(
			HttpServletRequest request,
			Model model) throws Exception {
		
		model.addAttribute( "llistaParametres", scspService.findAllParamConf());
//		omplirModelPerMostrarLlistat(request, model);
		
		return "paramConfList";
	}
	
	@RequestMapping(value = "/new", method = RequestMethod.GET)
	public String get(Model model) {
		
		model.addAttribute( ParamConfCommand.commandForCreate() );
		
		return "paramConfForm";
	}
	
	@RequestMapping(value = "/{paramConfNom:.+}", method = RequestMethod.GET)
	public String get(
			@PathVariable String paramConfNom,
			Model model) {
		
		ParamConfDto dto = null;
		if (paramConfNom != null)
			dto = scspService.findParamConfByNom(paramConfNom);
		
		if (dto != null)
			model.addAttribute( ParamConfCommand.asCommandForUpdate(dto) );
		else
			model.addAttribute( ParamConfCommand.commandForCreate() );
		
		return "paramConfForm";
	}
	
	@RequestMapping(value = "/save", method = RequestMethod.POST)
	public String save(
			HttpServletRequest request,
			Model model,
			@Valid ParamConfCommand command,
			BindingResult bindingResult) throws ParamConfNotFoundException {
		
		if (bindingResult.hasErrors()) {
			return "paramConfForm";
		}
		
		if (command.isForcreate()) {
			scspService.createParamConf(ParamConfCommand.asDto(command));
			AlertHelper.success(
					request, 
					getMessage(
							request, 
							"paramconf.controller.creat.ok"));
		} else {
			scspService.updateParamConf(ParamConfCommand.asDto(command));
			AlertHelper.success(
					request, 
					getMessage(
							request, 
							"paramconf.controller.modificat.ok"));
		}
		
		return "redirect:./";
	}
	
	@RequestMapping(value = "/{paramConfNom:.+}/delete", method = RequestMethod.GET)
	public String delete(
			HttpServletRequest request,
			@PathVariable String paramConfNom) throws ParamConfNotFoundException {
		
		scspService.deleteParamConf(paramConfNom);
		
		AlertHelper.success(
				request, 
				getMessage(
						request, 
						"paramconf.controller.esborrat.ok"));
		
		return "redirect:../";
	}
	
}
