/**
 * 
 */
package es.caib.pinbal.logic.helper;

import es.caib.pinbal.logic.intf.dto.CarregaDto;
import es.caib.pinbal.persist.repository.ConsultaRepository;
import es.caib.pinbal.scsp.Solicitud;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Helper per a emmagatzemar les estadístiques de càrrega de peticions SCSP.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@RequiredArgsConstructor
@Component
public class PeticioScspEstadistiquesHelper {

	private final ConsultaRepository consultaRepository;

	private List<CarregaDto> carreguesAny;
	private List<CarregaDto> carreguesMes;
	private List<CarregaDto> carreguesDia;
	private List<CarregaDto> carreguesHora;
	private List<CarregaDto> carreguesMinut;

	public List<CarregaDto> consultaEstadistiques() {
		initEstadistiquesCarrega();
		List<CarregaDto> carregues = new ArrayList<CarregaDto>();
		for (CarregaDto carregaAny: carreguesAny) {
			CarregaDto carrega = new CarregaDto(
					0,
					0,
					carregaAny.getEntitatId(),
					carregaAny.getEntitatCodi(),
					carregaAny.getEntitatNom(),
					carregaAny.getEntitatCif(),
					carregaAny.getDepartamentNom(),
					carregaAny.getProcedimentServeiId(),
					carregaAny.getProcedimentCodi(),
					carregaAny.getProcedimentNom(),
					carregaAny.getServeiCodi(),
					carregaAny.getServeiDescripcio());
			long countWebMes = getCountFromCarregues(carrega, carreguesMes, true);
			long countWebDia = getCountFromCarregues(carrega, carreguesDia, true);
			long countWebHora = getCountFromCarregues(carrega, carreguesHora, true);
			long countWebMinut = getCountFromCarregues(carrega, carreguesMinut, true);
			carrega.setDetailedWebCount(
					new CarregaDto.CarregaDetailedCountDto(
							carregaAny.getCountWeb(),
							countWebMes,
							countWebDia,
							countWebHora,
							countWebMinut));
			long countRecobrimentMes = getCountFromCarregues(carrega, carreguesMes, false);
			long countRecobrimentDia = getCountFromCarregues(carrega, carreguesDia, false);
			long countRecobrimentHora = getCountFromCarregues(carrega, carreguesHora, false);
			long countRecobrimentMinut = getCountFromCarregues(carrega, carreguesMinut, false);
			carrega.setDetailedRecobrimentCount(
					new CarregaDto.CarregaDetailedCountDto(
							carregaAny.getCountRecobriment(),
							countRecobrimentMes,
							countRecobrimentDia,
							countRecobrimentHora,
							countRecobrimentMinut));
			carregues.add(carrega);
		}
		return carregues;
	}

	public void actualitzarEstadistiquesPeticio(
			Long entitatId,
			List<Solicitud> solicituds,
			boolean recobriment) {
		initEstadistiquesCarrega();
		if (solicituds != null && solicituds.size() > 0) {
			Solicitud solicitud = solicituds.get(0);
			afegirConsultaEstadistiquesCarrega(
					entitatId,
					solicitud.getUnitatTramitadora(),
					solicitud.getProcedimentCodi(),
					solicitud.getServeiCodi(),
					recobriment,
					carreguesAny);
			afegirConsultaEstadistiquesCarrega(
					entitatId,
					solicitud.getUnitatTramitadora(),
					solicitud.getProcedimentCodi(),
					solicitud.getServeiCodi(),
					recobriment,
					carreguesMes);
			afegirConsultaEstadistiquesCarrega(
					entitatId,
					solicitud.getUnitatTramitadora(),
					solicitud.getProcedimentCodi(),
					solicitud.getServeiCodi(),
					recobriment,
					carreguesDia);
			afegirConsultaEstadistiquesCarrega(
					entitatId,
					solicitud.getUnitatTramitadora(),
					solicitud.getProcedimentCodi(),
					solicitud.getServeiCodi(),
					recobriment,
					carreguesHora);
			afegirConsultaEstadistiquesCarrega(
					entitatId,
					solicitud.getUnitatTramitadora(),
					solicitud.getProcedimentCodi(),
					solicitud.getServeiCodi(),
					recobriment,
					carreguesMinut);
		}
	}

	private void initEstadistiquesCarrega() {
		if (carreguesAny == null) {
			carreguesAny = Collections.synchronizedList(
					consultaRepository.findCarrega(DateUtils.truncate(new Date(), Calendar.YEAR)));
		}
		if (carreguesMes == null) {
			carreguesMes = Collections.synchronizedList(
					consultaRepository.findCarrega(DateUtils.truncate(new Date(), Calendar.MONTH)));
		}
		if (carreguesDia == null) {
			carreguesDia = Collections.synchronizedList(
					consultaRepository.findCarrega(DateUtils.truncate(new Date(), Calendar.DAY_OF_MONTH)));
		}
		if (carreguesHora == null) {
			carreguesHora = Collections.synchronizedList(
					consultaRepository.findCarrega(DateUtils.truncate(new Date(), Calendar.HOUR_OF_DAY)));
		}
		if (carreguesMinut == null) {
			carreguesMinut = Collections.synchronizedList(
					consultaRepository.findCarrega(DateUtils.truncate(new Date(), Calendar.MINUTE)));
		}
	}

	private void afegirConsultaEstadistiquesCarrega(
			Long entitatId,
			String departamentNom,
			String procedimentCodi,
			String serveiCodi,
			boolean recobriment,
			List<CarregaDto> carregues) {
		for (CarregaDto carrega: carregues) {
			if (	carrega.getEntitatId().equals(entitatId) &&
					carrega.getDepartamentNom().equals(departamentNom) &&
					carrega.getProcedimentCodi().equals(procedimentCodi) &&
					carrega.getServeiCodi().equals(serveiCodi)) {
				if (!recobriment) {
					carrega.setCountWeb(carrega.getCountWeb() + 1);
				} else {
					carrega.setCountRecobriment(carrega.getCountRecobriment() + 1);
				}
				break;
			}
		}
	}

	private long getCountFromCarregues(
			CarregaDto carrega,
			List<CarregaDto> carregues,
			boolean web) {
		int index = carregues.indexOf(carrega);
		if (index != -1) {
			CarregaDto carregaTrobada = carregues.get(index);
			return web ? carregaTrobada.getCountWeb() : carregaTrobada.getCountRecobriment();
		} else {
			return 0;
		}
	}

}
