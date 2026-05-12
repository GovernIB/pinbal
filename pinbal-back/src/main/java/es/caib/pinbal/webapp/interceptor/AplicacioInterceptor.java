/**
 * 
 */
package es.caib.pinbal.webapp.interceptor;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import org.springframework.web.servlet.support.RequestContextUtils;


/**
 * Interceptor per a les accions de context d'aplicació.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class AplicacioInterceptor extends HandlerInterceptorAdapter {

	public static final String REQUEST_ATTRIBUTE_MANIFEST_ATRIBUTES = "manifestAtributes";
	public static final String REQUEST_ATTRIBUTE_LOCALE = "requestLocale";
	
	@Autowired
	private ServletContext servletContext;

	private Map<String, Object> manifestAtributsMap;

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public boolean preHandle(
			HttpServletRequest request,
			HttpServletResponse response,
			Object handler) throws Exception {
		if (manifestAtributsMap == null) {
			Manifest manifest = new Manifest(servletContext.getResourceAsStream("/" + JarFile.MANIFEST_NAME));
			Attributes manifestAtributs = manifest.getMainAttributes();
			manifestAtributsMap = new HashMap<String, Object>();
			for (Object key: new HashMap(manifestAtributs).keySet()) {
				manifestAtributsMap.put(key.toString(), manifestAtributs.get(key));
			}
			Locale locale = new Locale(RequestContextUtils.getLocale(request).getLanguage(), 
					RequestContextUtils.getLocale(request).getCountry());
			manifestAtributsMap.put(
					"Build-Timestamp",
					formatBuildTimestamp(
							manifestAtributsMap.get("Build-Timestamp").toString(), locale));
		}

		request.setAttribute(
				REQUEST_ATTRIBUTE_MANIFEST_ATRIBUTES,
				manifestAtributsMap);
		request.setAttribute(
				REQUEST_ATTRIBUTE_LOCALE,
				RequestContextUtils.getLocale(request).getLanguage());
		return true;
	}
	
	private String formatBuildTimestamp(String timestamp, Locale locale) throws ParseException {
		String ISO_DATE_FORMAT_ZERO_OFFSET = "yyyy-MM-dd'T'HH:mm:ss'Z'";
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(ISO_DATE_FORMAT_ZERO_OFFSET);
		simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
		
		SimpleDateFormat dateFormat = new SimpleDateFormat("YYYY.MM.dd HH:mm", locale);		
		Date date = simpleDateFormat.parse(timestamp);
		return dateFormat.format(date) + "h";
	}

}
