package es.caib.pinbal.api.interna.controller;

import es.caib.pinbal.api.interna.api.ApiApi;
import es.caib.pinbal.client.comu.EntitatInfo;
import es.caib.pinbal.logic.intf.service.EntitatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Controlador que exposa la documentació de la API REST.
 *
 * @author Limit Tecnologies <limit@limit.es>
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ApiRestController implements ApiApi {

	private final EntitatService entitatService;

	/**
	 * Si es modifica aquest endpoint, actualitzar també la documentació OpenAPI
	 * definida a la interfície ApiApi.
	 */
	@Override
	@GetMapping(value= "/entitats", produces = "application/json")
	public ResponseEntity<List<EntitatInfo>> test() {
		List<EntitatInfo> entitats = entitatService.getEntitatsInfo();
		return new ResponseEntity<List<EntitatInfo>>(entitats, HttpStatus.OK);
	}
}