/**
 * 
 */
package es.caib.pinbal.webapp.controller;

import es.caib.pinbal.webapp.common.AlertHelper;
import es.caib.pinbal.webapp.helper.AjaxHelper;
import es.caib.pinbal.webapp.helper.ModalHelper;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.web.servlet.support.RequestContext;

import javax.activation.MimetypesFileTypeMap;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Locale;

/**
 * Controlador base que implementa funcionalitats comunes.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
public class BaseController implements MessageSourceAware {

	MessageSource messageSource;

	protected String ajaxUrlOk() {
		return "redirect:" + AjaxHelper.ACCIO_AJAX_OK;
	}

	protected String getAjaxControllerReturnValueSuccess(HttpServletRequest request, String url, String messageKey) {
		return getAjaxControllerReturnValueSuccess(request, url, messageKey, null);
	}

	protected String getAjaxControllerReturnValueSuccess(
			HttpServletRequest request,
			String url,
			String messageKey,
			Object[] messageArgs) {
		if (messageKey != null) {
			AlertHelper.success(request, getMessage(request, messageKey, messageArgs));
		}
		if (AjaxHelper.isAjax(request)) {
			return ajaxUrlOk();
		} else {
			return url;
		}
	}

	protected String getAjaxControllerReturnValueError(HttpServletRequest request, String url, String messageKey) {
		return getAjaxControllerReturnValueError(request, url, messageKey, null);
	}

	protected String getAjaxControllerReturnValueError(
			HttpServletRequest request,
			String url,
			String messageKey,
			Object[] messageArgs) {
		if (messageKey != null) {
			AlertHelper.error(request, getMessage(request, messageKey, messageArgs));
		}
		if (AjaxHelper.isAjax(request)) {
			return ajaxUrlOk();
		} else {
			return url;
		}
	}

	protected String getModalControllerReturnValueSuccess(
			HttpServletRequest request,
			String url,
			String messageKey) {
		return getModalControllerReturnValueSuccess(
				request,
				url,
				messageKey,
				null);
	}
	protected String getModalControllerReturnValueSuccess(
			HttpServletRequest request,
			String url,
			String messageKey,
			Object[] messageArgs) {
		if (messageKey != null) {
			AlertHelper.success(
					request, 
					getMessage(
							request, 
							messageKey,
							messageArgs));
		}
		if (ModalHelper.isModal(request)) {
			//String redirectionPath = redirectFromModal ? url : "";
			return modalUrlTancar();
		} else {
			return url;
		}
	}

	protected String getModalControllerReturnValueWarning(
			HttpServletRequest request,
			String url,
			String messageKey,
			Object[] messageArgs) {
		if (messageKey != null) {
			AlertHelper.warning(
					request, 
					getMessage(
							request, 
							messageKey,
							messageArgs));
		}
		if (ModalHelper.isModal(request)) {
			//String redirectionPath = redirectFromModal ? url : "";
			return modalUrlTancar();
		} else {
			return url;
		}
	}

	protected String getModalControllerReturnValueError(
			HttpServletRequest request,
			String url,
			String messageKey) {
		return getModalControllerReturnValueError(
				request,
				url,
				messageKey,
				null);
	}
	protected String getModalControllerReturnValueError(
			HttpServletRequest request,
			String url,
			String messageKey,
			Object[] messageArgs) {
		if (messageKey != null) {
			AlertHelper.error(
					request, 
					getMessage(
							request, 
							messageKey,
							messageArgs));
		}
		if (ModalHelper.isModal(request)) {
			return modalUrlTancar();
		} else {
			return url;
		}
	}

	protected String modalUrlTancar() {
		return "redirect:" + ModalHelper.ACCIO_MODAL_TANCAR;
	}

	protected void writeFileToResponse(
			String fileName,
			byte[] fileContent,
			HttpServletResponse response) throws IOException {
		writeFileToResponse(
				fileName,
				null,
				fileContent,
				response);
	}
	protected void writeFileToResponse(
			String fileName,
			String contentType,
			byte[] fileContent,
			HttpServletResponse response) throws IOException {
		response.setHeader("Pragma", "");
		response.setHeader("Expires", "");
		response.setHeader("Cache-Control", "");
		response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
		if (contentType != null) {
			response.setContentType(contentType);
		} else {
			response.setContentType(new MimetypesFileTypeMap().getContentType(fileName));
		}
		response.getOutputStream().write(fileContent);
	}

	protected String getMessage(HttpServletRequest request, String key, Object[] args) {
		String message = messageSource.getMessage(
				key,
				args,
				"???" + key + "???",
				new RequestContext(request).getLocale());
		return message;
	}
	protected String getMessage(String key, Object[] args, Locale locale) {
		String message = messageSource.getMessage(
				key,
				args,
				"???" + key + "???",
				locale);
		return message;
	}

	protected String getMessage(HttpServletRequest request, String key) {
		return getMessage(request, key, null);
	}
	protected String getMessage(String key, Locale locale) {
		return getMessage(key, null, locale);
	}

	public void setMessageSource(MessageSource messageSource) {
		this.messageSource = messageSource;
	}

}
