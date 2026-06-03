package es.caib.pinbal.back.base.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import es.caib.pinbal.back.base.util.ResourceServiceLocator;
import es.caib.pinbal.logic.intf.base.annotation.ResourceConfig;
import es.caib.pinbal.logic.intf.base.annotation.ResourceField;
import es.caib.pinbal.logic.intf.base.config.PropertyConfig;
import es.caib.pinbal.logic.intf.base.exception.AnswerRequiredException;
import es.caib.pinbal.logic.intf.base.exception.ArtifactFormNotFoundException;
import es.caib.pinbal.logic.intf.base.exception.ArtifactNotFoundException;
import es.caib.pinbal.logic.intf.base.exception.ResourceFieldNotFoundException;
import es.caib.pinbal.logic.intf.base.model.DownloadableFile;
import es.caib.pinbal.logic.intf.base.model.ExportField;
import es.caib.pinbal.logic.intf.base.model.FieldOption;
import es.caib.pinbal.logic.intf.base.model.OnChangeEvent;
import es.caib.pinbal.logic.intf.base.model.ReportFileType;
import es.caib.pinbal.logic.intf.base.model.Resource;
import es.caib.pinbal.logic.intf.base.model.ResourceArtifact;
import es.caib.pinbal.logic.intf.base.model.ResourceArtifactType;
import es.caib.pinbal.logic.intf.base.model.ResourceReference;
import es.caib.pinbal.logic.intf.base.model.UnpagedButSorted;
import es.caib.pinbal.logic.intf.base.permission.ResourcePermissions;
import es.caib.pinbal.logic.intf.base.service.ReadonlyResourceService;
import es.caib.pinbal.logic.intf.base.service.ResourceApiService;
import es.caib.pinbal.logic.intf.base.util.HttpRequestUtil;
import es.caib.pinbal.logic.intf.base.util.JsonUtil;
import es.caib.pinbal.logic.intf.base.util.RequestSessionUtil;
import es.caib.pinbal.logic.intf.base.util.StringUtil;
import es.caib.pinbal.logic.intf.base.util.TypeUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cglib.core.ReflectUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.core.MethodParameter;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.hateoas.AffordanceModel;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.TemplateVariable;
import org.springframework.hateoas.TemplateVariable.VariableType;
import org.springframework.hateoas.TemplateVariables;
import org.springframework.hateoas.UriTemplate;
import org.springframework.hateoas.mediatype.Affordances;
import org.springframework.hateoas.mediatype.hal.forms.CustomHalFormsPropertyFactory;
import org.springframework.hateoas.mediatype.hal.forms.CustomHalFormsTemplateBuilder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.ReflectionUtils;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.SmartValidator;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.groups.Default;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.MonthDay;
import java.time.YearMonth;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/**
 * <p>Classe base pels controladors de l'API REST que conté els mètodes
 * necessaris per a consultar recursos.</p>
 *
 * @param <R>
 *            el tipus del recurs que ha de gestionar aquest Controller. Aquest
 *            tipus ha d'estendre de EntityResource&lt;ID&gt;.
 * @param <ID>
 *            el tipus de la clau primària del recurs. Aquest tipus ha
 *            d'implementar la interfície Serializable.
 *
 * @author Límit Tecnologies
 */
@Slf4j
public abstract class BaseReadonlyResourceController<R extends Resource<? extends Serializable>, ID extends Serializable>
		extends BaseController
		implements ReadonlyResourceController<R, ID> {

	@Value("${" + PropertyConfig.PROP_BACKEND_HTTP_HEADER_ANSWERS + ":OnChange-Answers}")
	private String httpHeaderAnswers;

	protected static final HttpMethod FAKE_DEFAULT_TEMPLATE_HTTP_METHOD = HttpMethod.OPTIONS;

	@Autowired
	private ApplicationContext applicationContext;
	@Autowired
	protected ReadonlyResourceService<R, ID> readonlyResourceService;
	@Autowired
	protected ResourceApiService resourceApiService;
	@Autowired
	protected ObjectMapper objectMapper;
	@Autowired
	protected SmartValidator validator;
	@Autowired
	protected ResourceServiceLocator resourceServiceLocator;

	private Class<R> resourceClass;
	private ExpressionParser parser;

	@PostConstruct
	public void registerResourceService() {
		resourceApiService.resourceRegister(getResourceClass());
	}

	@Override
	@GetMapping(value = "/{id}")
	@Operation(operationId = "getOne", summary = "Consulta d'un recurs")
	@PreAuthorize("this.isPublic() or hasPermission(#resourceId, this.getResourceClass().getName(), this.getOperation('GET_ONE'))")
	public ResponseEntity<EntityModel<R>> getOne(
			@PathVariable
			@Parameter(description = "Identificador del recurs")
			final ID id,
			@RequestParam(value = "perspective", required = false)
			final String[] perspectives) {
		log.debug("Obtenint recurs (id={})", id);
		R resource = getReadonlyResourceService().getOne(
				id,
				perspectives);
		EntityModel<R> entityModel = toEntityModel(
				resource,
				buildSingleResourceLinks(
						resource.getId(),
						perspectives,
						null,
						null,
						resourceApiService.permissionsCurrentUser(
								getResourceClass(),
								id),
						true,
						true).toArray(new Link[0]));
		return ResponseEntity.ok(entityModel);
	}

	@Override
	@GetMapping
	@Operation(operationId = "find", summary = "Consulta de múltiples recursos")
	@PreAuthorize("this.isPublic() or hasPermission(null, this.getResourceClass().getName(), this.getOperation('FIND'))")
	public ResponseEntity<PagedModel<EntityModel<R>>> find(
			@RequestParam(value = "quickFilter", required = false)
			@Parameter(description = "Filtre ràpid (text)")
			final String quickFilter,
			@RequestParam(value = "filter", required = false)
			@Parameter(description = "Consulta en format Spring Filter")
			final String filter,
			@RequestParam(value = "namedQuery", required = false)
			@Parameter(description = "Consultes predefinides")
			final String[] namedQueries,
			@RequestParam(value = "perspective", required = false)
			@Parameter(description = "Perspectives de la consulta")
			final String[] perspectives,
			@ParameterObject
			@Parameter(description = "Paginació dels resultats")
			final Pageable pageable) {
		log.debug("Consultant recursos amb filtre i paginació (" +
						"quickFilter={}, filter={}, namedQueries={}, " +
						"perspectives={}, pageable={})",
				quickFilter,
				filter,
				Arrays.toString(namedQueries),
				Arrays.toString(perspectives),
				pageable);
		ResourcePermissions resourcePermissions = resourceApiService.permissionsCurrentUser(
				getResourceClass(),
				null);
		if (pageable != null) {
			// Només es fa la consulta de recursos si la petició conté informació de paginació.
			long t0 = System.currentTimeMillis();
			long initialTime = t0;
			String filterWithFieldParams = filterWithFieldParameters(filter, getResourceClass());
			Page<R> page = getReadonlyResourceService().findPage(
					quickFilter,
					filterWithFieldParams,
					namedQueries,
					perspectives,
					pageable);
			long queryTime = System.currentTimeMillis() - t0;
			log.trace("\ttemps de consulta: {}ms", queryTime);
			t0 = System.currentTimeMillis();
			PagedModel<EntityModel<R>> pagedModel = toPagedModel(
					page,
					perspectives,
					null,
					resourcePermissions,
					true,
					false,
					buildResourceCollectionLinks(
							quickFilter,
							filter,
							namedQueries,
							perspectives,
							pageable,
							page,
							null,
							resourcePermissions).toArray(new Link[0]));
			long conversionTime = System.currentTimeMillis() - t0;
			log.trace("\ttemps de conversió dels resultats: {}ms", conversionTime);
			long totalTime = System.currentTimeMillis() - initialTime;
			log.trace("\ttemps total:{}ms", totalTime);
			log.trace("\tnúm. resultats:{}", page.getTotalElements());
			return ResponseEntity.ok(pagedModel);
		} else {
			// Si la petició no conté informació depaginació únicament es
			// retornen els ellaços a les possibles accions sobre aquest
			// recurs.
			return ResponseEntity.ok(
					PagedModel.empty(
							buildResourceCollectionLinks(
									quickFilter,
									filter,
									namedQueries,
									perspectives,
									null,
									null,
									null,
									resourcePermissions)));
		}
	}

	@Override
	@GetMapping(value = "/export")
	@Operation(operationId = "export", summary = "Exportació de recursos")
	@PreAuthorize("!this.forbiddenExportLogic() and (this.isPublic() or hasPermission(null, this.getResourceClass().getName(), this.getOperation('EXPORT')))")
	public ResponseEntity<InputStreamResource> export(
			@RequestParam(value = "quickFilter", required = false)
			@Parameter(description = "Filtre ràpid (text)")
			final String quickFilter,
			@RequestParam(value = "filter", required = false)
			@Parameter(description = "Consulta en format Spring Filter")
			final String filter,
			@RequestParam(value = "namedQuery", required = false)
			@Parameter(description = "Consultes predefinides")
			final String[] namedQueries,
			@RequestParam(value = "perspective", required = false)
			@Parameter(description = "Perspectives de la consulta")
			final String[] perspectives,
			@Parameter(description = "Paginació dels resultats")
			final Pageable pageable,
			@RequestParam(value = "field", required = false)
			@Parameter(description = "Camps a exportar (tots si no s'especifica)")
			final String[] fields,
			@RequestParam(value = "fileType", required = false)
			@Parameter(description = "Tipus de fitxer que s'ha de generar")
			final ReportFileType fileType) throws IOException {
		log.debug("Exportant recursos amb filtre i paginació (" +
						"quickFilter={}, filter={}, namedQueries={}, perspectives={}, " +
						"pageable={}, fields={}, fileType={})",
				quickFilter,
				filter,
				Arrays.toString(namedQueries),
				Arrays.toString(perspectives),
				pageable,
				fields,
				fileType);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DownloadableFile file = getReadonlyResourceService().export(
				quickFilter,
				filter,
				namedQueries,
				perspectives,
				pageable,
				toExportFields(fields),
				fileType,
				baos);
		if (file.getContent() != null && baos.size() == 0) {
			baos.write(file.getContent());
		}
		InputStreamResource resource = new InputStreamResource(new ByteArrayInputStream(baos.toByteArray()));
		return writeDownloadableFileToResponse(file, resource);
	}

	@Override
	@GetMapping(value = "/{id}/fields/{fieldName}/download")
	@Operation(operationId = "fieldDownload", summary = "Descàrrega de l'arxiu associat a un camp del recurs")
	@PreAuthorize("!this.forbiddenFieldsLogic() and (this.isPublic() or hasPermission(#id, this.getResourceClass().getName(), this.getOperation('FIELDDOWNLOAD')))")
	public ResponseEntity<InputStreamResource> fieldDownload(
			@PathVariable
			@Parameter(description = "Identificador del recurs")
			final ID id,
			@PathVariable
			@Parameter(description = "Nom del camp")
			final String fieldName) throws IOException {
		log.debug("Descàrrega de l'arxiu associat al camp del recurs (id={}, fieldName={})",
				id,
				fieldName);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DownloadableFile file = getReadonlyResourceService().fieldDownload(
				id,
				fieldName,
				baos);
		if (file.getContent() != null && baos.size() == 0) {
			baos.write(file.getContent());
		}
		InputStreamResource resource = new InputStreamResource(new ByteArrayInputStream(baos.toByteArray()));
		return writeDownloadableFileToResponse(file, resource);
	}

	@Override
	@GetMapping("/artifacts")
	@Operation(operationId = "artifacts", summary = "Llista d'artefactes relacionats amb aquest servei")
	@PreAuthorize("!this.forbiddenArtifactLogic() and (this.isPublic() or hasPermission(null, this.getResourceClass().getName(), this.getOperation('ARTIFACT')))")
	public ResponseEntity<CollectionModel<EntityModel<ResourceArtifact>>> artifacts() {
		log.debug("Consulta dels artefactes disponibles pel recurs");
		List<ResourceArtifact> artifacts = getReadonlyResourceService().artifactFindAll(null);
		List<EntityModel<ResourceArtifact>> artifactsAsEntities = artifacts.stream().
				map(a -> EntityModel.of(a, buildSingleArtifactLinks(a))).
				collect(Collectors.toList());
		return ResponseEntity.ok(
				CollectionModel.of(
						artifactsAsEntities,
						buildArtifactsLinks()));
	}

	@Override
	@GetMapping("/artifacts/{type}/{code}")
	@Operation(operationId = "artifactGetOne", summary = "Informació d'un artefacte")
	@PreAuthorize("!this.forbiddenArtifactLogic() and (this.isPublic() or hasPermission(null, this.getResourceClass().getName(), this.getOperation('ARTIFACT')))")
	public ResponseEntity<EntityModel<ResourceArtifact>> artifactGetOne(
			@PathVariable
			@Parameter(description = "Tipus de l'artefacte")
			ResourceArtifactType type,
			@PathVariable
			@Parameter(description = "Codi de l'artefacte")
			String code) {
		log.debug("Detalls d'un artefacte del recurs (type={}, code={})", type, code);
		ResourceArtifact artifact = getReadonlyResourceService().artifactGetOne(type, code);
		return ResponseEntity.ok(
				EntityModel.of(artifact, buildSingleArtifactLinks(artifact)));
	}

	@Override
	@PatchMapping(value = "/artifacts/{type}/{code}/onChange", produces = MediaType.APPLICATION_JSON_VALUE)
	@Operation(operationId = "artifactFormOnChange", summary = "Processa els canvis en els camps del formulari d'un artefacte")
	@PreAuthorize("!this.forbiddenArtifactLogic() and (this.isPublic() or hasPermission(null, this.getResourceClass().getName(), this.getOperation('ARTIFACT')))")
	public ResponseEntity<String> artifactFormOnChange(
			@PathVariable
			@Parameter(description = "Tipus de l'artefacte")
			final ResourceArtifactType type,
			@PathVariable
			@Parameter(description = "Codi de l'artefacte")
			final String code,
			@RequestBody @Valid
			final OnChangeEvent<ID> onChangeEvent) throws ArtifactNotFoundException, JsonProcessingException {
		log.debug("Validació del formulari d'un artefacte (type={}, code={}, onChangeEvent={})", type, code, onChangeEvent);
		Class<? extends Serializable> artifactFormClass = getArtifactFormClass(type, code);
		Serializable previous = getOnChangePrevious(onChangeEvent, artifactFormClass);
		Object fieldValue = getOnChangeFieldValue(onChangeEvent, artifactFormClass);
		Map<String, AnswerRequiredException.AnswerValue> answers = getAnswersFromHeaderOrRequest(onChangeEvent.getAnswers());
		Map<String, Object> processat = getReadonlyResourceService().artifactOnChange(
				type,
				code,
				onChangeEvent.getId(),
				previous,
				onChangeEvent.getFieldName(),
				fieldValue,
				answers);
		if (processat != null) {
			String serialized = objectMapper.writeValueAsString(new BaseMutableResourceController.OnChangeForSerialization(processat));
			String response = serialized.substring(serialized.indexOf("\":{") + 2, serialized.length() - 1);
			return ResponseEntity.ok(response);
		} else {
			return ResponseEntity.ok("{}");
		}
	}

	@Override
	@PostMapping("/artifacts/{type}/{code}/validate")
	@Operation(operationId = "artifactFormValidate", summary = "Validació del formulari d'un artefacte")
	@PreAuthorize("!this.forbiddenArtifactLogic() and (this.isPublic() or hasPermission(null, this.getResourceClass().getName(), this.getOperation('ARTIFACT')))")
	public ResponseEntity<?> artifactFormValidate(
			@PathVariable
			@Parameter(description = "Tipus de l'artefacte")
			final ResourceArtifactType type,
			@PathVariable
			@Parameter(description = "Codi de l'artefacte")
			final String code,
			@RequestBody
			final JsonNode params,
			BindingResult bindingResult) throws ArtifactNotFoundException, JsonProcessingException, MethodArgumentNotValidException {
		log.debug("Validació del formulari d'un artefacte (type={}, code={}, params={})", type, code, params);
		Class<?> formClass = getArtifactFormClass(type, code);
		getArtifactParamsAsObjectWithFormClass(
				formClass,
				params,
				bindingResult);
		return ResponseEntity.ok().build();
	}

	@Override
	@GetMapping(value = "/artifacts/{type}/{code}/fields/{fieldName}/enumOptions")
	@Operation(operationId = "artifactFieldEnumOptionsFind", summary = "Consulta les opcions disponibles per a emplenar un camp enumerat que pertany al formulari d'un artefacte.")
	@PreAuthorize("!this.forbiddenArtifactLogic() and (this.isPublic() or hasPermission(null, this.getResourceClass().getName(), this.getOperation('ARTIFACT')))")
	public ResponseEntity<CollectionModel<EntityModel<FieldOption>>> artifactFieldEnumOptionsFind(
			@PathVariable
			@Parameter(description = "Tipus de l'artefacte")
			final ResourceArtifactType type,
			@PathVariable
			@Parameter(description = "Codi de l'artefacte")
			final String code,
			@PathVariable
			@Parameter(description = "Nom del camp")
			final String fieldName) {
		log.debug("Consultant possibles valors pel camp enumerat d'un artefacte (type={}, code={}, fieldName={})",
				type,
				code,
				fieldName);
		List<FieldOption> fieldOptions = getReadonlyResourceService().artifactFieldEnumOptions(
				type,
				code,
				fieldName,
				HttpRequestUtil.getCurrentHttpRequest().get().getParameterMap());
		Link selfLink = linkTo(methodOn(getClass()).artifactFieldEnumOptionsFind(type, code, fieldName)).withSelfRel();
		if (fieldOptions != null) {
			return ResponseEntity.ok(
					CollectionModel.of(
							fieldOptions.stream().
									map(fo -> EntityModel.of(
											fo,
											linkTo(methodOn(getClass()).artifactFieldEnumOptionsGetOne(type, code, fieldName, fo.getValue())).withSelfRel())).
									collect(Collectors.toList()),
							selfLink));
		} else {
			return ResponseEntity.ok(CollectionModel.empty(selfLink));
		}
	}

	@Override
	@GetMapping(value = "/artifacts/{type}/{code}/fields/{fieldName}/enumOptions/{value}")
	@Operation(operationId = "artifactFieldEnumOptionsGetOne", summary = "Consulta una de les opcions disponibles per a emplenar un camp enumerat que pertany al formulari d'un artefacte.")
	@PreAuthorize("!this.forbiddenArtifactLogic() and (this.isPublic() or hasPermission(null, this.getResourceClass().getName(), this.getOperation('ARTIFACT')))")
	public ResponseEntity<EntityModel<FieldOption>> artifactFieldEnumOptionsGetOne(
			@PathVariable
			@Parameter(description = "Tipus de l'artefacte")
			final ResourceArtifactType type,
			@PathVariable
			@Parameter(description = "Codi de l'artefacte")
			final String code,
			@PathVariable
			@Parameter(description = "Nom del camp")
			final String fieldName,
			@PathVariable
			@Parameter(description = "Valor de l'opció")
			final String value) {
		log.debug("Consultant un únic valor pel camp enumerat d'un artefacte (type={}, code={}, fieldName={}, value={})",
				type,
				code,
				fieldName,
				value);
		List<FieldOption> fieldOptions = getReadonlyResourceService().artifactFieldEnumOptions(
				type,
				code,
				fieldName,
				HttpRequestUtil.getCurrentHttpRequest().get().getParameterMap());
		FieldOption found = null;
		if (fieldOptions != null) {
			found = fieldOptions.stream().
					filter(fo -> fo.getValue().equals(value)).
					findFirst().orElse(null);
		}
		if (found != null) {
			return ResponseEntity.ok(
					EntityModel.of(
							found,
							linkTo(methodOn(getClass()).artifactFieldEnumOptionsGetOne(type, code, fieldName, found.getValue())).withSelfRel()));
		} else {
			return ResponseEntity.notFound().build();
		}
	}

	@Override
	@GetMapping(value = "/artifacts/{type}/{code}/fields/{fieldName}/options")
	@Operation(operationId = "artifactFieldOptionsFind", summary = "Consulta paginada de les opcions disponibles per a emplenar un camp de tipus ResourceReference " +
			"que pertany al formulari d'un artefacte.")
	@PreAuthorize("!this.forbiddenArtifactLogic() and (this.isPublic() or hasPermission(null, this.getResourceClass().getName(), this.getOperation('ARTIFACT')))")
	public <RR extends Resource<?>> ResponseEntity<PagedModel<EntityModel<RR>>> artifactFieldOptionsFind(
			@PathVariable
			@Parameter(description = "Tipus de l'artefacte")
			final ResourceArtifactType type,
			@PathVariable
			@Parameter(description = "Codi de l'artefacte")
			final String code,
			@PathVariable
			@Parameter(description = "Nom del camp")
			final String fieldName,
			@RequestParam(value = "quickFilter", required = false)
			@Parameter(description = "Filtre ràpid (text)")
			final String quickFilter,
			@RequestParam(value = "filter", required = false)
			@Parameter(description = "Consulta en format Spring Filter")
			final String filter,
			@RequestParam(value = "namedQuery", required = false)
			@Parameter(description = "Consultes predefinides")
			final String[] namedQueries,
			@RequestParam(value = "perspective", required = false)
			@Parameter(description = "Perspectives de la consulta")
			final String[] perspectives,
			@Parameter(description = "Paginació dels resultats")
			final Pageable pageable) {
		log.debug("Consultant possibles valors del camp de formulari de l'artefacte amb filtre i paginació (" +
						"type={}, code={}, fieldName={}, quickFilter={}, filter={}, namedQueries={}, perspectives={}, pageable={})",
				type,
				code,
				fieldName,
				quickFilter,
				filter,
				namedQueries,
				perspectives,
				pageable);
		Link resourceCollectionBaseSelfLink = linkTo(methodOn(getClass()).artifactFieldOptionsFind(
				type,
				code,
				fieldName,
				quickFilter,
				filter,
				namedQueries,
				perspectives,
				pageable)).withSelfRel();
		Link singleResourceBaseSelfLink = linkTo(methodOn(getClass()).artifactFieldOptionsGetOne(
				type,
				code,
				fieldName,
				SELF_RESOURCE_ID_TOKEN,
				null)).withSelfRel();
		return fieldOptionsFind(
				fieldName,
				quickFilter,
				filter,
				namedQueries,
				perspectives,
				pageable,
				type,
				code,
				resourceCollectionBaseSelfLink,
				singleResourceBaseSelfLink);
	}

	@Override
	@GetMapping(value = "/artifacts/{type}/{code}/fields/{fieldName}/options/{id}")
	@Operation(operationId = "artifactFieldOptionsGetOne", summary = "Consulta una de les opcions disponibles per a emplenar un camp de tipus ResourceReference " +
            "que pertany al formulari d'un artefacte")
	@PreAuthorize("!this.forbiddenArtifactLogic() and (this.isPublic() or hasPermission(null, this.getResourceClass().getName(), this.getOperation('ARTIFACT')))")
	public <RR extends Resource<RID>, RID extends Serializable> ResponseEntity<EntityModel<RR>> artifactFieldOptionsGetOne(
			@PathVariable
			@Parameter(description = "Tipus de l'artefacte")
			final ResourceArtifactType type,
			@PathVariable
			@Parameter(description = "Codi de l'artefacte")
			final String code,
			@PathVariable
			@Parameter(description = "Nom del camp")
			final String fieldName,
			@PathVariable
			@Parameter(description = "Id de l'element")
			final RID id,
			@RequestParam(value = "perspective", required = false)
			@Parameter(description = "Perspectives de la consulta")
			final String[] perspectives) {
		log.debug("Consultant un dels possibles valors del camp (type={}, code={}, fieldName={}, id={}, perspectives={})",
				type,
				code,
				fieldName,
				id,
				perspectives);
		Link singleResourceBaseSelfLink = linkTo(methodOn(getClass()).artifactFieldOptionsGetOne(
				type,
				code,
				fieldName,
				SELF_RESOURCE_ID_TOKEN,
				null)).withSelfRel();
		return fieldOptionsGetOne(
				fieldName,
				id,
				perspectives,
				type,
				code,
				singleResourceBaseSelfLink);
	}

	@Override
	@PostMapping("/artifacts/report/{code}")
	@Operation(operationId = "artifactReportGenerate", summary = "Generació de l'informe associat a un recurs")
	@PreAuthorize("!this.forbiddenArtifactLogic() and (this.isPublic() or hasPermission(null, this.getResourceClass().getName(), this.getOperation('REPORT')))")
	public ResponseEntity<InputStreamResource> artifactReportGenerate(
			@PathVariable
			@Parameter(description = "Codi de l'informe")
			final String code,
			@RequestParam(value = "fileType", required = false)
			@Parameter(description = "Tipus d'arxiu generat")
			final ReportFileType fileType,
			@RequestBody(required = false)
			final JsonNode params,
			BindingResult bindingResult) throws ArtifactNotFoundException, IOException, MethodArgumentNotValidException {
		return artifactReportGenerateId(null, code, fileType, params, bindingResult);
	}

	@Override
	@PostMapping("/{id}/artifacts/report/{code}")
	@Operation(operationId = "artifactReportGenerateId", summary = "Generació de l'informe associat a un recurs amb id")
	@PreAuthorize("!this.forbiddenArtifactLogic() and (this.isPublic() or hasPermission(null, this.getResourceClass().getName(), this.getOperation('REPORT')))")
	public ResponseEntity<InputStreamResource> artifactReportGenerateId(
			@PathVariable(required = false)
			@Parameter(description = "Identificador del recurs")
			final ID id,
			@PathVariable
			@Parameter(description = "Codi de l'informe")
			final String code,
			@RequestParam(value = "fileType", required = false)
			@Parameter(description = "Tipus d'arxiu generat")
			final ReportFileType fileType,
			@RequestBody(required = false)
			final JsonNode params,
			BindingResult bindingResult) throws ArtifactNotFoundException, IOException, MethodArgumentNotValidException {
		log.debug("Generació de l'informe associat al recurs (id={}, code={}, fileType={}, params={})",
				id,
				code,
				fileType,
				params);
		Class<?> formClass = getArtifactFormClass(ResourceArtifactType.REPORT, code);
		Serializable paramsObject = getArtifactParamsAsObjectWithFormClass(
				formClass,
				params,
				bindingResult);
		List<?> items = getReadonlyResourceService().artifactReportGenerateData(id, code, paramsObject);
		if (fileType == null) {
			List<EntityModel<?>> itemsAsEntities = items.stream().
					map(i -> EntityModel.of(i, buildReportItemLink(code, items.indexOf(i)))).
					collect(Collectors.toList());
			Link reportLink;
			if (id != null) {
				reportLink = linkTo(methodOn(getClass()).artifactReportGenerateId(id, code, null, null, null)).withSelfRel();
			} else {
				reportLink = linkTo(methodOn(getClass()).artifactReportGenerate(code, null, null, null)).withSelfRel();
			}
			CollectionModel<EntityModel<?>> responseCollection = CollectionModel.of(
					itemsAsEntities,
					Link.of(reportLink.toUri().toString()).withSelfRel());
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			objectMapper.writeValue(baos, responseCollection);
			ResponseEntity.BodyBuilder bodyBuilder = ResponseEntity.ok();
			bodyBuilder.contentType(MediaType.APPLICATION_JSON);
			bodyBuilder.contentLength(baos.size());
			return bodyBuilder.body(new InputStreamResource(new ByteArrayInputStream(baos.toByteArray())));
		} else {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			DownloadableFile file = getReadonlyResourceService().artifactReportGenerateFile(
					code,
					items,
					fileType,
					baos);
			if (file.getContent() != null && baos.size() == 0) {
				baos.write(file.getContent());
			}
			InputStreamResource resource = new InputStreamResource(new ByteArrayInputStream(baos.toByteArray()));
			return writeDownloadableFileToResponse(file, resource);
		}
	}

	@Override
	@GetMapping(value = "/artifacts/report/{code}/fields/{fieldName}/options")
	@Operation(operationId = "artifactReportFieldOptionsFind", summary = "Consulta paginada de les opcions disponibles per a emplenar un camp de tipus ResourceReference que pertany al formulari de l'informe")
	@PreAuthorize("!this.forbiddenArtifactLogic() and (this.isPublic() or hasPermission(null, this.getResourceClass().getName(), this.getOperation('REPORT')))")
	public <RR extends Resource<?>> ResponseEntity<PagedModel<EntityModel<RR>>> artifactReportFieldOptionsFind(
			@PathVariable
			@Parameter(description = "Codi de l'informe")
			final String code,
			@PathVariable
			@Parameter(description = "Nom del camp")
			final String fieldName,
			@RequestParam(value = "quickFilter", required = false)
			@Parameter(description = "Filtre ràpid (text)")
			final String quickFilter,
			@RequestParam(value = "filter", required = false)
			@Parameter(description = "Consulta en format Spring Filter")
			final String filter,
			@RequestParam(value = "namedQuery", required = false)
			@Parameter(description = "Consultes predefinides")
			final String[] namedQueries,
			@RequestParam(value = "perspective", required = false)
			@Parameter(description = "Perspectives de la consulta")
			final String[] perspectives,
			@Parameter(description = "Paginació dels resultats")
			final Pageable pageable) {
		log.debug("Consultant possibles valors del camp del formulari de l'informe (" +
						"code={}, fieldName={}, quickFilter={}, filter={}, namedQueries={}, perspectives={}, pageable={})",
				code,
				fieldName,
				quickFilter,
				filter,
				namedQueries,
				perspectives,
				pageable);
		Link resourceCollectionBaseSelfLink = linkTo(methodOn(getClass()).artifactReportFieldOptionsFind(
				code,
				fieldName,
				quickFilter,
				filter,
				namedQueries,
				perspectives,
				pageable)).withSelfRel();
		Link singleResourceBaseSelfLink = linkTo(methodOn(getClass()).artifactReportFieldOptionsGetOne(
				code,
				fieldName,
				SELF_RESOURCE_ID_TOKEN,
				null)).withSelfRel();
		return fieldOptionsFind(
				fieldName,
				quickFilter,
				filter,
				namedQueries,
				perspectives,
				pageable,
				ResourceArtifactType.REPORT,
				code,
				resourceCollectionBaseSelfLink,
				singleResourceBaseSelfLink);
	}

	@Override
	@GetMapping(value = "/artifacts/report/{code}/fields/{fieldName}/options/{id}")
	@Operation(operationId = "artifactReportFieldOptionsGetOne", summary = "Consulta d'una de les opcions disponibles per a emplenar un camp de tipus ResourceReference que pertany al formulari de l'informe")
	@PreAuthorize("!this.forbiddenArtifactLogic() and (this.isPublic() or hasPermission(null, this.getResourceClass().getName(), this.getOperation('REPORT')))")
	public <RR extends Resource<RID>, RID extends Serializable> ResponseEntity<EntityModel<RR>> artifactReportFieldOptionsGetOne(
			@PathVariable
			@Parameter(description = "Codi de l'informe")
			final String code,
			@PathVariable
			@Parameter(description = "Nom del camp")
			final String fieldName,
			@PathVariable
			@Parameter(description = "Id de l'element")
			final RID id,
			@RequestParam(value = "perspective", required = false)
			@Parameter(description = "Perspectives de la consulta")
			final String[] perspectives) {
		log.debug("Consultant un dels possibles valors del camp del formulari de l'informe (" +
						"code={}, fieldName={}, id={}, perspectives={})",
				code,
				fieldName,
				id,
				perspectives);
		Link singleResourceBaseSelfLink = linkTo(methodOn(getClass()).artifactReportFieldOptionsGetOne(
				code,
				fieldName,
				SELF_RESOURCE_ID_TOKEN,
				null)).withSelfRel();
		return fieldOptionsGetOne(
				fieldName,
				id,
				perspectives,
				ResourceArtifactType.REPORT,
				code,
				singleResourceBaseSelfLink);
	}

	@Override
	@GetMapping(value = "/artifacts/filter/{code}/fields/{fieldName}/options")
	@Operation(operationId = "artifactFilterFieldOptionsFind", summary = "Consulta paginada de les opcions disponibles per a emplenar un camp de tipus ResourceReference que pertany al formulari del filtre")
	@PreAuthorize("!this.forbiddenArtifactLogic() and (this.isPublic() or hasPermission(null, this.getResourceClass().getName(), this.getOperation('FIND')))")
	public <RR extends Resource<?>> ResponseEntity<PagedModel<EntityModel<RR>>> artifactFilterFieldOptionsFind(
			@PathVariable
			@Parameter(description = "Codi de l'informe")
			final String code,
			@PathVariable
			@Parameter(description = "Nom del camp")
			final String fieldName,
			@RequestParam(value = "quickFilter", required = false)
			@Parameter(description = "Filtre ràpid (text)")
			final String quickFilter,
			@RequestParam(value = "filter", required = false)
			@Parameter(description = "Consulta en format Spring Filter")
			final String filter,
			@RequestParam(value = "namedQuery", required = false)
			@Parameter(description = "Consultes predefinides")
			final String[] namedQueries,
			@RequestParam(value = "perspective", required = false)
			@Parameter(description = "Perspectives de la consulta")
			final String[] perspectives,
			@Parameter(description = "Paginació dels resultats")
			final Pageable pageable) {
		log.debug("Consultant possibles valors del camp del formulari del filtre (" +
						"code={}, fieldName={}, quickFilter={}, filter={}, namedQueries={}, perspectives={}, pageable={})",
				code,
				fieldName,
				quickFilter,
				filter,
				namedQueries,
				perspectives,
				pageable);
		Link resourceCollectionBaseSelfLink = linkTo(methodOn(getClass()).artifactFilterFieldOptionsFind(
				code,
				fieldName,
				quickFilter,
				filter,
				namedQueries,
				perspectives,
				pageable)).withSelfRel();
		Link singleResourceBaseSelfLink = linkTo(methodOn(getClass()).artifactFilterFieldOptionsGetOne(
				code,
				fieldName,
				SELF_RESOURCE_ID_TOKEN,
				null)).withSelfRel();
		return fieldOptionsFind(
				fieldName,
				quickFilter,
				filter,
				namedQueries,
				perspectives,
				pageable,
				ResourceArtifactType.FILTER,
				code,
				resourceCollectionBaseSelfLink,
				singleResourceBaseSelfLink);
	}

	@Override
	@GetMapping(value = "/artifacts/filter/{code}/fields/{fieldName}/options/{id}")
	@Operation(operationId = "artifactFilterFieldOptionsGetOne", summary = "Consulta d'una de les opcions disponibles per a emplenar un camp de tipus ResourceReference que pertany al formulari del filtre")
	@PreAuthorize("!this.forbiddenArtifactLogic() and (this.isPublic() or hasPermission(null, this.getResourceClass().getName(), this.getOperation('FIND')))")
	public <RR extends Resource<RID>, RID extends Serializable> ResponseEntity<EntityModel<RR>> artifactFilterFieldOptionsGetOne(
			@PathVariable
			@Parameter(description = "Codi de l'informe")
			final String code,
			@PathVariable
			@Parameter(description = "Nom del camp")
			final String fieldName,
			@PathVariable
			@Parameter(description = "Id de l'element")
			final RID id,
			@RequestParam(value = "perspective", required = false)
			@Parameter(description = "Perspectives de la consulta")
			final String[] perspectives) {
		log.debug("Consultant un dels possibles valors del camp del formulari del filtre (" +
						"code={}, fieldName={}, id={}, perspectives={})",
				code,
				fieldName,
				id,
				perspectives);
		Link singleResourceBaseSelfLink = linkTo(methodOn(getClass()).artifactFilterFieldOptionsGetOne(
				code,
				fieldName,
				SELF_RESOURCE_ID_TOKEN,
				null)).withSelfRel();
		return fieldOptionsGetOne(
				fieldName,
				id,
				perspectives,
				ResourceArtifactType.FILTER,
				code,
				singleResourceBaseSelfLink);
	}

	public Class<R> getResourceClass() {
		if (resourceClass == null) {
			resourceClass = TypeUtil.getArgumentClassFromGenericSuperclass(
					getClass(),
					BaseReadonlyResourceController.class,
					0);
		}
		return resourceClass;
	}

	@Override
	protected Link getIndexLink() {
		String rel = StringUtil.decapitalize(getResourceClass().getSimpleName());
		return linkTo(getClass()).withRel(rel);
	}

	protected ReadonlyResourceService<R, ID> getReadonlyResourceService() {
		return readonlyResourceService;
	}

	protected <RR extends Resource<?>> EntityModel<RR> toEntityModel(
			RR resource,
			Link... links) {
		return EntityModel.of(
				resource,
				links);
	}

	protected <RR extends Resource<?>> PagedModel<EntityModel<RR>> toPagedModel(
			Page<RR> page,
			String[] perspectives,
			Link singleResourceSelfLink,
			ResourcePermissions resourcePermissions,
			boolean withDownloadLink,
			boolean withEditLinksInputAndOutput,
			Link... links) {
		List<ResourceArtifact> artifactsAll = getReadonlyResourceService().artifactFindAll(null);
		return PagedModel.of(
				page.getContent().stream().map(resource -> {
					Link[] resourceLinks = resource != null ? buildSingleResourceLinks(
							resource.getId(),
							perspectives,
							singleResourceSelfLink,
							artifactsAll,
							resourcePermissions,
							withDownloadLink,
							withEditLinksInputAndOutput).toArray(new Link[0]) : new Link[0];
					return toEntityModel(resource, resourceLinks);
				}).collect(Collectors.toList()),
				new PagedModel.PageMetadata(
						page.getNumberOfElements(),
						page.getNumber(),
						page.getTotalElements(),
						page.getTotalPages()),
				links);
	}

	protected Class<? extends Serializable> getArtifactFormClass(
			ResourceArtifactType artifactType,
			String code) {
		ResourceArtifact artifact = getReadonlyResourceService().artifactGetOne(
				artifactType,
				code);
		return artifact.isFormClassActive() ? artifact.getFormClass() : null;
	}

	protected Serializable getArtifactParamsAsObjectWithFormClass(
			Class<?> formClass,
			JsonNode params,
			BindingResult bindingResult) throws JsonProcessingException, MethodArgumentNotValidException {
		Serializable paramsObject = null;
		if (formClass != null) {
			paramsObject = (Serializable) JsonUtil.getInstance().fromJsonToObjectWithType(
					params,
					formClass);
			validateResource(paramsObject, 1, bindingResult);
		}
		return paramsObject;
	}

	protected <P extends Serializable> P getOnChangePrevious(
			OnChangeEvent<ID> onChangeEvent,
			Class<P> resourceClass) throws JsonProcessingException {
		P previous = null;
		if (onChangeEvent.getPrevious() != null && resourceClass != null) {
			previous = (P) ReflectUtils.newInstance(resourceClass);
			JsonUtil.getInstance().fillResourceWithFieldsMap(
					previous,
					JsonUtil.getInstance().fromJsonToMap(onChangeEvent.getPrevious(), resourceClass),
					null,
					null);
		}
		return previous;
	}

	protected <P extends Serializable> Object getOnChangeFieldValue(
			OnChangeEvent<ID> onChangeEvent,
			Class<P> resourceClass) {
		if (onChangeEvent.getFieldName() != null) {
			return JsonUtil.getInstance().fillResourceWithFieldsMap(
					ReflectUtils.newInstance(resourceClass),
					null,
					onChangeEvent.getFieldName(),
					onChangeEvent.getFieldValue());
		} else {
			return null;
		}
	}

	protected Map<String, AnswerRequiredException.AnswerValue> getAnswersFromHeaderOrRequest(
			Map<String, Object> requestAnswers) {
		RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
		if (requestAttributes instanceof ServletRequestAttributes) {
			HttpServletRequest request = ((ServletRequestAttributes)requestAttributes).getRequest();
			Map<String, AnswerRequiredException.AnswerValue> answers = new HashMap<>();
			if (requestAnswers != null) {
				Map<String, AnswerRequiredException.AnswerValue> answersFromRequest = requestAnswers.entrySet().stream().
						collect(Collectors.toMap(
								Map.Entry::getKey,
								e -> {
									if (e.getValue() == null) {
										return new AnswerRequiredException.AnswerValue();
									} else if (e.getValue() instanceof Boolean) {
										return new AnswerRequiredException.AnswerValue((Boolean)e.getValue());
									} else {
										return new AnswerRequiredException.AnswerValue(e.getValue().toString());
									}
								}));
				answers = new HashMap<>(answersFromRequest);
			}
			String headerAnswers = request.getHeader(httpHeaderAnswers);
			if (headerAnswers != null) {
				try {
					JsonNode headerAnswersJson = objectMapper.readTree(headerAnswers);
					Map<String, Object> headerAnswersMap = objectMapper.convertValue(
							headerAnswersJson,
							new TypeReference<>(){});
					Map<String, AnswerRequiredException.AnswerValue> answersFromHeader = headerAnswersMap.entrySet().stream().
							collect(Collectors.toMap(
									Map.Entry::getKey,
									e -> {
										if (e.getValue() == null) {
											return new AnswerRequiredException.AnswerValue();
										} else if (e.getValue() instanceof Boolean) {
											return new AnswerRequiredException.AnswerValue((Boolean)e.getValue());
										} else {
											return new AnswerRequiredException.AnswerValue(e.getValue().toString());
										}
									}));
					answers.putAll(answersFromHeader);
				} catch (JsonProcessingException ex) {
					log.warn("Error al parsejar la capçalera {}", httpHeaderAnswers, ex);
					return null;
				}
			}
			return answers;
		} else {
			return null;
		}
	}

	protected <RR extends Resource<?>> ResponseEntity<PagedModel<EntityModel<RR>>> fieldOptionsFind(
			String fieldName,
			String quickFilter,
			String filter,
			String[] namedQueries,
			String[] perspectives,
			Pageable pageable,
			ResourceArtifactType artifactType,
			String artifactCode,
			Link resourceCollectionBaseSelfLink,
			Link singleResourceBaseSelfLink) {
		Optional<FieldAndClass> referencedResourceFieldAndClass = findReferenceFieldAndClass(
				getArtifactAwareResourceClass(artifactType, artifactCode),
				fieldName);
		if (referencedResourceFieldAndClass.isPresent()) {
			if (pageable != null) {
				// Només es fa la consulta de recursos si la petició conté informació de paginació.
				Class<?> referencedResourceClass = referencedResourceFieldAndClass.get().getClazz();
				ReadonlyResourceService<RR, ?> resourceService = (ReadonlyResourceService<RR, ?>)resourceServiceLocator.
						getReadOnlyEntityResourceServiceForResourceClass(referencedResourceClass);
				Page<RR> page = resourceService.findPage(
						quickFilter,
						fieldOptionsProcessedFilterWithFieldAnnotation(
								referencedResourceFieldAndClass.get().getField(),
								filter),
						fieldOptionsProcessedNamedQueriesWithFieldAnnotation(
								referencedResourceFieldAndClass.get().getField(),
								namedQueries),
						perspectives,
						fieldOptionsProcessedPageableWithResourceAnnotation(
								pageable,
								referencedResourceClass));
				return ResponseEntity.ok(
						toPagedModel(
								page,
								perspectives,
								singleResourceBaseSelfLink,
								ResourcePermissions.readOnly(),
								false,
								true,
								buildOptionsLinks(
										referencedResourceFieldAndClass.get().getClazz(),
										quickFilter,
										filter,
										namedQueries,
										perspectives,
										pageable,
										page,
										resourceCollectionBaseSelfLink,
										ResourcePermissions.readOnly()).toArray(new Link[0])));
			} else {
				// Si la petició no conté informació de paginació únicament es retornen els ellaços
				// a les possibles accions sobre aquest recurs.
				return ResponseEntity.ok(
						PagedModel.empty(
								buildOptionsLinks(
										referencedResourceFieldAndClass.get().getClazz(),
										quickFilter,
										filter,
										namedQueries,
										perspectives,
										null,
										null,
										resourceCollectionBaseSelfLink,
										ResourcePermissions.readOnly())));
			}
		} else {
			throw new ResourceFieldNotFoundException(resourceClass, fieldName);
		}
	}

	protected <RR extends Resource<RID>, RID extends Serializable> ResponseEntity<EntityModel<RR>> fieldOptionsGetOne(
			final String fieldName,
			final RID id,
			final String[] perspectives,
			ResourceArtifactType artifactType,
			String artifactCode,
			Link singleResourceBaseSelfLink) {
		Optional<FieldAndClass> referencedResourceFieldAndClass = findReferenceFieldAndClass(
				getArtifactAwareResourceClass(artifactType, artifactCode),
				fieldName);
		if (referencedResourceFieldAndClass.isPresent()) {
			Class<?> referencedResourceClass = referencedResourceFieldAndClass.get().getClazz();
			ReadonlyResourceService<RR, RID> resourceService = (ReadonlyResourceService<RR, RID>)resourceServiceLocator.
					getReadOnlyEntityResourceServiceForResourceClass(referencedResourceClass);
			RR resource = resourceService.getOne(id, perspectives);
			EntityModel<RR> entityModel = toEntityModel(
					resource,
					buildSingleResourceLinks(
							resource.getId(),
							perspectives,
							singleResourceBaseSelfLink,
							null,
							ResourcePermissions.readOnly(),
							false,
							true).toArray(new Link[0]));
			return ResponseEntity.ok(entityModel);
		} else {
			throw new ResourceFieldNotFoundException(resourceClass, fieldName);
		}
	}

	protected <T> void validateResource(
			T resource,
			int paramIndex,
			BindingResult bindingResult,
			Object... validationHints) throws MethodArgumentNotValidException {
		BindingResult resourceBindingResult = new BeanPropertyBindingResult(
				resource,
				bindingResult != null ? bindingResult.getObjectName() : "resource");
		Object[] finalValidationHints = validationHints;
		if (validationHints == null || validationHints.length == 0) {
			finalValidationHints = new Object[] { Default.class };
		}
		validator.validate(
				resource,
				resourceBindingResult,
				finalValidationHints);
		if (resourceBindingResult.hasErrors() && bindingResult != null) {
			bindingResult.addAllErrors(resourceBindingResult);
			throw new MethodArgumentNotValidException(
					new MethodParameter(
							new Object() {}.getClass().getEnclosingMethod(),
							paramIndex),
					bindingResult);
		}
	}

	protected static final String SELF_RESOURCE_ID_TOKEN = "#resourceId#";
	protected List<Link> buildSingleResourceLinks(
			Serializable id,
			String[] perspective,
			Link singleResourceBaseSelfLink,
			List<ResourceArtifact> artifactsAll,
			ResourcePermissions resourcePermissions,
			boolean withDownloadLink,
			boolean withEditLinksInputAndOutput) {
		List<Link> ls = new ArrayList<>();
		Link selfLink;
		if (singleResourceBaseSelfLink != null) {
			String baseSelfResourceLinkHref = singleResourceBaseSelfLink.getHref();
			String token = URLEncoder.encode(SELF_RESOURCE_ID_TOKEN, StandardCharsets.UTF_8);
			baseSelfResourceLinkHref = baseSelfResourceLinkHref.replace(token, id.toString());
			selfLink = Link.of(baseSelfResourceLinkHref).withSelfRel();
		} else {
			selfLink = linkTo(methodOn(getClass()).getOne(id, perspective)).withSelfRel();
		}
		Map<String, Object> expandMap = new HashMap<>();
		expandMap.put("perspective", perspective);
		ls.add(selfLink.expand(expandMap));
		if (withDownloadLink) {
			ls.add(buildFieldDownloadLink(id));
		}
		ls.addAll(buildSingleResourceArtifactLinks(id, artifactsAll));
		return ls;
	}

	@SneakyThrows
	protected List<Link> buildResourceCollectionLinks(
			String quickFilter,
			String filter,
			String[] namedQuery,
			String[] perspective,
			Pageable pageable,
			Page<?> page,
			Link resourceCollectionBaseSelfLink,
			ResourcePermissions resourcePermissions) {
		List<Link> ls = new ArrayList<>();
		if (pageable == null) {
			// Enllaços que es retornen quan no es fa cap consulta
			Link selfLink = linkTo(getClass()).withSelfRel();
			if (resourcePermissions.isReadGranted()) {
				ls.add(selfLinkWithDefaultProperties(selfLink, true));
				// Els enllaços de les accions find, getOne i create només es
				// retornen si a la petició s'ha especificat informació de
				// paginació.
				Link getOneLink = linkTo(methodOn(getClass()).getOne(null, null)).withRel("getOne");
				String getOneLinkHref = getOneLink.getHref().replace("perspective", "perspective*");
				ls.add(Link.of(UriTemplate.of(getOneLinkHref), "getOne"));
				ls.add(buildFindLink(resourceCollectionBaseSelfLink));
				Link exportLink = linkTo(methodOn(getClass()).export(
						null,
						null,
						null,
						null,
						null,
						null,
						null)).withRel("export");
				ls.add(buildFindLinkWithParams(
						exportLink,
						null,
						null,
						null,
						null,
						null,
						TemplateVariable.requestParameter("field").composite(),
						TemplateVariable.requestParameter("fileType")));
				ls.add(linkTo(methodOn(getClass()).artifacts()).withRel("artifacts"));
				ls.addAll(buildResourceCollectionArtifactLinks());
			} else {
				ls.add(selfLink);
			}
		} else {
			// Enllaços que es retornen amb els resultats de la consulta
			Link baseLink = resourceCollectionBaseSelfLink != null ? resourceCollectionBaseSelfLink : linkTo(getClass()).withSelfRel();
			Link selfLink = buildFindLinkWithParams(
					baseLink,
					quickFilter,
					filter,
					namedQuery,
					perspective,
					pageable);
			if (resourcePermissions.isReadGranted()) {
				ls.add(selfLinkWithDefaultProperties(selfLink, false));
				if (resourcePermissions.isReadGranted() && pageable.isPaged()) {
					if (pageable.getPageNumber() < page.getTotalPages()) {
						if (!page.isFirst()) {
							ls.add(
									buildFindLinkWithParams(
											baseLink.withRel("first"),
											quickFilter,
											filter,
											namedQuery,
											perspective,
											pageable.first()));
						}
						if (page.hasPrevious()) {
							ls.add(
									buildFindLinkWithParams(
											baseLink.withRel("previous"),
											quickFilter,
											filter,
											namedQuery,
											perspective,
											pageable.previousOrFirst()));
						}
						if (page.hasNext()) {
							ls.add(
									buildFindLinkWithParams(
											baseLink.withRel("next"),
											quickFilter,
											filter,
											namedQuery,
											perspective,
											pageable.next()));
						}
						if (!page.isLast()) {
							ls.add(
									buildFindLinkWithParams(
											baseLink.withRel("last"),
											quickFilter,
											filter,
											namedQuery,
											perspective,
											PageRequest.of(page.getTotalPages() - 1, pageable.getPageSize())));
						}
						if (page.getTotalElements() > 0 && page.getTotalPages() > 1) {
							Link findLink = buildFindLinkWithParams(
									baseLink.withRel("toPageNumber"),
									quickFilter,
									filter,
									namedQuery,
									perspective,
									PageRequest.of(0, pageable.getPageSize()));
							// Al link generat li eliminam la variable page amb el valor 0
							String findLinkHref = findLink.getHref().replace("page=0&", "").replace("page=0", "");
							TemplateVariables findTemplateVariables = new TemplateVariables(
									new TemplateVariable("page", VariableType.REQUEST_PARAM));
							// I a més hi afegim la variable page
							ls.add(
									Link.of(
											UriTemplate.of(findLinkHref).with(findTemplateVariables),
											"toPageNumber"));
						}
					}
				}
			} else {
				ls.add(selfLink);
			}
		}
		return ls;
	}

	protected Link selfLinkWithDefaultProperties(Link selfLink, boolean withProperties) {
		// Aquest mètode proporciona modifica el Link self afegint una Affordance que
		// es mostrarà a dins els _templates de HAL FORMS amb el nom "default".
		// Hem modificat les classes HalFormsTemplateBuilder i HalFormsPropertyFactory
		// per a que mostrin als templates les Affordances amb mètode GET amb les seves
		// properties.
		if (withProperties) {
			return Affordances.of(selfLink).
					afford(HttpMethod.GET).
					withInputAndOutput(getResourceClass()).
					withName("default").
					toLink();
		} else {
			return Affordances.of(selfLink).
					afford(FAKE_DEFAULT_TEMPLATE_HTTP_METHOD).
					withName("default").
					toLink();
		}
	}

	protected Link buildFindLink(
			Link baseLink) {
		String rel = "find";
		Link findLink = baseLink != null ? baseLink : linkTo(methodOn(getClass()).find(null, null, null, null, null)).withRel(rel);
		// Al link generat li canviam les variables namedQuery i
		// perspective perquè no les posa com a múltiples.
		String findLinkHref = findLink.getHref().
				replace("namedQuery", "namedQuery*").
				replace("perspective", "perspective*")/*.
				replace("field", "field*")*/;
		// I a més hi afegim les variables page, size i sort que no les
		// detecta a partir de la classe de tipus Pageable
		TemplateVariables findTemplateVariables = new TemplateVariables(
				new TemplateVariable("page", VariableType.REQUEST_PARAM),
				new TemplateVariable("size", VariableType.REQUEST_PARAM),
				new TemplateVariable("sort", VariableType.REQUEST_PARAM).composite());
		return Link.of(UriTemplate.of(findLinkHref).with(findTemplateVariables), rel);
	}

	protected Link buildFindLinkWithParams(
			Link baseLink,
			String quickFilter,
			String filter,
			String[] namedQuery,
			String[] perspective,
			Pageable pageable,
			TemplateVariable... additionalTemplateVariables) {
		Map<String, Object> expandMap = new HashMap<>();
		if (pageable != null) {
			if (pageable.isPaged()) {
				expandMap.put("page", pageable.getPageNumber());
				expandMap.put("size", pageable.getPageSize());
			} else {
				expandMap.put("page", "UNPAGED");
			}
		}
		if (pageable != null) {
			expandMap.put(
					"sort",
					pageable.getSort().stream().
							map(o -> o.getProperty() + "," + o.getDirection().name().toLowerCase()).
							collect(Collectors.toList()));
		}
		if (quickFilter != null) {
			expandMap.put("quickFilter", quickFilter);
		}
		if (filter != null) {
			expandMap.put("filter", filter);
		}
		if (namedQuery != null) {
			expandMap.put("namedQuery", Arrays.asList(namedQuery));
		}
		if (perspective != null) {
			expandMap.put("perspective", Arrays.asList(perspective));
		}
		String linkTemplate = baseLink.getHref();
		int linkTemplateGroupIndex = baseLink.getHref().lastIndexOf('{');
		if (linkTemplateGroupIndex != -1) {
			linkTemplate = baseLink.getHref().substring(0, linkTemplateGroupIndex);
		}
		List<TemplateVariable> mergedTemplateVariables = new ArrayList<>(Arrays.asList(
				TemplateVariable.requestParameter("page"),
				TemplateVariable.requestParameter("size"),
				TemplateVariable.requestParameter("sort").composite(),
				TemplateVariable.requestParameter("quickFilter"),
				TemplateVariable.requestParameter("filter"),
				TemplateVariable.requestParameter("namedQuery").composite(),
				TemplateVariable.requestParameter("perspective").composite()
		));
		if (additionalTemplateVariables != null) {
			mergedTemplateVariables.addAll(Arrays.asList(additionalTemplateVariables));
		}
		Link link = Link.of(
				UriTemplate.of(linkTemplate, new TemplateVariables(mergedTemplateVariables)),
				baseLink.getRel());
		return expandMap.isEmpty() ? link : link.expand(expandMap);
	}

	protected List<Link> buildSingleResourceArtifactLinks(Serializable id, List<ResourceArtifact> artifactsAll) {
		List<ResourceArtifact> thisArtifactsAll = artifactsAll != null ?
			artifactsAll :
			getReadonlyResourceService().artifactFindAll(null);
		return thisArtifactsAll.stream().
				filter(a -> a.getType() == ResourceArtifactType.REPORT && a.getRequiresId() != null && a.getRequiresId()).
				map(a -> buildReportLinkWithAffordances(a, id)).
				collect(Collectors.toList());
	}

	protected List<Link> buildResourceCollectionArtifactLinks() {
		List<ResourceArtifact> artifacts = getReadonlyResourceService().artifactFindAll(null);
		return artifacts.stream().
				filter(a -> a.getType() == ResourceArtifactType.REPORT && (a.getRequiresId() == null || !a.getRequiresId())).
				map(a -> buildReportLinkWithAffordances(a, null)).
				collect(Collectors.toList());
	}

	protected Link[] buildArtifactsLinks() {
		List<Link> ls = new ArrayList<>();
		Link selfLink = linkTo(methodOn(getClass()).artifacts()).withSelfRel();
		ls.add(selfLinkWithDefaultProperties(selfLink, false));
		return ls.toArray(new Link[0]);
	}

	@SneakyThrows
	protected Link[] buildSingleArtifactLinks(ResourceArtifact artifact) {
		List<Link> links = new ArrayList<>();
		Link selfLink = linkTo(methodOn(getClass()).artifactGetOne(artifact.getType(), artifact.getCode())).withSelfRel();
		links.add(selfLink);
		if (artifact.getFormClass() != null) {
			Link artifactFieldOptionsFind = linkTo(methodOn(getClass()).artifactFieldOptionsFind(
					artifact.getType(),
					artifact.getCode(),
					null,
					null,
					null,
					null,
					null,
					null)).withRel("artifactFieldOptionsFind");
			links.add(buildFindLinkWithParams(
					artifactFieldOptionsFind,
					null,
					null,
					null,
					null,
					null));
			Link formValidateLink = Affordances.
					of(linkTo(methodOn(getClass()).artifactFormValidate(artifact.getType(), artifact.getCode(), null, null)).withRel("formValidate")).
					afford(HttpMethod.POST).
					withInputAndOutput(artifact.getFormClass()).
					withName("formValidate").
					toLink();
			links.add(formValidateLink);
			Link onChangeLink = Affordances.
					of(linkTo(methodOn(getClass()).artifactFormOnChange(artifact.getType(), artifact.getCode(), null)).withRel("formOnChange")).
					afford(HttpMethod.PATCH).
					withInputAndOutput(artifact.getFormClass()).
					withName("formOnChange").
					toLink();
			links.add(onChangeLink);
		}
		if (artifact.getType() == ResourceArtifactType.FILTER) {
			links.add(buildFilterLinkWithAffordances(artifact));
		}
		if (artifact.getType() == ResourceArtifactType.REPORT) {
			links.add(buildReportLinkWithAffordances(artifact, null));
		}
		// TODO no sé per què es creen templates addicionals amb els noms "artifactFormOnChange",
		//  "artifactReportGenerate" i "artifactActionExec". Només haurien d'aparèixer "formValidate" i "formOnChange".
		return links.toArray(new Link[0]);
	}

	private Link buildFilterLinkWithAffordances(ResourceArtifact artifact) {
		String rel = "filter_" + artifact.getCode();
		Link findLink = buildFindLink(null).withRel(rel);
		if (artifact.getFormClass() != null) {
			return Affordances.of(findLink).
					afford(HttpMethod.GET).
					withInputAndOutput(artifact.getFormClass()).
					withName(findLink.getRel().value()).
					toLink();
		} else {
			return Affordances.of(findLink).
					afford(HttpMethod.GET).
					withName(findLink.getRel().value()).
					toLink();
		}
	}

	@SneakyThrows
	private Link buildFieldDownloadLink(Serializable id) {
		return linkTo(methodOn(getClass()).fieldDownload(id, null)).withRel("fieldDownload");
	}

	@SneakyThrows
	private Link buildReportLink(ResourceArtifact artifact, Serializable id) {
		String rel = "generate_" + artifact.getCode();
		if (artifact.getRequiresId() != null && artifact.getRequiresId()) {
			return linkTo(methodOn(getClass()).artifactReportGenerateId(id, artifact.getCode(), null, null, null)).withRel(rel);
		} else {
			return linkTo(methodOn(getClass()).artifactReportGenerate(artifact.getCode(), null, null, null)).withRel(rel);
		}
	}
	private Link buildReportLinkWithAffordances(ResourceArtifact artifact, Serializable id) {
		Link reportLink = buildReportLink(artifact, id);
		if (artifact.getFormClass() != null) {
			return Affordances.of(reportLink).
					afford(HttpMethod.POST).
					withInputAndOutput(artifact.getFormClass()).
					withName(reportLink.getRel().value()).
					toLink();
		} else {
			return Affordances.of(reportLink).
					afford(HttpMethod.POST).
					withName(reportLink.getRel().value()).
					toLink();
		}
	}
	@SneakyThrows
	private Link buildReportItemLink(String code, int index) {
		Link reportLink = linkTo(methodOn(getClass()).artifacts()).withSelfRel();
		return Link.of(reportLink.toUri().toString() + "/report/" + code + "/item/" + index).withSelfRel();
	}

	private List<Link> buildOptionsLinks(
			Class<?> artifactFormClass,
			String quickFilter,
			String filter,
			String[] namedQuery,
			String[] perspective,
			Pageable pageable,
			Page<?> page,
			Link resourceCollectionBaseSelfLink,
			ResourcePermissions resourcePermissions) {
		if (page != null) {
			return buildResourceCollectionLinks(
					quickFilter,
					filter,
					namedQuery,
					perspective,
					pageable,
					page,
					resourceCollectionBaseSelfLink,
					resourcePermissions).stream().
					filter(l -> !l.getRel().value().equals("getOne")).
					collect(Collectors.toList());
		} else {
			return Collections.singletonList(
					Affordances.of(resourceCollectionBaseSelfLink).
							afford(HttpMethod.GET).
							withInputAndOutput(artifactFormClass).
							withName(resourceCollectionBaseSelfLink.getRel().value()).
							toLink());
		}
	}

	private String filterWithFieldParameters(String filter, Class<?> resourceClass) {
		// Retorna l'spring filter emplenat amb paràmetres del request
		Optional<HttpServletRequest> request = HttpRequestUtil.getCurrentHttpRequest();
		if (request.isPresent()) {
			Set<String> paramNames = request.get().getParameterMap().keySet();
			List<String> filterResourceFields = new ArrayList<>();
			ReflectionUtils.doWithFields(resourceClass, field -> {
				int modifiers = field.getModifiers();
				boolean isStaticFinal = Modifier.isStatic(modifiers) && Modifier.isFinal(modifiers);
				if (!isStaticFinal) {
					String fieldName = field.getName();
					boolean hasLikeParam = paramNames.contains(fieldName + "~");
					boolean hasBetweenParams = paramNames.contains(fieldName + "1") && paramNames.contains(fieldName + "2");
					if (paramNames.contains(fieldName) || hasLikeParam || hasBetweenParams) {
						filterResourceFields.add(field.getName());
					}
				}
			});
			String[] springFilterParts = filterResourceFields.stream().
					map(p -> requestParamResourceFieldToSpringFilter(resourceClass, p, request.get())).
					filter(Objects::nonNull).
					toArray(String[]::new);
			if (springFilterParts.length > 0) {
				String requestParamsFilter = String.join(" and ", springFilterParts);
				if (filter != null && !filter.isBlank()) {
					return "(" + filter + ") and (" + requestParamsFilter + ")";
				} else {
					return requestParamsFilter;
				}
			} else {
				return filter;
			}
		} else {
			return filter;
		}
	}

	private String requestParamResourceFieldToSpringFilter(
			Class<?> resourceClass,
			String requestParamResource,
			HttpServletRequest request) {
		Field field = ReflectionUtils.findField(resourceClass, requestParamResource);
		if (field != null) {
			boolean isFieldReferenceType = field.getType().isAssignableFrom(ResourceReference.class);
			boolean isFieldTextType = field.isEnumConstant() ||
					field.getType().isAssignableFrom(String.class) ||
					field.getType().isAssignableFrom(Date.class) ||
					field.getType().isAssignableFrom(LocalDate.class) ||
					field.getType().isAssignableFrom(LocalDateTime.class) ||
					field.getType().isAssignableFrom(ZonedDateTime.class) ||
					field.getType().isAssignableFrom(Instant.class) ||
					field.getType().isAssignableFrom(YearMonth.class) ||
					field.getType().isAssignableFrom(MonthDay.class);
			if (isFieldReferenceType) {
				String value = request.getParameter(requestParamResource);
				return "(" + requestParamResource + ".id:" + value + ")";
			} else {
				String value = request.getParameter(requestParamResource);
				if (isFieldTextType && value != null) value = "'" + value + "'";
				String value1 = request.getParameter(requestParamResource + "1");
				if (isFieldTextType && value1 != null) value1 = "'" + value1 + "'";
				String value2 = request.getParameter(requestParamResource + "2");
				if (isFieldTextType && value2 != null) value2 = "'" + value2 + "'";
				String valueLike = request.getParameter(requestParamResource + "~");
				if (value != null) {
					return "(" + requestParamResource + ":" + value + ")";
				} else if (value1 != null && value2 != null) {
					return "(" + requestParamResource + ">:" + value1 + " and " + requestParamResource + "<:" + value2 + ")";
				} else if (valueLike != null) {
					return "(" + requestParamResource + "~'*" + valueLike + "*')";
				} else {
					return null;
				}
			}
		} else {
			return null;
		}
	}

	private Class<?> getArtifactAwareResourceClass(
			ResourceArtifactType artifactType,
			String artifactCode) throws ArtifactFormNotFoundException {
		Class<?> resourceClass;
		if (artifactType == null && artifactCode == null) {
			resourceClass = getResourceClass();
		} else {
			ResourceArtifact artifact = getReadonlyResourceService().artifactGetOne(
					artifactType,
					artifactCode);
			if (artifact.isFormClassActive()) {
				resourceClass = artifact.getFormClass();
			} else {
				throw new ArtifactFormNotFoundException(getResourceClass(), artifactType, artifactCode);
			}
		}
		return resourceClass;
	}

	private Optional<FieldAndClass> findReferenceFieldAndClass(
			Class<?> resourceClass,
			String fieldName) {
		if (!fieldName.isBlank()) {
			String currentFieldName;
			if (fieldName.contains(".")) {
				String[] fieldNameParts = fieldName.split("\\.");
				currentFieldName = fieldNameParts[0];
			} else {
				currentFieldName = fieldName;
			}
			Field currentField = ReflectionUtils.findField(resourceClass, currentFieldName);
			if (currentField != null) {
				Class<?> currentFieldType = TypeUtil.getFieldTypeMultipleAware(currentField);
				if (ResourceReference.class.isAssignableFrom(currentFieldType)) {
					Class<?> referencedResourceClass = TypeUtil.getReferencedResourceClass(currentField);
					if (fieldName.contains(".")) {
						return findReferenceFieldAndClass(
								referencedResourceClass,
								fieldName.substring(currentFieldName.length() + 1));
					} else {
						return Optional.of(
								new FieldAndClass(currentField, referencedResourceClass));
					}
				}
			}
		}
		return Optional.empty();
	}

	private String fieldOptionsProcessedFilterWithFieldAnnotation(Field field, String filterFromRequest) {
		String processedFilter = filterFromRequest;
		ResourceField resourceFieldAnnotation = field.getAnnotation(ResourceField.class);
		if (resourceFieldAnnotation != null && !resourceFieldAnnotation.springFilter().isEmpty()) {
			String springFilter = resourceFieldAnnotation.springFilter();
			if (springFilter.startsWith("#{") && springFilter.endsWith("}")) {
				String expressionToParse = springFilter.substring(2, springFilter.length() - 1);
				StandardEvaluationContext context = new StandardEvaluationContext(
						new BaseMutableResourceController.ProcessedOptionsQueryExpressionContext(RequestSessionUtil.getRequestSession()));
				String parsedSpringFilter = getExpressionParser().
						parseExpression(expressionToParse).
						getValue(context, String.class);
				processedFilter = parsedSpringFilter + (filterFromRequest != null ? " and (" + filterFromRequest + ")" : "");
			} else {
				processedFilter = springFilter + (filterFromRequest != null ? " and (" + filterFromRequest + ")" : "");
			}
		}
		return processedFilter;
	}

	private String[] fieldOptionsProcessedNamedQueriesWithFieldAnnotation(Field field, String[] namedQueries) {
		List<String> processedNamedQueries = new ArrayList<>();
		if (namedQueries != null) {
			Collections.addAll(processedNamedQueries, namedQueries);
		}
		ResourceField resourceFieldAnnotation = field.getAnnotation(ResourceField.class);
		if (resourceFieldAnnotation != null) {
			Collections.addAll(processedNamedQueries, resourceFieldAnnotation.namedQueries());
		}
		return processedNamedQueries.toArray(new String[0]);
	}

	private Pageable fieldOptionsProcessedPageableWithResourceAnnotation(
			Pageable pageable,
			Class<?> resourceClass) {
		if (pageable != null) {
			ResourceConfig resourceConfigAnotation = resourceClass.getAnnotation(ResourceConfig.class);
			if (resourceConfigAnotation != null) {
				Sort sort = Sort.unsorted();
				if (resourceConfigAnotation.defaultSortFields().length > 0) {
					List<Sort.Order> orders = new ArrayList<>();
					for (ResourceConfig.ResourceSort sortField: resourceConfigAnotation.defaultSortFields()) {
						orders.add(new Sort.Order(sortField.direction(), sortField.field()));
					}
					sort = Sort.by(orders);
				}/* else if (!resourceConfigAnotation.descriptionField().isEmpty()) {
					sort = Sort.by(Sort.Order.asc(resourceConfigAnotation.descriptionField()));
				}*/
				if (pageable.isPaged()) {
					return PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), pageable.getSortOr(sort));
				} else {
					return new UnpagedButSorted(pageable.getSortOr(sort));
				}
			} else {
				return pageable;
			}
		} else {
			return Pageable.unpaged();
		}
	}

	private ExportField[] toExportFields(String[] fields) {
		String[] fieldNames;
		if (fields == null) {
			List<String> fs = new ArrayList<>();
			ReflectionUtils.doWithFields(
					resourceClass,
					f -> fs.add(f.getName()),
					f -> !Modifier.isStatic(f.getModifiers()));
			ReflectionUtils.doWithMethods(
					resourceClass,
					m -> fs.add(StringUtil.decapitalize(m.getName().substring(3))),
					m -> m.getName().startsWith("get") && !Modifier.isStatic(m.getModifiers()));
			fieldNames = fs.toArray(new String[0]);
		} else {
			fieldNames = fields;
		}
		Link linkWithAffordances = Affordances.
				of(linkTo(methodOn(getClass()).getOne(null, null)).withSelfRel()).
				afford(HttpMethod.POST).
				withInputAndOutput(getResourceClass()).
				withName("default").
				toLink();
		AffordanceModel halFormsModel = linkWithAffordances.getAffordances().get(1).getAffordanceModel(MediaTypes.HAL_FORMS_JSON);
		Object halFormsTemplatePropertyWriter = applicationContext.getBean("halFormsTemplatePropertyWriter");
		Field builderField = ReflectionUtils.findField(halFormsTemplatePropertyWriter.getClass(), "builder");
		ReflectionUtils.makeAccessible(builderField);
		CustomHalFormsTemplateBuilder halFormsTemplateBuilder = (CustomHalFormsTemplateBuilder)ReflectionUtils.getField(builderField, halFormsTemplatePropertyWriter);
		CustomHalFormsPropertyFactory halFormsPropertyFactory = halFormsTemplateBuilder.getCustomHalFormsPropertyFactory();
		Method createPropertiesMethod = ReflectionUtils.findMethod(halFormsPropertyFactory.getClass(), "createProperties", halFormsModel.getClass());
		ReflectionUtils.makeAccessible(createPropertiesMethod);
		List<?> properties = (List<?>)ReflectionUtils.invokeMethod(createPropertiesMethod, halFormsPropertyFactory, halFormsModel);
		return Arrays.stream(fieldNames).
				map(name -> properties != null ? properties.stream().
						filter(p -> name.equals(TypeUtil.getFieldValue(p, "name"))).
						findFirst().
						map(p -> new ExportField(
								TypeUtil.getFieldValue(p, "name"),
								TypeUtil.getFieldValue(p, "prompt"))).
						orElse(null) : null).
				filter(Objects::nonNull).
				toArray(ExportField[]::new);
	}

	private ResponseEntity<InputStreamResource> writeDownloadableFileToResponse(
			DownloadableFile file,
			InputStreamResource resource) throws IOException {
		ResponseEntity.BodyBuilder bodyBuilder = ResponseEntity.ok();
		HttpHeaders headers = new HttpHeaders();
		headers.set("Content-Disposition", "attachment; filename=" + file.getName());
		headers.set("Access-Control-Expose-Headers", "Content-Disposition");
		bodyBuilder.headers(headers);
		MediaType mediaType = file.getContentType() != null ? MediaType.valueOf(file.getContentType()) : MediaType.APPLICATION_OCTET_STREAM;
		bodyBuilder.contentType(mediaType);
		if (file.getContent() != null) {
			bodyBuilder.contentLength(file.getContent().length);
		} else if (file.getContentLength() != null) {
			bodyBuilder.contentLength(file.getContentLength());
		}
		return bodyBuilder.body(resource);
	}

	private ExpressionParser getExpressionParser() {
		if (parser == null) {
			parser = new SpelExpressionParser();
		}
		return parser;
	}

	@Getter
	@AllArgsConstructor
	private static class FieldAndClass {
		Field field;
		Class<?> clazz;
	}

}
