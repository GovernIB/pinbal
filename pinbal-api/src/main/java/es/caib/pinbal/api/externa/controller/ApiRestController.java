package es.caib.pinbal.api.externa.controller;

import com.wordnik.swagger.annotations.Api;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;

/**
 * Controlador que exposa un servei REST per a la gestio de
 * notificacions.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/api")
@Api(value = "/notificacio", description = "Notificaio API")
public class ApiRestController {

	@RequestMapping(value = {"/apidoc", "/rest" }, method = RequestMethod.GET)
	public String documentacio(HttpServletRequest request) {
		return "restDoc";
	}

}