/**
 * 
 */
package es.caib.pinbal.webapp.controller;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
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

import es.caib.pinbal.core.dto.ClauPrivadaDto;
import es.caib.pinbal.core.service.ScspService;
import es.caib.pinbal.core.service.exception.ClauPrivadaNotFoundException;
import es.caib.pinbal.core.service.exception.ParamConfNotFoundException;
import es.caib.pinbal.webapp.command.ClauPrivadaCommand;
import es.caib.pinbal.webapp.command.ParamConfCommand;
import es.caib.pinbal.webapp.common.AlertHelper;

/**
 * Controlador per al manteniment de claus privades.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/scsp/clauprivada")
public class ClauPrivadaController extends BaseController {
	
	@Autowired
	private ScspService scspService;
	
	
	@RequestMapping(method = RequestMethod.GET)
	public String get(
			HttpServletRequest request,
			Model model) throws Exception {
		
		model.addAttribute( "llistaClausPrivades", scspService.findAllClauPrivada());
		
		return "clauPrivadaList";
	}
	
	@RequestMapping(value = "/new", method = RequestMethod.GET)
	public String get(Model model) {
		
		model.addAttribute( new ClauPrivadaCommand() );
		model.addAttribute( "organismes", scspService.findAllOrganismeCessionari());
		
		return "clauPrivadaForm";
	}
	
	@RequestMapping(value = "/{clauPrivadaId:.+}", method = RequestMethod.GET)
	public String get(
			@PathVariable Long clauPrivadaId,
			Model model) {
		
		ClauPrivadaDto dto = null;
		if (clauPrivadaId != null)
			dto = scspService.findClauPrivadaById(clauPrivadaId);
		
		if (dto != null)
			model.addAttribute( ClauPrivadaCommand.asCommand(dto) );
		else
			model.addAttribute( new ClauPrivadaCommand() );
		
		model.addAttribute( "organismes", scspService.findAllOrganismeCessionari());
		
		return "clauPrivadaForm";
	}
	
	@RequestMapping(value = "/save", method = RequestMethod.POST)
	public String save(
			HttpServletRequest request,
			Model model,
			@Valid ClauPrivadaCommand command,
			BindingResult bindingResult) throws ClauPrivadaNotFoundException {
		
		if (bindingResult.hasErrors()) {
			model.addAttribute( "organismes", scspService.findAllOrganismeCessionari());
			return "clauPrivadaForm";
		}
		
		if (command.getId() == null) {
			scspService.createClauPrivada(ClauPrivadaCommand.asDto(command));
			AlertHelper.success(
					request, 
					getMessage(
							request, 
							"clau.privada.controller.creat.ok"));
		} else {
			scspService.updateClauPrivada(ClauPrivadaCommand.asDto(command));
			AlertHelper.success(
					request, 
					getMessage(
							request, 
							"clau.privada.controller.modificat.ok"));
		}
		
		return "redirect:./";
	}
	
	@RequestMapping(value = "/{clauPrivadaId:.+}/delete", method = RequestMethod.GET)
	public String delete(
			HttpServletRequest request,
			@PathVariable Long clauPrivadaId) throws ClauPrivadaNotFoundException {
		
		scspService.deleteClauPrivada(clauPrivadaId);
		
		AlertHelper.success(
				request, 
				getMessage(
						request, 
						"paramconf.controller.esborrat.ok"));
		
		return "redirect:../";
	}
	
	@InitBinder
	public void initBinder(WebDataBinder binder) {
		binder.registerCustomEditor(Date.class, new CustomDateEditor(new SimpleDateFormat("dd/MM/yyyy"), true));
	}
	
}
