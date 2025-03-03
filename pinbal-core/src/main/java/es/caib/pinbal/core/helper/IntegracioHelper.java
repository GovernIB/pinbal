/**
 * 
 */
package es.caib.pinbal.core.helper;

import es.caib.pinbal.core.dto.IntegracioAccioDto;
import es.caib.pinbal.core.dto.IntegracioAccioEstatEnumDto;
import es.caib.pinbal.core.dto.IntegracioAccioParamDto;
import es.caib.pinbal.core.dto.IntegracioAccioTipusEnumDto;
import es.caib.pinbal.core.dto.IntegracioDto;
import es.caib.pinbal.core.service.IntegracioAccioService;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Mètodes per a la gestió d'integracions.
 * 
 * @author Limit Tecnologies <limit@limit.es>
 */
@Component
public class IntegracioHelper {
	
	@Resource
	private UsuariHelper usuariHelper;

	public static final int DEFAULT_MAX_ACCIONS = 20;


	public static final String INTCODI_ARXIU = "ARXIU";
	public static final String INTCODI_FIRMASERV = "FIRMASERV";
	public static final String INTCODI_SERVEIS_SCSP = "SERVEIS_SCSP";
	public static final String INTCODI_ORGANS = "ORGANS";
	public static final String INTCODI_USUARIS = "USUARIS";
	public static final String INTCODI_EXPLOTACIO = "EXPLOTACIO";
	
	@Autowired
	private IntegracioAccioService integracioAccioService;

	public List<IntegracioDto> findAll() {
		List<IntegracioDto> integracions = new ArrayList<IntegracioDto>();
		integracions.add(novaIntegracio(INTCODI_ARXIU));
		integracions.add(novaIntegracio(INTCODI_FIRMASERV));
		integracions.add(novaIntegracio(INTCODI_SERVEIS_SCSP));
		integracions.add(novaIntegracio(INTCODI_ORGANS));
		integracions.add(novaIntegracio(INTCODI_USUARIS));
		integracions.add(novaIntegracio(INTCODI_EXPLOTACIO));
		return integracions;
	}

	public void addAccioOk(String integracioCodi, String descripcio, Map<String, String> parametres, IntegracioAccioTipusEnumDto tipus, long tempsResposta) {
		addAccioOk("--", integracioCodi, descripcio, parametres, tipus, tempsResposta);
	}

	// Adaptat per BBDD
	public void addAccioOk(
			String idPeticio,
			String integracioCodi,
			String descripcio,
			Map<String, String> parametres,
			IntegracioAccioTipusEnumDto tipus,
			long tempsResposta) {

		IntegracioAccioDto accio = new IntegracioAccioDto();
		accio.setIdPeticio(idPeticio);
		
//		accio.setIntegracio(novaIntegracio(integracioCodi));
		accio.setCodi(integracioCodi);
		
		accio.setData(new Date());
		accio.setDescripcio(descripcio);
		
//		accio.setParametres(parametres);
		accio.setParametres(this.buildParams(parametres));
		
		accio.setTipus(tipus);
		accio.setTempsResposta(tempsResposta);
		accio.setEstat(IntegracioAccioEstatEnumDto.OK);
		
//		addAccio(integracioCodi, accio);
		integracioAccioService.create(accio);		
		
		logger.debug(descripcio + ", Parametres: " + parametres + ", Temps resposta: " + tempsResposta);
	}

//	Adaptat per BBDD
	private List<IntegracioAccioParamDto> buildParams(Map<String, String> parametres) {
		List<IntegracioAccioParamDto> parametresDto = new ArrayList<>();
		if (parametres != null && !parametres.isEmpty()) {
			for (String nom : parametres.keySet()) {
				IntegracioAccioParamDto paramDto = new IntegracioAccioParamDto();
				paramDto.setNom(nom);
				paramDto.setDescripcio(parametres.get(nom));
				parametresDto.add(paramDto);
			}
		}
		return parametresDto;
	}
	
	public void addAccioError(String integracioCodi, String desc, Map<String, String> params, IntegracioAccioTipusEnumDto tipus, long tempsResposta, String errorDesc, Throwable t) {
		addAccioError("", integracioCodi, desc, params, tipus, tempsResposta, errorDesc, t);
	}

	public void addAccioError(
			String idPeticio,
			String integracioCodi,
			String descripcio,
			Map<String, String> parametres,
			IntegracioAccioTipusEnumDto tipus,
			long tempsResposta,
			String errorDescripcio) {
		addAccioError(
				idPeticio,
				integracioCodi,
				descripcio,
				parametres,
				tipus,
				tempsResposta,
				errorDescripcio,
				null);
	}

//	Adaptat per BBDD	
	public void addAccioError(
			String idPeticio,
			String integracioCodi,
			String descripcio,
			Map<String, String> parametres,
			IntegracioAccioTipusEnumDto tipus,
			long tempsResposta,
			String errorDescripcio,
			Throwable throwable) {

		IntegracioAccioDto accio = new IntegracioAccioDto();
		accio.setIdPeticio(idPeticio != null ? idPeticio : "--");
		
//		accio.setIntegracio(novaIntegracio(integracioCodi));
		accio.setCodi(integracioCodi);
		
		accio.setData(new Date());
		accio.setDescripcio(descripcio);
		
//		accio.setParametres(parametres);
		accio.setParametres(this.buildParams(parametres));
		
		accio.setTipus(tipus);
		accio.setTempsResposta(tempsResposta);
		accio.setEstat(IntegracioAccioEstatEnumDto.ERROR);
		accio.setErrorDescripcio(errorDescripcio);
		if (throwable != null){
			accio.setExcepcioMessage(trimToMaxLength(ExceptionUtils.getMessage(throwable), 1000));
			accio.setExcepcioStacktrace(trimToMaxLength(ExceptionUtils.getStackTrace(throwable), 2000));
		} 
		
//		addAccio(integracioCodi, accio);
		integracioAccioService.create(accio);
	}

	private String trimToMaxLength (String input, int maxLength){
		if (input != null && input.length() > maxLength) {
			return input.substring(0, maxLength);
		}
		return input;
	}

	private IntegracioDto novaIntegracio(
			String codi) {
		IntegracioDto integracio = new IntegracioDto();
		integracio.setCodi(codi);
		if (INTCODI_ARXIU.equals(codi)) {
			integracio.setNom("Arxiu digital");
		} else if (INTCODI_FIRMASERV.equals(codi)) {
			integracio.setNom("Firma servidor");
		} else if (INTCODI_SERVEIS_SCSP.equals(codi)) {
			integracio.setNom("Serveis SCSP");
		} else if (INTCODI_ORGANS.equals(codi)) {
			integracio.setNom("Organs gestors");
		} else if (INTCODI_USUARIS.equals(codi)) {
			integracio.setNom("Usuaris");
		} else if (INTCODI_EXPLOTACIO.equals(codi)) {
			integracio.setNom("Explotació de dades");
		}
		return integracio;
	}

	private static final Logger logger = LoggerFactory.getLogger(IntegracioHelper.class);

}
