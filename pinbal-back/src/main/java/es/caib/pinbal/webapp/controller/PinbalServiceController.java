package es.caib.pinbal.webapp.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.wordnik.swagger.annotations.Api;

/**
 * Controlador que exposa un servei REST per a la gestio de
 * notificacions.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/api")
@Api(value = "/notificacio", description = "Notificaio API")
public class PinbalServiceController extends BaseController {

	@RequestMapping(value = {"/apidoc", "/rest" }, method = RequestMethod.GET)
	public String documentacio(HttpServletRequest request) {
		return "restDoc";
	}

}