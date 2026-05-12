/**
 * 
 */
package es.caib.pinbal.webapp.controller;

import es.caib.pinbal.core.dto.AvisDto;
import es.caib.pinbal.core.service.AvisService;
import es.caib.pinbal.webapp.command.AvisCommand;
import es.caib.pinbal.webapp.datatables.ServerSideRequest;
import es.caib.pinbal.webapp.datatables.ServerSideResponse;
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

import javax.naming.NamingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

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

	private static final String SESSION_SELECTED_IDS = "avisSelectedIds";

	@SuppressWarnings("unchecked")
	private Set<Long> getSelectedSet(HttpSession session) {
		Object obj = session.getAttribute(SESSION_SELECTED_IDS);
		if (obj instanceof Set) {
			return (Set<Long>) obj;
		}
		Set<Long> set = new HashSet<>();
		session.setAttribute(SESSION_SELECTED_IDS, set);
		return set;
	}

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
			return getModalControllerReturnValueSuccess(
					request,
					"redirect:../avis",
					"avis.controller.modificat.ok");
		} else {
			avisService.create(AvisCommand.asDto(command));
			return getModalControllerReturnValueSuccess(
					request,
					"redirect:../avis",
					"avis.controller.creat.ok");
		}
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

	// --- Selecció a sessió ---
	@RequestMapping(value = "/selection/list", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public Set<Long> selectionList(HttpServletRequest request) {
		return new HashSet<>(getSelectedSet(request.getSession()));
	}

	@RequestMapping(value = "/selection/add", method = RequestMethod.POST)
	@ResponseBody
	public String selectionAdd(HttpServletRequest request) {
		String idsStr = request.getParameter("ids");
		Set<Long> set = getSelectedSet(request.getSession());
		if (idsStr != null && !idsStr.isEmpty()) {
			for (String s : idsStr.split(",")) {
				try { set.add(Long.valueOf(s.trim())); } catch (Exception ignore) {}
			}
		}
		return "OK";
	}

	@RequestMapping(value = "/selection/remove", method = RequestMethod.POST)
	@ResponseBody
	public String selectionRemove(HttpServletRequest request) {
		String idsStr = request.getParameter("ids");
		Set<Long> set = getSelectedSet(request.getSession());
		if (idsStr != null && !idsStr.isEmpty()) {
			for (String s : idsStr.split(",")) {
				try { set.remove(Long.valueOf(s.trim())); } catch (Exception ignore) {}
			}
		}
		return "OK";
	}

	@RequestMapping(value = "/selection/clear", method = RequestMethod.POST)
	@ResponseBody
	public String selectionClear(HttpServletRequest request) {
		getSelectedSet(request.getSession()).clear();
		return "OK";
	}

	@RequestMapping(value = "/selection/selectAll", method = RequestMethod.POST)
	@ResponseBody
	public String selectionSelectAll(HttpServletRequest request) {
		Set<Long> set = getSelectedSet(request.getSession());
        set.addAll(avisService.findAllIds());
		return "OK";
	}

	@RequestMapping(value = "/selected/enable", method = RequestMethod.POST, produces = "application/json")
	@ResponseBody
	public java.util.Map<String, Object> enableSelected(HttpServletRequest request) {
		int processed = 0;
		for (Long id : getSelectedSet(request.getSession())) {
			avisService.updateActiva(id, true);
			processed++;
		}
		java.util.Map<String, Object> resp = new java.util.HashMap<>();
		resp.put("action", "enable");
		resp.put("processed", processed);
		return resp;
	}
	@RequestMapping(value = "/selected/disable", method = RequestMethod.POST, produces = "application/json")
	@ResponseBody
	public java.util.Map<String, Object> disableSelected(HttpServletRequest request) {
		int processed = 0;
		for (Long id : getSelectedSet(request.getSession())) {
			avisService.updateActiva(id, false);
			processed++;
		}
		java.util.Map<String, Object> resp = new java.util.HashMap<>();
		resp.put("action", "disable");
		resp.put("processed", processed);
		return resp;
	}
	@RequestMapping(value = "/selected/delete", method = RequestMethod.POST, produces = "application/json")
	@ResponseBody
	public java.util.Map<String, Object> deleteSelected(HttpServletRequest request) {
		int processed = 0;
		for (Long id : new HashSet<>(getSelectedSet(request.getSession()))) {
			avisService.delete(id);
			processed++;
		}
		getSelectedSet(request.getSession()).clear();
		java.util.Map<String, Object> resp = new java.util.HashMap<>();
		resp.put("action", "delete");
		resp.put("processed", processed);
		return resp;
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
