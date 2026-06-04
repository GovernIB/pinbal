/**
 * 
 */
package es.caib.pinbal.back.controller;

import es.caib.pinbal.back.helper.AlertHelper;
import es.caib.pinbal.back.helper.RolHelper;
import es.caib.pinbal.logic.intf.service.ConsultaService;
import es.caib.pinbal.logic.intf.service.HistoricConsultaService;
import es.caib.pinbal.logic.intf.service.ProcedimentService;
import es.caib.pinbal.logic.intf.service.ServeiService;
import es.caib.pinbal.logic.intf.service.UsuariService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

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
	@Autowired
	private HistoricConsultaService historicConsultaService;


	@GetMapping
	public String get(
			HttpServletRequest request,
			Model model) {
		return "informeList";
	}

	@GetMapping("/procediments")
	public String procediments(
			HttpServletRequest request,
			Model model) {
		model.addAttribute(
				"informeDades",
				procedimentService.informeProcedimentsAgrupatsEntitatDepartament());
		return "informeProcedimentsExcelView";
	}

	@GetMapping("/usuaris")
	public String usuaris(
			HttpServletRequest request,
			Model model) {
		model.addAttribute(
				"informeDades",
				usuariService.informeUsuarisAgrupatsEntitatDepartament());
		return "informeUsuarisExcelView";
	}

	@GetMapping("/serveis")
	public String serveis(
			HttpServletRequest request,
			Model model) {
		model.addAttribute(
				"informeDades",
				serveiService.findActius());
		return "informeServeisExcelView";
	}

	@GetMapping("/generalEstat")
	public String general(
			HttpServletRequest request,
			@RequestParam(value = "historic", required = false) boolean historic,
			@RequestParam("dataInici") @DateTimeFormat(pattern="dd/MM/yyyy") Date dataInici,
			@RequestParam("dataFi") @DateTimeFormat(pattern="dd/MM/yyyy") Date dataFi,
			Model model) {

//		if (historic == null)
//			historic = Boolean.FALSE;

		if (dataInici != null && dataFi != null) {
			model.addAttribute(
					"informeDades",
					historic ?
							historicConsultaService.informeGeneralEstat(dataInici, dataFi) :
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

	@GetMapping("/usuarisEntitatOrganProcedimentServei")
	public String usuarisEntitatOrganProcedimentServei(
			HttpServletRequest request,
			Model model) {
		 
		model.addAttribute(
				"informeDades",
				consultaService.informeUsuarisEntitatOrganProcedimentServei(null, RolHelper.getRolActual(request), null));

		model.addAttribute("isAdministrador", RolHelper.isRolActualAdministrador(request));
		
		return "informeUsrEntOrgProcServExcelView";
	}

}
