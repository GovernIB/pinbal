package es.caib.pinbal.back.base.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import es.caib.pinbal.logic.intf.base.exception.ArtifactNotFoundException;
import es.caib.pinbal.logic.intf.base.model.*;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.io.Serializable;

/**
 * Mètodes dels controladors de l'API REST per a modificar un recurs de l'aplicació.
 *
 * @author Límit Tecnologies
 */
public interface MutableResourceController<R extends Resource<? extends Serializable>, ID extends Serializable>
		extends ReadonlyResourceController<R, ID> {

	/**
	 * Crea un nou recurs amb la informació especificada.
	 *
	 * @param resource
	 *            informació del recurs.
	 * @param bindingResult
	 *            instància de BindingResult per a validar l'element.
	 * @return el recurs creat.
	 * @throws MethodArgumentNotValidException
	 *            si s'envia una creació errònia o no permesa.
	 */
	ResponseEntity<EntityModel<R>> create(
			final R resource,
			BindingResult bindingResult) throws MethodArgumentNotValidException;

	/**
	 * Modifica un recurs existent amb la informació especificada.
	 *
	 * @param id
	 *            id de l'element que es vol modificar.
	 * @param resource
	 *            informació del recurs.
	 * @param bindingResult
	 *            instància de BindingResult per a validar l'element.
	 * @return el recurs modificat.
	 * @throws MethodArgumentNotValidException
	 *            si s'envia una modificació errònia o no permesa.
	 */
	ResponseEntity<EntityModel<R>> update(
			final ID id,
			final R resource,
			BindingResult bindingResult) throws MethodArgumentNotValidException;

	/**
	 * Modifica un recurs existent amb la informació especificada.
	 *
	 * @param id
	 *            id de l'element que es vol modificar.
	 * @param jsonNode
	 *            camps del recurs que s'han de modificar.
	 * @param bindingResult
	 *            instància de BindingResult per a validar l'element.
	 * @return el recurs modificat.
	 * @throws JsonProcessingException
	 *            si es produeixen errors al parsejar els camps.
	 * @throws MethodArgumentNotValidException
	 *            si s'envia una modificació errònia o no permesa.
	 */
	ResponseEntity<EntityModel<R>> patch(
			final ID id,
			final JsonNode jsonNode,
			final BindingResult bindingResult) throws JsonProcessingException, MethodArgumentNotValidException;

	/**
	 * Esborra un recurs existent.
	 *
	 * @param id
	 *            id de l'element que es vol esborrar.
	 * @return HTTP 200 si tot ha anat be.
	 */
	ResponseEntity<?> delete(
			final ID id);

	/**
	 * Processament en el backend dels canvis en els camps dels recursos que
	 * es generen al front.
	 * En aquest mètode no es faran modificacions al recurs sinó que
	 * únicament es processaran els canvis fets en el front. Aquests
	 * canvis es poden propagar com a canvis en altres camps, del
	 * recurs, que es retornaran com a resposta.
	 *
	 * @param onChangeEvent
	 *            informació de l'event onChange.
	 * @return el recurs resultat de processar els canvis.
	 * @throws JsonProcessingException
	 *            si es produeixen errors al parsejar els camps.
	 */
	ResponseEntity<String> onChange(
			final OnChangeEvent<ID> onChangeEvent) throws JsonProcessingException;

	/**
	 * Executa accions sobre múltiples recursos (PATCH, ACTION, DELETE).
	 *
	 * @param bulkRequest
	 *            paràmetres de l'execució múltiple.
	 * @return el resultat de l'execució (conté informació de l'execució sobre cada recurs).
	 */
	ResponseEntity<BulkResponse<ID>> bulk(
			@Valid
			@RequestBody
			final BulkRequest<ID> bulkRequest);

	/**
	 * Consulta paginada de les opcions disponibles per a emplenar un camp de tipus ResourceReference.
	 *
	 * @param <RR>
	 *            Classe del recurs (ha d'estendre de Resource).
	 * @param fieldName
	 *            nom del camp del recurs.
	 * @param quickFilter
	 *            text per a filtrar múltiples camps.
	 * @param filter
	 *            consulta en format Spring Filter.
	 * @param namedQueries
	 *            llista de noms de consultes a aplicar.
	 * @param perspectives
	 *            la llista de perspectives a aplicar.
	 * @param pageable
	 *            informació sobre la pagina de resultats que es vol obtenir.
	 * @return la pàgina amb els resultats de la consulta.
	 */
	<RR extends Resource<?>> ResponseEntity<PagedModel<EntityModel<RR>>> fieldOptionsFind(
			final String fieldName,
			final String quickFilter,
			final String filter,
			final String[] namedQueries,
			final String[] perspectives,
			final Pageable pageable);

	/**
	 * Consulta una de les opcions disponibles per a emplenar un camp de tipus ResourceReference.
	 *
	 * @param <RR>
	 *            Classe del recurs (ha d'estendre de Resource).
	 * @param <RID>
	 *            Tipus de l'id del recurs (ha d'estendre de Serializable).
	 * @param fieldName
	 *            nom del camp del recurs.
	 * @param id
	 *            id de l'element que es vol consultar.
	 * @param perspectives
	 *            la llista de perspectives a aplicar.
	 * @return L'element amb l'id especificat.
	 */
	<RR extends Resource<RID>, RID extends Serializable> ResponseEntity<EntityModel<RR>> fieldOptionsGetOne(
			final String fieldName,
			final RID id,
			final String[] perspectives);

	/**
	 * Consulta les opcions disponibles per a un camp de tipus enumerat.
	 *
	 * @param fieldName
	 *            nom del camp del recurs.
	 * @return la llista d'opcions disponibles.
	 */
	ResponseEntity<CollectionModel<EntityModel<FieldOption>>> fieldEnumOptionsFind(
			final String fieldName);

	/**
	 * Consulta una de les opcions disponibles per a un camp de tipus enumerat.
	 *
	 * @param fieldName
	 *            nom del camp del recurs.
	 * @param value
	 *            valor de l'enumerat a retornar.
	 * @return l'opció demanada.
	 */
	ResponseEntity<EntityModel<FieldOption>> fieldEnumOptionsGetOne(
			final String fieldName,
			final String value);

	/**
	 * Execució d'una acció associada a un recurs.
	 *
	 * @param code
	 *            codi de l'acció a executar.
	 * @param params
	 *            paràmetres per a executar l'acció.
	 * @param bindingResult
	 *            instància de BindingResult per a poder validar els paràmetres.
	 * @return el resultat de l'execució.
	 * @throws ArtifactNotFoundException
	 *             si no es troba l'informe amb el codi especificat.
	 * @throws JsonProcessingException
	 *             si es produeix algun error al extreure els paràmetres.
	 * @throws MethodArgumentNotValidException
	 *             si es troben errors de validació en els paràmetres.
	 */
	ResponseEntity<?> artifactActionExec(
			final String code,
			final JsonNode params,
			BindingResult bindingResult) throws ArtifactNotFoundException, JsonProcessingException, MethodArgumentNotValidException;

	/**
	 * Execució d'una acció associada a un recurs amb id.
	 *
	 * @param id
	 *            id del recurs sobre el que s'ha d'executar l'acció.
	 * @param code
	 *            codi de l'acció a executar.
	 * @param params
	 *            paràmetres per a executar l'acció.
	 * @param bindingResult
	 *            instància de BindingResult per a poder validar els paràmetres.
	 * @return el resultat de l'execució.
	 * @throws ArtifactNotFoundException
	 *             si no es troba l'informe amb el codi especificat.
	 * @throws JsonProcessingException
	 *             si es produeix algun error al extreure els paràmetres.
	 * @throws MethodArgumentNotValidException
	 *             si es troben errors de validació en els paràmetres.
	 */
	ResponseEntity<?> artifactActionExecId(
			final ID id,
			final String code,
			final JsonNode params,
			BindingResult bindingResult) throws ArtifactNotFoundException, JsonProcessingException, MethodArgumentNotValidException;

	/**
	 * Consulta paginada de les opcions disponibles per a emplenar un camp de
	 * tipus ResourceReference que pertany al formulari de l'acció.
	 *
	 * @param <RR>
	 *            Classe del recurs (ha d'estendre de Resource).
	 * @param code
	 *            codi de l'acció.
	 * @param fieldName
	 *            nom del camp del recurs.
	 * @param quickFilter
	 *            text per a filtrar múltiples camps.
	 * @param filter
	 *            consulta en format Spring Filter.
	 * @param namedQueries
	 *            llista de noms de consultes a aplicar.
	 * @param perspectives
	 *            la llista de perspectives a aplicar.
	 * @param pageable
	 *            informació sobre la pagina de resultats que es vol obtenir.
	 * @return la pàgina amb els resultats de la consulta.
	 */
	<RR extends Resource<?>> ResponseEntity<PagedModel<EntityModel<RR>>> artifactActionFieldOptionsFind(
			final String code,
			final String fieldName,
			final String quickFilter,
			final String filter,
			final String[] namedQueries,
			final String[] perspectives,
			final Pageable pageable);

	/**
	 * Consulta d'una de les opcions disponibles per a emplenar un camp de
	 * tipus ResourceReference que pertany al formulari de l'acció.
	 *
	 * @param <RR>
	 *            Classe del recurs (ha d'estendre de Resource).
	 * @param <RID>
	 *            Tipus de l'id del recurs (ha d'estendre de Serializable).
	 * @param code
	 *            codi de l'acció.
	 * @param fieldName
	 *            nom del camp del recurs.
	 * @param id
	 *            id de l'element que es vol consultar.
	 * @param perspectives
	 *            la llista de perspectives a aplicar.
	 * @return L'element amb l'id especificat.
	 */
	<RR extends Resource<RID>, RID extends Serializable> ResponseEntity<EntityModel<RR>> artifactActionFieldOptionsGetOne(
			final String code,
			final String fieldName,
			final RID id,
			final String[] perspectives);

}