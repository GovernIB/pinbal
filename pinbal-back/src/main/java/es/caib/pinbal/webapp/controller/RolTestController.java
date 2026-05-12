/**
 * 
 */
package es.caib.pinbal.webapp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import es.caib.pinbal.core.service.ConsultaService;
import es.caib.pinbal.core.service.EntitatService;
import es.caib.pinbal.core.service.ProcedimentService;
import es.caib.pinbal.core.service.exception.ConsultaNotFoundException;
import es.caib.pinbal.core.service.exception.ScspException;

/**
 * Controlador per a proves.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/roltest")
public class RolTestController {

	@Autowired
	private EntitatService entitatService;
	@Autowired
	private ProcedimentService procedimentService;
	@Autowired
	private ConsultaService consultaService;

	@RequestMapping("/consultaAdmin")
	public String consultaAdmin() {
		entitatService.findAll();
		return "redirect:../index";
	}

	@RequestMapping("/consultaRepres")
	public String consultaRepres() {
		procedimentService.findById(new Long(1));
		return "redirect:../index";
	}

	@RequestMapping("/consultaDeleg")
	public String consultaDeleg() throws ScspException, ConsultaNotFoundException {
		consultaService.findOneDelegat(new Long(1));
		return "redirect:../index";
	}

}
