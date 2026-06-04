/**
 * 
 */
package es.caib.pinbal.back.controller;

import es.caib.pinbal.back.command.ParamConfCommand;
import es.caib.pinbal.back.datatables.ServerSideRequest;
import es.caib.pinbal.back.datatables.ServerSideResponse;
import es.caib.pinbal.back.helper.AlertHelper;
import es.caib.pinbal.logic.intf.dto.ParamConfDto;
import es.caib.pinbal.logic.intf.service.ScspService;
import es.caib.pinbal.logic.intf.service.exception.EntitatNotFoundException;
import es.caib.pinbal.logic.intf.service.exception.ParamConfNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.naming.NamingException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;

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
	
	
	@GetMapping
	public String get(
			HttpServletRequest request,
			Model model) throws Exception {		
		return "paramConfList";
	}
	
	@GetMapping(value = "/datatable", produces="application/json")
	@ResponseBody
	public ServerSideResponse<ParamConfDto, Long> datatable(HttpServletRequest request, Model model)
	      throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, NamingException,
	      SQLException, EntitatNotFoundException {

		ServerSideRequest serverSideRequest = new ServerSideRequest(request);
		 
		Page<ParamConfDto> page = scspService.findAllParamConf(serverSideRequest.toPageable());	

		return new ServerSideResponse<ParamConfDto, Long>(serverSideRequest, page);
	}
	
	@GetMapping("/new")
	public String get(Model model) {
		
		model.addAttribute( ParamConfCommand.commandForCreate() );
		
		return "paramConfForm";
	}
	
	@GetMapping("/{paramConfNom:.+}")
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
	
	@PostMapping("/save")
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
			return getModalControllerReturnValueSuccess(
					request,
					"redirect:./",
					"paramconf.controller.creat.ok");
		} else {
			scspService.updateParamConf(ParamConfCommand.asDto(command));
			return getModalControllerReturnValueSuccess(
					request,
					"redirect:./",
					"paramconf.controller.modificat.ok");
		}
	}
	
	@GetMapping("/{paramConfNom:.+}/delete")
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
