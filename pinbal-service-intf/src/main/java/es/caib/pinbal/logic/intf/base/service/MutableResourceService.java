package es.caib.pinbal.logic.intf.base.service;

import es.caib.pinbal.logic.intf.base.exception.*;
import es.caib.pinbal.logic.intf.base.model.FieldOption;
import es.caib.pinbal.logic.intf.base.model.Resource;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Mètodes a implementar pels serveis que gestionen un recurs que es pot modificar.
 *
 * @param <R> classe del recurs.
 * @param <ID> classe de la clau primària del recurs.
 *
 * @author Límit Tecnologies
 */
public interface MutableResourceService<R extends Resource<? extends Serializable>, ID extends Serializable>
		extends ReadonlyResourceService<R, ID> {

	/**
	 * Crea una nova instància del recurs per a inicialitzar el formulari de creació.
	 * @return la nova instància del recurs.
	 */
	R newResourceInstance();

	/**
	 * Crea un nou recurs.
	 *
	 * @param resource
	 *            informació del recurs.
	 * @param answers
	 *            respostes a les preguntes formulades en el front.
	 * @return el recurs creat.
	 * @throws ResourceAlreadyExistsException
	 *             si el recurs que es vol crear ja existeix.
	 * @throws ResourceNotCreatedException
	 *             si no s'ha pogut crear el recurs especificat.
	 * @throws AnswerRequiredException
	 *             si es requereixen respostes de l'usuari per a crear el registre.
	 */
	R create(
			R resource,
			Map<String, AnswerRequiredException.AnswerValue> answers) throws ResourceAlreadyExistsException, ResourceNotCreatedException, AnswerRequiredException;

	/**
	 * Actualitza la informació d'un recurs.
	 *
	 * @param id
	 *            clau primària del recurs.
	 * @param resource
	 *            informació del recurs.
	 * @param answers
	 *            respostes a les preguntes formulades en el front.
	 * @return el recurs modificat.
	 * @throws ResourceNotFoundException
	 *             si no s'ha trobat el recurs especificat.
	 * @throws ResourceNotUpdatedException
	 *             si no s'ha pogut modificar el recurs especificat.
	 * @throws AnswerRequiredException
	 *             si es requereixen respostes de l'usuari per a modificar el registre.
	 */
	R update(
			ID id,
			R resource,
			Map<String, AnswerRequiredException.AnswerValue> answers) throws ResourceNotFoundException, ResourceNotUpdatedException, AnswerRequiredException;

	/**
	 * Esborra un recurs donat el seu identificador.
	 *
	 * @param id
	 *            clau primària del recurs.
	 * @throws ResourceNotFoundException
	 *             si no s'ha trobat el recurs especificat.
	 * @throws ResourceNotDeletedException
	 *             si no s'ha pogut esborrar el recurs especificat.
	 * @throws AnswerRequiredException
	 *             si es requereixen respostes de l'usuari per a esborrar el registre.
	 */
	void delete(
			ID id,
			Map<String, AnswerRequiredException.AnswerValue> answers) throws ResourceNotFoundException, ResourceNotDeletedException, AnswerRequiredException;

	/**
	 * Processament en el backend dels canvis en els camps dels recursos que
	 * es generen al front.
	 * En aquest mètode no es faran modificacions al recurs sinó que
	 * únicament es processaran els canvis fets en el front. Aquests
	 * canvis es poden propagar com a canvis en altres camps, del
	 * recurs, que es retornaran com a resposta.
	 *
	 * @param id
	 *            clau primària del recurs.
	 * @param previous
	 *            informació del recurs abans del canvi.
	 * @param fieldName
	 *            nom del camp que s'ha canviat.
	 * @param fieldValue
	 *            el valor del camp que s'ha canviat.
	 * @param answers
	 *            respostes a les preguntes formulades en el front.
	 * @return un map amb els canvis resultants de processar la petició.
	 * @throws ResourceFieldNotFoundException
	 *            si no es troba el camp del recurs.
	 * @throws AnswerRequiredException
	 *            si es requereix alguna resposta addicional de l'usuari.
	 */
	Map<String, Object> onChange(
			ID id,
			R previous,
			String fieldName,
			Object fieldValue,
			Map<String, AnswerRequiredException.AnswerValue> answers) throws ResourceFieldNotFoundException, AnswerRequiredException;

	/**
	 * Executa l'acció amb el codi especificat.
	 *
	 * @param id
	 *            clau primària del recurs (pot ser null si l'acció no s'executa sobre un recurs determinat).
	 * @param code
	 *            el codi de l'acció.
	 * @param params
	 *            els paràmetres necessaris per a executar l'acció.
	 * @return el resultat de l'execució.
	 * @param <P> el tipus dels paràmetres.
	 * @throws ArtifactNotFoundException
	 *             si no es troba l'acció amb el codi especificat.
	 * @throws ActionExecutionException
	 *             si es produeix algun error executant l'acció.
	 */
	<P extends Serializable> Serializable artifactActionExec(
			ID id,
			String code,
			P params) throws ArtifactNotFoundException, ActionExecutionException;

	/**
	 * Consulta les opcions disponibles per a un camp de tipus enumerat.
	 *
	 * @param fieldName
	 *            nom del camp del recurs.
	 * @param requestParameterMap
	 *            paràmetres de la petició.
	 * @return la llista d'opcions disponibles.
	 */
	List<FieldOption> fieldEnumOptions(
			String fieldName,
			Map<String,String[]> requestParameterMap);

}
