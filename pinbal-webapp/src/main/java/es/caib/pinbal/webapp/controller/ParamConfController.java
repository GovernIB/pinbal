/**
 * 
 */
package es.caib.pinbal.webapp.controller;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;

import javax.naming.NamingException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import es.caib.pinbal.core.dto.ParamConfDto;
import es.caib.pinbal.core.service.ScspService;
import es.caib.pinbal.core.service.exception.EntitatNotFoundException;
import es.caib.pinbal.core.service.exception.ParamConfNotFoundException;
import es.caib.pinbal.webapp.command.ParamConfCommand;
import es.caib.pinbal.webapp.common.AlertHelper;
import es.caib.pinbal.webapp.datatables.ServerSideRequest;
import es.caib.pinbal.webapp.datatables.ServerSideResponse;

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
		return "paramConfList";
	}
	
	@RequestMapping(value = "/datatable", produces="application/json", method = RequestMethod.GET)
	@ResponseBody
	public ServerSideResponse<ParamConfDto, Long> datatable(HttpServletRequest request, Model model)
	      throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, NamingException,
	      SQLException, EntitatNotFoundException {

		ServerSideRequest serverSideRequest = new ServerSideRequest(request);
		 
		Page<ParamConfDto> page = scspService.findAllParamConf(serverSideRequest.toPageable());	

		return new ServerSideResponse<ParamConfDto, Long>(serverSideRequest, page);
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
