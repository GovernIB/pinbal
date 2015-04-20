/**
 * 
 */
package es.caib.pinbal.webapp.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import es.caib.pinbal.core.service.DadesExternesService;

/**
 * Controlador per a proves.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/dades")
public class DadesExternesController {

	@Autowired
	private DadesExternesService dadesExternesService;

	@RequestMapping("/provincies")
	public void provincies(
			HttpServletResponse response) throws IOException {
		response.getOutputStream().write(
				dadesExternesService.findProvincies());
	}

	@RequestMapping("/municipis/{provinciaCodi}")
	public void municipis(
			@PathVariable String provinciaCodi,
			HttpServletResponse response) throws IOException {
		response.getOutputStream().write(
				dadesExternesService.findMunicipisPerProvincia(provinciaCodi));
	}

}
