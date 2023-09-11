package es.caib.pinbal.api.interna.controller;

import es.caib.pinbal.client.comu.EntitatInfo;
import es.caib.pinbal.core.service.EntitatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * Controlador que exposa la documentació de la API REST.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/api")
public class ApiRestController {

	@Autowired
	private EntitatService entitatService;

	@RequestMapping(value = {"/apidoc", "/rest" }, method = RequestMethod.GET)
	public String documentacio(HttpServletRequest request) {
		return "restDoc";
	}

	@RequestMapping(value= "/entitats", method = RequestMethod.GET, produces = "application/json")
	public ResponseEntity<List<EntitatInfo>> test() {
		List<EntitatInfo> entitats = entitatService.getEntitatsInfo();
		return new ResponseEntity<List<EntitatInfo>>(entitats, HttpStatus.OK);
	}
}