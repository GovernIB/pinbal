/**
 * 
 */
package es.caib.pinbal.webapp.controller;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.List;

import javax.naming.NamingException;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import es.caib.pinbal.core.dto.EntitatDto;
import es.caib.pinbal.core.dto.ServeiDto;
import es.caib.pinbal.core.service.EntitatService;
import es.caib.pinbal.core.service.ServeiService;
import es.caib.pinbal.core.service.exception.EntitatNotFoundException;
import es.caib.pinbal.core.service.exception.EntitatServeiNotFoundException;
import es.caib.pinbal.core.service.exception.ServeiNotFoundException;
import es.caib.pinbal.webapp.common.AlertHelper;
import es.caib.pinbal.webapp.datatables.ServerSideRequest;
import es.caib.pinbal.webapp.datatables.ServerSideResponse;

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


	@RequestMapping(value = "/{entitatId}/servei", method = RequestMethod.GET)
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

	@RequestMapping(value = "/{entitatId}/servei/datatable", produces="application/json", method = RequestMethod.GET)
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
	@RequestMapping(value = "/{entitatId}/servei/{serveiCodi}/add", method = RequestMethod.GET)
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

	@RequestMapping(value = "/{entitatId}/servei/{serveiCodi}/remove", method = RequestMethod.GET)
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
