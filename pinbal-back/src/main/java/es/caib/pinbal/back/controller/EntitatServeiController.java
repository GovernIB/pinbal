/**
 * 
 */
package es.caib.pinbal.back.controller;

import es.caib.pinbal.back.datatables.ServerSideRequest;
import es.caib.pinbal.back.datatables.ServerSideResponse;
import es.caib.pinbal.back.helper.AlertHelper;
import es.caib.pinbal.logic.intf.dto.EntitatDto;
import es.caib.pinbal.logic.intf.dto.ServeiDto;
import es.caib.pinbal.logic.intf.service.EntitatService;
import es.caib.pinbal.logic.intf.service.ServeiService;
import es.caib.pinbal.logic.intf.service.exception.EntitatNotFoundException;
import es.caib.pinbal.logic.intf.service.exception.EntitatServeiNotFoundException;
import es.caib.pinbal.logic.intf.service.exception.ServeiNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.naming.NamingException;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.List;

/**
 * Controlador per al manteniment dels serveis d'una entitat.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/entitat")
public class EntitatServeiController extends BaseController {

	@Autowired
	private EntitatService entitatService;
	@Autowired
	private ServeiService serveiService;


	@GetMapping("/{entitatId}/servei")
	public String servei(
			HttpServletRequest request,
			@PathVariable Long entitatId,
			Model model) throws EntitatNotFoundException {
		EntitatDto entitat = null;
		if (entitatId != null)
			entitat = entitatService.findById(entitatId);
		if (entitat == null) {
			AlertHelper.error(
					request, 
					getMessage(
							request, 
							"entitat.controller.entitat.no.existeix"));
			return "redirect:../../entitat";
		}
						
		model.addAttribute("entitat", entitat);
		return "entitatServeis";

	}

	@GetMapping(value = "/{entitatId}/servei/datatable", produces="application/json")
	@ResponseBody
	public ServerSideResponse<ServeiDto, Long> datatable(
			HttpServletRequest request,
			@PathVariable Long entitatId,
			Model model)
	      throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, NamingException,
	      SQLException, EntitatNotFoundException {
		EntitatDto entitat = entitatService.findById(entitatId);
		ServerSideRequest serverSideRequest = new ServerSideRequest(request);
		List<ServeiDto> listServeis = serveiService.findActius();
		List<String> serveisEntitat = entitat.getServeis();
		for (ServeiDto servei: listServeis) {
			servei.setActiu(false);
			for (String codi: serveisEntitat) {
				if (servei.getCodi().equals(codi)) {
					servei.setActiu(true);
					break;
				}
			}
		}
		Page<ServeiDto> page = new PageImpl<ServeiDto>(listServeis, null, listServeis.size());
		return new ServerSideResponse<ServeiDto, Long>(serverSideRequest, page);
	}
	@GetMapping("/{entitatId}/servei/{serveiCodi}/add")
	public String serveiAdd(
			HttpServletRequest request,
			@PathVariable Long entitatId,
			@PathVariable String serveiCodi,
			Model model) throws EntitatNotFoundException, ServeiNotFoundException {
		EntitatDto entitat = null;
		if (entitatId != null)
			entitat = entitatService.findById(entitatId);
		if (entitat != null) {
			entitatService.addServei(entitatId, serveiCodi);
			AlertHelper.success(
					request, 
					getMessage(
							request, 
							"entitat.controller.activat.servei",
							new Object[] {serveiCodi}));
			return "redirect:../../servei";
		} else {
			AlertHelper.error(
					request, 
					getMessage(
							request, 
							"entitat.controller.entitat.no.existeix"));
			return "redirect:../../../../entitat";
		}
	}

	@GetMapping("/{entitatId}/servei/{serveiCodi}/remove")
	public String serveiRemove(
			HttpServletRequest request,
			@PathVariable Long entitatId,
			@PathVariable String serveiCodi,
			Model model) throws EntitatNotFoundException, EntitatServeiNotFoundException {
		EntitatDto entitat = null;
		if (entitatId != null)
			entitat = entitatService.findById(entitatId);
		if (entitat != null) {
			entitatService.removeServei(entitatId, serveiCodi);
			AlertHelper.success(
					request, 
					getMessage(
							request, 
							"entitat.controller.desactivat.servei",
							new Object[] {serveiCodi}));
			return "redirect:../../servei";
		} else {
			AlertHelper.error(
					request, 
					getMessage(
							request, 
							"entitat.controller.entitat.no.existeix"));
			return "redirect:../../../../entitat";
		}
	}

}
