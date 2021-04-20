/**
 * 
 */
package es.caib.pinbal.webapp.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import es.caib.pinbal.client.comu.Departament;
import es.caib.pinbal.client.comu.Entitat;
import es.caib.pinbal.client.comu.Procediment;
import es.caib.pinbal.client.comu.Servei;
import es.caib.pinbal.client.comu.Usuari;
import es.caib.pinbal.core.dto.InformeGeneralEstatDto;
import es.caib.pinbal.core.dto.InformeProcedimentDto;
import es.caib.pinbal.core.dto.InformeUsuariDto;
import es.caib.pinbal.core.dto.ServeiDto;
import es.caib.pinbal.core.service.ConsultaService;
import es.caib.pinbal.core.service.ProcedimentService;
import es.caib.pinbal.core.service.ServeiService;
import es.caib.pinbal.core.service.UsuariService;

/**
 * Controlador pel servei REST de consulta d'informes.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/api/interna/reports")
public class ExplotacioDadesInternesRestController extends BaseController {

	@Autowired
	private ProcedimentService procedimentService;
	@Autowired
	private UsuariService usuariService;
	@Autowired
	private ServeiService serveiService;
	@Autowired
	private ConsultaService consultaService;

	@RequestMapping(
			value= "/procediments",
			method = RequestMethod.POST,
			produces = "application/json")
	public ResponseEntity<List<Entitat>> procediments(
			HttpServletRequest request) {
		// Informe de procediments agrupats per entitat i departament
		List<InformeProcedimentDto> informeProcediments = procedimentService.informeProcedimentsAgrupatsEntitatDepartament();
		List<Entitat> entitats = new ArrayList<Entitat>();
		Entitat entitatActual = null;
		Long entitatActualId = null;
		Departament departamentActual = null;
		for (InformeProcedimentDto informeProcediment: informeProcediments) {
			if (entitatActual == null || !entitatActualId.equals(informeProcediment.getEntitat().getId())) {
				entitatActualId = informeProcediment.getEntitat().getId();
				entitatActual = new Entitat();
				entitatActual.setCodi(informeProcediment.getEntitat().getCodi());
				entitatActual.setNom(informeProcediment.getEntitat().getNom());
				entitatActual.setNif(informeProcediment.getEntitat().getCif());
				entitats.add(entitatActual);
			}
			if (informeProcediment.getDepartament() != null) {
				if (departamentActual == null || departamentActual.getNom() != informeProcediment.getDepartament()) {
					departamentActual = new Departament();
					//departamentActual.setCodi(informeProcediment.getDepartamentCodi());
					departamentActual.setNom(informeProcediment.getDepartament());
					if (entitatActual.getDepartaments() == null) {
						entitatActual.setDepartaments(new ArrayList<Departament>());
					}
					entitatActual.getDepartaments().add(departamentActual);
				}
				Procediment procediment = new Procediment();
				procediment.setCodi(informeProcediment.getCodi());
				procediment.setNom(informeProcediment.getNom());
				procediment.setActiu(informeProcediment.isActiu());
				if (departamentActual.getProcediments() == null) {
					departamentActual.setProcediments(new ArrayList<Procediment>());
				}
				departamentActual.getProcediments().add(procediment);
			}
		}
		return new ResponseEntity<List<Entitat>>(entitats, HttpStatus.OK);
	}

	@RequestMapping(
			value= "/usuaris",
			method = RequestMethod.POST,
			produces = "application/json")
	public ResponseEntity<List<Entitat>> usuaris(
			HttpServletRequest request) {
		// Informe d'usuaris agrupats per entitat i departament
		List<InformeUsuariDto> informeUsuaris = usuariService.informeUsuarisAgrupatsEntitatDepartament();
		List<Entitat> entitats = new ArrayList<Entitat>();
		Entitat entitatActual = null;
		Long entitatActualId = null;
		Departament departamentActual = null;
		for (InformeUsuariDto informeUsuari: informeUsuaris) {
			if (entitatActual == null || !entitatActualId.equals(informeUsuari.getEntitat().getId())) {
				entitatActualId = informeUsuari.getEntitat().getId();
				entitatActual = new Entitat();
				entitatActual.setCodi(informeUsuari.getEntitat().getCodi());
				entitatActual.setNom(informeUsuari.getEntitat().getNom());
				entitatActual.setNif(informeUsuari.getEntitat().getCif());
				entitats.add(entitatActual);
			}
			if (informeUsuari.getDepartament() != null) {
				if (departamentActual == null || departamentActual.getNom() != informeUsuari.getDepartament()) {
					departamentActual = new Departament();
					//departamentActual.setCodi(informeProcediment.getDepartamentCodi());
					departamentActual.setNom(informeUsuari.getDepartament());
					if (entitatActual.getDepartaments() == null) {
						entitatActual.setDepartaments(new ArrayList<Departament>());
					}
					entitatActual.getDepartaments().add(departamentActual);
				}
				Usuari usuari = new Usuari();
				usuari.setCodi(informeUsuari.getCodi());
				usuari.setNom(informeUsuari.getNom());
				usuari.setNif(informeUsuari.getNif());
				if (departamentActual.getUsuaris() == null) {
					departamentActual.setUsuaris(new ArrayList<Usuari>());
				}
				departamentActual.getUsuaris().add(usuari);
			}
		}
		return new ResponseEntity<List<Entitat>>(entitats, HttpStatus.OK);
	}

	@RequestMapping(
			value= "/serveis",
			method = RequestMethod.POST,
			produces = "application/json")
	public ResponseEntity<List<Servei>> serveis(
			HttpServletRequest request) {
		// Informe de seveis
		List<ServeiDto> informeServeis = serveiService.findActius();
		List<Servei> serveis = new ArrayList<Servei>();
		for (ServeiDto informeServei: informeServeis) {
			Servei servei = new Servei();
			servei.setCodi(informeServei.getCodi());
			servei.setNom(informeServei.getDescripcio());
			servei.setEmisor(informeServei.getScspEmisorNom());
			serveis.add(servei);
		}
		return new ResponseEntity<List<Servei>>(serveis, HttpStatus.OK);
	}

	@RequestMapping(
			value= "/general",
			method = RequestMethod.POST,
			produces = "application/json")
	public ResponseEntity<List<Entitat>> general(
			HttpServletRequest request,
			@RequestParam final Date dataInici,
			@RequestParam final Date dataFi) {
		// Informe general d'estat
		List<Entitat> entitats = new ArrayList<Entitat>();
		List<InformeGeneralEstatDto> informeGeneralFiles = consultaService.informeGeneralEstat(dataInici, dataFi);
		Entitat entitatActual = null;
		String entitatActualCif = null;
		Departament departamentActual = null;
		Procediment procedimentActual = null;
		for (InformeGeneralEstatDto informeGeneralFila: informeGeneralFiles) {
			if (entitatActual == null || !entitatActualCif.equals(informeGeneralFila.getEntitatCif())) {
				entitatActualCif = informeGeneralFila.getEntitatCif();
				entitatActual = new Entitat();
				entitatActual.setNom(informeGeneralFila.getEntitatNom());
				entitatActual.setNif(informeGeneralFila.getEntitatCif());
				entitats.add(entitatActual);
			}
			if (informeGeneralFila.getDepartament() != null) {
				if (departamentActual == null || departamentActual.getNom() != informeGeneralFila.getDepartament()) {
					departamentActual = new Departament();
					//departamentActual.setCodi(informeProcediment.getDepartamentCodi());
					departamentActual.setNom(informeGeneralFila.getDepartament());
					if (entitatActual.getDepartaments() == null) {
						entitatActual.setDepartaments(new ArrayList<Departament>());
					}
					entitatActual.getDepartaments().add(departamentActual);
				}
				if (procedimentActual == null || procedimentActual.getCodi() != informeGeneralFila.getProcedimentCodi()) {
					procedimentActual = new Procediment();
					procedimentActual.setCodi(informeGeneralFila.getProcedimentCodi());
					procedimentActual.setNom(informeGeneralFila.getProcedimentNom());
					if (departamentActual.getProcediments() == null) {
						departamentActual.setProcediments(new ArrayList<Procediment>());
					}
					departamentActual.getProcediments().add(procedimentActual);
				}
				Servei servei = new Servei();
				servei.setCodi(informeGeneralFila.getServeiCodi());
				servei.setNom(informeGeneralFila.getServeiNom());
				servei.setEmisor(informeGeneralFila.getServeiEmisor().getNom());
				servei.setUsuarisAmbPermisos(informeGeneralFila.getServeiUsuaris());
				servei.setConsultesOk(informeGeneralFila.getPeticionsCorrectes());
				servei.setConsultesError(informeGeneralFila.getPeticionsErronees());
				if (procedimentActual.getServeis() == null) {
					procedimentActual.setServeis(new ArrayList<Servei>());
				}
				procedimentActual.getServeis().add(servei);
			}
		}
		return new ResponseEntity<List<Entitat>>(entitats, HttpStatus.OK);
	}

}
