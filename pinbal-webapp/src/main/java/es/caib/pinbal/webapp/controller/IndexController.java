/**
 * 
 */
package es.caib.pinbal.webapp.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import es.caib.pinbal.webapp.common.RolHelper;

/**
 * Controlador per a la pàgina inicial (index).
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/index")
public class IndexController {

	@RequestMapping(method = RequestMethod.GET)
	public String get(
			HttpServletRequest request) {
		if (RolHelper.isRolActualDelegat(request)) {
			return "redirect:consulta";
		} else if (RolHelper.isRolActualRepresentant(request)) {
			return "redirect:representant/usuari";
		} else if (RolHelper.isRolActualAdministrador(request)) {
			return "redirect:admin/consulta";
		} else if (RolHelper.isRolActualAuditor(request)) {
			return "redirect:auditor";
		} else if (RolHelper.isRolActualSuperauditor(request)) {
			return "redirect:superauditor";
		} else {
			return "delegatNoAutoritzat";
		}
	}

	@RequestMapping(value = "/missatges", method = RequestMethod.GET)
	public String get() {
		return "import/alerts";
	}
}
