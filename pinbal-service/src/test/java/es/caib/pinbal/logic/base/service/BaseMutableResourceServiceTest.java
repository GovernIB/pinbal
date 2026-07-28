package es.caib.pinbal.logic.base.service;

import es.caib.pinbal.logic.base.helper.BasePermissionHelper;
import es.caib.pinbal.logic.base.helper.JasperReportsHelper;
import es.caib.pinbal.logic.base.helper.ObjectMappingHelper;
import es.caib.pinbal.logic.base.helper.ResourceEntityMappingHelper;
import es.caib.pinbal.logic.base.helper.ResourceReferenceToEntityHelper;
import es.caib.pinbal.logic.intf.base.annotation.ResourceArtifact;
import es.caib.pinbal.logic.intf.base.annotation.ResourceConfig;
import es.caib.pinbal.logic.intf.base.exception.ActionExecutionException;
import es.caib.pinbal.logic.intf.base.exception.AnswerRequiredException;
import es.caib.pinbal.logic.intf.base.exception.ArtifactNotFoundException;
import es.caib.pinbal.logic.intf.base.exception.FieldArtifactNotFoundException;
import es.caib.pinbal.logic.intf.base.exception.ResourceAlreadyExistsException;
import es.caib.pinbal.logic.intf.base.exception.ResourceFieldNotFoundException;
import es.caib.pinbal.logic.intf.base.exception.ResourceNotFoundException;
import es.caib.pinbal.logic.intf.base.model.DownloadableFile;
import es.caib.pinbal.logic.intf.base.model.FieldOption;
import es.caib.pinbal.logic.intf.base.model.FileReference;
import es.caib.pinbal.logic.intf.base.model.Resource;
import es.caib.pinbal.logic.intf.base.model.ResourceArtifactType;
import es.caib.pinbal.persist.base.entity.ReorderableEntity;
import es.caib.pinbal.persist.base.entity.ResourceEntity;
import es.caib.pinbal.persist.base.repository.BaseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Proves de {@link BaseMutableResourceService} mitjançant un harness de prova concret
 * ({@link TestMutableResourceService}) que exercita create/update/delete, onChange,
 * artefactes d'acció, gestió d'arxius de camp i la lògica de reordenació.
 */
public class BaseMutableResourceServiceTest {

    @ResourceConfig(
            quickFilterFields = { "nom" },
            orderField = "sequence",
            artifacts = {
                    @ResourceArtifact(type = ResourceArtifactType.ACTION, code = "act1", formClass = FixtureResource.class),
                    @ResourceArtifact(type = ResourceArtifactType.ACTION, code = "act2", requiresId = true, formClass = FixtureResource.class),
                    @ResourceArtifact(type = ResourceArtifactType.REPORT, code = "rep1", formClass = FixtureResource.class),
                    @ResourceArtifact(type = ResourceArtifactType.FILTER, code = "filt1", formClass = FixtureResource.class),
                    @ResourceArtifact(type = ResourceArtifactType.PERSPECTIVE, code = "persp1")
            }
    )
    public static class FixtureResource implements Resource<Long> {
        private Long id;
        private String nom;
        private boolean actiu;
        private Long sequence;
        private FileReference document;

        @Override public Long getId() { return id; }
        @Override public void setId(Long id) { this.id = id; }
        public String getNom() { return nom; }
        public void setNom(String nom) { this.nom = nom; }
        public boolean isActiu() { return actiu; }
        public void setActiu(boolean actiu) { this.actiu = actiu; }
        public Long getSequence() { return sequence; }
        public void setSequence(Long sequence) { this.sequence = sequence; }
        public FileReference getDocument() { return document; }
        public void setDocument(FileReference document) { this.document = document; }
    }

    public static class FixtureEntity implements ResourceEntity<FixtureResource, Long>, ReorderableEntity<Long> {
        private Long id;
        private String nom;
        private boolean actiu;
        private Long order;
        private Long parentId;

        @Override public Long getId() { return id; }
        @Override public void setId(Long id) { this.id = id; }
        @Override public boolean isNew() { return id == null; }
        public String getNom() { return nom; }
        public void setNom(String nom) { this.nom = nom; }
        public boolean isActiu() { return actiu; }
        public void setActiu(boolean actiu) { this.actiu = actiu; }
        @Override public Long getOrder() { return order; }
        @Override public void setOrder(Long order) { this.order = order; }
        @Override public Long getOrderParentId() { return parentId; }
        public void setParentId(Long parentId) { this.parentId = parentId; }
    }

    public static class TestMutableResourceService extends BaseMutableResourceService<FixtureResource, Long, FixtureEntity> {
    }

    static FixtureEntity buildEntity(Long id, String nom) {
        FixtureEntity entity = new FixtureEntity();
        entity.setId(id);
        entity.setNom(nom);
        return entity;
    }

    private BaseRepository<FixtureEntity, Long> entityRepository;
    private BasePermissionHelper permissionHelper;
    private JasperReportsHelper jasperReportsHelper;
    private ApplicationContext applicationContext;
    private TestMutableResourceService service;

    @SuppressWarnings("unchecked")
    @BeforeEach
    void setUp() {
        entityRepository = mock(BaseRepository.class);
        permissionHelper = mock(BasePermissionHelper.class);
        jasperReportsHelper = mock(JasperReportsHelper.class);
        applicationContext = mock(ApplicationContext.class);
        service = new TestMutableResourceService();
        wire(service);
    }

    private void wire(BaseMutableResourceService<?, ?, ?> svc) {
        ObjectMappingHelper objectMappingHelper = new ObjectMappingHelper();
        ReflectionTestUtils.setField(svc, "objectMappingHelper", objectMappingHelper);
        ReflectionTestUtils.setField(svc, "resourceEntityMappingHelper", new ResourceEntityMappingHelper(objectMappingHelper));
        ReflectionTestUtils.setField(svc, "permissionHelper", permissionHelper);
        ReflectionTestUtils.setField(svc, "jasperReportsHelper", jasperReportsHelper);
        ReflectionTestUtils.setField(svc, "applicationContext", applicationContext);
        ReflectionTestUtils.setField(svc, "entityRepository", entityRepository);
        ReflectionTestUtils.setField(svc, "resourceReferenceToEntityHelper", new ResourceReferenceToEntityHelper());
    }

    private void stubSaveMergeDetach() {
        when(entityRepository.saveAndFlush(any(FixtureEntity.class))).thenAnswer(inv -> inv.getArgument(0));
        doNothing().when(entityRepository).refresh(any());
        doNothing().when(entityRepository).detach(any());
        when(entityRepository.merge(any(FixtureEntity.class))).thenAnswer(inv -> inv.getArgument(0));
    }

    // ---------------------------------------------------------------- create

    @Test
    @SuppressWarnings("unchecked")
    void create_success_mapsResourceSavesEntityAndHandlesFieldFiles() {
        stubSaveMergeDetach();
        BaseMutableResourceService.FieldFileManager<FixtureEntity> fileManager = mock(BaseMutableResourceService.FieldFileManager.class);
        FileReference stored = new FileReference("f.txt", new byte[] { 1 }, "text/plain", 1);
        when(fileManager.read(any(FixtureEntity.class), eq("document"))).thenReturn(stored);
        service.register("document", fileManager);

        FixtureResource input = new FixtureResource();
        input.setNom("New");

        FixtureResource result = service.create(input, null);

        assertEquals("New", result.getNom());
        assertEquals(stored, result.getDocument());
        verify(fileManager).save(any(FixtureEntity.class), eq("document"), isNull());
        verify(fileManager).read(any(FixtureEntity.class), eq("document"));
    }

    @Test
    void create_resourceAlreadyExists_throwsResourceAlreadyExistsException() {
        FixtureResource input = new FixtureResource();
        input.setId(5L);
        when(entityRepository.findOne(any(Specification.class))).thenReturn(Optional.of(buildEntity(5L, "existing")));

        assertThrows(ResourceAlreadyExistsException.class, () -> service.create(input, null));
    }

    @Test
    void create_invokesLifecycleHooksInOrder() {
        stubSaveMergeDetach();
        List<String> calls = new ArrayList<>();
        TestMutableResourceService svc = new TestMutableResourceService() {
            @Override
            protected void beforeCreateEntity(FixtureEntity entity, FixtureResource resource, Map<String, AnswerRequiredException.AnswerValue> answers) {
                calls.add("beforeCreateEntity");
            }
            @Override
            protected void beforeCreateSave(FixtureEntity entity, FixtureResource resource, Map<String, AnswerRequiredException.AnswerValue> answers) {
                calls.add("beforeCreateSave");
            }
            @Override
            protected void afterCreateSave(FixtureEntity entity, FixtureResource resource, Map<String, AnswerRequiredException.AnswerValue> answers, boolean anyOrderChanged) {
                calls.add("afterCreateSave");
            }
            @Override
            protected void afterCreate(FixtureEntity entity, FixtureResource resource, Map<String, AnswerRequiredException.AnswerValue> answers) {
                calls.add("afterCreate");
            }
        };
        wire(svc);

        svc.create(new FixtureResource(), null);

        assertEquals(List.of("beforeCreateEntity", "beforeCreateSave", "afterCreateSave", "afterCreate"), calls);
    }

    // ---------------------------------------------------------------- update

    @Test
    void update_notFound_throwsResourceNotFoundException() {
        when(entityRepository.findOne(any(Specification.class))).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.update(1L, new FixtureResource(), null));
    }

    @Test
    void update_reordersBothOldAndNewParent_whenParentChanges() {
        FixtureEntity existing = buildEntity(1L, "Old");
        existing.setParentId(10L);
        existing.setOrder(3L);
        when(entityRepository.findOne(any(Specification.class))).thenReturn(Optional.of(existing));
        stubSaveMergeDetach();

        List<Long> queriedParents = new ArrayList<>();
        TestMutableResourceService svc = new TestMutableResourceService() {
            @Override
            protected void beforeUpdateEntity(FixtureEntity entity, FixtureResource resource, Map<String, AnswerRequiredException.AnswerValue> answers) {
                entity.setParentId(20L);
            }
            @Override
            protected List<FixtureEntity> reorderFindLinesWithParent(Serializable parentId) {
                queriedParents.add((Long) parentId);
                return Collections.emptyList();
            }
        };
        wire(svc);

        FixtureResource input = new FixtureResource();
        input.setNom("New");
        input.setSequence(5L);

        FixtureResource result = svc.update(1L, input, null);

        assertEquals("New", result.getNom());
        assertEquals(List.of(20L, 10L), queriedParents);
    }

    // ---------------------------------------------------------------- delete

    @Test
    void delete_success_invokesHooksAndFileCleanupAndReorder() {
        FixtureEntity entity = buildEntity(1L, "x");
        entity.setParentId(5L);
        entity.setOrder(3L);
        when(entityRepository.findOne(any(Specification.class))).thenReturn(Optional.of(entity));
        doNothing().when(entityRepository).delete(any());
        doNothing().when(entityRepository).flush();
        @SuppressWarnings("unchecked")
        BaseMutableResourceService.FieldFileManager<FixtureEntity> manager = mock(BaseMutableResourceService.FieldFileManager.class);

        List<String> calls = new ArrayList<>();
        TestMutableResourceService svc = new TestMutableResourceService() {
            @Override
            protected void beforeDelete(FixtureEntity e, Map<String, AnswerRequiredException.AnswerValue> a) {
                calls.add("before");
            }
            @Override
            protected void afterDelete(FixtureEntity e, Map<String, AnswerRequiredException.AnswerValue> a) {
                calls.add("after");
            }
            @Override
            protected List<FixtureEntity> reorderFindLinesWithParent(Serializable parentId) {
                return Collections.emptyList();
            }
        };
        wire(svc);
        svc.register("document", manager);

        svc.delete(1L, null);

        assertEquals(List.of("before", "after"), calls);
        verify(entityRepository).delete(entity);
        verify(entityRepository).flush();
        verify(manager).delete(entity, "document");
    }

    @Test
    void delete_notFound_throwsResourceNotFoundException() {
        when(entityRepository.findOne(any(Specification.class))).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.delete(1L, null));
    }

    // ---------------------------------------------------------------- onChange

    @Test
    void onChange_dispatchesToRegisteredProcessorForField() {
        service.register("nom", (BaseReadonlyResourceService.OnChangeLogicProcessor<FixtureResource>)
                (id, previous, fieldName, fieldValue, answers, previousFieldNames, target) -> target.setActiu(true));

        Map<String, Object> changes = service.onChange(1L, new FixtureResource(), "nom", "new", new HashMap<>());

        assertEquals(Boolean.TRUE, changes.get("actiu"));
    }

    @Test
    void onChange_noProcessorRegisteredForField_returnsNoChanges() {
        assertNull(service.onChange(1L, new FixtureResource(), "nom", "new", null));
    }

    @Test
    void onChange_unknownField_throwsResourceFieldNotFoundException() {
        assertThrows(ResourceFieldNotFoundException.class, () -> service.onChange(1L, new FixtureResource(), "bogus", "x", null));
    }

    // ---------------------------------------------------------------- artifactActionExec

    @Test
    @SuppressWarnings("unchecked")
    void artifactActionExec_variants() {
        BaseMutableResourceService.ActionExecutor<FixtureEntity, Serializable, Serializable> executor =
                mock(BaseMutableResourceService.ActionExecutor.class);
        service.register("act1", executor);

        when(executor.exec("act1", null, "p")).thenReturn("ok");
        assertEquals("ok", service.artifactActionExec(null, "act1", "p"));

        FixtureEntity entity = buildEntity(9L, "x");
        when(entityRepository.findOne(any(Specification.class))).thenReturn(Optional.of(entity));
        when(executor.exec(eq("act1"), eq(entity), eq("p2"))).thenReturn("ok2");
        assertEquals("ok2", service.artifactActionExec(9L, "act1", "p2"));

        when(executor.exec(eq("act1"), isNull(), eq("boom")))
                .thenThrow(new ActionExecutionException(FixtureResource.class, null, "act1", "x"));
        assertThrows(ActionExecutionException.class, () -> service.artifactActionExec(null, "act1", "boom"));

        when(executor.exec(eq("act1"), isNull(), eq("err"))).thenThrow(new RuntimeException("fail"));
        assertThrows(ActionExecutionException.class, () -> service.artifactActionExec(null, "act1", "err"));

        assertThrows(ArtifactNotFoundException.class, () -> service.artifactActionExec(null, "missing", "p"));

        @SuppressWarnings("unchecked")
        BaseMutableResourceService.ActionExecutor<FixtureEntity, Serializable, Serializable> executor2 =
                mock(BaseMutableResourceService.ActionExecutor.class);
        service.register("act2", executor2);
        assertThrows(ActionExecutionException.class, () -> service.artifactActionExec(null, "act2", "p"));
    }

    // ---------------------------------------------------------------- fieldEnumOptions / artifact dispatch

    @Test
    void fieldEnumOptions_registeredAndUnregistered() {
        service.register("nom", (BaseMutableResourceService.FieldOptionsProvider) (fieldName, params) -> List.of(new FieldOption("a", "b")));

        assertEquals(1, service.fieldEnumOptions("nom", null).size());
        assertNull(service.fieldEnumOptions("missing", null));
    }

    @Test
    @SuppressWarnings("unchecked")
    void artifactFindAll_includesActionArtifactsFilteredByPermission() {
        BaseMutableResourceService.ActionExecutor<FixtureEntity, Serializable, Serializable> executor =
                mock(BaseMutableResourceService.ActionExecutor.class);
        service.register("act1", executor);
        when(permissionHelper.checkResourceArtifactPermission(FixtureResource.class, ResourceArtifactType.ACTION, "act1")).thenReturn(true);

        List<es.caib.pinbal.logic.intf.base.model.ResourceArtifact> all = service.artifactFindAll(ResourceArtifactType.ACTION);

        assertEquals(1, all.size());
        assertEquals("act1", all.get(0).getCode());
    }

    @Test
    @SuppressWarnings("unchecked")
    void artifactGetOne_actionArtifact_allowed_andOtherTypesFallThroughToSuper() {
        BaseMutableResourceService.ActionExecutor<FixtureEntity, Serializable, Serializable> executor =
                mock(BaseMutableResourceService.ActionExecutor.class);
        service.register("act1", executor);
        when(permissionHelper.checkResourceArtifactPermission(FixtureResource.class, ResourceArtifactType.ACTION, "act1")).thenReturn(true);

        assertEquals("act1", service.artifactGetOne(ResourceArtifactType.ACTION, "act1").getCode());

        BaseReadonlyResourceService.FilterProcessor<FixtureResource> filterProc =
                (id, previous, fieldName, fieldValue, answers, previousFieldNames, target) -> {};
        ((BaseReadonlyResourceService<FixtureResource, Long, FixtureEntity>) service).register("filt1", filterProc);
        when(permissionHelper.checkResourceArtifactPermission(FixtureResource.class, ResourceArtifactType.FILTER, "filt1")).thenReturn(true);

        assertEquals("filt1", service.artifactGetOne(ResourceArtifactType.FILTER, "filt1").getCode());
    }

    @Test
    @SuppressWarnings("unchecked")
    void artifactFieldEnumOptions_actionArtifact_delegatesToActionExecutor() {
        BaseMutableResourceService.ActionExecutor<FixtureEntity, Serializable, Serializable> executor =
                mock(BaseMutableResourceService.ActionExecutor.class);
        service.register("act1", executor);
        when(executor.getOptions("field", null)).thenReturn(List.of(new FieldOption("x", "y")));

        List<FieldOption> options = service.artifactFieldEnumOptions(ResourceArtifactType.ACTION, "act1", "field", null);

        assertEquals(1, options.size());
    }

    @Test
    @SuppressWarnings("unchecked")
    void artifactOnChange_actionArtifact_dispatchesToActionExecutor() {
        BaseMutableResourceService.ActionExecutor<FixtureEntity, Serializable, Serializable> executor =
                mock(BaseMutableResourceService.ActionExecutor.class);
        service.register("act1", executor);
        when(permissionHelper.checkResourceArtifactPermission(FixtureResource.class, ResourceArtifactType.ACTION, "act1")).thenReturn(true);
        FixtureResource previous = new FixtureResource();

        service.artifactOnChange(ResourceArtifactType.ACTION, "act1", null, previous, "nom", "v", null);

        verify(executor).onChange(isNull(), eq(previous), eq("nom"), eq("v"), isNull(), isNull(), any());
    }

    // ---------------------------------------------------------------- fieldDownload

    @Test
    @SuppressWarnings("unchecked")
    void fieldDownload_withFieldFileManager_returnsFile() throws Exception {
        FixtureEntity entity = buildEntity(1L, "x");
        when(entityRepository.findOne(any(Specification.class))).thenReturn(Optional.of(entity));
        BaseMutableResourceService.FieldFileManager<FixtureEntity> manager = mock(BaseMutableResourceService.FieldFileManager.class);
        when(manager.read(entity, "document")).thenReturn(new FileReference("f.txt", new byte[] { 1, 2 }, "text/plain", 2));
        service.register("document", manager);

        DownloadableFile result = service.fieldDownload(1L, "document", new ByteArrayOutputStream());

        assertEquals("f.txt", result.getName());
        assertEquals("text/plain", result.getContentType());
    }

    @Test
    void fieldDownload_noFieldFileManagerRegistered_fallsBackAndThrowsFieldArtifactNotFoundException() {
        assertThrows(FieldArtifactNotFoundException.class, () -> service.fieldDownload(1L, "nom", new ByteArrayOutputStream()));
    }

    @Test
    void fieldDownload_unknownField_throwsResourceFieldNotFoundException() {
        assertThrows(ResourceFieldNotFoundException.class, () -> service.fieldDownload(1L, "bogus", new ByteArrayOutputStream()));
    }

    // ---------------------------------------------------------------- reorder logic

    @Test
    void reorderWithParentId_insertsAmongSiblingsAndShiftsSequences() {
        FixtureEntity moving = buildEntity(1L, "Moving");
        FixtureEntity sib1 = buildEntity(2L, "Sib1");
        sib1.setOrder(10L);
        FixtureEntity sib2 = buildEntity(3L, "Sib2");
        sib2.setOrder(20L);
        TestMutableResourceService svc = new TestMutableResourceService() {
            @Override
            protected List<FixtureEntity> reorderFindLinesWithParent(Serializable parentId) {
                return List.of(sib1, sib2);
            }
            @Override
            protected Integer reorderGetIncrement() {
                return 5;
            }
        };
        wire(svc);

        boolean changed = svc.reorderWithParentId(moving, 15L, null, false, true, false);

        assertTrue(changed);
        assertEquals(5L, sib1.getOrder());
        assertEquals(10L, moving.getOrder());
        assertEquals(15L, sib2.getOrder());
    }

    @Test
    void reorderWithParentId_deleteCleanup_renumbersSiblingsSequentially() {
        FixtureEntity sib1 = buildEntity(2L, "Sib1");
        sib1.setOrder(5L);
        FixtureEntity sib2 = buildEntity(3L, "Sib2");
        sib2.setOrder(9L);
        TestMutableResourceService svc = new TestMutableResourceService() {
            @Override
            protected List<FixtureEntity> reorderFindLinesWithParent(Serializable parentId) {
                return List.of(sib1, sib2);
            }
        };
        wire(svc);

        boolean changed = svc.reorderWithParentId(null, null, null, false, false, true);

        assertTrue(changed);
        assertEquals(1L, sib1.getOrder());
        assertEquals(2L, sib2.getOrder());
    }

    @Test
    void reorderGetSequenceFromResourceOrEntity_prefersResourceOrderFieldThenFallsBackToEntity() {
        FixtureResource resource = new FixtureResource();
        resource.setSequence(42L);
        FixtureEntity entity = buildEntity(1L, "x");
        entity.setOrder(7L);

        assertEquals(42L, service.reorderGetSequenceFromResourceOrEntity(resource, entity));

        resource.setSequence(null);
        assertEquals(7L, service.reorderGetSequenceFromResourceOrEntity(resource, entity));
    }

    @Test
    void reorderGetParentId_returnsEntityOrderParentId() {
        FixtureEntity entity = buildEntity(1L, "x");
        entity.setParentId(99L);

        assertEquals(99L, service.reorderGetParentId(entity));
    }

    // ---------------------------------------------------------------- misc protected helpers

    @Test
    void getPkFromResource_returnsNullOrId() {
        FixtureResource r = new FixtureResource();
        assertNull(service.getPkFromResource(r));

        r.setId(3L);
        assertEquals(3L, service.getPkFromResource(r));
    }

    @Test
    void buildPkChechingIfEntityAlreadyExists_variants() {
        FixtureResource r = new FixtureResource();
        assertNull(service.buildPkChechingIfEntityAlreadyExists(r));

        r.setId(5L);
        when(entityRepository.findOne(any(Specification.class))).thenReturn(Optional.empty());
        assertEquals(5L, service.buildPkChechingIfEntityAlreadyExists(r));

        when(entityRepository.findOne(any(Specification.class))).thenReturn(Optional.of(buildEntity(5L, "x")));
        assertThrows(ResourceAlreadyExistsException.class, () -> service.buildPkChechingIfEntityAlreadyExists(r));
    }

    @Test
    void newResourceInstance_createsInstance() {
        assertNotNull(service.newResourceInstance());
    }

}
