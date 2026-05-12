package es.caib.pinbal.webapp.controller;

import es.caib.pinbal.core.service.ConsultaService;
import es.caib.pinbal.core.service.PropertyService;
import es.caib.pinbal.core.service.ServeiService;
import es.caib.pinbal.webapp.helper.AjaxHelper;
import es.caib.pinbal.webapp.helper.ModalHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.util.Calendar;

/**
 * Controlador accions internes de PINBAL.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
public class PinbalController extends BaseController {

	@Autowired
	private PropertyService propertyService;
	@Autowired
	private ConsultaService consultaService;
	@Autowired
	private ServeiService serveiService;


	@RequestMapping(value = ModalHelper.ACCIO_MODAL_TANCAR, method = RequestMethod.GET)
	@ResponseStatus(value = HttpStatus.OK)
	public void modalTancar() {
	}
	@RequestMapping(value = AjaxHelper.ACCIO_AJAX_OK, method = RequestMethod.GET)
	@ResponseStatus(value = HttpStatus.OK)
	public void ajaxOk() {
	}
	@RequestMapping(value = "/missatges", method = RequestMethod.GET)
	public String get() {
		return "util/missatges";
	}

	@RequestMapping(value = "/generarDadesExplotacio", method = RequestMethod.GET)
	@ResponseBody
	public String generarDadesExplotacio(HttpServletRequest request) throws Exception {
		consultaService.generarDadesExplotacio(null);
		return "Done";
	}

	@RequestMapping(value = "/generarDadesExplotacio/{dies}", method = RequestMethod.GET)
	@ResponseBody
	public String generarDadesExplotacio(
			HttpServletRequest request,
			@PathVariable Integer dies) throws Exception {

		Calendar cal = Calendar.getInstance();
		for (int i = 0; i < dies; i++) {
			consultaService.generarDadesExplotacio(cal.getTime());
			cal.add(Calendar.DAY_OF_YEAR, -1);
		}
		return "Done";
	}

}