package es.caib.pinbal.webapp.controller;

import es.caib.pinbal.core.service.ConsultaService;
import es.caib.pinbal.core.service.PropertyService;
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

//	@RequestMapping(value = "/log/download", method = RequestMethod.GET)
//	@ResponseBody
//	public ResponseEntity<Resource> logDownload(
//			HttpServletRequest request,
//			HttpServletResponse response) throws Exception {
//
//		return getLogFile(request, response, null);
//	}
//
//	@RequestMapping(value = "/log/download/{dia}", method = RequestMethod.GET)
//	@ResponseBody
//	public ResponseEntity<Resource> logDownload(
//			HttpServletRequest request,
//			@PathVariable String dia,
//			HttpServletResponse response) throws Exception {
//
//		return getLogFile(request, response, dia);
//	}
//
//	private ResponseEntity getLogFile(HttpServletRequest request, HttpServletResponse response, String dia) throws IOException {
//		String thisPath = getClass().getProtectionDomain().getCodeSource().getLocation().getPath();
//		if (thisPath.startsWith("file:")) {
//			thisPath = thisPath.substring(5);
//		}
//		Path rootPath = Paths.get(thisPath).getParent().getParent().getParent();
//		String logPath = propertyService.get("es.caib.pinbal.log.path", Paths.get(rootPath.toString(), "log", "server.log").toString());
//		if (dia != null) {
//			logPath = logPath.substring(0, logPath.length() - 4) + "." + dia + logPath.substring(logPath.length() - 4);
//		}
//		File logFile = new File(logPath);
//
//		boolean logExist = logFile.exists();
//
//		String fileName = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
//		String contentType = "text/plain";
//
//		if (logExist && request.isUserInRole("ROLE_ADMIN")) {
//			fileName = "server." + (dia != null ? dia : fileName) + ".log";
//		} else {
//			fileName = "no." + (dia != null ? dia : fileName) + ".log";
//			writeFileToResponse(fileName, contentType, new byte[0], response);
//			return null;
//		}
//
//		response.setHeader("Pragma", "no-cache");
//		response.setHeader("Expires", "0");
//		response.setHeader("Cache-Control", "");
//		response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
//		return new ResponseEntity(new FileSystemResource(logFile), HttpStatus.OK);
//	}
}