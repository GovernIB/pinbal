package es.caib.pinbal.logic.base.service;

import es.caib.pinbal.logic.base.helper.BasePermissionHelper;
import es.caib.pinbal.logic.base.helper.JasperReportsHelper;
import es.caib.pinbal.logic.base.helper.ObjectMappingHelper;
import es.caib.pinbal.logic.base.helper.ResourceEntityMappingHelper;
import es.caib.pinbal.logic.intf.base.annotation.ResourceArtifact;
import es.caib.pinbal.logic.intf.base.annotation.ResourceConfig;
import es.caib.pinbal.logic.intf.base.annotation.ResourceField;
import es.caib.pinbal.logic.intf.base.exception.ActionExecutionException;
import es.caib.pinbal.logic.intf.base.exception.AnswerRequiredException;
import es.caib.pinbal.logic.intf.base.exception.ArtifactNotFoundException;
import es.caib.pinbal.logic.intf.base.exception.FieldArtifactNotFoundException;
import es.caib.pinbal.logic.intf.base.exception.ReportGenerationException;
import es.caib.pinbal.logic.intf.base.exception.ResourceFieldNotFoundException;
import es.caib.pinbal.logic.intf.base.exception.ResourceNotFoundException;
import es.caib.pinbal.logic.intf.base.model.DownloadableFile;
import es.caib.pinbal.logic.intf.base.model.ExportField;
import es.caib.pinbal.logic.intf.base.model.FieldOption;
import es.caib.pinbal.logic.intf.base.model.ReportFileType;
import es.caib.pinbal.logic.intf.base.model.Resource;
import es.caib.pinbal.logic.intf.base.model.ResourceArtifactType;
import es.caib.pinbal.logic.intf.base.model.ResourceReference;
import es.caib.pinbal.persist.base.entity.ResourceEntity;
import es.caib.pinbal.persist.base.repository.BaseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.core.ResolvableType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Proves de {@link BaseReadonlyResourceService} mitjançant un harness de prova concret
 * ({@link TestReadonlyResourceService}) que exercita directament la lògica genèrica de
 * paginació, filtratge, artefactes (perspectives/informes/filtres) i onChange.
 */
public class BaseReadonlyResourceServiceTest {

    /**
     * Recurs referenciat des de {@link FixtureResource} (per provar el quickFilter amb camps de referència).
     */
    @ResourceConfig(quickFilterFields = { "label" })
    public static class FixtureRefResource implements Resource<Long> {
        private Long id;
        private String label;
        @Override public Long getId() { return id; }
        @Override public void setId(Long id) { this.id = id; }
        public String getLabel() { return label; }
        public void setLabel(String label) { this.label = label; }
    }

    /**
     * Recurs sense configuració de quickFilter (per provar la branca sense camps configurats).
     */
    public static class NoConfigResource implements Resource<Long> {
        private Long id;
        @Override public Long getId() { return id; }
        @Override public void setId(Long id) { this.id = id; }
    }

    @ResourceConfig(
            quickFilterFields = { "nom", "ref", "ref.label", "refs", "refs.label" },
            defaultSortFields = { @ResourceConfig.ResourceSort(field = "nom", direction = Sort.Direction.DESC) },
            orderField = "sequence",
            artifacts = {
                    @ResourceArtifact(type = ResourceArtifactType.REPORT, code = "rep1", requiresId = true, formClass = FixtureResource.class),
                    @ResourceArtifact(type = ResourceArtifactType.FILTER, code = "filt1", formClass = FixtureResource.class),
                    @ResourceArtifact(type = ResourceArtifactType.PERSPECTIVE, code = "persp1"),
                    @ResourceArtifact(type = ResourceArtifactType.PERSPECTIVE, code = "persp2"),
                    @ResourceArtifact(type = ResourceArtifactType.ACTION, code = "act1", formClass = FixtureResource.class)
            }
    )
    public static class FixtureResource implements Resource<Long> {
        private Long id;
        @ResourceField(onChangeActive = true)
        private String nom;
        private boolean actiu;
        private Long sequence;
        private ResourceReference<FixtureRefResource, Long> ref;
        private List<ResourceReference<FixtureRefResource, Long>> refs;

        @Override public Long getId() { return id; }
        @Override public void setId(Long id) { this.id = id; }
        public String getNom() { return nom; }
        public void setNom(String nom) { this.nom = nom; }
        public boolean isActiu() { return actiu; }
        public void setActiu(boolean actiu) { this.actiu = actiu; }
        public Long getSequence() { return sequence; }
        public void setSequence(Long sequence) { this.sequence = sequence; }
        public ResourceReference<FixtureRefResource, Long> getRef() { return ref; }
        public void setRef(ResourceReference<FixtureRefResource, Long> ref) { this.ref = ref; }
        public List<ResourceReference<FixtureRefResource, Long>> getRefs() { return refs; }
        public void setRefs(List<ResourceReference<FixtureRefResource, Long>> refs) { this.refs = refs; }
    }

    public static class FixtureEntity implements ResourceEntity<FixtureResource, Long> {
        private Long id;
        private String nom;
        private boolean actiu;

        @Override public Long getId() { return id; }
        @Override public void setId(Long id) { this.id = id; }
        @Override public boolean isNew() { return id == null; }
        public String getNom() { return nom; }
        public void setNom(String nom) { this.nom = nom; }
        public boolean isActiu() { return actiu; }
        public void setActiu(boolean actiu) { this.actiu = actiu; }
    }

    public static class TestReadonlyResourceService extends BaseReadonlyResourceService<FixtureResource, Long, FixtureEntity> {
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
    private TestReadonlyResourceService service;

    @SuppressWarnings("unchecked")
    @BeforeEach
    void setUp() {
        entityRepository = mock(BaseRepository.class);
        permissionHelper = mock(BasePermissionHelper.class);
        jasperReportsHelper = mock(JasperReportsHelper.class);
        applicationContext = mock(ApplicationContext.class);
        service = new TestReadonlyResourceService();
        wire(service);
    }

    private void wire(BaseReadonlyResourceService<?, ?, ?> svc) {
        ObjectMappingHelper objectMappingHelper = new ObjectMappingHelper();
        ReflectionTestUtils.setField(svc, "objectMappingHelper", objectMappingHelper);
        ReflectionTestUtils.setField(svc, "resourceEntityMappingHelper", new ResourceEntityMappingHelper(objectMappingHelper));
        ReflectionTestUtils.setField(svc, "permissionHelper", permissionHelper);
        ReflectionTestUtils.setField(svc, "jasperReportsHelper", jasperReportsHelper);
        ReflectionTestUtils.setField(svc, "applicationContext", applicationContext);
        ReflectionTestUtils.setField(svc, "entityRepository", entityRepository);
    }

    // ---------------------------------------------------------------- getOne / getEntity

    @Test
    void getOne_found_mapsEntityAndAppliesPerspective() throws Exception {
        FixtureEntity entity = buildEntity(1L, "Foo");
        when(entityRepository.findOne(any(Specification.class))).thenReturn(Optional.of(entity));
        List<String> applied = new ArrayList<>();
        service.register("persp1", (BaseReadonlyResourceService.PerspectiveApplicator<FixtureEntity, FixtureResource>)
                (code, e, r) -> applied.add(r.getNom()));

        FixtureResource resource = service.getOne(1L, new String[] { "persp1" });

        assertEquals("Foo", resource.getNom());
        assertEquals(List.of("Foo"), applied);
    }

    @Test
    void getOne_unknownPerspective_throwsArtifactNotFoundException() {
        FixtureEntity entity = buildEntity(1L, "Foo");
        when(entityRepository.findOne(any(Specification.class))).thenReturn(Optional.of(entity));

        assertThrows(ArtifactNotFoundException.class, () -> service.getOne(1L, new String[] { "unknown" }));
    }

    @Test
    void getEntity_notFound_simpleMessage() {
        when(entityRepository.findOne(any(Specification.class))).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () -> service.getEntity(7L));

        assertEquals("7", ex.getId());
    }

    @Test
    void getEntity_notFound_withAdditionalSpringFilter_includesFilterInMessage() {
        TestReadonlyResourceService svc = new TestReadonlyResourceService() {
            @Override
            protected String additionalSpringFilter(String currentSpringFilter, String[] namedQueries) {
                return "estat=='OK'";
            }
        };
        wire(svc);
        when(entityRepository.findOne(any(Specification.class))).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () -> svc.getEntity(9L));

        assertTrue(ex.getId().contains("springFilter=estat=='OK'"));
    }

    // ---------------------------------------------------------------- findPage / export

    @Test
    void findPage_unpaged_noFilters_returnsAllMappedEntities() {
        when(entityRepository.findAll(any(Specification.class), any(Sort.class)))
                .thenReturn(List.of(buildEntity(1L, "A"), buildEntity(2L, "B")));

        Page<FixtureResource> page = service.findPage(null, null, null, null, Pageable.unpaged());

        assertEquals(2, page.getTotalElements());
        assertEquals("A", page.getContent().get(0).getNom());
    }

    @Test
    void findPage_paged_appliesMultiplePerspectivesBothBranches() {
        Pageable pageable = PageRequest.of(0, 5);
        Page<FixtureEntity> entityPage = new PageImpl<>(
                List.of(buildEntity(1L, "A"), buildEntity(2L, "B")), pageable, 2);
        when(entityRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(entityPage);

        service.register("persp1", new BaseReadonlyResourceService.PerspectiveApplicator<FixtureEntity, FixtureResource>() {
            @Override
            public boolean applyMultiple(String code, List<FixtureEntity> entities, List<FixtureResource> resources) {
                resources.forEach(r -> r.setActiu(true));
                return true;
            }
            @Override
            public void applySingle(String code, FixtureEntity entity, FixtureResource resource) {
                fail("applySingle should not be called when applyMultiple returns true");
            }
        });
        List<String> singleCalls = new ArrayList<>();
        service.register("persp2", (BaseReadonlyResourceService.PerspectiveApplicator<FixtureEntity, FixtureResource>)
                (code, entity, resource) -> singleCalls.add(resource.getNom()));

        Page<FixtureResource> result = service.findPage(
                "A", "nom~~'A'", new String[] { "x" }, new String[] { "persp1", "persp2" }, pageable);

        assertEquals(2, result.getTotalElements());
        assertTrue(result.getContent().stream().allMatch(FixtureResource::isActiu));
        assertEquals(List.of("A", "B"), singleCalls);
    }

    @Test
    void findPage_namedQueries_useOverriddenSpecificationHooks() {
        List<String> capturedNamedQueries = new ArrayList<>();
        TestReadonlyResourceService svc = new TestReadonlyResourceService() {
            @Override
            protected String additionalSpringFilter(String currentSpringFilter, String[] namedQueries) {
                capturedNamedQueries.addAll(Arrays.asList(namedQueries));
                return "actiu==true";
            }
            @Override
            protected Specification<FixtureEntity> additionalSpecification(String[] namedQueries) {
                return Specification.where(null);
            }
            @Override
            protected String namedQueryToSpringFilter(String namedQuery) {
                return "nq".equals(namedQuery) ? "id!=null" : null;
            }
            @Override
            protected <P> Specification<P> namedQueryToSpecification(String namedQuery) {
                return "nq2".equals(namedQuery) ? Specification.where(null) : null;
            }
        };
        wire(svc);
        when(entityRepository.findAll(any(Specification.class), any(Sort.class))).thenReturn(List.of());

        Page<FixtureResource> result = svc.findPage(null, null, new String[] { "nq", "nq2" }, null, Pageable.unpaged());

        assertEquals(0, result.getTotalElements());
        assertEquals(List.of("nq", "nq2"), capturedNamedQueries);
    }

    @Test
    void export_delegatesToJasperReportsHelperWithMappedContent() {
        when(entityRepository.findAll(any(Specification.class), any(Sort.class)))
                .thenReturn(List.of(buildEntity(1L, "A")));
        OutputStream out = new ByteArrayOutputStream();
        DownloadableFile expected = DownloadableFile.builder().name("f").contentType("text/csv").content(new byte[0]).build();
        when(jasperReportsHelper.export(eq(FixtureResource.class), anyList(), any(ExportField[].class), eq(ReportFileType.CSV), eq(out)))
                .thenReturn(expected);

        DownloadableFile result = service.export(
                null, null, null, null, Pageable.unpaged(),
                new ExportField[] { new ExportField("nom", "Nom") }, ReportFileType.CSV, out);

        assertSame(expected, result);
    }

    // ---------------------------------------------------------------- fieldDownload

    @Test
    void fieldDownload_registered_returnsFile() throws Exception {
        when(entityRepository.findOne(any(Specification.class))).thenReturn(Optional.of(buildEntity(3L, "A")));
        DownloadableFile expected = DownloadableFile.builder().name("f").contentType("text/plain").content(new byte[0]).build();
        service.register("nom", (BaseReadonlyResourceService.FieldDownloader<FixtureEntity>) (entity, fieldName, out) -> expected);

        DownloadableFile result = service.fieldDownload(3L, "nom", new ByteArrayOutputStream());

        assertSame(expected, result);
    }

    @Test
    void fieldDownload_noDownloaderRegistered_throwsFieldArtifactNotFoundException() {
        assertThrows(FieldArtifactNotFoundException.class, () -> service.fieldDownload(1L, "nom", new ByteArrayOutputStream()));
    }

    @Test
    void fieldDownload_unknownField_throwsResourceFieldNotFoundException() {
        assertThrows(ResourceFieldNotFoundException.class, () -> service.fieldDownload(1L, "bogus", new ByteArrayOutputStream()));
    }

    // ---------------------------------------------------------------- artifactFindAll / artifactGetOne

    @Test
    void artifactFindAll_filtersByPermissionAndType() {
        service.register("persp1", (BaseReadonlyResourceService.PerspectiveApplicator<FixtureEntity, FixtureResource>) (code, e, r) -> {});
        service.register("filt1", (BaseReadonlyResourceService.FilterProcessor<FixtureResource>)
                (id, previous, fieldName, fieldValue, answers, previousFieldNames, target) -> {});
        @SuppressWarnings("unchecked")
        BaseReadonlyResourceService.ReportGenerator<FixtureEntity, java.io.Serializable, java.io.Serializable> reportGenerator = mock(BaseReadonlyResourceService.ReportGenerator.class);
        service.register("rep1", reportGenerator);

        when(permissionHelper.checkResourceArtifactPermission(FixtureResource.class, ResourceArtifactType.PERSPECTIVE, "persp1")).thenReturn(true);
        when(permissionHelper.checkResourceArtifactPermission(FixtureResource.class, ResourceArtifactType.FILTER, "filt1")).thenReturn(true);
        when(permissionHelper.checkResourceArtifactPermission(FixtureResource.class, ResourceArtifactType.REPORT, "rep1")).thenReturn(false);

        List<es.caib.pinbal.logic.intf.base.model.ResourceArtifact> all = service.artifactFindAll(null);
        assertEquals(2, all.size());

        List<es.caib.pinbal.logic.intf.base.model.ResourceArtifact> onlyReport = service.artifactFindAll(ResourceArtifactType.REPORT);
        assertTrue(onlyReport.isEmpty());
    }

    @Test
    void artifactGetOne_perspective_allowedAndDenied() {
        service.register("persp1", (BaseReadonlyResourceService.PerspectiveApplicator<FixtureEntity, FixtureResource>) (code, e, r) -> {});
        when(permissionHelper.checkResourceArtifactPermission(FixtureResource.class, ResourceArtifactType.PERSPECTIVE, "persp1")).thenReturn(true);

        es.caib.pinbal.logic.intf.base.model.ResourceArtifact result = service.artifactGetOne(ResourceArtifactType.PERSPECTIVE, "persp1");
        assertEquals("persp1", result.getCode());

        when(permissionHelper.checkResourceArtifactPermission(FixtureResource.class, ResourceArtifactType.PERSPECTIVE, "persp1")).thenReturn(false);
        assertThrows(ArtifactNotFoundException.class, () -> service.artifactGetOne(ResourceArtifactType.PERSPECTIVE, "persp1"));
    }

    @Test
    void artifactGetOne_unregistered_throwsArtifactNotFoundException() {
        assertThrows(ArtifactNotFoundException.class, () -> service.artifactGetOne(ResourceArtifactType.REPORT, "nope"));
    }

    @Test
    void register_codeNotInResourceConfig_isNotRegistered() {
        service.register("notConfigured", (BaseReadonlyResourceService.PerspectiveApplicator<FixtureEntity, FixtureResource>) (code, e, r) -> {});

        assertThrows(ArtifactNotFoundException.class, () -> service.artifactGetOne(ResourceArtifactType.PERSPECTIVE, "notConfigured"));
    }

    // ---------------------------------------------------------------- artifactOnChange

    @Test
    void artifactOnChange_perspectiveArtifact_formClassNull_warnsAndReturnsEmptyMap() {
        service.register("persp1", (BaseReadonlyResourceService.PerspectiveApplicator<FixtureEntity, FixtureResource>) (code, e, r) -> {});
        when(permissionHelper.checkResourceArtifactPermission(FixtureResource.class, ResourceArtifactType.PERSPECTIVE, "persp1")).thenReturn(true);

        Map<String, Object> result = service.artifactOnChange(
                ResourceArtifactType.PERSPECTIVE, "persp1", 1L, new FixtureResource(), "nom", "x", null);

        assertTrue(result.isEmpty());
    }

    @Test
    void artifactOnChange_filterArtifact_processesRecursiveLogicWithNestedOnChangeActiveField() {
        BaseReadonlyResourceService.FilterProcessor<FixtureResource> filterProc =
                (id, previous, fieldName, fieldValue, answers, previousFieldNames, target) -> {
                    if (!"nom".equals(fieldName)) {
                        target.setNom("changed");
                    }
                };
        service.register("filt1", filterProc);
        when(permissionHelper.checkResourceArtifactPermission(FixtureResource.class, ResourceArtifactType.FILTER, "filt1")).thenReturn(true);
        FixtureResource previous = new FixtureResource();
        previous.setNom("orig");

        Map<String, Object> changes = service.artifactOnChange(
                ResourceArtifactType.FILTER, "filt1", null, previous, "actiu", true, new HashMap<>());

        assertEquals("changed", changes.get("nom"));
    }

    @Test
    void artifactOnChange_unknownFieldOnFormClass_throwsResourceFieldNotFoundException() {
        service.register("filt1", (BaseReadonlyResourceService.FilterProcessor<FixtureResource>)
                (id, previous, fieldName, fieldValue, answers, previousFieldNames, target) -> {});
        when(permissionHelper.checkResourceArtifactPermission(FixtureResource.class, ResourceArtifactType.FILTER, "filt1")).thenReturn(true);

        assertThrows(ResourceFieldNotFoundException.class, () -> service.artifactOnChange(
                ResourceArtifactType.FILTER, "filt1", null, new FixtureResource(), "bogusField", "x", null));
    }

    // ---------------------------------------------------------------- artifactFieldEnumOptions

    @Test
    void artifactFieldEnumOptions_withProviderRegistered_returnsOptions() {
        service.register("filt1", new BaseReadonlyResourceService.FilterProcessor<FixtureResource>() {
            @Override
            public void onChange(java.io.Serializable id, FixtureResource previous, String fieldName, Object fieldValue,
                                  Map<String, AnswerRequiredException.AnswerValue> answers, String[] previousFieldNames, FixtureResource target) {
            }
            @Override
            public List<FieldOption> getOptions(String fieldName, Map<String, String[]> requestParameterMap) {
                return List.of(new FieldOption("v", "d"));
            }
        });

        List<FieldOption> options = service.artifactFieldEnumOptions(ResourceArtifactType.FILTER, "filt1", "someField", null);

        assertEquals(1, options.size());
    }

    @Test
    void artifactFieldEnumOptions_unregistered_warnsAndReturnsNull() {
        assertNull(service.artifactFieldEnumOptions(ResourceArtifactType.REPORT, "missing", "someField", null));
    }

    // ---------------------------------------------------------------- artifactReportGenerateData

    @Test
    @SuppressWarnings("unchecked")
    void artifactReportGenerateData_variants() {
        BaseReadonlyResourceService.ReportGenerator<FixtureEntity, java.io.Serializable, java.io.Serializable> generator =
                mock(BaseReadonlyResourceService.ReportGenerator.class);
        service.register("rep1", generator);

        when(generator.generateData("rep1", null, "p")).thenReturn(List.of("x"));
        assertEquals(List.of("x"), service.artifactReportGenerateData(null, "rep1", "p"));

        FixtureEntity entity = buildEntity(5L, "A");
        when(entityRepository.findOne(any(Specification.class))).thenReturn(Optional.of(entity));
        when(generator.generateData(eq("rep1"), eq(entity), eq("p2"))).thenReturn(List.of("y"));
        assertEquals(List.of("y"), service.artifactReportGenerateData(5L, "rep1", "p2"));

        when(generator.generateData(eq("rep1"), isNull(), eq("boom")))
                .thenThrow(new ActionExecutionException(FixtureResource.class, null, "rep1", "x"));
        assertThrows(ActionExecutionException.class, () -> service.artifactReportGenerateData(null, "rep1", "boom"));

        when(generator.generateData(eq("rep1"), isNull(), eq("err"))).thenThrow(new RuntimeException("fail"));
        assertThrows(ReportGenerationException.class, () -> service.artifactReportGenerateData(null, "rep1", "err"));

        assertThrows(ArtifactNotFoundException.class, () -> service.artifactReportGenerateData(null, "missing", "p"));
    }

    // ---------------------------------------------------------------- artifactReportGenerateFile

    @Test
    @SuppressWarnings("unchecked")
    void artifactReportGenerateFile_variants() throws Exception {
        BaseReadonlyResourceService.ReportGenerator<FixtureEntity, java.io.Serializable, java.io.Serializable> generator =
                mock(BaseReadonlyResourceService.ReportGenerator.class);
        service.register("rep1", generator);
        OutputStream out = new ByteArrayOutputStream();
        List<?> data = List.of("a");
        DownloadableFile file = DownloadableFile.builder().name("f").contentType("text/csv").content(new byte[0]).build();

        when(generator.generateFile("rep1", data, ReportFileType.CSV, out)).thenReturn(file);
        assertSame(file, service.artifactReportGenerateFile("rep1", data, ReportFileType.CSV, out));

        URL url = new URL("file:///dummy.jrxml");
        when(generator.generateFile("rep1", data, ReportFileType.PDF, out)).thenReturn(null);
        when(generator.getJasperReportUrl("rep1", ReportFileType.PDF)).thenReturn(url);
        when(jasperReportsHelper.generate(eq(FixtureResource.class), eq("rep1"), eq(url), eq(data), any(), isNull(), eq(ReportFileType.PDF), eq(out)))
                .thenReturn(file);
        assertSame(file, service.artifactReportGenerateFile("rep1", data, ReportFileType.PDF, out));

        when(generator.generateFile("rep1", data, ReportFileType.XLSX, out)).thenReturn(null);
        when(generator.getJasperReportUrl("rep1", ReportFileType.XLSX)).thenReturn(null);
        assertThrows(ReportGenerationException.class, () -> service.artifactReportGenerateFile("rep1", data, ReportFileType.XLSX, out));

        when(generator.generateFile("rep1", data, ReportFileType.ODS, out)).thenThrow(new RuntimeException("x"));
        assertThrows(ReportGenerationException.class, () -> service.artifactReportGenerateFile("rep1", data, ReportFileType.ODS, out));

        when(generator.generateFile("rep1", data, ReportFileType.DOCX, out))
                .thenThrow(new ActionExecutionException(FixtureResource.class, null, "rep1", "boom"));
        assertThrows(ActionExecutionException.class, () -> service.artifactReportGenerateFile("rep1", data, ReportFileType.DOCX, out));

        assertThrows(ArtifactNotFoundException.class, () -> service.artifactReportGenerateFile("missing", data, ReportFileType.CSV, out));
    }

    // ---------------------------------------------------------------- quickFilter / sort building

    @Test
    void buildSpringFilterForQuickFilter_nullQuickFilter_returnsNull() {
        assertNull(service.buildSpringFilterForQuickFilter(FixtureResource.class, null, null));
    }

    @Test
    void buildSpringFilterForQuickFilter_noQuickFilterFieldsConfigured_returnsNull() {
        assertNull(service.buildSpringFilterForQuickFilter(NoConfigResource.class, null, "abc"));
    }

    @Test
    void buildSpringFilterForQuickFilter_buildsExpressionForSimpleReferenceAndCollectionFields() {
        String result = service.buildSpringFilterForQuickFilter(FixtureResource.class, null, "a'b");

        assertNotNull(result);
        assertTrue(result.contains("lower(nom)"));
        assertTrue(result.contains("ref."));
        assertTrue(result.contains("lower(ref.label)"));
        assertTrue(result.contains("refs.label"));
        assertTrue(result.contains("exists("));
        assertTrue(result.contains("a\\'b"));
    }

    @Test
    void toProcessedSort_unsorted_usesDefaultSortFromResourceConfig() {
        Sort result = service.toProcessedSort(Sort.unsorted());

        assertTrue(result.isSorted());
        Sort.Order order = result.iterator().next();
        assertEquals("nom", order.getProperty());
        assertEquals(Sort.Direction.DESC, order.getDirection());
    }

    @Test
    void toProcessedSort_explicitSort_mapsKnownFieldsAndIgnoresUnknown() {
        Sort input = Sort.by(Sort.Order.asc("nom"), Sort.Order.desc("bogus"));

        Sort result = service.toProcessedSort(input);

        List<Sort.Order> orders = new ArrayList<>();
        result.forEach(orders::add);
        assertEquals(1, orders.size());
        assertEquals("nom", orders.get(0).getProperty());
        assertTrue(orders.get(0).isAscending());
    }

    @Test
    void toProcessedSort_processSortOverrideReturnsNull_resultsUnsorted() {
        TestReadonlyResourceService svc = new TestReadonlyResourceService() {
            @Override
            protected Sort processSort(Sort sort) {
                return null;
            }
        };
        wire(svc);

        assertTrue(svc.toProcessedSort(Sort.by("nom")).isUnsorted());
    }

    // ---------------------------------------------------------------- metadata / reflection helpers

    @Test
    void typeResolution_resolvesGenericParameters() {
        assertEquals(FixtureResource.class, service.getResourceClass());
        assertEquals(Long.class, service.getPkClass());
        assertEquals(FixtureEntity.class, service.getEntityClass());
    }

    @Test
    void artifactMetadataHelpers_returnConfiguredValues() {
        assertTrue(service.artifactIsPresentInResourceConfig(ResourceArtifactType.REPORT, "rep1"));
        assertFalse(service.artifactIsPresentInResourceConfig(ResourceArtifactType.REPORT, "nope"));
        assertEquals(Boolean.TRUE, service.artifactRequiresId(ResourceArtifactType.REPORT, "rep1"));
        assertNull(service.artifactRequiresId(ResourceArtifactType.PERSPECTIVE, "persp1"));
        assertNull(service.artifactRequiresId(ResourceArtifactType.REPORT, "missingCode"));
        assertEquals(FixtureResource.class, service.artifactGetFormClass(ResourceArtifactType.REPORT, "rep1"));
        assertNull(service.artifactGetFormClass(ResourceArtifactType.PERSPECTIVE, "persp1"));

        List<ResourceArtifact> filters = service.artifactGetFilterAll();
        assertEquals(1, filters.size());
        assertEquals("filt1", filters.get(0).code());
    }

    @Test
    void isEntityRepositoryOptional_defaultsToFalse() {
        assertFalse(service.isEntityRepositoryOptional());
    }

    @Test
    void newClassInstance_successAndFailure() {
        assertNotNull(service.newClassInstance(FixtureResource.class));
        assertNull(service.newClassInstance(Long.class));
    }

    @Test
    void cloneObjectWithFieldsMap_appliesFieldAndOverridesExceptExcluded() {
        FixtureResource original = new FixtureResource();
        original.setNom("orig");
        original.setActiu(false);
        Map<String, Object> fields = new HashMap<>();
        fields.put("actiu", true);
        fields.put("sequence", 5L);

        Object cloned = service.cloneObjectWithFieldsMap(original, "nom", "new-nom", fields, "sequence");

        FixtureResource clonedResource = (FixtureResource) cloned;
        assertEquals("new-nom", clonedResource.getNom());
        assertTrue(clonedResource.isActiu());
        assertNull(clonedResource.getSequence());
        assertEquals("orig", original.getNom());
    }

    // ---------------------------------------------------------------- initRepository

    @Test
    void initRepository_beanFound_setsEntityRepository() {
        DefaultListableBeanFactory bf = mock(DefaultListableBeanFactory.class);
        when(applicationContext.getAutowireCapableBeanFactory()).thenReturn(bf);
        when(bf.getBeanNamesForType(any(ResolvableType.class))).thenReturn(new String[] { "fixtureRepo" });
        @SuppressWarnings("unchecked")
        BaseRepository<FixtureEntity, Long> repoBean = mock(BaseRepository.class);
        when(applicationContext.getBean("fixtureRepo")).thenReturn(repoBean);

        service.initRepository();

        assertSame(repoBean, ReflectionTestUtils.getField(service, "entityRepository"));
    }

    @Test
    void initRepository_beanNotFoundAndNotOptional_throws() {
        DefaultListableBeanFactory bf = mock(DefaultListableBeanFactory.class);
        when(applicationContext.getAutowireCapableBeanFactory()).thenReturn(bf);
        when(bf.getBeanNamesForType(any(ResolvableType.class))).thenReturn(new String[0]);

        assertThrows(IllegalStateException.class, service::initRepository);
    }

    @Test
    void initRepository_beanNotFoundButOptional_doesNotThrow() {
        TestReadonlyResourceService svc = new TestReadonlyResourceService() {
            @Override
            protected boolean isEntityRepositoryOptional() {
                return true;
            }
        };
        wire(svc);
        DefaultListableBeanFactory bf = mock(DefaultListableBeanFactory.class);
        when(applicationContext.getAutowireCapableBeanFactory()).thenReturn(bf);
        when(bf.getBeanNamesForType(any(ResolvableType.class))).thenReturn(new String[0]);

        assertDoesNotThrow(svc::initRepository);
    }

}
