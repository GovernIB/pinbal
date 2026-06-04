/**
 * 
 */
package es.caib.pinbal.back.controller;

import es.caib.pinbal.back.command.EmissorCertCommand;
import es.caib.pinbal.back.datatables.ServerSideRequest;
import es.caib.pinbal.back.datatables.ServerSideResponse;
import es.caib.pinbal.back.helper.AlertHelper;
import es.caib.pinbal.logic.intf.dto.EmissorCertDto;
import es.caib.pinbal.logic.intf.service.ScspService;
import es.caib.pinbal.logic.intf.service.exception.EmissorCertNotFoundException;
import es.caib.pinbal.logic.intf.service.exception.EntitatNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.naming.NamingException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

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

	@GetMapping
	public String get(HttpServletRequest request, Model model) throws Exception {
		return "emissorCertList";
	}

	@GetMapping(value = "/datatable", produces="application/json")
	@ResponseBody
	public ServerSideResponse<EmissorCertDto, Long> datatable(HttpServletRequest request, Model model)
	      throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, NamingException,
	      SQLException, EntitatNotFoundException {

		ServerSideRequest serverSideRequest = new ServerSideRequest(request);
		 
		Page<EmissorCertDto> page = scspService.findAllEmissorCert(serverSideRequest.toPageable());	

		return new ServerSideResponse<EmissorCertDto, Long>(serverSideRequest, page);
	}
	
	
	@GetMapping("/new")
	public String get(Model model) {

		model.addAttribute(new EmissorCertCommand());

		return "emissorCertForm";
	}

	@GetMapping("/{emissorCertId}")
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

	@PostMapping("/save")
	public String save(HttpServletRequest request, Model model, @Valid EmissorCertCommand command,
			BindingResult bindingResult) throws EmissorCertNotFoundException {

		if (bindingResult.hasErrors()) {
			return "emissorCertForm";
		}

		if (command.getId() == null) {
			scspService.createEmissorCert(EmissorCertCommand.asDto(command));
			return getModalControllerReturnValueSuccess(
					request,
					"redirect:./",
					"emissorcert.controller.creat.ok");
		} else {
			scspService.updateEmissorCert(EmissorCertCommand.asDto(command));
			return getModalControllerReturnValueSuccess(
					request,
					"redirect:./",
					"emissorcert.controller.modificat.ok");
		}
	}

	@GetMapping("/{emissorCertId}/delete")
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
