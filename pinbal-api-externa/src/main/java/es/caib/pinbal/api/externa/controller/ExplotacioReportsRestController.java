/**
 * 
 */
package es.caib.pinbal.api.externa.controller;

import es.caib.pinbal.api.externa.api.ExplotacioReportsApi;
import es.caib.pinbal.client.comu.*;
import es.caib.pinbal.logic.intf.dto.InformeGeneralEstatDto;
import es.caib.pinbal.logic.intf.dto.InformeProcedimentDto;
import es.caib.pinbal.logic.intf.dto.InformeUsuariDto;
import es.caib.pinbal.logic.intf.dto.ServeiDto;
import es.caib.pinbal.logic.intf.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Controlador pel servei REST de consulta d'informes.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/reports")
public class ExplotacioReportsRestController implements ExplotacioReportsApi {

	private final EntitatService entitatService;
	private final ProcedimentService procedimentService;
	private final ServeiService serveiService;
	private final UsuariService usuariService;
	private final ConsultaService consultaService;
	private final HistoricConsultaService historicConsultaService;

	@Override
	@PreAuthorize("hasRole('PBL_REPORT')")
	@GetMapping(value= "/procediments", produces = "application/json")
	// IMPORTANT: Si es modifica aquest endpoint, actualitzar també la documentació OpenAPI definida a la interfície ExplotacioReportsApi.
	public ResponseEntity<List<EntitatEstadistiques>> procediments(
			HttpServletRequest request) {
		// Informe de procediments agrupats per entitat i departament
		List<InformeProcedimentDto> informeProcediments = procedimentService.informeProcedimentsAgrupatsEntitatDepartament();
		List<EntitatEstadistiques> entitats = new ArrayList<EntitatEstadistiques>();
		EntitatEstadistiques entitatActual = null;
		Long entitatActualId = null;
		DepartamentEstadistiques departamentActual = null;
		for (InformeProcedimentDto informeProcediment: informeProcediments) {
			if (entitatActual == null || !entitatActualId.equals(informeProcediment.getEntitat().getId())) {
				entitatActualId = informeProcediment.getEntitat().getId();
				entitatActual = new EntitatEstadistiques();
				entitatActual.setCodi(informeProcediment.getEntitat().getCodi());
				entitatActual.setNom(informeProcediment.getEntitat().getNom());
				entitatActual.setNif(informeProcediment.getEntitat().getCif());
				entitats.add(entitatActual);
			}
			if (informeProcediment.getDepartament() != null) {
				if (departamentActual == null || departamentActual.getNom() != informeProcediment.getDepartament()) {
					departamentActual = new DepartamentEstadistiques();
					//departamentActual.setCodi(informeProcediment.getDepartamentCodi());
					departamentActual.setNom(informeProcediment.getDepartament());
					if (entitatActual.getDepartaments() == null) {
						entitatActual.setDepartaments(new ArrayList<DepartamentEstadistiques>());
					}
					entitatActual.getDepartaments().add(departamentActual);
				}
				ProcedimentEstadistiques procediment = new ProcedimentEstadistiques();
				procediment.setCodi(informeProcediment.getCodi());
				procediment.setNom(informeProcediment.getNom());
				procediment.setActiu(informeProcediment.isActiu());
				if (departamentActual.getProcediments() == null) {
					departamentActual.setProcediments(new ArrayList<ProcedimentEstadistiques>());
				}
				departamentActual.getProcediments().add(procediment);
			}
		}
		return new ResponseEntity<List<EntitatEstadistiques>>(entitats, HttpStatus.OK);
	}

	@Override
	@PreAuthorize("hasRole('PBL_REPORT')")
	@GetMapping(value= "/usuaris", produces = "application/json")
	// IMPORTANT: Si es modifica aquest endpoint, actualitzar també la documentació OpenAPI definida a la interfície ExplotacioReportsApi.
	public ResponseEntity<List<EntitatEstadistiques>> usuaris(
			HttpServletRequest request) {
		// Informe d'usuaris agrupats per entitat i departament
		List<InformeUsuariDto> informeUsuaris = usuariService.informeUsuarisAgrupatsEntitatDepartament();
		List<EntitatEstadistiques> entitats = new ArrayList<EntitatEstadistiques>();
		EntitatEstadistiques entitatActual = null;
		Long entitatActualId = null;
		DepartamentEstadistiques departamentActual = null;
		for (InformeUsuariDto informeUsuari: informeUsuaris) {
			if (entitatActual == null || !entitatActualId.equals(informeUsuari.getEntitat().getId())) {
				entitatActualId = informeUsuari.getEntitat().getId();
				entitatActual = new EntitatEstadistiques();
				entitatActual.setCodi(informeUsuari.getEntitat().getCodi());
				entitatActual.setNom(informeUsuari.getEntitat().getNom());
				entitatActual.setNif(informeUsuari.getEntitat().getCif());
				entitats.add(entitatActual);
			}
			if (informeUsuari.getDepartament() != null) {
				if (departamentActual == null || departamentActual.getNom() != informeUsuari.getDepartament()) {
					departamentActual = new DepartamentEstadistiques();
					//departamentActual.setCodi(informeProcediment.getDepartamentCodi());
					departamentActual.setNom(informeUsuari.getDepartament());
					if (entitatActual.getDepartaments() == null) {
						entitatActual.setDepartaments(new ArrayList<DepartamentEstadistiques>());
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
		return new ResponseEntity<List<EntitatEstadistiques>>(entitats, HttpStatus.OK);
	}

	@Override
	@PreAuthorize("hasRole('PBL_REPORT')")
	@GetMapping(value= "/serveis", produces = "application/json")
	// IMPORTANT: Si es modifica aquest endpoint, actualitzar també la documentació OpenAPI definida a la interfície ExplotacioReportsApi.
	public ResponseEntity<List<ServeiEstadistiques>> serveis(
			HttpServletRequest request) {
		// Informe de seveis
		List<ServeiDto> informeServeis = serveiService.findActius();
		List<ServeiEstadistiques> serveis = new ArrayList<ServeiEstadistiques>();
		for (ServeiDto informeServei: informeServeis) {
			ServeiEstadistiques servei = new ServeiEstadistiques();
			servei.setCodi(informeServei.getCodi());
			servei.setNom(informeServei.getDescripcio());
			servei.setEmisor(informeServei.getScspEmisorNom());
			serveis.add(servei);
		}
		return new ResponseEntity<List<ServeiEstadistiques>>(serveis, HttpStatus.OK);
	}

	@Override
	@PreAuthorize("hasRole('PBL_REPORT')")
	@GetMapping(value= "/general", produces = "application/json")
	// IMPORTANT: Si es modifica aquest endpoint, actualitzar també la documentació OpenAPI definida a la interfície ExplotacioReportsApi.
	public ResponseEntity<List<EntitatEstadistiques>> general(
			HttpServletRequest request,
			@RequestParam(value = "historic", required = false) boolean historic,
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) final Date dataInici,
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) final Date dataFi) {
		// Informe general d'estat
		List<EntitatEstadistiques> entitats = new ArrayList<EntitatEstadistiques>();
		List<InformeGeneralEstatDto> informeGeneralFiles;
		if (historic) {
			informeGeneralFiles = historicConsultaService.informeGeneralEstat(dataInici, dataFi);
		} else {
			informeGeneralFiles = consultaService.informeGeneralEstat(dataInici, dataFi);
		}
		EntitatEstadistiques entitatActual = null;
		String entitatActualCodi = null;
		DepartamentEstadistiques departamentActual = null;
		ProcedimentEstadistiques procedimentActual = null;
		for (InformeGeneralEstatDto informeGeneralFila: informeGeneralFiles) {
			if (entitatActual == null || !entitatActualCodi.equals(informeGeneralFila.getEntitatCodi())) {
				entitatActualCodi = informeGeneralFila.getEntitatCodi();
				entitatActual = new EntitatEstadistiques();
				entitatActual.setCodi(informeGeneralFila.getEntitatCodi());
				entitatActual.setNom(informeGeneralFila.getEntitatNom());
				entitatActual.setNif(informeGeneralFila.getEntitatCif());
				entitats.add(entitatActual);
			}
//			if (informeGeneralFila.getDepartament() != null) {
				if (departamentActual == null || departamentActual.getNom() != informeGeneralFila.getDepartament()) {
					departamentActual = new DepartamentEstadistiques();
					//departamentActual.setCodi(informeProcediment.getDepartamentCodi());
					departamentActual.setNom(informeGeneralFila.getDepartament());
					if (entitatActual.getDepartaments() == null) {
						entitatActual.setDepartaments(new ArrayList<DepartamentEstadistiques>());
					}
					entitatActual.getDepartaments().add(departamentActual);
				}
				if (procedimentActual == null || procedimentActual.getCodi() != informeGeneralFila.getProcedimentCodi()) {
					procedimentActual = new ProcedimentEstadistiques();
					procedimentActual.setCodi(informeGeneralFila.getProcedimentCodi());
					procedimentActual.setNom(informeGeneralFila.getProcedimentNom());
					if (departamentActual.getProcediments() == null) {
						departamentActual.setProcediments(new ArrayList<ProcedimentEstadistiques>());
					}
					departamentActual.getProcediments().add(procedimentActual);
				}
				ServeiEstadistiques servei = new ServeiEstadistiques();
				servei.setCodi(informeGeneralFila.getServeiCodi());
				servei.setNom(informeGeneralFila.getServeiNom());
				servei.setEmisor(informeGeneralFila.getServeiEmisor().getNom());
				servei.setUsuarisAmbPermisos(informeGeneralFila.getServeiUsuaris());
				servei.setConsultesOk(informeGeneralFila.getPeticionsCorrectes());
				servei.setConsultesError(informeGeneralFila.getPeticionsErronees());
				if (procedimentActual.getServeis() == null) {
					procedimentActual.setServeis(new ArrayList<ServeiEstadistiques>());
				}
				procedimentActual.getServeis().add(servei);
//			}
		}
		return new ResponseEntity<List<EntitatEstadistiques>>(entitats, HttpStatus.OK);
	}

}
