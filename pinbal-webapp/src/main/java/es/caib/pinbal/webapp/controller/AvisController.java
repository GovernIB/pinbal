/**
 * 
 */
package es.caib.pinbal.webapp.controller;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.naming.NamingException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import es.caib.distribucio.core.api.dto.AvisDto;
import es.caib.pinbal.core.service.AvisService;
import es.caib.pinbal.webapp.command.AvisCommand;
import es.caib.pinbal.webapp.common.AlertHelper;
import es.caib.pinbal.webapp.datatables.ServerSideRequest;
import es.caib.pinbal.webapp.datatables.ServerSideResponse;

/**
 * Controlador per al manteniment de avisos.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/avis")
public class AvisController extends BaseController {
	
	@Autowired
	private AvisService avisService;

	
	@RequestMapping(method = RequestMethod.GET)
	public String get() {
		return "avisList";
	}
	@RequestMapping(value = "/datatable", method = RequestMethod.GET)
	@ResponseBody
	public ServerSideResponse<AvisDto, Long> datatable(HttpServletRequest request) 
		throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, NamingException, SQLException {
		
		ServerSideRequest serverSideRequest = new ServerSideRequest(request);
		
		Page<AvisDto> page = avisService.findPaginat(
				serverSideRequest.toPageable());
		
		
		return new ServerSideResponse<AvisDto, Long>(serverSideRequest, page);
	}

	@RequestMapping(value = "/new", method = RequestMethod.GET)
	public String getNew(Model model) {
		return get(null, model);
	}
	
	
	@RequestMapping(value = "/{avisId}", method = RequestMethod.GET)
	public String get(
			@PathVariable Long avisId,
			Model model) {
		AvisDto avis = null;
		if (avisId != null)
			avis = avisService.findById(avisId);
		if (avis != null) {
			model.addAttribute(AvisCommand.asCommand(avis));
		} else {
			AvisCommand avisCommand = new AvisCommand();
			avisCommand.setDataInici(new Date());
			model.addAttribute(avisCommand);
		}
		return "avisForm";
	}
	@RequestMapping(value = "/save",method = RequestMethod.POST)
	public String save(
			HttpServletRequest request,
			@Valid AvisCommand command,
			BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			return "avisForm";
		}
		if (command.getId() != null) {
			avisService.update(AvisCommand.asDto(command));
			AlertHelper.success(
					request, 
					getMessage(
							request, 
							"avis.controller.modificat.ok"));
		} else {
			avisService.create(AvisCommand.asDto(command));
			AlertHelper.success(
					request, 
					getMessage(
							request, 
							"avis.controller.creat.ok"));
		}
		return "redirect:../avis";
	}

	@RequestMapping(value = "/{avisId}/enable", method = RequestMethod.GET)
	public String enable(
			HttpServletRequest request,
			@PathVariable Long avisId) {
		avisService.updateActiva(avisId, true);
		return getAjaxControllerReturnValueSuccess(
				request,
				"redirect:../../avis",
				"avis.controller.activat.ok");
	}
	@RequestMapping(value = "/{avisId}/disable", method = RequestMethod.GET)
	public String disable(
			HttpServletRequest request,
			@PathVariable Long avisId) {
		avisService.updateActiva(avisId, false);
		return getAjaxControllerReturnValueSuccess(
				request,
				"redirect:../../avis",
				"avis.controller.desactivat.ok");
	}

	@RequestMapping(value = "/{avisId}/delete", method = RequestMethod.GET)
	public String delete(
			HttpServletRequest request,
			@PathVariable Long avisId) {
		avisService.delete(avisId);
		return getAjaxControllerReturnValueSuccess(
				request,
				"redirect:../../avis",
				"avis.controller.esborrat.ok");
	}
	
	@InitBinder
	protected void initBinder(WebDataBinder binder) {
	    binder.registerCustomEditor(
	    		Date.class,
	    		new CustomDateEditor(
	    				new SimpleDateFormat("dd/MM/yyyy"),
	    				true));
	}
	
}
