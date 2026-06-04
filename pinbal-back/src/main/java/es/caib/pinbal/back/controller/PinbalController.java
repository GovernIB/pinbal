package es.caib.pinbal.back.controller;

import es.caib.pinbal.back.helper.AjaxHelper;
import es.caib.pinbal.back.helper.MissatgesHelper;
import es.caib.pinbal.back.helper.ModalHelper;
import es.caib.pinbal.logic.intf.service.AplicacioService;
import es.caib.pinbal.logic.intf.service.ConfigService;
import es.caib.pinbal.logic.intf.service.ConsultaService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.info.BuildProperties;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.annotation.PostConstruct;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

/**
 * Controlador accions internes de PINBAL.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Slf4j
@Controller
public class PinbalController extends BaseController {

	@Autowired
	private ConsultaService consultaService;
	@Autowired
	private ConfigService configService;
	@Autowired
	private AplicacioService aplicacioService;
	@Autowired
	private ServletContext servletContext;
	@Autowired(required = false)
	private BuildProperties buildProperties;


	@GetMapping(ModalHelper.ACCIO_MODAL_TANCAR)
	@ResponseStatus(value = HttpStatus.OK)
	public void modalTancar() {
	}
	@GetMapping(AjaxHelper.ACCIO_AJAX_OK)
	@ResponseStatus(value = HttpStatus.OK)
	public void ajaxOk() {
	}
	@GetMapping("/missatges")
	public String get() {
		return "util/missatges";
	}

	@GetMapping("/generarDadesExplotacio")
	@ResponseBody
	public String generarDadesExplotacio(HttpServletRequest request) throws Exception {
		consultaService.generarDadesExplotacio(null);
		return "Done";
	}

	@GetMapping("/generarDadesExplotacio/{dies}")
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

	@GetMapping(value = "/error")
	public String error(HttpServletRequest request, Model model) {

		var error = new ErrorObject(request);
		if ("/pinbalback/usuari/logout".equals(error.getRequestUri())) {
			return "redirect:/";
		}
		model.addAttribute("errorObject", error);
		return "util/error";
	}

	public static class ErrorObject {
		Integer statusCode;
		Throwable throwable;
		String exceptionMessage;
		String requestUri;
		String message;

		public ErrorObject(HttpServletRequest request) {

			statusCode = (Integer)request.getAttribute("javax.servlet.error.status_code");
			throwable = (Throwable)request.getAttribute("javax.servlet.error.exception");
			exceptionMessage = getExceptionMessage(throwable, statusCode);
			requestUri = (String)request.getAttribute("javax.servlet.error.request_uri");
			if (requestUri == null) {
				requestUri = "Desconeguda";
			}
			message = "Retornat codi d'error " + statusCode + " per al recurs " + requestUri + " amb el missatge: " + exceptionMessage;
		}

		public Integer getStatusCode() {
			return statusCode;
		}
		public Throwable getThrowable() {
			return throwable;
		}
		public String getThrowableClassName() {
			return throwable != null ? throwable.getClass().getName() : "";
		}
		public String getThrowableCauseClassName() {
			return throwable != null && throwable.getCause() != null ? throwable.getCause().getClass().getName() : "";
		}
		public String getExceptionMessage() {
			return exceptionMessage;
		}
		public String getRequestUri() {
			return requestUri;
		}
		public String getMessage() {
			return message;
		}
		public String getStackTrace() {
			return throwable != null ? ExceptionUtils.getStackTrace(throwable) : "";
		}
		public String getFullStackTrace() {
			return throwable != null ? ExceptionUtils.getStackTrace(throwable) : "";
		}
		private String getExceptionMessage(Throwable throwable, Integer statusCode) {

			if (throwable == null) {
				HttpStatus httpStatus = HttpStatus.valueOf(statusCode);
				return httpStatus.getReasonPhrase();
			}
			var rootCause = ExceptionUtils.getRootCause(throwable);
			return rootCause != null ? rootCause.getMessage() : throwable.getMessage();
		}
	}

	@PostConstruct
	public void propagateDbProperties() {

		configService.propagateDbProperties();
		MissatgesHelper.setManifestAtributsMap(getManifestAttributes());
	}

	private Map<String, Object> getManifestAttributes() {

		Map<String, Object> manifestAtributsMap = null;
		try {
			var is = servletContext.getResourceAsStream("/" + JarFile.MANIFEST_NAME);
			var implVersion = "Implementation-Version";
			var buildTimeStamp = "Build-Timestamp";
			if (is != null) {
				manifestAtributsMap = new HashMap<>();
				var manifest = new Manifest(is);
				var manifestAtributs = manifest.getMainAttributes();
				for (var key : new HashMap<>(manifestAtributs).keySet()) {
					manifestAtributsMap.put(key.toString(), manifestAtributs.get(key));
				}
				aplicacioService.setAppVersion((String) manifestAtributsMap.get(implVersion));
				manifestAtributsMap.put(buildTimeStamp, formatBuildTimestamp(manifestAtributsMap.get(buildTimeStamp).toString()));
			} else if (buildProperties != null){
				manifestAtributsMap = new HashMap<>();
				manifestAtributsMap.put(buildTimeStamp, formatBuildTimestamp(buildProperties.get(buildTimeStamp)));
				manifestAtributsMap.put("Implementation-Vendor", buildProperties.get("Implementation-Vendor"));
				manifestAtributsMap.put(implVersion, buildProperties.get(implVersion));
				manifestAtributsMap.put("Implementation-SCM-Branch", buildProperties.get("Implementation-SCM-Branch"));
				manifestAtributsMap.put("Implementation-SCM-Revision", buildProperties.get("Implementation-SCM-Revision"));
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm").withZone(ZoneId.systemDefault());
				manifestAtributsMap.put("Version", buildProperties.getVersion());
				manifestAtributsMap.put("Time", formatter.format(buildProperties.getTime()));
				aplicacioService.setAppVersion(buildProperties.get(implVersion));
			}
		} catch (Exception ex) {
			log.error("Error obtenint els atributs del manifest per a mostrar el numero de versió", ex);
		}
		return manifestAtributsMap;
	}

	private String formatBuildTimestamp(String timestamp) throws ParseException {

		var ISO_DATE_FORMAT_ZERO_OFFSET = "yyyy-MM-dd'T'HH:mm:ss'Z'";
		var simpleDateFormat = new SimpleDateFormat(ISO_DATE_FORMAT_ZERO_OFFSET);
		simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
		var dateFormat = new SimpleDateFormat("yyyyy.MM.dd HH:mm", new Locale("ca", "ES"));
		var date = simpleDateFormat.parse(timestamp);
		return dateFormat.format(date) + "h";
	}

}