/**
 * 
 */
package es.caib.pinbal.webapp.controller;

import javax.activation.MimetypesFileTypeMap;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.web.servlet.support.RequestContext;

/**
 * Controlador base que implementa funcionalitats comunes.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class BaseController implements MessageSourceAware {

	MessageSource messageSource;


	protected void writeFileToResponse(
			String fileName,
			byte[] fileContent,
			HttpServletResponse response) throws Exception {
		response.setHeader("Pragma", "");
		response.setHeader("Expires", "");
		response.setHeader("Cache-Control", "");
		response.setHeader("Content-Disposition","attachment; filename=\"" + fileName + "\"");
		response.setContentType(new MimetypesFileTypeMap().getContentType(fileName));
		response.getOutputStream().write(fileContent);
	}

	protected String getMessage(
			HttpServletRequest request,
			String key,
			Object[] args) {
		String message = messageSource.getMessage(
				key,
				args,
				"???" + key + "???",
				new RequestContext(request).getLocale());
		return message;
	}
	protected String getMessage(
			HttpServletRequest request,
			String key) {
		return getMessage(request, key, null);
	}



	public void setMessageSource(MessageSource messageSource) {
		this.messageSource = messageSource;
	}

}