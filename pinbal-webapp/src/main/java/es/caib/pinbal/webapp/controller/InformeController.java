/**
 * 
 */
package es.caib.pinbal.webapp.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import es.caib.pinbal.core.service.ProcedimentService;
import es.caib.pinbal.core.service.ServeiService;
import es.caib.pinbal.core.service.UsuariService;

/**
 * Controlador pels informes.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/informe")
public class InformeController {

	@Autowired
	private ProcedimentService procedimentService;
	@Autowired
	private UsuariService usuariService;
	@Autowired
	private ServeiService serveiService;



	@RequestMapping(method = RequestMethod.GET)
	public String get(
			HttpServletRequest request,
			Model model) {
		return "informeList";
	}

	@RequestMapping(value = "/procediments", method = RequestMethod.GET)
	public String procediments(
			HttpServletRequest request,
			Model model) {
		model.addAttribute(
				"informeDades",
				procedimentService.informeProcedimentsAgrupatsEntitatDepartament());
		return "informeProcedimentsExcelView";
	}

	@RequestMapping(value = "/usuaris", method = RequestMethod.GET)
	public String usuaris(
			HttpServletRequest request,
			Model model) {
		model.addAttribute(
				"informeDades",
				usuariService.informeUsuarisAgrupatsEntitatDepartament());
		return "informeUsuarisExcelView";
	}

	@RequestMapping(value = "/serveis", method = RequestMethod.GET)
	public String serveis(
			HttpServletRequest request,
			Model model) {
		model.addAttribute(
				"informeDades",
				serveiService.findActius());
		return "informeServeisExcelView";
	}

}
