/**
 * 
 */
package es.caib.pinbal.core.helper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import es.caib.pinbal.core.dto.IntegracioAccioDto;
import es.caib.pinbal.core.dto.IntegracioAccioEstatEnumDto;
import es.caib.pinbal.core.dto.IntegracioAccioTipusEnumDto;
import es.caib.pinbal.core.dto.IntegracioDto;
import es.caib.pinbal.core.model.Usuari;

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

	private Map<String, LinkedList<IntegracioAccioDto>> accionsIntegracio = Collections.synchronizedMap(new HashMap<String, LinkedList<IntegracioAccioDto>>());
	private Map<String, Integer> maxAccionsIntegracio = new HashMap<String, Integer>();
	
	private Long idAccio = 0L;
	
	private Long generateIdAccio() {
		idAccio++;
		return idAccio;
	}

	public List<IntegracioDto> findAll() {
		List<IntegracioDto> integracions = new ArrayList<IntegracioDto>();
		integracions.add(
				novaIntegracio(
						INTCODI_ARXIU));
		integracions.add(
				novaIntegracio(
						INTCODI_FIRMASERV));
		integracions.add(
				novaIntegracio(
						INTCODI_SERVEIS_SCSP));
		return integracions;
	}

	public List<IntegracioAccioDto> findAccionsByIntegracioCodi(
			String integracioCodi) {
		return getLlistaAccions(integracioCodi);
	}

	public void addAccioOk(
			String integracioCodi,
			String descripcio,
			Map<String, String> parametres,
			IntegracioAccioTipusEnumDto tipus,
			long tempsResposta) {
		IntegracioAccioDto accio = new IntegracioAccioDto();
		accio.setIntegracio(novaIntegracio(integracioCodi));
		accio.setData(new Date());
		accio.setDescripcio(descripcio);
		accio.setParametres(parametres);
		accio.setTipus(tipus);
		accio.setTempsResposta(tempsResposta);
		accio.setEstat(IntegracioAccioEstatEnumDto.OK);
		addAccio(
				integracioCodi,
				accio);
		
		logger.debug(descripcio + ", Parametres: " + parametres + ", Temps resposta: " + tempsResposta);
	}
	public void addAccioError(
			String integracioCodi,
			String descripcio,
			Map<String, String> parametres,
			IntegracioAccioTipusEnumDto tipus,
			long tempsResposta,
			String errorDescripcio) {
		addAccioError(
				integracioCodi,
				descripcio,
				parametres,
				tipus,
				tempsResposta,
				errorDescripcio,
				null);
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void addAccioError(
			String integracioCodi,
			String descripcio,
			Map<String, String> parametres,
			IntegracioAccioTipusEnumDto tipus,
			long tempsResposta,
			String errorDescripcio,
			Throwable throwable) {
		IntegracioAccioDto accio = new IntegracioAccioDto();
		accio.setIntegracio(novaIntegracio(integracioCodi));
		accio.setData(new Date());
		accio.setDescripcio(descripcio);
		accio.setParametres(parametres);
		accio.setTipus(tipus);
		accio.setTempsResposta(tempsResposta);
		accio.setEstat(IntegracioAccioEstatEnumDto.ERROR);
		accio.setErrorDescripcio(errorDescripcio);
		if (throwable != null){
			accio.setExcepcioMessage(
					ExceptionUtils.getMessage(throwable));
			accio.setExcepcioStacktrace(
					ExceptionUtils.getStackTrace(throwable));
		} 
		addAccio(
				integracioCodi,
				accio);
		/*logger.error("Error d'integracio " + descripcio + ": " + errorDescripcio + "("
				+ "integracioCodi=" + integracioCodi + ", "
				+ "parametres=" + parametres + ", "
				+ "tipus=" + tipus + ", "
				+ "tempsResposta=" + tempsResposta + ")",
				throwable);*/
	}

	private LinkedList<IntegracioAccioDto> getLlistaAccions(
			String integracioCodi) {
		synchronized(accionsIntegracio){
		LinkedList<IntegracioAccioDto> accions = accionsIntegracio.get(integracioCodi);
		if (accions == null) {
				accions = new LinkedList<IntegracioAccioDto>();
				accionsIntegracio.put(
						integracioCodi,
						accions);
			}
			return accions;
		} 
	}
	private int getMaxAccions(
			String integracioCodi) {
		Integer max = maxAccionsIntegracio.get(integracioCodi);
		if (max == null) {
			max = new Integer(DEFAULT_MAX_ACCIONS);
			maxAccionsIntegracio.put(
					integracioCodi,
					max);
		}
		return max.intValue();
	}

	private void addAccio(
			String integracioCodi,
			IntegracioAccioDto accio) {
		afegirParametreUsuari(accio);
		LinkedList<IntegracioAccioDto> accions = getLlistaAccions(integracioCodi);
		synchronized(accions) {
			int max = getMaxAccions(integracioCodi);
			while (accions.size() >= max) {
				accions.remove(accions.size() - 1);
			}
			accio.setId(generateIdAccio());
			
			accions.add(
					0,
					accio);
		}
	}

	private void afegirParametreUsuari(
			IntegracioAccioDto accio) {
		String usuariNomCodi = "";
		Usuari usuari = usuariHelper.getUsuariAutenticat();
		if (usuari != null) {
			usuariNomCodi = usuari.getNom();
			if (!usuari.getNom().equals(usuari.getCodi()))
				usuariNomCodi = usuariNomCodi + " (" + usuari.getCodi() + ")";
		} else {
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			if (auth != null)
				usuariNomCodi = auth.getName();
		}
		if(accio.getParametres() == null)
			accio.setParametres(new HashMap<String, String>());
		accio.getParametres().put("usuari", usuariNomCodi);
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
		}
		
		
		
		return integracio;
	}

	private static final Logger logger = LoggerFactory.getLogger(IntegracioHelper.class);

}
