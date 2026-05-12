/**
 * 
 */
package es.caib.pinbal.webapp.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import es.caib.pinbal.webapp.helper.HtmlSelectOptionHelper.HtmlOption;

/**
 * Controlador per a les consultes ajax dels enums.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/enumajax") // No podem posar "/ajaxenum" per mor del AjaxInterceptor
public class AjaxEnumController extends BaseController {


	@RequestMapping(value = "/enum/{enumClass}", method = RequestMethod.GET)
	@ResponseBody
	public List<HtmlOption> enumValorsAmbText(
			HttpServletRequest request,
			@PathVariable String enumClass) throws ClassNotFoundException {
		Class<?> enumeracio = Class.forName("es.caib.pinbal.core.dto." + enumClass);
		StringBuilder textKeyPrefix = new StringBuilder();
		String[] textKeys = StringUtils.splitByCharacterTypeCamelCase(enumClass);
		for (String textKey: textKeys) {
			if (!"dto".equalsIgnoreCase(textKey)) {
				textKeyPrefix.append(textKey.toLowerCase());
				textKeyPrefix.append(".");
			}
		}
		List<HtmlOption> resposta = new ArrayList<HtmlOption>();
		if (enumeracio.isEnum()) {
			for (Object e: enumeracio.getEnumConstants()) {
				resposta.add(new HtmlOption(
						((Enum<?>)e).name(),
						getMessage(
								request,
								textKeyPrefix.toString() + ((Enum<?>)e).name(),
								null)));
			}
		}
		return resposta;
	}

}
