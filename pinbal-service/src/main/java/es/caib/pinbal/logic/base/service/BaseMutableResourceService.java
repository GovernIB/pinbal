package es.caib.pinbal.logic.base.service;

import es.caib.pinbal.logic.base.helper.ResourceReferenceToEntityHelper;
import es.caib.pinbal.logic.intf.base.annotation.ResourceConfig;
import es.caib.pinbal.logic.intf.base.exception.*;
import es.caib.pinbal.logic.intf.base.model.*;
import es.caib.pinbal.logic.intf.base.service.MutableResourceService;
import es.caib.pinbal.logic.intf.base.util.TypeUtil;
import es.caib.pinbal.persist.base.entity.ReorderableEntity;
import es.caib.pinbal.persist.base.entity.ResourceEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Persistable;
import org.springframework.lang.Nullable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ReflectionUtils;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Servei amb la funcionalitat básica per a la gestió d'un recurs que es pot modificar.
 *
 * @param <R> classe del recurs.
 * @param <ID> classe de la clau primària del recurs.
 * @param <E> classe de l'entitat de base de dades del recurs.
 *
 * @author Límit Tecnologies
 */
@Slf4j
public abstract class BaseMutableResourceService<R extends Resource<ID>, ID extends Serializable, E extends ResourceEntity<R, ID>>
		extends BaseReadonlyResourceService<R, ID, E>
		implements MutableResourceService<R, ID>, BaseReadonlyResourceService.OnChangeLogicProcessor<R> {

	@Autowired
	private ResourceReferenceToEntityHelper resourceReferenceToEntityHelper;

	private final Map<String, ActionExecutor<E, ?, ?>> actionExecutorMap = new HashMap<>();
	private final Map<String, OnChangeLogicProcessor<R>> onChangeLogicProcessorMap = new HashMap<>();
	private final Map<String, FieldFileManager<E>> fieldFileManagerMap = new HashMap<>();
	private final Map<String, FieldOptionsProvider> fieldOptionsProviderMap = new HashMap<>();

	@Override
	public R newResourceInstance() {
		log.debug("Creating new resource instance");
		return newClassInstance(getResourceClass());
	}

	@Override
	@Transactional
	public R create(
			R resource,
			Map<String, AnswerRequiredException.AnswerValue> answers) {
		log.debug("Creating resource (resource={})", resource);
		completeResource(resource);
		ID pk = buildPkChechingIfEntityAlreadyExists(resource);
		Map<String, Persistable<?>> referencedEntities = resourceReferenceToEntityHelper.getReferencedEntitiesForResource(
				resource,
				getEntityClass());
		E entity = resourceToEntity(resource, pk, referencedEntities);
		beforeCreateEntity(entity, resource, answers);
		updateEntityWithResource(entity, resource, referencedEntities);
		beforeCreateSave(entity, resource, answers);
		boolean anyOrderChanged = reorderIfReorderable(
				entity,
				null,
				null,
				true,
				false);
		E saved = entitySaveFlushAndRefresh(entity);
		fieldFilesSave(resource, saved);
		afterCreateSave(saved, resource, answers, anyOrderChanged);
		return entityDetachConvertAndMerge(saved, answers, true);
	}

	@Override
	@Transactional
	public R update(
			ID id,
			R resource,
			Map<String, AnswerRequiredException.AnswerValue> answers) throws ResourceNotFoundException {
		log.debug("Updating resource (id={}, resource={})", id, resource);
		completeResource(resource);
		E entity = getEntity(id);
		ID reorderPreviousParentId = reorderGetParentId(entity);
		Long reorderResourceSequence = reorderGetSequenceFromResourceOrEntity(resource, entity);
		beforeUpdateEntity(entity, resource, answers);
		Map<String, Persistable<?>> referencedEntities = resourceReferenceToEntityHelper.getReferencedEntitiesForResource(
				resource,
				getEntityClass());
		updateEntityWithResource(entity, resource, referencedEntities);
		beforeUpdateSave(entity, resource, answers);
		E saved = entitySaveFlushAndRefresh(entity);
		boolean anyOrderChanged = reorderIfReorderable(
				saved,
				reorderResourceSequence,
				reorderPreviousParentId,
				true,
				false);
		fieldFilesSave(resource, saved);
		afterUpdateSave(saved, resource, answers, anyOrderChanged);
		return entityDetachConvertAndMerge(saved, answers, false);
	}

	@Override
	@Transactional
	public void delete(
			ID id,
			Map<String, AnswerRequiredException.AnswerValue> answers) throws ResourceNotFoundException {
		log.debug("Deleting resource (id={})", id);
		E entity = getEntity(id);
		beforeDelete(entity, answers);
		entityRepositoryDelete(entity);
		reorderIfReorderable(
				entity,
				null,
				null,
				true,
				true);
		fieldFilesDelete(entity);
		entityRepositoryFlush();
		afterDelete(entity, answers);
	}

	@Override
	@Transactional(readOnly = true)
	public Map<String, Object> onChange(
			ID id,
			R previous,
			String fieldName,
			Object fieldValue,
			Map<String, AnswerRequiredException.AnswerValue> answers) throws ResourceFieldNotFoundException, AnswerRequiredException {
		log.debug("Processing onChange event (previous={}, fieldName={}, fieldValue={}, answers={})",
				previous,
				fieldName,
				fieldValue,
				answers);
		onChangeCheckIfFieldExists(getResourceClass(), fieldName);
		return onChangeProcessRecursiveLogic(
				id,
				previous,
				fieldName,
				fieldValue,
				null,
				this,
				answers);
	}

	@Override
	@Transactional
	public <P extends Serializable> Serializable artifactActionExec(
			ID id,
			String code,
			P params) throws ArtifactNotFoundException, ActionExecutionException {
		log.debug("Executing action (code={}, params={})", code, params);
		ActionExecutor<E, P, ?> executor = (ActionExecutor<E, P, ?>)actionExecutorMap.get(code);
		if (executor != null) {
			E entity = null;
			if (id != null) {
				entity = getEntity(id);
			} else if (artifactRequiresId(ResourceArtifactType.ACTION, code)) {
				throw new ActionExecutionException(
						getResourceClass(),
						null,
						code,
						"This action requires id");
			}
			try {
				return executor.exec(code, entity, params);
			} catch (ActionExecutionException ex) {
				throw ex;
			} catch (Exception ex) {
				ActionExecutionException aex = new ActionExecutionException(
						getResourceClass(),
						id,
						code,
						"",
						ex);
				log.error(aex.getMessage(), ex);
				throw aex;
			}
		} else {
			throw new ArtifactNotFoundException(getResourceClass(), ResourceArtifactType.ACTION, code);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public List<FieldOption> fieldEnumOptions(
			String fieldName,
			Map<String,String[]> requestParameterMap) {
		log.debug("Querying field enum options (fieldName={}, requestParameterMap={})", fieldName, requestParameterMap);
		FieldOptionsProvider fieldOptionsProvider = fieldOptionsProviderMap.get(fieldName);
		if (fieldOptionsProvider != null) {
			return fieldOptionsProvider.getOptions(fieldName, requestParameterMap);
		} else {
			log.warn("Couldn't find FieldOptionsProvider (resourceClass={}, fieldName={}, requestParameterMap={})",
					getResourceClass(),
					fieldName,
					requestParameterMap);
			return null;
		}
	}

	@Override
	@Transactional(readOnly = true)
	public List<ResourceArtifact> artifactFindAll(ResourceArtifactType type) {
		log.debug("Querying allowed artifacts (type={})", type);
		List<ResourceArtifact> artifacts = new ArrayList<>(super.artifactFindAll(type));
		if (type == null || type == ResourceArtifactType.ACTION) {
			artifacts.addAll(
					actionExecutorMap.keySet().stream().
							filter(code -> permissionHelper.checkResourceArtifactPermission(
									getResourceClass(),
									ResourceArtifactType.ACTION,
									code)).
							map(code -> new ResourceArtifact(
									ResourceArtifactType.ACTION,
									code,
									artifactRequiresId(ResourceArtifactType.ACTION, code),
									artifactGetFormClass(ResourceArtifactType.ACTION, code))).
							collect(Collectors.toList()));
		}
		return artifacts;
	}

	@Override
	@Transactional(readOnly = true)
	public ResourceArtifact artifactGetOne(ResourceArtifactType type, String code) throws ArtifactNotFoundException {
		log.debug("Querying artifact form class (type={}, code={})", type, code);
		if (type == ResourceArtifactType.ACTION) {
			ActionExecutor<E, ?, ?> generator = actionExecutorMap.get(code);
			if (generator != null) {
				boolean allowed = permissionHelper.checkResourceArtifactPermission(
						getResourceClass(),
						ResourceArtifactType.ACTION,
						code);
				if (allowed) {
					return new ResourceArtifact(
							ResourceArtifactType.ACTION,
							code,
							artifactRequiresId(ResourceArtifactType.ACTION, code),
							artifactGetFormClass(ResourceArtifactType.ACTION, code));
				}
			}
		}
		return super.artifactGetOne(type, code);
	}

	@Override
	@Transactional(readOnly = true)
	public DownloadableFile fieldDownload(
			ID id,
			String fieldName,
			OutputStream out) throws ResourceNotFoundException, ResourceFieldNotFoundException, FieldArtifactNotFoundException, IOException {
		Field field = ReflectionUtils.findField(getResourceClass(), fieldName);
		if (field != null) {
			FieldFileManager<E> fieldFileManager = fieldFileManagerMap.get(fieldName);
			if (fieldFileManager != null) {
				FileReference fileReference = fieldFileManager.read(
						getEntity(id),
						fieldName);
				out.write(fileReference.getContent());
				return new DownloadableFile(
						fileReference.getName(),
						fileReference.getContentType(),
						null);
			} else {
				return super.fieldDownload(id, fieldName, out);
			}
		} else {
			throw new ResourceFieldNotFoundException(getResourceClass(), fieldName);
		}
	}

	protected ID getPkFromResource(R resource) {
		if (resource.getId() == null) {
			return null;
		}
		return resource.getId();
	}

	@Override
	protected R entityToResource(E entity) {
		R resource = super.entityToResource(entity);
		fieldFilesRead(resource, entity);
		return resource;
	}

	protected void completeResource(R resource) {}
	protected void beforeCreateEntity(E entity, R resource, Map<String, AnswerRequiredException.AnswerValue> answers) throws ResourceNotCreatedException {}
	protected void beforeCreateSave(E entity, R resource, Map<String, AnswerRequiredException.AnswerValue> answers) {}
	protected void afterCreateSave(E entity, R resource, Map<String, AnswerRequiredException.AnswerValue> answers, boolean anyOrderChanged) {}
	protected void afterCreate(E entity, R resource, Map<String, AnswerRequiredException.AnswerValue> answers) {}
	protected void beforeUpdateEntity(E entity, R resource, Map<String, AnswerRequiredException.AnswerValue> answers) throws ResourceNotUpdatedException {}
	protected void beforeUpdateSave(E entity, R resource, Map<String, AnswerRequiredException.AnswerValue> answers) {}
	protected void afterUpdateSave(E entity, R resource, Map<String, AnswerRequiredException.AnswerValue> answers, boolean anyOrderChanged) {}
	protected void afterUpdate(E entity, R resource, Map<String, AnswerRequiredException.AnswerValue> answers) {}
	protected void beforeDelete(E entity, Map<String, AnswerRequiredException.AnswerValue> answers) throws ResourceNotDeletedException {}
	protected void afterDelete(E entity, Map<String, AnswerRequiredException.AnswerValue> answers) {}

	@Override
	public void onChange(
			Serializable id,
			R previous,
			String fieldName,
			Object fieldValue,
			Map<String, AnswerRequiredException.AnswerValue> answers,
			String[] previousFieldsChanged,
			R target) {
		if (onChangeLogicProcessorMap.get(fieldName) != null) {
			onChangeLogicProcessorMap.get(fieldName).onChange(
					id,
					previous,
					fieldName,
					fieldValue,
					answers,
					previousFieldsChanged,
					target);
		}
	}

	@Override
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
		super.internalArtifactOnChange(
				type,
				code,
				id,
				previous,
				fieldName,
				fieldValue,
				answers,
				previousFieldsChanged,
				target);
		if (type == ResourceArtifactType.ACTION) {
			ActionExecutor<E, P, ?> actionExecutor = (ActionExecutor<E, P, ?>)actionExecutorMap.get(code);
			if (actionExecutor != null) {
				actionExecutor.onChange(
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

	@Override
	protected FieldOptionsProvider artifactGetFieldOptionsProvider(
			ResourceArtifactType type,
			String code) {
		FieldOptionsProvider fieldOptionsProvider = null;
		if (type == ResourceArtifactType.ACTION) {
			fieldOptionsProvider = actionExecutorMap.get(code);
		} else {
			fieldOptionsProvider = super.artifactGetFieldOptionsProvider(type, code);
		}
		return fieldOptionsProvider;
	}

	protected ID buildPkChechingIfEntityAlreadyExists(R resource) {
		// Es crea la pk a partir de la informació del recurs
		ID pk = getPkFromResource(resource);
		// Si la pk no és null comprova si el recurs ja existeix
		if (pk != null) {
			Optional<E> existingEntity = entityRepositoryFindOne(pk);
			if (existingEntity.isPresent()) {
				throw new ResourceAlreadyExistsException(
						resource.getClass(),
						pk.toString());
			}
		}
		return pk;
	}

	protected E resourceToEntity(
			R resource,
			ID pk,
			Map<String, Persistable<?>> referencedEntities) {
		return resourceEntityMappingHelper.resourceToEntity(
				resource,
				pk,
				getEntityClass(),
				referencedEntities);
	}

	protected void updateEntityWithResource(
			E entity,
			R resource,
			Map<String, Persistable<?>> referencedEntities) {
		resourceEntityMappingHelper.updateEntityWithResource(entity, resource, referencedEntities);
	}

	protected List<E> reorderFindLinesWithParent(Serializable parentId) {
		return Collections.emptyList();
	}
	protected Integer reorderGetIncrement() {
		return null;
	}
	protected Long reorderGetSequenceFromResourceOrEntity(R resource, E entity) {
		Long sequence = null;
		ResourceConfig resourceConfig = resource.getClass().getAnnotation(ResourceConfig.class);
		if (resourceConfig != null && !resourceConfig.orderField().isEmpty()) {
			sequence = TypeUtil.getFieldOrGetterValue(resourceConfig.orderField(), resource, Long.class);
		}
		if (sequence == null && entity instanceof ReorderableEntity<?>) {
			ReorderableEntity<ID> reorderableEntity = (ReorderableEntity<ID>)entity;
			sequence = reorderableEntity.getOrder();
		}
		return sequence;
	}
	protected ID reorderGetParentId(E entity) {
		if (entity instanceof ReorderableEntity<?>) {
			ReorderableEntity<ID> reorderableEntity = (ReorderableEntity<ID>)entity;
			return reorderableEntity.getOrderParentId();
		} else {
			return null;
		}
	}
	protected long reorderSetNextSequence(ReorderableEntity<ID> reorderableEntity, long index) {
		Integer increment = reorderGetIncrement();
		long nextValue = index * (increment != null ? increment : 1);
		reorderableEntity.setOrder(nextValue);
		return nextValue;
	}
	protected boolean reorderIfReorderable(
			E entity,
			Long sequenceForEntity,
			ID previousParentId,
			boolean sameSequenceInsertBefore,
			boolean isDelete) {
		boolean anyOrderChanged = false;
		if (entity instanceof ReorderableEntity<?>) {
			ReorderableEntity<ID> reorderableEntity = (ReorderableEntity<ID>)entity;
			boolean parentIdChanged = !Objects.equals(reorderableEntity.getOrderParentId(), previousParentId);
			log.debug("\tReordenant entitat {} amb la seqüència {} (previousParentId={})",
					entity,
					sequenceForEntity,
					previousParentId);
			boolean anyOrderChanged1 = reorderWithParentId(
					reorderableEntity,
					sequenceForEntity,
					reorderableEntity.getOrderParentId(),
					parentIdChanged,
					sameSequenceInsertBefore,
					isDelete);
			if (anyOrderChanged1) anyOrderChanged = true;
			if (parentIdChanged) {
				boolean anyOrderChanged2 = reorderWithParentId(
						null,
						null,
						previousParentId,
						false,
						false,
						false);
				if (anyOrderChanged2) anyOrderChanged = true;
			}
		}
		return anyOrderChanged;
	}
	protected boolean reorderWithParentId(
			@Nullable ReorderableEntity<ID> reorderableEntity,
			@Nullable Long sequenceForEntity,
			@Nullable ID parentId,
			boolean parentIdChanged,
			boolean sameSequenceInsertBefore,
			boolean isDelete) {
		boolean anyOrderChanged = false;
		List<E> linesToReorder = reorderFindLinesWithParent(parentId);
		log.debug("\tConsulta d'entitats a reordenar (pareId={}): {} entitats trobades",
				parentId,
				linesToReorder.size());
		boolean inserted = isDelete;
		long index = 1;
		for (E value: linesToReorder) {
			ReorderableEntity<ID> line = (ReorderableEntity<ID>)value;
			if (!line.equals(reorderableEntity)) {
				Long currentSequence = line.getOrder();
				boolean insertHere = !parentIdChanged && sequenceForEntity != null && (sameSequenceInsertBefore ?
						currentSequence != null && currentSequence.compareTo(sequenceForEntity) >= 0 :
						currentSequence != null && currentSequence.compareTo(sequenceForEntity) > 0);
				if (!inserted && insertHere) {
					long sequence = reorderSetNextSequence(reorderableEntity, index++);
					log.debug("\tInsertant entitat {} amb ordre {}", reorderableEntity, sequence);
					inserted = true;
					anyOrderChanged = true;
				}
				long sequence = reorderSetNextSequence(line, index++);
				log.debug("\tConfigurant ordre de l'entitat {}: {} (abans {})", line, sequence, currentSequence);
				if (currentSequence == null || sequence != currentSequence) {
					anyOrderChanged = true;
				}
			} else {
				log.debug("\tIgnorant ordre de l'entitat {}", line);
			}
		}
		if (!inserted && reorderableEntity != null) {
			long sequence = reorderSetNextSequence(reorderableEntity, index);
			log.debug("\tConfigurant ordre de l'entitat {}: {}", reorderableEntity, sequence);
			anyOrderChanged = true;
		}
		return anyOrderChanged;
	}

	protected void register(
			String actionCode,
			ActionExecutor<E, ?, ?> actionExecutor) {
		if (artifactIsPresentInResourceConfig(ResourceArtifactType.ACTION, actionCode)) {
			actionExecutorMap.put(actionCode, actionExecutor);
		} else {
			log.error("Artifact not registered because it doesn't exist in ResourceConfig annotation (" +
					"resourceClass=" + getResourceClass() + ", " +
					"artifactType=" + ResourceArtifactType.ACTION + ", " +
					"artifactCode=" + actionCode + ")");
		}
	}

	protected void register(
			String fieldName,
			OnChangeLogicProcessor<R> logicProcessor) {
		onChangeLogicProcessorMap.put(fieldName, logicProcessor);
	}

	protected void register(
			String fieldName,
			FieldFileManager<E> fieldFileManager) {
		fieldFileManagerMap.put(fieldName, fieldFileManager);
	}

	protected void register(
			String fieldName,
			FieldOptionsProvider fieldOptionsProvider) {
		fieldOptionsProviderMap.put(fieldName, fieldOptionsProvider);
	}

	protected E entitySaveFlushAndRefresh(E entity) {
		E saved = entityRepository.saveAndFlush(entity);
		entityRepository.refresh(saved);
		return saved;
	}

	protected R entityDetachConvertAndMerge(
			E entity,
			Map<String, AnswerRequiredException.AnswerValue> answers,
			boolean create) {
		entityRepository.detach(entity);
		R response = entityToResource(entity);
		E merged = entityRepository.merge(entity);
		entityAfterMergeLogic(response, merged, answers, create);
		return response;
	}

	protected void entityRepositoryDelete(E entity) {
		entityRepository.delete(entity);
	}

	protected void entityRepositoryFlush() {
		entityRepository.flush();
	}

	protected void entityAfterMergeLogic(
			R response,
			E merged,
			Map<String, AnswerRequiredException.AnswerValue> answers,
			boolean create) {
		afterConversion(merged, response);
		if (create) {
			afterCreate(merged, response, answers);
		} else {
			afterUpdate(merged, response, answers);
		}
	}

	private void fieldFilesRead(R resource, E entity) {
		ReflectionUtils.doWithFields(resource.getClass(), field -> {
			FieldFileManager<E> fieldFileManager = fieldFileManagerMap.get(field.getName());
			if (fieldFileManager != null) {
				FileReference fileReference = fieldFileManager.read(entity, field.getName());
				TypeUtil.setFieldOrSetterValue(field, resource, fileReference);
			}
		}, field -> FileReference.class.isAssignableFrom(TypeUtil.getFieldTypeMultipleAware(field)));
	}

	private void fieldFilesSave(R resource, E entity) {
		ReflectionUtils.doWithFields(resource.getClass(), field -> {
			FieldFileManager<E> fieldFileManager = fieldFileManagerMap.get(field.getName());
			if (fieldFileManager != null) {
				fieldFileManager.save(
						entity,
						field.getName(),
						TypeUtil.getFieldOrGetterValue(field, resource));
			}
		}, field -> FileReference.class.isAssignableFrom(TypeUtil.getFieldTypeMultipleAware(field)));
	}

	private void fieldFilesDelete(E entity) {
		ReflectionUtils.doWithFields(getResourceClass(), field -> {
			FieldFileManager<E> fieldFileManager = fieldFileManagerMap.get(field.getName());
			if (fieldFileManager != null) {
				fieldFileManager.delete(
						entity,
						field.getName());
			}
		}, field -> FileReference.class.isAssignableFrom(TypeUtil.getFieldTypeMultipleAware(field)));
	}

	/**
	 * Interfície a implementar per a gestionar els arxius associats a un camp del recurs.
	 *
	 * @param <E> classe de l'entitat.
	 */
	public interface FieldFileManager<E extends ResourceEntity<?, ?>> {
		/**
		 * Lògica per a retornar la informació de l'arxiu.
		 *
		 * @param entity
		 *            l'entitat amb els valors previs a la modificació.
		 * @param fieldName
		 *            el nom del camp de l'entitat.
		 */
		FileReference read(
				E entity,
				String fieldName);
		/**
		 * Lògica per a emmagatzemar l'arxiu associat al camp.
		 *
		 * @param entity
		 *            l'entitat amb els valors previs a la modificació.
		 * @param fieldName
		 *            el nom del camp de l'entitat.
		 * @param fileReference
		 *            la informació de l'arxiu adjunt.
		 */
		void save(
				E entity,
				String fieldName,
				FileReference fileReference);
		/**
		 * Lògica per a esborrar l'arxiu associat al camp.
		 *
		 * @param entity
		 *            l'entitat amb els valors previs a la modificació.
		 * @param fieldName
		 *            el nom del camp de l'entitat.
		 */
		void delete(
				E entity,
				String fieldName);
	}

	/**
	 * Interfície a implementar pels artefactes encarregats d'executar accions.
	 *
	 * @param <E> classe de l'entitat a la que està associada l'acció.
	 * @param <P> classe dels paràmetres necessaris per a executar l'acció.
	 * @param <R> classe de la resposta retornada com a resultat.
	 */
	public interface ActionExecutor<E extends ResourceEntity<?, ?>, P extends Serializable, R extends Serializable>
			extends OnChangeLogicProcessor<P>, FieldOptionsProvider {
		/**
		 * Executa l'acció.
		 *
		 * @param code
		 *            el codi de l'acció.
		 * @param entity
		 *            entitat sobre la que s'executa l'acció (pot ser null si l'acció no s'executa sobre una entitat en
		 *            concret).
		 * @param params
		 *            els paràmetres per a l'execució.
		 * @return el resultat de l'execució (pot ser null).
		 * @throws ActionExecutionException
		 *             si es produeix algun error generant les dades.
		 */
		R exec(String code, E entity, P params) throws ActionExecutionException;
		@Override
		default List<FieldOption> getOptions(String fieldName, Map<String,String[]> requestParameterMap) {
			return new ArrayList<>();
		}
	}

	/**
	 * Interfície a implementar per a retornar les opcions de camps enumerats.
	 */
	public interface FieldOptionsProvider {
		/**
		 * Retorna la llista d'opcions que correspon al camp especificat.
		 *
		 * @param fieldName
		 *            el nom del camp.
		 * @param requestParameterMap
		 *            Els paràmetres de la petició.
		 * @return la llista d'opcions (si es retorna null s'indica que no hi ha opcions).
		 */
		List<FieldOption> getOptions(String fieldName, Map<String,String[]> requestParameterMap);
	}

}
