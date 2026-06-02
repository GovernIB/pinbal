package es.caib.pinbal.logic.base.service;

import es.caib.pinbal.logic.base.helper.BasePermissionHelper;
import es.caib.pinbal.logic.base.helper.JasperReportsHelper;
import es.caib.pinbal.logic.base.helper.ObjectMappingHelper;
import es.caib.pinbal.logic.base.helper.ResourceEntityMappingHelper;
import es.caib.pinbal.logic.base.springfilter.FilterSpecification;
import es.caib.pinbal.logic.intf.base.annotation.ResourceArtifact;
import es.caib.pinbal.logic.intf.base.annotation.ResourceConfig;
import es.caib.pinbal.logic.intf.base.annotation.ResourceField;
import es.caib.pinbal.logic.intf.base.exception.ActionExecutionException;
import es.caib.pinbal.logic.intf.base.exception.AnswerRequiredException;
import es.caib.pinbal.logic.intf.base.exception.ArtifactNotFoundException;
import es.caib.pinbal.logic.intf.base.exception.FieldArtifactNotFoundException;
import es.caib.pinbal.logic.intf.base.exception.PerspectiveApplicationException;
import es.caib.pinbal.logic.intf.base.exception.ReportGenerationException;
import es.caib.pinbal.logic.intf.base.exception.ResourceFieldNotFoundException;
import es.caib.pinbal.logic.intf.base.exception.ResourceNotFoundException;
import es.caib.pinbal.logic.intf.base.model.DownloadableFile;
import es.caib.pinbal.logic.intf.base.model.ExportField;
import es.caib.pinbal.logic.intf.base.model.FieldArtifactType;
import es.caib.pinbal.logic.intf.base.model.FieldOption;
import es.caib.pinbal.logic.intf.base.model.ReportFileType;
import es.caib.pinbal.logic.intf.base.model.Resource;
import es.caib.pinbal.logic.intf.base.model.ResourceArtifactType;
import es.caib.pinbal.logic.intf.base.model.ResourceReference;
import es.caib.pinbal.logic.intf.base.service.ReadonlyResourceService;
import es.caib.pinbal.logic.intf.base.util.StringUtil;
import es.caib.pinbal.logic.intf.base.util.TypeUtil;
import es.caib.pinbal.persist.base.entity.ResourceEntity;
import es.caib.pinbal.persist.base.repository.BaseRepository;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.aopalliance.intercept.MethodInterceptor;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.ResolvableType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Persistable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ReflectionUtils;

import javax.annotation.PostConstruct;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Servei amb la funcionalitat básica per a la gestió d'un recurs que només es pot consultar.
 *
 * @param <R> classe del recurs.
 * @param <ID> classe de la clau primària del recurs.
 * @param <E> classe de l'entitat de base de dades del recurs.
 *
 * @author Límit Tecnologies
 */
@Slf4j
public abstract class BaseReadonlyResourceService<R extends Resource<ID>, ID extends Serializable, E extends ResourceEntity<R, ID>>
		implements ReadonlyResourceService<R, ID> {

	protected BaseRepository<E, ID> entityRepository;

	@Autowired
	protected ApplicationContext applicationContext;
	@Autowired
	protected ObjectMappingHelper objectMappingHelper;
	@Autowired
	protected JasperReportsHelper jasperReportsHelper;
	@Autowired
	protected ResourceEntityMappingHelper resourceEntityMappingHelper;
	@Autowired
	protected BasePermissionHelper permissionHelper;

	private Class<R> resourceClass;
	private Class<ID> pkClass;
	private Class<E> entityClass;

	private final Map<String, ReportGenerator<E, ?, ? extends Serializable>> reportGeneratorMap = new HashMap<>();
	private final Map<String, FilterProcessor<?>> filterProcessorMap = new HashMap<>();
	private final Map<String, PerspectiveApplicator<E, R>> perspectiveApplicatorMap = new HashMap<>();
	private final Map<String, FieldDownloader<E>> fieldDownloaderMap = new HashMap<>();

	@PostConstruct
	public void initRepository() {
		Class<E> entityClass = getEntityClass();
		Class<ID> pkClass = getPkClass();
		ResolvableType type = ResolvableType.forClassWithGenerics(BaseRepository.class, entityClass, pkClass);
		String[] beanNames = ((DefaultListableBeanFactory)applicationContext.getAutowireCapableBeanFactory()).getBeanNamesForType(type);
		if (beanNames.length == 0) {
			if (!isEntityRepositoryOptional()) {
				throw new IllegalStateException("Couldn't find BaseRepository<" + entityClass + ", " + pkClass + ">");
			}
		} else {
			entityRepository = (BaseRepository<E, ID>)applicationContext.getBean(beanNames[0]);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public R getOne(
			ID id,
			String[] perspectives) throws ResourceNotFoundException {
		log.debug("Getting single resource (id={}, perspectives={})", id, perspectives);
		beforeGetOne(perspectives);
		E entity = getEntity(id);
		beforeConversion(entity);
		R response = entityToResource(entity);
		afterConversion(entity, response);
		if (perspectives != null) {
			applyPerspectives(entity, response, perspectives);
		}
		return response;
	}

	@Override
	@Transactional(readOnly = true)
	public Page<R> findPage(
			String quickFilter,
			String filter,
			String[] namedQueries,
			String[] perspectives,
			Pageable pageable) {
		long t0 = System.currentTimeMillis();
		log.debug(
				"Querying entities page with filter and pagination (" +
						"quickFilter={}, filter={}, namedQueries={}, " +
						"perspectives={}, pageable={})",
				quickFilter,
				filter,
				Arrays.toString(namedQueries),
				Arrays.toString(perspectives),
				pageable);
		beforeFind(
				quickFilter,
				filter,
				namedQueries,
				pageable);
		Page<E> resultat = entityRepositoryFindEntities(
				quickFilter,
				filter,
				namedQueries,
				pageable);
		long elapsedDatabase = System.currentTimeMillis() - t0;
		t0 = System.currentTimeMillis();
		beforeConversion(resultat.getContent());
		Page<R> response = new PageImpl<>(
				entitiesToResources(resultat.getContent()),
				pageable,
				resultat.getTotalElements());
		afterConversion(resultat.getContent(), response.getContent());
		long elapsedConversion = System.currentTimeMillis() - t0;
		long elapsedPerspectives = 0;
		if (perspectives != null) {
			t0 = System.currentTimeMillis();
			applyPerspectives(
					resultat.getContent(),
					response.getContent(),
					perspectives);
			elapsedPerspectives = System.currentTimeMillis() - t0;
		}
		log.debug(
				"Query elapsed time (database={}ms, conversion={}ms, perspectives={}ms)",
				elapsedDatabase,
				elapsedConversion,
				elapsedPerspectives);
		return response;
	}

	@Override
	@Transactional(readOnly = true)
	public DownloadableFile export(
			String quickFilter,
			String filter,
			String[] namedQueries,
			String[] perspectives,
			Pageable pageable,
			ExportField[] fields,
			ReportFileType fileType,
			OutputStream out) {
		long t0 = System.currentTimeMillis();
		log.debug(
				"Querying entities for export with filter and pagination (" +
						"quickFilter={}, filter={}, namedQueries={}, " +
						"perspectives={}, pageable={}, fieldNamesAndLabels={}, fileType={})",
				quickFilter,
				filter,
				Arrays.toString(namedQueries),
				Arrays.toString(perspectives),
				pageable,
				fields,
				fileType);
		beforeFind(
				quickFilter,
				filter,
				namedQueries,
				pageable);
		Page<E> resultat = entityRepositoryFindEntities(
				quickFilter,
				filter,
				namedQueries,
				pageable);
		long elapsedDatabase = System.currentTimeMillis() - t0;
		beforeConversion(resultat.getContent());
		Page<R> response = new PageImpl<>(
				entitiesToResources(resultat.getContent()),
				pageable,
				resultat.getTotalElements());
		afterConversion(resultat.getContent(), response.getContent());
		long elapsedConversion = System.currentTimeMillis() - elapsedDatabase;
		DownloadableFile exportFile = jasperReportsHelper.export(
				getResourceClass(),
				response.getContent(),
				fields,
				fileType,
				out);
		long elapsedGeneration = System.currentTimeMillis() - elapsedDatabase;
		log.debug(
				"Export elapsed time (database={}ms, conversion={}ms, generation={}ms)",
				elapsedDatabase,
				elapsedConversion,
				elapsedGeneration);
		return exportFile;
	}

	@Override
	@Transactional(readOnly = true)
	public DownloadableFile fieldDownload(
			ID id,
			String fieldName,
			OutputStream out) throws ResourceNotFoundException, ResourceFieldNotFoundException, FieldArtifactNotFoundException, IOException {
		Field field = ReflectionUtils.findField(getResourceClass(), fieldName);
		if (field != null) {
			FieldDownloader<E> fieldDownloader = fieldDownloaderMap.get(fieldName);
			if (fieldDownloader != null) {
				return fieldDownloader.download(
						getEntity(id),
						fieldName,
						out);
			} else {
				throw new FieldArtifactNotFoundException(getResourceClass(), FieldArtifactType.DOWNLOAD, fieldName);
			}
		} else {
			throw new ResourceFieldNotFoundException(getResourceClass(), fieldName);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public List<es.caib.pinbal.logic.intf.base.model.ResourceArtifact> artifactFindAll(ResourceArtifactType type) {
		log.debug("Querying all artifacts (type={})", type);
		List<es.caib.pinbal.logic.intf.base.model.ResourceArtifact> artifacts = new ArrayList<>();
		if (type == null || type == ResourceArtifactType.PERSPECTIVE) {
			artifacts.addAll(
					perspectiveApplicatorMap.keySet().stream().
							filter(code -> permissionHelper.checkResourceArtifactPermission(
									getResourceClass(),
									ResourceArtifactType.PERSPECTIVE,
									code)).
							map(code -> new es.caib.pinbal.logic.intf.base.model.ResourceArtifact(
									ResourceArtifactType.PERSPECTIVE,
									code,
									null,
									null)).
							collect(Collectors.toList()));
		}
		if (type == null || type == ResourceArtifactType.REPORT) {
			artifacts.addAll(
					reportGeneratorMap.keySet().stream().
							filter(code -> permissionHelper.checkResourceArtifactPermission(
									getResourceClass(),
									ResourceArtifactType.REPORT,
									code)).
							map(code -> new es.caib.pinbal.logic.intf.base.model.ResourceArtifact(
									ResourceArtifactType.REPORT,
									code,
									artifactRequiresId(ResourceArtifactType.REPORT, code),
									artifactGetFormClass(ResourceArtifactType.REPORT, code))).
							collect(Collectors.toList()));
		}
		if (type == null || type == ResourceArtifactType.FILTER) {
			artifacts.addAll(
					artifactGetFilterAll().stream().
							filter(f -> permissionHelper.checkResourceArtifactPermission(
									getResourceClass(),
									ResourceArtifactType.FILTER,
									f.code())).
							map(f -> new es.caib.pinbal.logic.intf.base.model.ResourceArtifact(
									ResourceArtifactType.FILTER,
									f.code(),
									null,
									artifactGetFormClass(ResourceArtifactType.FILTER, f.code()))).
							collect(Collectors.toList()));
		}
		return artifacts;
	}

	@Override
	@Transactional(readOnly = true)
	public es.caib.pinbal.logic.intf.base.model.ResourceArtifact artifactGetOne(
			ResourceArtifactType type,
			String code) throws ArtifactNotFoundException {
		log.debug("Querying artifact form class (type={}, code={})", type, code);
		if (type == ResourceArtifactType.PERSPECTIVE) {
			PerspectiveApplicator<?, ?> perspectiveApplicator = perspectiveApplicatorMap.get(code);
			if (perspectiveApplicator != null) {
				boolean allowed = permissionHelper.checkResourceArtifactPermission(
						getResourceClass(),
						ResourceArtifactType.PERSPECTIVE,
						code);
				if (allowed) {
					return new es.caib.pinbal.logic.intf.base.model.ResourceArtifact(
							ResourceArtifactType.PERSPECTIVE,
							code,
							null,
							null);
				}
			}
		} else if (type == ResourceArtifactType.REPORT) {
			ReportGenerator<E, ?, ?> reportGenerator = reportGeneratorMap.get(code);
			if (reportGenerator != null) {
				boolean allowed = permissionHelper.checkResourceArtifactPermission(
						getResourceClass(),
						ResourceArtifactType.REPORT,
						code);
				if (allowed) {
					return new es.caib.pinbal.logic.intf.base.model.ResourceArtifact(
							ResourceArtifactType.REPORT,
							code,
							artifactRequiresId(ResourceArtifactType.REPORT, code),
							artifactGetFormClass(ResourceArtifactType.REPORT, code));
				}
			}
		} else if (type == ResourceArtifactType.FILTER) {
			if (artifactIsPresentInResourceConfig(type, code)) {
				boolean allowed = permissionHelper.checkResourceArtifactPermission(
						getResourceClass(),
						ResourceArtifactType.FILTER,
						code);
				if (allowed) {
					return new es.caib.pinbal.logic.intf.base.model.ResourceArtifact(
							ResourceArtifactType.FILTER,
							code,
							null,
							artifactGetFormClass(ResourceArtifactType.FILTER, code));
				}
			}
		}
		throw new ArtifactNotFoundException(getResourceClass(), type, code);
	}

	@Override
	@Transactional(readOnly = true)
	public <P extends Serializable> Map<String, Object> artifactOnChange(
			ResourceArtifactType type,
			String code,
			ID id,
			P previous,
			String fieldName,
			Object fieldValue,
			Map<String, AnswerRequiredException.AnswerValue> answers) throws ArtifactNotFoundException, ResourceFieldNotFoundException, AnswerRequiredException {
		log.debug("Processing onChange event for artifact (type={}, code={}, previous={}, fieldName={}, fieldValue={}, answers={})",
				type,
				code,
				previous,
				fieldName,
				fieldValue,
				answers);
		es.caib.pinbal.logic.intf.base.model.ResourceArtifact artifact = artifactGetOne(type, code);
		if (artifact.getFormClass() != null) {
			onChangeCheckIfFieldExists(artifact.getFormClass(), fieldName);
			return onChangeProcessRecursiveLogic(
					id,
					previous,
					fieldName,
					fieldValue,
					null,
					(id2,
					 previous1,
					 fieldName1,
					 fieldValue1,
					 answers1,
					 previousFieldNames,
					 target) -> internalArtifactOnChange(
							type,
							code,
							id2,
							previous1,
							fieldName1,
							fieldValue1,
							answers1,
							previousFieldNames,
							target),
					answers);
		} else {
			log.warn("Couldn't find form class for artifact (resourceClass={}, type={}, code={})", getResourceClass(), type, code);
			return new HashMap<>();
		}
	}

	@Override
	@Transactional(readOnly = true)
	public List<FieldOption> artifactFieldEnumOptions(
			ResourceArtifactType type,
			String code,
			String fieldName,
			Map<String,String[]> requestParameterMap) {
		log.debug("Querying field enum options for artifact (type={}, code={}, fieldName={}, requestParameterMap={})",
				type,
				code,
				fieldName,
				requestParameterMap);
		BaseMutableResourceService.FieldOptionsProvider fieldOptionsProvider = artifactGetFieldOptionsProvider(type, code);
		if (fieldOptionsProvider != null) {
			return fieldOptionsProvider.getOptions(fieldName, requestParameterMap);
		} else {
			log.warn("Couldn't find FieldOptionsProvider for artifact (resourceClass={}, type={}, code={}, fieldName={}, requestParameterMap={})",
					getResourceClass(),
					type,
					code,
					fieldName,
					requestParameterMap);
			return null;
		}
	}

	@Override
	@Transactional(readOnly = true)
	public <P extends Serializable> List<?> artifactReportGenerateData(
			ID id,
			String code,
			P params) throws ArtifactNotFoundException, ReportGenerationException {
		log.debug("Generating report data (id={}, code={}, params={})", id, code, params);
		ReportGenerator<E, P, ?> generator = (ReportGenerator<E, P, ?>)reportGeneratorMap.get(code);
		if (generator != null) {
			E entity = null;
			if (id != null) {
				entity = getEntity(id);
			}
			try {
				return generator.generateData(code, entity, params);
			} catch (ActionExecutionException ex) {
				throw ex;
			} catch (Exception ex) {
				ReportGenerationException rgex = new ReportGenerationException(
						getResourceClass(),
						id,
						code,
						"",
						ex);
				log.error(rgex.getMessage(), ex);
				throw rgex;
			}
		} else {
			throw new ArtifactNotFoundException(getResourceClass(), ResourceArtifactType.REPORT, code);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public DownloadableFile artifactReportGenerateFile(
			String code,
			List<?> data,
			ReportFileType fileType,
			OutputStream out) throws ArtifactNotFoundException, ReportGenerationException {
		log.debug("Generating report file (code={}, data={}, fileType={})", code, data, fileType);
		ReportGenerator<E, ?, ?> generator = reportGeneratorMap.get(code);
		if (generator != null) {
			try {
				DownloadableFile downloadableFile = generator.generateFile(code, data, fileType, out);
				if (downloadableFile != null) {
					return downloadableFile;
				} else {
					URL reportUrl = generator.getJasperReportUrl(code, fileType);
					if (reportUrl != null) {
						return jasperReportsHelper.generate(
								getResourceClass(),
								code,
								reportUrl,
								data,
								LocaleContextHolder.getLocale(),
								null,
								fileType,
								out);
					} else {
						throw new ReportGenerationException(
								getResourceClass(),
								null,
								code,
								"Couldn't generate report file: both generateFile and getJasperReportUrl methods returned null (fileType=" + fileType + ")");
					}
				}
			} catch (ActionExecutionException ex) {
				throw ex;
			} catch (Exception ex) {
				ReportGenerationException rgex = new ReportGenerationException(
						getResourceClass(),
						null,
						code,
						"",
						ex);
				log.error(rgex.getMessage(), ex);
				throw rgex;
			}

		} else {
			throw new ArtifactNotFoundException(getResourceClass(), ResourceArtifactType.REPORT, code);
		}
	}

	protected E getEntity(ID id) throws ResourceNotFoundException {
		Optional<E> result = entityRepositoryFindOne(id);
		if (result.isPresent()) {
			return result.get();
		} else {
			String idToString = id != null ? id.toString() : "<null>";
			String idMessage = idToString;
			String additionalSpringFilter = additionalSpringFilter(null, null);
			Specification<E> additionalSpecification = additionalSpecification(null);
			if (additionalSpringFilter != null && !additionalSpringFilter.trim().isEmpty()) {
				idMessage = "{" +
						"id=" + idToString + ", " +
						"springFilter=" + additionalSpringFilter + ", " +
						"additionalSpecification=" + additionalSpecification + "}";
			}
			throw new ResourceNotFoundException(getResourceClass(), idMessage);
		}
	}

	protected R entityToResource(E entity) {
		return resourceEntityMappingHelper.entityToResource(entity, getResourceClass());
	}

	protected List<R> entitiesToResources(List<E> entities) {
		return entities.stream().map(this::entityToResource).collect(Collectors.toList());
	}

	protected void applyPerspectives(
			List<E> entities,
			List<R> resources,
			String[] perspectives) throws ArtifactNotFoundException {
		Arrays.stream(perspectives).forEach(p -> {
			PerspectiveApplicator<E, R> perspectiveApplicator = perspectiveApplicatorMap.get(p);
			if (perspectiveApplicator != null) {
				boolean modified = perspectiveApplicator.applyMultiple(p, entities, resources);
				if (!modified) {
					IntStream.range(0, entities.size()).forEach(i -> {
						perspectiveApplicator.applySingle(
								p,
								entities.get(i),
								resources.get(i));
					});
				}
			} else {
				throw new ArtifactNotFoundException(getResourceClass(), ResourceArtifactType.PERSPECTIVE, p);
			}
		});
	}

	protected void applyPerspectives(
			E entity,
			R resource,
			String[] perspectives) {
		Arrays.stream(perspectives).forEach(p -> {
			PerspectiveApplicator<E, R> perspectiveApplicator = perspectiveApplicatorMap.get(p);
			if (perspectiveApplicator != null) {
				perspectiveApplicator.applySingle(p, entity, resource);
			} else {
				throw new ArtifactNotFoundException(getResourceClass(), ResourceArtifactType.PERSPECTIVE, p);
			}
		});
	}

	protected List<SortedField> getResourceDefaultSortFields(Class<?> resourceClass) {
		ResourceConfig resourceAnnotation = getResourceClass().getAnnotation(ResourceConfig.class);
		if (resourceAnnotation != null && resourceAnnotation.defaultSortFields().length > 0) {
			return Arrays.stream(resourceAnnotation.defaultSortFields()).
					map(s -> new SortedField(s.field(), s.direction())).
					collect(Collectors.toList());
		} else {
			return Collections.emptyList();
		}
	}

	protected void onChangeCheckIfFieldExists(Class<?> formClass, String fieldName) {
		if (fieldName != null) {
			Field field = ReflectionUtils.findField(formClass, fieldName);
			if (field == null) {
				throw new ResourceFieldNotFoundException(formClass, fieldName);
			}
		}
	}

	protected <P extends Serializable> Map<String, Object> onChangeProcessRecursiveLogic(
			Serializable id,
			P previous,
			String fieldName,
			Object fieldValue,
			String[] previousFieldNames,
			OnChangeLogicProcessor<P> onChangeLogicProcessor,
			Map<String, AnswerRequiredException.AnswerValue> answers) {
		Map<String, Object> changesToReturn = null;
		P newInstance = (P)newClassInstance(previous.getClass());
		if (newInstance != null) {
			Map<String, Object> changes = new HashMap<>();
			ProxyFactory factory = new ProxyFactory(newInstance);
			factory.setProxyTargetClass(true);
			factory.addAdvice((MethodInterceptor) invocation -> {
				String methodName = invocation.getMethod().getName();
				Object[] arguments = invocation.getArguments();
				if (methodName.startsWith("set") && arguments.length > 0) {
					changes.put(
							StringUtil.decapitalize(methodName.substring("set".length())),
							arguments[0]);
				}
				return invocation.proceed();
			});
			P target = (P)factory.getProxy();
			if (onChangeLogicProcessor != null) {
				onChangeLogicProcessor.onChange(
						id,
						previous,
						fieldName,
						fieldValue,
						answers,
						previousFieldNames,
						target);
			}
			if (!changes.isEmpty()) {
				changesToReturn = new HashMap<>(changes);
				for (String changedFieldName: changes.keySet()) {
					Field changedField = ReflectionUtils.findField(previous.getClass(), changedFieldName);
					if (changedField != null) {
						ResourceField fieldAnnotation = changedField.getAnnotation(ResourceField.class);
						if (fieldAnnotation != null && fieldAnnotation.onChangeActive()) {
							Object previousWithChanges = cloneObjectWithFieldsMap(
									previous,
									fieldName,
									fieldValue,
									changes,
									changedFieldName);
							List<String> previousFieldNamesWithChangedFieldName = new ArrayList<>();
							if (previousFieldNames != null) {
								previousFieldNamesWithChangedFieldName.addAll(Arrays.asList(previousFieldNames));
							}
							if (fieldName != null) {
								previousFieldNamesWithChangedFieldName.add(fieldName);
							}
							Map<String, Object> changesPerField = onChangeProcessRecursiveLogic(
									id,
									(P)previousWithChanges,
									changedFieldName,
									changes.get(changedFieldName),
									previousFieldNamesWithChangedFieldName.toArray(new String[0]),
									onChangeLogicProcessor,
									answers);
							if (changesPerField != null) {
								changesToReturn.putAll(changesPerField);
							}
						}
					}
				}
			}
		}
		return changesToReturn;
	}

	protected Object cloneObjectWithFieldsMap(
			Object resource,
			String fieldName,
			Object fieldValue,
			Map<String, Object> fields,
			String excludeField) {
		Object clonedResource = objectMappingHelper.clone(resource);
		if (fieldName != null) {
			Field field = ReflectionUtils.findField(resource.getClass(), fieldName);
			if (field != null) {
				ReflectionUtils.makeAccessible(field);
				ReflectionUtils.setField(field, clonedResource, fieldValue);
			} else {
				log.error(
						"Processing onChange request: couldn't find field {} on resource {}",
						fieldName,
						resource.getClass().getName());
			}
		}
		fields.forEach((k, v) -> {
			if (!k.equals(excludeField)) {
				Field f = ReflectionUtils.findField(resource.getClass(), k);
				if (f != null) {
					ReflectionUtils.makeAccessible(f);
					ReflectionUtils.setField(f, clonedResource, v);
				}
			}
		});
		return clonedResource;
	}

	protected BaseMutableResourceService.FieldOptionsProvider artifactGetFieldOptionsProvider(
			ResourceArtifactType type,
			String code) {
		BaseMutableResourceService.FieldOptionsProvider fieldOptionsProvider = null;
		if (type == ResourceArtifactType.REPORT) {
			fieldOptionsProvider = reportGeneratorMap.get(code);
		} else if (type == ResourceArtifactType.FILTER) {
			fieldOptionsProvider = filterProcessorMap.get(code);
		}
		return fieldOptionsProvider;
	}

	protected <C> C newClassInstance(Class<C> clazz) {
		try {
			return clazz.getDeclaredConstructor().newInstance();
		} catch (Exception ex) {
			log.error("Couldn't create new resource instance (resourceClass={})", getResourceClass(), ex);
			return null;
		}
	}

	protected String additionalSpringFilter(
			String currentSpringFilter,
			String[] namedQueries) {
		return null;
	}

	protected Specification<E> additionalSpecification(String[] namedQueries) {
		return null;
	}

	protected String namedQueryToSpringFilter(String namedQuery) {
		return null;
	}
	protected <P> Specification<P> namedQueryToSpecification(String namedQuery) {
		return null;
	}

	protected <P> Specification<P> processSpecification(Specification<P> specification) {
		return specification;
	}

	protected Sort processSort(Sort sort) {
		return sort;
	}

	protected void beforeGetOne(String[] perspectives) {}
	protected void beforeFind(
			String quickFilter,
			String springFilter,
			String[] namedQueries,
			Pageable pageable) {}
	protected void beforeConversion(E entity) {}
	protected void afterConversion(E entity, R resource) {}
	protected void beforeConversion(List<E> entities) {
		if (entities != null) {
			for (E entity: entities) {
				beforeConversion(entity);
			}
		}
	}
	protected void afterConversion(List<E> entities, List<R> resources) {
		if (resources != null) {
			for (int i = 0; i < resources.size(); i++) {
				afterConversion(entities.get(i), resources.get(i));
			}
		}
	}

	protected Class<R> getResourceClass() {
		if (resourceClass == null) {
			resourceClass = TypeUtil.getArgumentClassFromGenericSuperclass(
					getClass(),
					BaseReadonlyResourceService.class,
					0);
		}
		return resourceClass;
	}

	protected Class<ID> getPkClass() {
		if (pkClass == null) {
			pkClass = TypeUtil.getArgumentClassFromGenericSuperclass(
					getClass(),
					BaseReadonlyResourceService.class,
					1);
		}
		return pkClass;
	}

	protected Class<E> getEntityClass() {
		if (entityClass == null) {
			entityClass = TypeUtil.getArgumentClassFromGenericSuperclass(
					getClass(),
					BaseReadonlyResourceService.class,
					2);
		}
		return entityClass;
	}

	protected <P extends Serializable> void internalArtifactOnChange(
			ResourceArtifactType type,
			String code,
			Serializable id,
			P previous,
			String fieldName,
			Object fieldValue,
			Map<String, AnswerRequiredException.AnswerValue> answers,
			String[] previousFieldsChanged,
			P target) {
		if (type == ResourceArtifactType.REPORT) {
			ReportGenerator<E, P, ?> reportGenerator = (ReportGenerator<E, P, ?>) reportGeneratorMap.get(code);
			if (reportGenerator != null) {
				reportGenerator.onChange(
						id,
						previous,
						fieldName,
						fieldValue,
						answers,
						previousFieldsChanged,
						target);
			}
		} else if (type == ResourceArtifactType.FILTER) {
			FilterProcessor<P> filterProcessor = (FilterProcessor<P>)filterProcessorMap.get(code);
			if (filterProcessor != null) {
				filterProcessor.onChange(
						id,
						previous,
						fieldName,
						fieldValue,
						answers,
						previousFieldsChanged,
						target);
			}
		}
	}

	protected void register(
			String reportCode,
			ReportGenerator<E, ?, ?> reportGenerator) {
		if (artifactIsPresentInResourceConfig(ResourceArtifactType.REPORT, reportCode)) {
			reportGeneratorMap.put(reportCode, reportGenerator);
		} else {
			log.error("Artifact not registered because it doesn't exist in ResourceConfig annotation (" +
					"resourceClass=" + getResourceClass() + ", " +
					"artifactType=" + ResourceArtifactType.REPORT + ", " +
					"artifactCode=" + reportCode + ")");
		}
	}

	protected void register(
			String filterCode,
			FilterProcessor<?> filterProcessor) {
		if (artifactIsPresentInResourceConfig(ResourceArtifactType.FILTER, filterCode)) {
			filterProcessorMap.put(filterCode, filterProcessor);
		} else {
			log.error("Artifact not registered because it doesn't exist in ResourceConfig annotation (" +
					"resourceClass=" + getResourceClass() + ", " +
					"artifactType=" + ResourceArtifactType.FILTER + ", " +
					"artifactCode=" + filterCode + ")");
		}
	}

	protected void register(
			String perspectiveCode,
			PerspectiveApplicator<E, R> perspectiveApplicator) {
		if (artifactIsPresentInResourceConfig(ResourceArtifactType.PERSPECTIVE, perspectiveCode)) {
			perspectiveApplicatorMap.put(perspectiveCode, perspectiveApplicator);
		} else {
			log.error("Artifact not registered because it doesn't exist in ResourceConfig annotation (" +
					"resourceClass=" + getResourceClass() + ", " +
					"artifactType=" + ResourceArtifactType.PERSPECTIVE + ", " +
					"artifactCode=" + perspectiveCode + ")");
		}
	}

	protected void register(
			String fieldName,
			FieldDownloader<E> fieldDownloader) {
		fieldDownloaderMap.put(fieldName, fieldDownloader);
	}

	protected boolean isEntityRepositoryOptional() {
		return false;
	}

	protected Optional<E> entityRepositoryFindOne(ID id) {
		Specification<E> specification = toGetOneProcessedSpecification(id);
		return entityRepository.findOne(specification);
	}

	protected Page<E> entityRepositoryFindEntities(
			String quickFilter,
			String filter,
			String[] namedQueries,
			Pageable pageable) {
		Specification<E> specification = toFindProcessedSpecification(
				quickFilter,
				filter,
				namedQueries);
		log.debug("Consulta amb specification ({})", specification);
		Sort processedSort = toProcessedSort(pageable.getSort());
		if (pageable.isUnpaged()) {
			List<E> resultList = entityRepository.findAll(specification, processedSort);
			return new PageImpl<>(resultList, pageable, resultList.size());
		} else {
			Pageable processedPageable = PageRequest.of(
					pageable.getPageNumber(),
					pageable.getPageSize(),
					processedSort);
			return entityRepository.findAll(specification, processedPageable);
		}
	}

	protected Specification<E> toGetOneProcessedSpecification(ID id) {
		Specification<E> processedSpecification = new PkSpec<>(id);
		String additionalSpringFilter = additionalSpringFilter(null, null);
		processedSpecification = appendSpecificationWithAnd(
				processedSpecification,
				getSpringFilterSpecification(additionalSpringFilter));
		return appendSpecificationWithAnd(
				processedSpecification,
				additionalSpecification(null));
	}

	protected <P> Specification<P> toFindProcessedSpecification(
			String quickFilter,
			String filter,
			String[] namedQueries) {
		Specification<P> processedSpecification = getSpringFilterSpecification(
				buildSpringFilterForQuickFilter(
						getResourceClass(),
						null,
						quickFilter));
		processedSpecification = appendSpecificationWithAnd(
				processedSpecification,
				getSpringFilterSpecification(filter));
		processedSpecification = appendSpecificationWithAnd(
				processedSpecification,
				getSpringFilterSpecification(
						additionalSpringFilter(filter, namedQueries)));
		processedSpecification = appendSpecificationWithAnd(
				processedSpecification,
				(Specification<P>)additionalSpecification(namedQueries));
		if (namedQueries != null) {
			for (String namedQuery: namedQueries) {
				Specification<P> namedSpecification;
				String namedSpringFilter = namedQueryToSpringFilter(namedQuery);
				if (namedSpringFilter != null) {
					namedSpecification = getSpringFilterSpecification(namedSpringFilter);
				} else {
					namedSpecification = namedQueryToSpecification(namedQuery);
				}
				processedSpecification = appendSpecificationWithAnd(
						processedSpecification,
						namedSpecification);
			}
		}
		Specification<P> finalSpecification = processSpecification(processedSpecification);
		return finalSpecification != null ? finalSpecification : Specification.where(null);
	}

	protected <P> Specification<P> getSpringFilterSpecification(String springFilter) {
		if (springFilter != null) {
			return new FilterSpecification<>(springFilter);
		} else {
			return null;
		}
	}

	protected <P> Specification<P> appendSpecificationWithAnd(
			Specification<P> currentSpecification,
			Specification<P> specification) {
		if (specification != null) {
			if (currentSpecification != null) {
				return currentSpecification.and(specification);
			} else {
				return specification;
			}
		} else {
			return currentSpecification;
		}
	}

	protected String buildSpringFilterForQuickFilter(
			Class<? extends Resource<?>> resourceClass,
			String prefix,
			String quickFilter) {
		ResourceConfig resourceConfigAnnotation = resourceClass.getAnnotation(ResourceConfig.class);
		if (quickFilter != null) {
			String[] quickFilterFields = quickFilterGetFieldsFromResourceClass(resourceClass);
			if (quickFilterFields != null) {
				log.debug(
						"Construint filtre Spring Filter per quickFilter (resourceClass={}, quickFilter={})",
						getResourceClass(),
						quickFilter);
				StringBuilder quickFilterSpringFilter = new StringBuilder();
				for (String quickFilterField : resourceConfigAnnotation.quickFilterFields()) {
					String springFilter = getSpringFilterFromQuickFilterPath(
							quickFilterField.split("\\."),
							resourceClass,
							quickFilterField,
							quickFilter,
							prefix);
					if (springFilter != null) {
						appendSpringFilter(
								quickFilterSpringFilter,
								springFilter,
								" or ");
					}
				}
				log.debug("Filtre Spring Filter resultant: {}", quickFilterSpringFilter);
				return quickFilterSpringFilter.toString();
			}
		}
		return null;
	}

	protected Boolean artifactRequiresId(ResourceArtifactType type, String code) {
		ResourceConfig resourceConfig = getResourceClass().getAnnotation(ResourceConfig.class);
		if (resourceConfig != null && (type == ResourceArtifactType.ACTION || type == ResourceArtifactType.REPORT)) {
			Optional<ResourceArtifact> artifact = Arrays.stream(resourceConfig.artifacts()).
					filter(a -> a.type() == type && a.code().equals(code)).
					findFirst();
			if (artifact.isPresent()) {
				return artifact.get().requiresId();
			}
		}
		return null;
	}

	protected Class<? extends Serializable> artifactGetFormClass(ResourceArtifactType type, String code) {
		ResourceConfig resourceConfig = getResourceClass().getAnnotation(ResourceConfig.class);
		if (resourceConfig != null) {
			Optional<ResourceArtifact> artifact = Arrays.stream(resourceConfig.artifacts()).
					filter(a -> a.type() == type && a.code().equals(code)).
					findFirst();
			if (artifact.isPresent() && !artifact.get().formClass().equals(Serializable.class)) {
				return artifact.get().formClass();
			}
		}
		return null;
	}

	protected boolean artifactIsPresentInResourceConfig(
			ResourceArtifactType type,
			String code) {
		ResourceConfig resourceConfig = getResourceClass().getAnnotation(ResourceConfig.class);
		if (resourceConfig != null) {
			Optional<ResourceArtifact> artifact = Arrays.stream(resourceConfig.artifacts()).
					filter(a -> a.type() == type && a.code().equals(code)).
					findFirst();
			return artifact.isPresent();
		} else {
			return false;
		}
	}

	protected List<ResourceArtifact> artifactGetFilterAll() {
		ResourceConfig resourceConfig = getResourceClass().getAnnotation(ResourceConfig.class);
		if (resourceConfig != null) {
			return Arrays.stream(resourceConfig.artifacts()).
					filter(a -> a.type() == ResourceArtifactType.FILTER).
					collect(Collectors.toList());
		} else {
			return Collections.emptyList();
		}
	}

	protected Sort toProcessedSort(Sort sort) {
		Sort resultSort;
		Sort protectedProcessedSort = processSort(addDefaultSort(sort));
		if (protectedProcessedSort != null) {
			log.debug("\tProcessant ordenació " + protectedProcessedSort);
			if (protectedProcessedSort.isSorted()) {
				List<Sort.Order> orders = new ArrayList<>();
				for (Sort.Order order: protectedProcessedSort) {
					String[] orderPaths = toProcessedSortPath(
							order.getProperty().split("\\."),
							getEntityClass());
					if (orderPaths != null) {
						for (String orderPath: orderPaths) {
							if (order.isAscending()) {
								log.debug("\t\tAfegint ordre " + orderPath + " asc");
								orders.add(Sort.Order.asc(orderPath));
							} else {
								log.debug("\t\tAfegint ordre " + orderPath + " desc");
								orders.add(Sort.Order.desc(orderPath));
							}
						}
					} else {
						log.debug("\t\tS'ha ignorat l'ordenació pel camp " + order.getProperty());
					}
				}
				resultSort = Sort.by(orders);
			} else {
				resultSort = protectedProcessedSort;
			}
		} else {
			resultSort = Sort.unsorted();
		}
		return resultSort;
	}

	private String[] toProcessedSortPath(
			String[] path,
			Class<?> entityClass) {
		if (path.length > 0) {
			log.debug("\t\tProcessant path d'ordenació" + Arrays.toString(path) + " per l'entitat " + entityClass);
			Field entityField = ReflectionUtils.findField(entityClass, path[0]);
			if (entityField != null) {
				if (Persistable.class.isAssignableFrom(entityField.getType())) {
					// Si el camp és una referència a una altra entitat
					if (path.length > 1) {
						// Si no s'ha arribat al final del path es torna a fer el
						// procés amb l'entitat a la que es fa referència.
						log.debug("\t\t\tDetectat camp d'entitat de tipus referencia no final " + path[0] + ", tornant a processar");
						String[] orderPaths = toProcessedSortPath(
								Arrays.copyOfRange(path, 1, path.length),
								entityField.getType());
						if (orderPaths != null) {
							// Retorna els paths afegint de nou el primer camp
							return Arrays.stream(orderPaths).
									filter(p -> !p.isEmpty()).
									map(p -> path[0] + "." + p).
									toArray(String[]::new);
						} else {
							return null;
						}
					} else {
						// Si s'ha arribat al final del path s'agafa l'ordenació
						// definida a l'anotació del recurs de l'entitat.
						return new String[] { String.join(".", path[0], "id") };
					}
				} else {
					// Si el camp no és una referència a una altra entitat
					log.debug("\t\t\tDetectat camp d'entitat normal " + path[0] + ", l'afegim");
					//return new String[] { String.join(".", "embedded", path[0]) };
					return new String[] { path[0] };
				}
			} else {
				log.warn("Ordenació no aplicable pel recurs {}, camp no trobat: {}", getResourceClass(), path[0]);
				return null;
			}
		} else {
			return null;
		}
	}

	private Sort addDefaultSort(Sort sort) {
		if (sort == null || sort.isEmpty()) {
			List<Sort.Order> orders = new ArrayList<>();
			for (SortedField sortedField: getResourceDefaultSortFields(getResourceClass())) {
				orders.add(new Sort.Order(
						sortedField.getDirection(),
						sortedField.getField()));
			}
			return Sort.by(orders);
		} else {
			return sort;
		}
	}

	private String[] quickFilterGetFieldsFromResourceClass(Class<?> resourceClass) {
		ResourceConfig resourceConfigAnnotation = resourceClass.getAnnotation(ResourceConfig.class);
		if (resourceConfigAnnotation != null && resourceConfigAnnotation.quickFilterFields().length > 0) {
			return resourceConfigAnnotation.quickFilterFields();
		} else {
			log.warn("Quick filter fields not specified for resource (class={})", resourceClass.getName());
			return null;
		}
	}

	private String getSpringFilterFromQuickFilterPath(
			String[] currentPath,
			Class<?> resourceClass,
			String fieldName,
			String quickFilter,
			String filterFieldPrefix) {
		log.debug("\t\tProcessant path de quickFilter" + Arrays.toString(currentPath) + " pel recurs " + resourceClass);
		Field resourceField = ReflectionUtils.findField(resourceClass, currentPath[0]);
		if (resourceField != null) {
			Class<?> processedResourceType = resourceField.getType();
			boolean resourceTypeIsCollection = false;
			Class<?> collectionFieldType = TypeUtil.getCollectionFieldType(resourceField);
			if (collectionFieldType != null) {
				processedResourceType = collectionFieldType;
				resourceTypeIsCollection = true;
			}
			StringBuilder springFilter = new StringBuilder();
			if (ResourceReference.class.isAssignableFrom(processedResourceType)) {
				// Si el camp és una referència a una altra entitat
				if (currentPath.length > 1) {
					// Si no s'ha arribat al final del path es torna a fer el
					// procés amb l'entitat a la que es fa referència.
					log.debug("\t\t\tDetectat camp de tipus referencia no final " + currentPath[0] + ", tornant a cercar");
					if (resourceTypeIsCollection) {
						springFilter.append("exists(");
					}
					springFilter.append(
							getSpringFilterFromQuickFilterPath(
									Arrays.copyOfRange(currentPath, 1, currentPath.length),
									TypeUtil.getReferencedResourceClass(resourceField),
									fieldName,
									quickFilter,
									filterFieldPrefix));
					if (resourceTypeIsCollection) {
						springFilter.append(")");
					}
				} else {
					log.debug("\t\t\tDetectat camp de tipus referencia final. S'inclouran tots els seus camps del quickFilter");
					springFilter.append("(");
					if (resourceTypeIsCollection) {
						springFilter.append("exists(");
					}
					springFilter.append(
							buildSpringFilterForQuickFilter(
									TypeUtil.getReferencedResourceClass(resourceField),
									(filterFieldPrefix != null) ? filterFieldPrefix + resourceField.getName() + "." : resourceField.getName() + ".",
									quickFilter));
					if (resourceTypeIsCollection) {
						springFilter.append(")");
					}
					springFilter.append(")");
				}
			} else {
				log.debug("\t\t\tAfegint camp final");
				// Si el camp no és una referència a una altra entitat
				if (filterFieldPrefix != null) {
					springFilter.append(filterFieldPrefix);
				}
				springFilter.append("lower(");
				springFilter.append(fieldName);
				springFilter.append(")");
				springFilter.append("~");
				springFilter.append("lower(");
				springFilter.append("'");
				springFilter.append("%");
				springFilter.append(cleanReservedFilterCharacters(quickFilter));
				springFilter.append("%");
				springFilter.append("'");
				springFilter.append(")");
			}
			return springFilter.toString();
		} else {
			log.debug("\t\t\tCamp no trobat");
			return null;
		}
	}

	private void appendSpringFilter(
			StringBuilder sb,
			String springFilter,
			String separator) {
		if (springFilter != null && !springFilter.isEmpty()) {
			if (sb.length() > 0) {
				sb.append(separator);
			}
			sb.append("(");
			sb.append(springFilter);
			sb.append(")");
		}
	}

	private String cleanReservedFilterCharacters(String quickFilter) {
		StringBuilder sb = new StringBuilder();
		for (int n = 0; n < quickFilter.length(); n++) {
			char c = quickFilter.charAt(n);
			if (c == '\'') {
				sb.append("\\'");
			} else {
				sb.append(c);
			}
		}
		return sb.toString();
	}

	@Getter
	@AllArgsConstructor
	public static class SortedField {
		private String field;
		private Sort.Direction direction;
	}

	@Getter
	@AllArgsConstructor
	private static class PkSpec<E, PK> implements Specification<E> {
		private PK id;
		@Override
		public Predicate toPredicate(
				Root<E> root,
				CriteriaQuery<?> query,
				CriteriaBuilder criteriaBuilder) {
			return criteriaBuilder.equal(root.get("id"), id);
		}
	}

	/**
	 * Interfície a implementar pels artefactes encarregats d'aplicar perspectives als recursos.
	 *
	 * @param <E> classe de l'entitat a la qual està associada aquesta perspectiva.
	 * @param <R> classe del recurs al qual està associada aquesta perspectiva.
	 */
	public interface PerspectiveApplicator<E extends ResourceEntity<R, ?>, R extends Resource<?>> {
		/**
		 * Aplica la perspectiva a múltiples recursos. Es pot sobreescriure o deixar sense implementar.
		 * Si es deixa sense implementar s'aplicarà la perspectiva a cada recurs per separat.
		 *
		 * @param code
		 *            el codi de la perspectiva a aplicar.
		 * @param entities
		 *            la llista d'entitats per a aplicar la perspectiva.
		 * @param resources
		 *            la llista de recursos a on aplicar la perspectiva.
		 * @return true si s'ha fet alguna modificació o false en cas contrari.
		 * @throws PerspectiveApplicationException
		 *             si es produeix algun error aplicant la perspectiva.
		 */
		default boolean applyMultiple(
				String code,
				List<E> entities,
				List<R> resources) throws PerspectiveApplicationException {
			return false;
		}
		/**
		 * Aplica la perspectiva a un únic recurs.
		 *
		 * @param code
		 *            el codi de la perspectiva a aplicar.
		 * @param entity
		 *            l'entitat per a aplicar la perspectiva.
		 * @param resource
		 *            el recurs a on aplicar la perspectiva.
		 * @throws PerspectiveApplicationException
		 *             si es produeix algun error aplicant la perspectiva.
		 */
		void applySingle(
				String code,
				E entity,
				R resource) throws PerspectiveApplicationException;
	}

	/**
	 * Interfície a implementar pels processadors de lògica onChange.
	 *
	 * @param <R> classe del recurs.
	 */
	@FunctionalInterface
	public interface OnChangeLogicProcessor<R extends Serializable> {
		/**
		 * Processa la lògica onChange d'un camp.
		 *
		 * @param id
		 *            clau primària del recurs.
		 * @param previous
		 *            el recurs amb els valors previs a la modificació.
		 * @param fieldName
		 *            el nom del camp modificat.
		 * @param fieldValue
		 *            el valor del camp modificat.
		 * @param answers
		 *            les respostes associades a la petició actual.
		 * @param previousFieldNames
		 *            la llista de camps canviats amb anterioritat a l'actual petició onChange.
		 * @param target
		 *            el recurs emmagatzemat a base de dades.
		 */
		void onChange(
				Serializable id,
				R previous,
				String fieldName,
				Object fieldValue,
				Map<String, AnswerRequiredException.AnswerValue> answers,
				String[] previousFieldNames,
				R target);
	}

	/**
	 * Interfície a implementar pels artefactes encarregats de generar dades pels informes.
	 *
	 * @param <E> classe de l'entitat a la que està associada l'informe.
	 * @param <P> classe dels paràmetres necessaris per a generar l'informe.
	 * @param <R> classe de la llista de dades retornades al generar l'informe.
	 */
	public interface ReportGenerator<E extends ResourceEntity<?, ?>, P extends Serializable, R extends Serializable>
			extends BaseMutableResourceService.OnChangeLogicProcessor<P>, BaseMutableResourceService.FieldOptionsProvider {
		/**
		 * Genera les dades per l'informe.
		 *
		 * @param code
		 *            el codi de l'informe.
		 * @param entity
		 *            entitat sobre la que es genera l'informe (pot ser null si l'acció no s'executa sobre una entitat
		 *            en concret).
		 * @param params
		 *            els paràmetres per a la generació.
		 * @return la llista amb les dades generades.
		 * @throws ReportGenerationException
		 *             si es produeix algun error generant les dades.
		 */
		List<R> generateData(
				String code,
				E entity,
				P params) throws ReportGenerationException;
		/**
		 * Genera el fitxer amb l'informe.
		 *
		 * @param code
		 *            el codi de l'informe.
		 * @param data
		 *            les dades de l'informe.
		 * @param fileType
		 *            tipus de fitxer que s'ha de generar.
		 * @param out
		 *            stream a on posar el fitxer generat.
		 * @return el fitxer generat o null si aquest generador no implementa aquesta funcionalitat.
		 */
		default DownloadableFile generateFile(
				String code,
				List<?> data,
				ReportFileType fileType,
				OutputStream out) {
			return null;
		}
		/**
		 * Retorna la ubicació del recurs amb l'informe de JasperReports.
		 *
		 * @param code
		 *            el codi de l'informe.
		 * @param fileType
		 *            tipus de fitxer que s'ha de generar.
		 * @return la ubicació de l'informe.
		 */
		default URL getJasperReportUrl(String code, ReportFileType fileType) {
			return null;
		}
		@Override
		default List<FieldOption> getOptions(String fieldName, Map<String,String[]> requestParameterMap) {
			return new ArrayList<>();
		}
	}

	/**
	 * Interfície a implementar pels artefactes encarregats de filtrar els recursos.
	 *
	 * @param <R> classe del recurs que representa el filtre.
	 */
	public interface FilterProcessor<R extends Serializable>
			extends BaseMutableResourceService.OnChangeLogicProcessor<R>, BaseMutableResourceService.FieldOptionsProvider {
		@Override
		default List<FieldOption> getOptions(String fieldName, Map<String,String[]> requestParameterMap) {
			return new ArrayList<>();
		}
	}

	/**
	 * Interfície a implementar per a retornar els arxius associats a un camp.
	 *
	 * @param <E> classe de l'entitat.
	 */
	public interface FieldDownloader<E extends ResourceEntity<?, ?>> {
		/**
		 * Retorna l'arxiu associat.
		 *
		 * @param entity
		 *            l'entitat amb els valors previs a la modificació.
		 * @param fieldName
		 *            el nom del camp de l'entitat.
		 * @param out
		 *            stream a on posar el fitxer generat.
		 * @throws IOException
		 *             si es produeix algun error de E/S al descarregar l'arxiu.
		 */
		DownloadableFile download(
				E entity,
				String fieldName,
				OutputStream out) throws IOException;
	}

}
