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

import es.caib.pinbal.core.dto.EmissorCertDto;
import es.caib.pinbal.core.service.ScspService;
import es.caib.pinbal.core.service.exception.EmissorCertNotFoundException;
import es.caib.pinbal.webapp.command.EmissorCertCommand;
import es.caib.pinbal.webapp.common.AlertHelper;

/**
 * Controlador per al manteniment d'entitats.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/scsp/emissorcert")
public class EmissorCertController extends BaseController {

	@Autowired
	private ScspService scspService;

	@RequestMapping(method = RequestMethod.GET)
	public String get(HttpServletRequest request, Model model) throws Exception {

		model.addAttribute("llistaEmissors", scspService.findAllEmissorCert());

		return "emissorCertList";
	}

	@RequestMapping(value = "/new", method = RequestMethod.GET)
	public String get(Model model) {

		model.addAttribute(new EmissorCertCommand());

		return "emissorCertForm";
	}

	@RequestMapping(value = "/{emissorCertId}", method = RequestMethod.GET)
	public String get(@PathVariable Long emissorCertId, Model model) {

		EmissorCertDto dto = null;
		if (emissorCertId != null)
			dto = scspService.findEmissorCertById(emissorCertId);

		if (dto != null)
			model.addAttribute(EmissorCertCommand.asCommand(dto));
		else
			model.addAttribute(new EmissorCertCommand());

		return "emissorCertForm";
	}

	@RequestMapping(value = "/save", method = RequestMethod.POST)
	public String save(HttpServletRequest request, Model model, @Valid EmissorCertCommand command,
			BindingResult bindingResult) throws EmissorCertNotFoundException {

		if (bindingResult.hasErrors()) {
			return "emissorCertForm";
		}

		if (command.getId() == null) {
			scspService.createEmissorCert(EmissorCertCommand.asDto(command));
			AlertHelper.success(request, getMessage(request, "emissorcert.controller.creat.ok"));
		} else {
			scspService.updateEmissorCert(EmissorCertCommand.asDto(command));
			AlertHelper.success(request, getMessage(request, "emissorcert.controller.modificat.ok"));
		}

		return "redirect:./";
	}

	@RequestMapping(value = "/{emissorCertId}/delete", method = RequestMethod.GET)
	public String delete(HttpServletRequest request, @PathVariable Long emissorCertId)
			throws EmissorCertNotFoundException {

		scspService.deleteEmissorCert(emissorCertId);

		AlertHelper.success(request, getMessage(request, "emissorcert.controller.esborrat.ok"));

		return "redirect:../";
	}

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		binder.registerCustomEditor(Date.class, new CustomDateEditor(new SimpleDateFormat("dd/MM/yyyy"), true));
	}

}
