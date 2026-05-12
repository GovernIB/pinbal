/**
 * 
 */
package es.caib.pinbal.webapp.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * Controlador per a les consultes ajax dels enums.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/sessionajax")
public class AjaxSessionController extends BaseController {


	@RequestMapping(value = "/{sessionAttrName}", method = RequestMethod.POST, consumes = "application/json")
	@ResponseBody
	public ResponseEntity<Void> enumValorsAmbText(
			HttpServletRequest request,
			@PathVariable String sessionAttrName,
			@RequestBody String checked) {

		Boolean bChecked = Boolean.parseBoolean(checked);
		request.getSession().setAttribute(sessionAttrName, bChecked);
		return new ResponseEntity<Void>(HttpStatus.OK);
	}

}
