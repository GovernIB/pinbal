/**
 * 
 */
package es.caib.pinbal.api.externa.controller;

import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import es.caib.pinbal.client.dadesobertes.DadesObertesResposta;
import es.caib.pinbal.client.dadesobertes.DadesObertesRespostaConsulta;
import es.caib.pinbal.core.dto.ConsultaOpenDataDto;
import es.caib.pinbal.core.service.ConsultaService;
import es.caib.pinbal.core.service.HistoricConsultaService;
import es.caib.pinbal.core.service.exception.EntitatNotFoundException;
import es.caib.pinbal.core.service.exception.ProcedimentNotFoundException;
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
 * Controlador pel servei REST de consulta de dades obertes.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Controller
@RequestMapping("/v2")
public class ExplotacioDadesObertesV2RestController {

	@Autowired
	private ConsultaService consultaService;
	@Autowired
	private HistoricConsultaService historicConsultaService;

	@RequestMapping(
			value= "/opendata",
			method = RequestMethod.GET,
			produces = "application/json")
	@ApiOperation(
			value = "Informe de procediments agrupats per entitat i departament", 
			notes = "Retorna una llista amb les entitats i l'estatus", response= DadesObertesResposta.class)
	public ResponseEntity<DadesObertesResposta> opendata(
			HttpServletRequest request,
			@ApiParam(name="historic", value="S'utilitzarà la informació històrica de consultes", required = false, defaultValue = "false")
			@RequestParam(value = "historic", required = false) boolean historic,
			@ApiParam(name="entitatCodi", value="Codi de l'entitat", required=false) 
			@RequestParam(required = false) final String entitatCodi,
			@ApiParam(name="dataInici", value="Data inicial de la consulta en format yyyy-MM-dd. Si no s'informa s'afagarà el primer dia del mes anterior")
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) final Date dataInici,
			@ApiParam(name="dataFi", value="Data final de la consulta en format yyyy-MM-dd. Si no s'informa s'agafarà l'últim dia del més anterior", required=false)
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) final Date dataFi,
			@ApiParam(name="procedimentCodi", value="Codi del procediment", required=false) 
			@RequestParam(required = false) final String procedimentCodi,
			@ApiParam(name="serveiCodi", value="Codi del servei", required=false) 
			@RequestParam(required = false) final String serveiCodi,
			@ApiParam(name = "pagina", value = "Número de pàgina a mostrar en la paginació. Es comença a contar des de 0. Si no s'informa es retornarà la primera pàgina", required = false)
			@RequestParam(value = "pagina", required = false) Integer pagina,
			@ApiParam(name = "mida", value = "Mida de la pàgina a mostrar en la paginació. Si no s'informa s'agafarà el valor 50", required = false)
			@RequestParam(value = "mida", required = false) Integer mida) throws EntitatNotFoundException, ProcedimentNotFoundException {

		ConsultaOpenDataDto consultaOpenDataDto = ConsultaOpenDataDto.builder()
				.entitatCodi(entitatCodi)
				.dataInici(dataInici)
				.dataFi(dataFi)
				.procedimentCodi(procedimentCodi)
				.serveiCodi(serveiCodi)
				.pagina(pagina)
				.mida(mida)
				.appPath(request.getRequestURL().toString())
				.build();

		// Informe de procediments agrupats per entitat i departament
		DadesObertesResposta resposta;
		if (historic) {
			resposta = historicConsultaService.findByFiltrePerOpenDataV2(consultaOpenDataDto);
		} else {
			resposta = consultaService.findByFiltrePerOpenDataV2(consultaOpenDataDto);
		}
		return new ResponseEntity<>(resposta, HttpStatus.OK);
	}

}
