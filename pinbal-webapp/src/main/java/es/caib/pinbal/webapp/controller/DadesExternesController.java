/**
 * 
 */
package es.caib.pinbal.webapp.controller;

import es.caib.pinbal.core.dto.dadesexternes.Municipi;
import es.caib.pinbal.core.dto.dadesexternes.Pais;
import es.caib.pinbal.core.dto.dadesexternes.Provincia;
import es.caib.pinbal.core.service.DadesExternesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

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

	@RequestMapping("/paisos")
	@ResponseBody
	public List<Pais> paisos(
			HttpServletResponse response) throws IOException {
		return dadesExternesService.findPaisos();
	}

	@RequestMapping("/provincies")
	@ResponseBody
	public List<Provincia> provincies(
			HttpServletResponse response) throws IOException {
		return dadesExternesService.findProvincies();
	}

	@RequestMapping("/municipis/{provinciaCodi}")
	@ResponseBody
	public List<Municipi> municipis(
			@PathVariable String provinciaCodi,
			HttpServletResponse response) throws IOException {
		return dadesExternesService.findMunicipisPerProvincia(provinciaCodi);
	}

}
