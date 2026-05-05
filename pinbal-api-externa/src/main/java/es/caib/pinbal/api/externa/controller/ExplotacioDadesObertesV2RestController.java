/**
 * 
 */
package es.caib.pinbal.api.externa.controller;

import es.caib.pinbal.api.externa.api.ExplotacioDadesObertesV2Api;
import es.caib.pinbal.client.dadesobertes.DadesObertesResposta;
import es.caib.pinbal.logic.intf.dto.ConsultaOpenDataDto;
import es.caib.pinbal.logic.intf.service.ConsultaService;
import es.caib.pinbal.logic.intf.service.HistoricConsultaService;
import es.caib.pinbal.logic.intf.service.exception.EntitatNotFoundException;
import es.caib.pinbal.logic.intf.service.exception.ProcedimentNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

/**
 * Controlador pel servei REST de consulta de dades obertes.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/v2/opendata")
public class ExplotacioDadesObertesV2RestController implements ExplotacioDadesObertesV2Api {

	private final ConsultaService consultaService;
	private final HistoricConsultaService historicConsultaService;

	// IMPORTANT: Si es modifica aquest endpoint, actualitzar també la documentació OpenAPI definida a la interfície ExplotacioDadesObertesV2Api.
	@Override
	@GetMapping(produces = "application/json")
	public ResponseEntity<DadesObertesResposta> opendata(
			HttpServletRequest request,
			@RequestParam(value = "historic", required = false) boolean historic,
			@RequestParam(required = false) final String entitatCodi,
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) final Date dataInici,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) final Date dataFi,
			@RequestParam(required = false) final String procedimentCodi,
			@RequestParam(required = false) final String serveiCodi,
			@RequestParam(value = "pagina", required = false) Integer pagina,
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
