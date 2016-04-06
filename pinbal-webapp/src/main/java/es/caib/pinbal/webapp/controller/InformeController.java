/**
 * 
 */
package es.caib.pinbal.webapp.controller;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import es.caib.pinbal.core.service.ConsultaService;
import es.caib.pinbal.core.service.ProcedimentService;
import es.caib.pinbal.core.service.ServeiService;
import es.caib.pinbal.core.service.UsuariService;
import es.caib.pinbal.webapp.common.AlertHelper;

/**
 * Controlador pels informes.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/informe")
public class InformeController extends BaseController {

	@Autowired
	private ProcedimentService procedimentService;
	@Autowired
	private UsuariService usuariService;
	@Autowired
	private ServeiService serveiService;
	@Autowired
	private ConsultaService consultaService;



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
	
	@RequestMapping(value = "/generalEstat", method = RequestMethod.GET)
	public String general(
			HttpServletRequest request,
			@RequestParam("dataInici") @DateTimeFormat(pattern="dd/MM/yyyy") Date dataInici,
			@RequestParam("dataFi") @DateTimeFormat(pattern="dd/MM/yyyy") Date dataFi,
			Model model) {

		if (dataInici != null && dataFi != null) {
			model.addAttribute(
					"informeDades",
					consultaService.informeGeneralEstat(dataInici, dataFi));
			return "informeGeneralEstatExcelView";
		} else {
			AlertHelper.warning(
					request, 
					getMessage(
							request, 
							"informe.missatges.dates.buides"));
			return "redirect:../informe";
		}
	}

}
