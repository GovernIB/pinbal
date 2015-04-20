/**
 * 
 */
package es.caib.pinbal.webapp.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import es.caib.pinbal.core.dto.EntitatDto;
import es.caib.pinbal.core.service.EntitatService;
import es.caib.pinbal.core.service.ServeiService;
import es.caib.pinbal.core.service.exception.EntitatNotFoundException;
import es.caib.pinbal.core.service.exception.EntitatServeiNotFoundException;
import es.caib.pinbal.core.service.exception.ServeiNotFoundException;
import es.caib.pinbal.webapp.common.AlertHelper;

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
		if (entitat != null) {
			model.addAttribute("entitat", entitat);
			model.addAttribute("serveisActius", serveiService.findActius());
			return "entitatServeis";
		} else {
			AlertHelper.error(
					request, 
					getMessage(
							request, 
							"entitat.controller.entitat.no.existeix"));
			return "redirect:../../entitat";
		}
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
