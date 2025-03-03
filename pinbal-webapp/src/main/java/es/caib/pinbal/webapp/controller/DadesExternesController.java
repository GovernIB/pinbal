/**
 * 
 */
package es.caib.pinbal.webapp.controller;

import es.caib.pinbal.core.dto.IdiomaEnumDto;
import es.caib.pinbal.core.dto.UsuariDto;
import es.caib.pinbal.core.dto.dadesexternes.Municipi;
import es.caib.pinbal.core.dto.dadesexternes.Pais;
import es.caib.pinbal.core.dto.dadesexternes.Provincia;
import es.caib.pinbal.core.service.DadesExternesService;
import es.caib.pinbal.core.service.exception.AccesExternException;
import es.caib.pinbal.webapp.common.UsuariHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
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
			HttpServletRequest request,
			HttpServletResponse response) throws IOException, AccesExternException {
		return dadesExternesService.findPaisos(getIdioma(request));
	}

	@RequestMapping("/provincies")
	@ResponseBody
	public List<Provincia> provincies(
			HttpServletRequest request,
			HttpServletResponse response) throws IOException, AccesExternException {
		UsuariDto dadesUsuariActual = UsuariHelper.getDadesUsuariActual(request);
		return dadesExternesService.findProvincies(getIdioma(request));
	}

	@RequestMapping("/municipis/{provinciaCodi}")
	@ResponseBody
	public List<Municipi> municipis(
			@PathVariable String provinciaCodi,
			HttpServletResponse response) throws IOException {
		return dadesExternesService.findMunicipisPerProvincia(provinciaCodi);
	}

	private static IdiomaEnumDto getIdioma(HttpServletRequest request) throws AccesExternException {
		IdiomaEnumDto idioma = IdiomaEnumDto.CA;
		UsuariDto dadesUsuariActual = UsuariHelper.getDadesUsuariActual(request);
		if (dadesUsuariActual != null && dadesUsuariActual.getIdioma() != null) {
			if ("ES".equalsIgnoreCase(dadesUsuariActual.getIdioma())) {
				idioma = IdiomaEnumDto.ES;
			}
		}
		return idioma;
	}

}
