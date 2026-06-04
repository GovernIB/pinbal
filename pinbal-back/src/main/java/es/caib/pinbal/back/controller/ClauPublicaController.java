/**
 * 
 */
package es.caib.pinbal.back.controller;

import es.caib.pinbal.back.command.ClauPublicaCommand;
import es.caib.pinbal.back.datatables.ServerSideRequest;
import es.caib.pinbal.back.datatables.ServerSideResponse;
import es.caib.pinbal.back.helper.AlertHelper;
import es.caib.pinbal.logic.intf.dto.ClauPublicaDto;
import es.caib.pinbal.logic.intf.service.ScspService;
import es.caib.pinbal.logic.intf.service.exception.ClauPublicaNotFoundException;
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
 * Controlador per al manteniment de claus públiques.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/scsp/claupublica")
public class ClauPublicaController extends BaseController {

	@Autowired
	private ScspService scspService;

	@GetMapping
	public String get(HttpServletRequest request, Model model) throws Exception {
		return "clauPublicaList";
	}

	@GetMapping(value = "/datatable", produces="application/json")
	@ResponseBody
	public ServerSideResponse<ClauPublicaDto, Long> datatable(HttpServletRequest request, Model model)
	      throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, NamingException,
	      SQLException, EntitatNotFoundException {

		ServerSideRequest serverSideRequest = new ServerSideRequest(request);
		 
		Page<ClauPublicaDto> page = scspService.findAllClauPublica(serverSideRequest.toPageable());	

		return new ServerSideResponse<ClauPublicaDto, Long>(serverSideRequest, page);
	}
	
	@GetMapping("/new")
	public String get(Model model) {

		model.addAttribute(new ClauPublicaCommand());

		return "clauPublicaForm";
	}

	@GetMapping("/{clauPublicaId}")
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

	@PostMapping("/save")
	public String save(HttpServletRequest request, Model model, @Valid ClauPublicaCommand command,
			BindingResult bindingResult) throws ClauPublicaNotFoundException {

		if (bindingResult.hasErrors()) {
			return "clauPublicaForm";
		}

		if (command.getId() == null) {
			scspService.createClauPublica(ClauPublicaCommand.asDto(command));
			return getModalControllerReturnValueSuccess(
					request,
					"redirect:./",
					"claupublica.controller.creat.ok");
		} else {
			scspService.updateClauPublica(ClauPublicaCommand.asDto(command));
			return getModalControllerReturnValueSuccess(
					request,
					"redirect:./",
					"claupublica.controller.modificat.ok");
		}
	}

	@GetMapping("/{clauPublicaId}/delete")
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
