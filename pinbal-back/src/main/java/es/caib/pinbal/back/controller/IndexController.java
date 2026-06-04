/**
 * 
 */
package es.caib.pinbal.back.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;

import es.caib.pinbal.back.helper.RolHelper;

/**
 * Controlador per a la pàgina inicial (index).
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/index")
public class IndexController {

	@GetMapping
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

	@GetMapping("/missatges")
	public String get() {
		return "import/alerts";
	}

	@GetMapping("/avisos")
	public String getAvisos() {
		return "util/avisos";
	}
}
