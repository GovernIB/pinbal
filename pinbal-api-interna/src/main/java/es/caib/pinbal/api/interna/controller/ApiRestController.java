package es.caib.pinbal.api.interna.controller;

import com.wordnik.swagger.annotations.Api;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;

/**
 * Controlador que exposa la documentació de la API REST.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/api")
public class ApiRestController {

	@RequestMapping(value = {"/apidoc", "/rest" }, method = RequestMethod.GET)
	public String documentacio(HttpServletRequest request) {
		return "restDoc";
	}

}