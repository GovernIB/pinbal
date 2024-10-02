/**
 * 
 */
package es.caib.pinbal.webapp.controller;

import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import es.caib.pinbal.client.comu.Departament;
import es.caib.pinbal.client.comu.Entitat;
import es.caib.pinbal.client.comu.Procediment;
import es.caib.pinbal.client.comu.Servei;
import es.caib.pinbal.client.comu.Servei.ConsultesOkError;
import es.caib.pinbal.client.comu.TotalAcumulat;
import es.caib.pinbal.client.comu.Usuari;
import es.caib.pinbal.core.dto.CarregaDto;
import es.caib.pinbal.core.dto.ConsultaDto.EstatTipus;
import es.caib.pinbal.core.dto.EntitatDto;
import es.caib.pinbal.core.dto.EstadisticaDto;
import es.caib.pinbal.core.dto.EstadistiquesFiltreDto;
import es.caib.pinbal.core.dto.EstadistiquesFiltreDto.EstadistiquesAgrupacioDto;
import es.caib.pinbal.core.dto.InformeGeneralEstatDto;
import es.caib.pinbal.core.dto.InformeProcedimentDto;
import es.caib.pinbal.core.dto.InformeUsuariDto;
import es.caib.pinbal.core.dto.ProcedimentDto;
import es.caib.pinbal.core.dto.ServeiDto;
import es.caib.pinbal.core.service.ConsultaService;
import es.caib.pinbal.core.service.EntitatService;
import es.caib.pinbal.core.service.HistoricConsultaService;
import es.caib.pinbal.core.service.ProcedimentService;
import es.caib.pinbal.core.service.ServeiService;
import es.caib.pinbal.core.service.UsuariService;
import es.caib.pinbal.core.service.exception.EntitatNotFoundException;
import es.caib.pinbal.core.service.exception.ProcedimentNotFoundException;
import es.caib.pinbal.webapp.command.EstadistiquesFiltreCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Controlador pel servei REST de consulta d'informes.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/api/interna")
public class ExplotacioDadesInternesRestController extends BaseController {

	@Autowired
	private EntitatService entitatService;
	@Autowired
	private ProcedimentService procedimentService;
	@Autowired
	private ServeiService serveiService;
	@Autowired
	private UsuariService usuariService;
	@Autowired
	private ConsultaService consultaService;
	@Autowired
	private HistoricConsultaService historicConsultaService;

	@RequestMapping(
			value= "/reports/procediments",
			method = RequestMethod.GET,
			produces = "application/json")
	@ApiOperation(
			value = "Informe de procediments agrupats per entitat i departament", 
			notes = "Aquest servei retorna una llista dels procediments disponibles a "
					+ "l’aplicació agrupats per entitat i departament. També s’indica si el "
					+ "procediment està actiu o no.<br/><br/>" 
					+ "Lista de objetos de tipo Entitat<br/>"
					+ "Per tant, el JSON resultant serà de la forma de array d'objectes de tipus **Entitat**:<br/>"
					+ "[{Entitat1}, {Entitat2}, {Entitat3}, {...}]<br/>"
					+ "El model d'**Entitat** es pot veure mes a baix en la informació del missatge de resposta.",
			responseContainer = "List",
			response = Entitat.class)
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
			value= "/reports/usuaris",
			method = RequestMethod.GET,
			produces = "application/json")
	@ApiOperation(
			value = "Informe d'usuaris agrupats per entitat i departament", 
			notes = "Aquest servei retorna l’acumulat de peticions realitzades entre dues "
					+ "dates distingint entre les peticions processades correctament o amb "
					+ "errors.<br/><br/>"
					+ "Lista de objetos de tipo Entitat<br/>"
					+ "Per tant, el JSON resultant serà de la forma de array d'objectes de tipus **Entitat**:<br/>"
					+ "[{Entitat1}, {Entitat2}, {Entitat3}, {...}]<br/>"
					+ "El model d'**Entitat** es pot veure mes a baix en la informació del missatge de resposta.",
			responseContainer = "List",
			response = Entitat.class) //, response=ArrayList.class)
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
			value= "/reports/serveis",
			method = RequestMethod.GET,
			produces = "application/json")
	@ApiOperation(
			value = "Informe de serveis", 
			notes = "Retorna una llista amb les serveis i l'estatus.<br/><br/>"
					+ "Lista de objetos de tipo Servei<br/>"
					+ "Per tant, el JSON resultant serà de la forma de array d'objectes de tipus **Servei**:<br/>"
					+ "[{Servei1}, {Servei2}, {Servei3}, {...}]<br/>"
					+ "El model de **Servei** es pot veure mes a baix en la informació del missatge de resposta.",
			responseContainer = "List",
			response = Servei.class)
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
			value= "/reports/general",
			method = RequestMethod.GET,
			produces = "application/json")
	@ApiOperation(
			value = "Informe general d'estat", 
			notes = "Retorna una llista amb les entitats i l'estatus.<br/><br/>"
					+ "Lista de objetos de tipo Entitat<br/>"
					+ "Per tant, el JSON resultant serà de la forma de array d'objectes de tipus **Entitat**:<br/>"
					+ "[{Entitat1}, {Entitat2}, {Entitat3}, {...}]<br/>"
					+ "El model d'**Entitat** es pot veure mes a baix en la informació del missatge de resposta.",
			responseContainer = "List",
			response = Entitat.class)
	public ResponseEntity<List<Entitat>> general(
			HttpServletRequest request,
			@ApiParam(name="historic", value="S'utilitzarà la informació històrica de consultes", required = false, defaultValue = "false")
			@RequestParam(value = "historic", required = false) boolean historic,
			@ApiParam(name="dataInici", value="Data d'inici")
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) final Date dataInici,
			@ApiParam(name="dataFi", value="Data de fi")
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) final Date dataFi
	) {
		// Informe general d'estat
		List<Entitat> entitats = new ArrayList<Entitat>();
		List<InformeGeneralEstatDto> informeGeneralFiles;
		if (historic) {
			informeGeneralFiles = historicConsultaService.informeGeneralEstat(dataInici, dataFi);
		} else {
			informeGeneralFiles = consultaService.informeGeneralEstat(dataInici, dataFi);
		}
		Entitat entitatActual = null;
		String entitatActualCodi = null;
		Departament departamentActual = null;
		Procediment procedimentActual = null;
		for (InformeGeneralEstatDto informeGeneralFila: informeGeneralFiles) {
			if (entitatActual == null || !entitatActualCodi.equals(informeGeneralFila.getEntitatCodi())) {
				entitatActualCodi = informeGeneralFila.getEntitatCodi();
				entitatActual = new Entitat();
				entitatActual.setCodi(informeGeneralFila.getEntitatCodi());
				entitatActual.setNom(informeGeneralFila.getEntitatNom());
				entitatActual.setNif(informeGeneralFila.getEntitatCif());
				entitats.add(entitatActual);
			}
//			if (informeGeneralFila.getDepartament() != null) {

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
//			}
		}
		return new ResponseEntity<List<Entitat>>(entitats, HttpStatus.OK);
	}

	@RequestMapping(
			value= "/stats/consultes",
			method = RequestMethod.GET,
			produces = "application/json")
	@ApiOperation(
			value = "Estadística de consultes", 
			notes = "Aquest servei retorna una l’acumulat de peticions agrupades per "
					+ "procediment i servei distingint entre les peticions realitzades via "
					+ "interfície web o via recobriment i també si hi ha hagut errors o no.<br/><br/>"
					+ "Lista de objetos de tipo Procediment<br/>"
					+ "Per tant, el JSON resultant serà de la forma de array d'objectes de tipus **Procediment**:<br/>"
					+ "[{Procediment}, {Procediment2}, {Procediment3}, {...}]<br/>"
					+ "El model de **Procediment** es pot veure mes a baix en la informació del missatge de resposta.<br/>",
			responseContainer = "List",
			response = Procediment.class)
	public ResponseEntity<List<Procediment>> consultes(
			HttpServletRequest request,
			@ApiParam(name="entitatCodi", value="Codi de l'entitat") 
			@RequestParam final String entitatCodi,
			@ApiParam(name="procedimentCodi", value="Codi del procediment", required=false) 
			@RequestParam(required = false) final String procedimentCodi,
			@ApiParam(name="serveiCodi", value="Codi del servei", required=false) 
			@RequestParam(required = false) final String serveiCodi,
			@ApiParam(name="estat", value="Tipus d'estat", required=false) 
			@RequestParam(required = false) final EstatTipus estat,
			@ApiParam(name="dataInici", value="Data d'inici", required = false)
			@RequestParam(required = false) final Date dataInici,
			@ApiParam(name="dataFi", value="Data de fi", required=false) 
			@RequestParam(required = false) final Date dataFi) throws EntitatNotFoundException, ProcedimentNotFoundException {
		// Estadística de consultes
		List<EstadisticaDto> estadistiques = consultaService.findEstadistiquesByFiltre(
				getEstadistiquesFiltre(
						entitatCodi,
						procedimentCodi,
						serveiCodi,
						estat,
						null,
						dataInici,
						dataFi));
		List<Procediment> estadisticaProcediments = new ArrayList<Procediment>();
		Procediment procedimentActual = null;
		Long procedimentActualId = null;
		for (EstadisticaDto estadistica: estadistiques) {
			if (procedimentActual == null || !procedimentActualId.equals(estadistica.getProcedimentId())) {
				procedimentActualId = estadistica.getProcedimentId();
				procedimentActual = new Procediment();
				procedimentActual.setCodi(estadistica.getProcedimentCodi());
				procedimentActual.setNom(estadistica.getProcedimentNom());
				estadisticaProcediments.add(procedimentActual);
			}
			Servei servei = new Servei();
			servei.setCodi(estadistica.getServeiCodi());
			servei.setNom(estadistica.getServeiNom());
			servei.setConsultesWeb(new ConsultesOkError(
					estadistica.getNumWebUIOk(),
					estadistica.getNumWebUIError()));
			servei.setConsultesRecobriment(new ConsultesOkError(
					estadistica.getNumRecobrimentOk(),
					estadistica.getNumRecobrimentError()));
			servei.setConsultesTotal(new ConsultesOkError(
					estadistica.getNumWebUIOk() + estadistica.getNumRecobrimentOk(),
					estadistica.getNumWebUIError() + estadistica.getNumRecobrimentError()));
			if (estadistica.isConteSumatori()) {
				procedimentActual.setConsultesWeb(new ConsultesOkError(
						estadistica.getSumatoriNumWebUIOk(),
						estadistica.getSumatoriNumWebUIError()));
				procedimentActual.setConsultesRecobriment(new ConsultesOkError(
						estadistica.getSumatoriNumRecobrimentOk(),
						estadistica.getSumatoriNumRecobrimentError()));
				procedimentActual.setConsultesTotal(new ConsultesOkError(
						estadistica.getSumatoriNumWebUIOk() + estadistica.getSumatoriNumRecobrimentOk(),
						estadistica.getSumatoriNumWebUIError() + estadistica.getSumatoriNumRecobrimentError()));
			}
			if (procedimentActual.getServeis() == null) {
				procedimentActual.setServeis(new ArrayList<Servei>());
			}
			procedimentActual.getServeis().add(servei);
		}
		return new ResponseEntity<List<Procediment>>(estadisticaProcediments, HttpStatus.OK);
	}

	@RequestMapping(
			value= "/stats/carrega",
			method = RequestMethod.GET,
			produces = "application/json")
	@ApiOperation(
			value = "Informe de carrega", 
			notes = "Aquest servei retorna una l’acumulat de peticions agrupades per "
					+ "procediment i servei distingint entre les peticions realitzades via "
					+ "interfície web o via recobriment i també si hi ha hagut errors o no.<br/><br/>"
					+ "Lista de objetos de tipo Entitat<br/>"
					+ "Per tant, el JSON resultant serà de la forma de array d'objectes de tipus **Entitat**:<br/>"
					+ "[{Entitat1}, {Entitat2}, {Entitat3}, {...}]<br/>"
					+ "El model d'**Entitat** es pot veure mes a baix en la informació del missatge de resposta.",
			responseContainer = "List",
			response = Entitat.class)
	public ResponseEntity<List<Entitat>> carrega(
			HttpServletRequest request) {
		// Informe de carrega
		List<CarregaDto> carregues = consultaService.findEstadistiquesCarrega();
		Entitat entitatActual = null;
		Long entitatActualId = null;
		Departament departamentActual = null;
		Procediment procedimentActual = null;
		List<Entitat> entitats = new ArrayList<Entitat>();
		for (CarregaDto carrega: carregues) {
			if (entitatActual == null || !entitatActualId.equals(carrega.getEntitatId())) {
				entitatActualId = carrega.getEntitatId();
				entitatActual = new Entitat();
				entitatActual.setCodi(carrega.getEntitatCodi());
				entitatActual.setNom(carrega.getEntitatNom());
				entitatActual.setNif(carrega.getEntitatCif());
				entitats.add(entitatActual);
			}
			if (carrega.getDepartamentNom() != null) {
				if (departamentActual == null || departamentActual.getNom() != carrega.getDepartamentNom()) {
					departamentActual = new Departament();
					//departamentActual.setCodi(informeProcediment.getDepartamentCodi());
					departamentActual.setNom(carrega.getDepartamentNom());
					if (entitatActual.getDepartaments() == null) {
						entitatActual.setDepartaments(new ArrayList<Departament>());
					}
					entitatActual.getDepartaments().add(departamentActual);
				}
				if (procedimentActual == null || procedimentActual.getCodi() != carrega.getProcedimentCodi()) {
					procedimentActual = new Procediment();
					procedimentActual.setCodi(carrega.getProcedimentCodi());
					procedimentActual.setNom(carrega.getProcedimentNom());
					if (departamentActual.getProcediments() == null) {
						departamentActual.setProcediments(new ArrayList<Procediment>());
					}
					departamentActual.getProcediments().add(procedimentActual);
				}
				Servei servei = new Servei();
				servei.setCodi(carrega.getServeiCodi());
				servei.setNom(carrega.getServeiDescripcio());
				if (carrega.getDetailedWebCount() != null) {
					servei.setTotalWeb(new TotalAcumulat(
							carrega.getDetailedWebCount().getAny(),
							carrega.getDetailedWebCount().getMes(),
							carrega.getDetailedWebCount().getDia(),
							carrega.getDetailedWebCount().getHora(),
							carrega.getDetailedWebCount().getMinut()));
				}
				if (carrega.getDetailedRecobrimentCount() != null) {
					servei.setTotalRecobriment(new TotalAcumulat(
							carrega.getDetailedRecobrimentCount().getAny(),
							carrega.getDetailedRecobrimentCount().getMes(),
							carrega.getDetailedRecobrimentCount().getDia(),
							carrega.getDetailedRecobrimentCount().getHora(),
							carrega.getDetailedRecobrimentCount().getMinut()));
				}
				if (procedimentActual.getServeis() == null) {
					procedimentActual.setServeis(new ArrayList<Servei>());
				}
				procedimentActual.getServeis().add(servei);
			}
		}
		return new ResponseEntity<List<Entitat>>(entitats, HttpStatus.OK);
	}

	private EstadistiquesFiltreDto getEstadistiquesFiltre(
			String entitatCodi,
			String procedimentCodi,
			String serveiCodi,
			EstatTipus estat,
			EstadistiquesAgrupacioDto agrupacio,
			Date dataInici,
			Date dataFi) throws EntitatNotFoundException, ProcedimentNotFoundException {
		EstadistiquesFiltreCommand filtre = new EstadistiquesFiltreCommand();
		EntitatDto entitat = entitatService.findByCodi(entitatCodi);
		if (entitat != null) {
			filtre.setEntitatId(entitat.getId());
		} else {
			throw new EntitatNotFoundException();
		}
		ProcedimentDto procediment = procedimentService.findAmbEntitatICodi(
				entitat.getId(),
				procedimentCodi);
		if (procediment != null) {
			filtre.setProcediment(procediment.getId());
		} else {
			throw new ProcedimentNotFoundException();
		}
		filtre.setServei(serveiCodi);
		filtre.setEstat(estat);
		filtre.setDataInici(dataInici);
		filtre.setDataFi(dataFi);
		filtre.setAgrupacio(agrupacio != null ? agrupacio : EstadistiquesAgrupacioDto.PROCEDIMENT_SERVEI);
		return EstadistiquesFiltreCommand.asDto(filtre);
	}

}
