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

import es.caib.pinbal.core.dto.ClauPublicaDto;
import es.caib.pinbal.core.service.ScspService;
import es.caib.pinbal.core.service.exception.ClauPublicaNotFoundException;
import es.caib.pinbal.webapp.command.ClauPublicaCommand;
import es.caib.pinbal.webapp.common.AlertHelper;

/**
 * Controlador per al manteniment de claus públiques.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/scsp/claupublica")
public class ClauPublicaController extends BaseController {

	@Autowired
	private ScspService scspService;

	@RequestMapping(method = RequestMethod.GET)
	public String get(HttpServletRequest request, Model model) throws Exception {

		model.addAttribute("llistaClausPubliques", scspService.findAllClauPublica());

		return "clauPublicaList";
	}

	@RequestMapping(value = "/new", method = RequestMethod.GET)
	public String get(Model model) {

		model.addAttribute(new ClauPublicaCommand());

		return "clauPublicaForm";
	}

	@RequestMapping(value = "/{clauPublicaId}", method = RequestMethod.GET)
	public String get(@PathVariable Long clauPublicaId, Model model) {

		ClauPublicaDto dto = null;
		if (clauPublicaId != null)
			dto = scspService.findClauPublicaById(clauPublicaId);

		if (dto != null)
			model.addAttribute(ClauPublicaCommand.asCommand(dto));
		else
			model.addAttribute(new ClauPublicaCommand());

		return "clauPublicaForm";
	}

	@RequestMapping(value = "/save", method = RequestMethod.POST)
	public String save(HttpServletRequest request, Model model, @Valid ClauPublicaCommand command,
			BindingResult bindingResult) throws ClauPublicaNotFoundException {

		if (bindingResult.hasErrors()) {
			return "clauPublicaForm";
		}

		if (command.getId() == null) {
			scspService.createClauPublica(ClauPublicaCommand.asDto(command));
			AlertHelper.success(request, getMessage(request, "claupublica.controller.creat.ok"));
		} else {
			scspService.updateClauPublica(ClauPublicaCommand.asDto(command));
			AlertHelper.success(request, getMessage(request, "claupublica.controller.modificat.ok"));
		}

		return "redirect:./";
	}

	@RequestMapping(value = "/{clauPublicaId}/delete", method = RequestMethod.GET)
	public String delete(HttpServletRequest request, @PathVariable Long clauPublicaId)
			throws ClauPublicaNotFoundException {

		scspService.deleteClauPublica(clauPublicaId);

		AlertHelper.success(request, getMessage(request, "claupublica.controller.esborrat.ok"));

		return "redirect:../";
	}

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		binder.registerCustomEditor(Date.class, new CustomDateEditor(new SimpleDateFormat("dd/MM/yyyy"), true));
	}

}
