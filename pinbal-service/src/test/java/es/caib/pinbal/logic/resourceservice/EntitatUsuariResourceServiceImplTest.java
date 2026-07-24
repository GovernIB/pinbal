package es.caib.pinbal.logic.resourceservice;

import es.caib.pinbal.logic.base.helper.ResourceEntityMappingHelper;
import es.caib.pinbal.logic.helper.CacheHelper;
import es.caib.pinbal.logic.intf.base.exception.ResourceNotFoundException;
import es.caib.pinbal.logic.intf.base.model.ResourceReference;
import es.caib.pinbal.logic.intf.model.EntitatUsuariResource;
import es.caib.pinbal.logic.intf.service.UsuariService;
import es.caib.pinbal.persist.resourceentity.EntitatResourceEntity;
import es.caib.pinbal.persist.resourceentity.EntitatUsuariResourceEntity;
import es.caib.pinbal.persist.resourceentity.UsuariResourceEntity;
import es.caib.pinbal.persist.resourcerepository.EntitatUsuariResourceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Prova que {@code create} delega a {@code UsuariService.actualitzarDadesAdmin} (necessari per
 * aprovisionar l'usuari des del sistema extern si cal) i que {@code afterUpdateSave} invalida la
 * cache de permisos. L'actualització de camps (departament, rols, actiu, principal) no té cap
 * lògica pròpia: la fa el mapeig genèric de {@code BaseMutableResourceService}.
 */
@MockitoSettings(strictness = Strictness.LENIENT)
@ExtendWith(MockitoExtension.class)
public class EntitatUsuariResourceServiceImplTest {

    @Mock private UsuariService usuariService;
    @Mock private CacheHelper cacheHelper;
    @Mock private EntitatUsuariResourceRepository entitatUsuariResourceRepository;
    @Mock private ResourceEntityMappingHelper resourceEntityMappingHelper;

    @InjectMocks
    private EntitatUsuariResourceServiceImpl service;

    @BeforeEach
    public void injectInheritedField() {
        // resourceEntityMappingHelper és un camp protected heretat de BaseReadonlyResourceService
        // (normalment el resol Spring); @InjectMocks no el garanteix, així que el fixem a mà.
        // Necessari perquè create() encara crida entityToResource(...) (heretat) al final.
        ReflectionTestUtils.setField(service, "resourceEntityMappingHelper", resourceEntityMappingHelper);
        when(resourceEntityMappingHelper.entityToResource(any(EntitatUsuariResourceEntity.class), eq(EntitatUsuariResource.class)))
                .thenAnswer(inv -> {
                    EntitatUsuariResourceEntity entity = inv.getArgument(0);
                    EntitatUsuariResource resource = new EntitatUsuariResource();
                    resource.setId(entity.getId());
                    return resource;
                });
    }

    private EntitatUsuariResourceEntity buildEntity(Long id, Long entitatId, String usuariCodi) {
        EntitatResourceEntity entitat = new EntitatResourceEntity();
        entitat.setId(entitatId);
        UsuariResourceEntity usuari = new UsuariResourceEntity();
        usuari.setId(usuariCodi);
        EntitatUsuariResourceEntity entity = new EntitatUsuariResourceEntity();
        entity.setId(id);
        entity.setEntitat(entitat);
        entity.setUsuari(usuari);
        return entity;
    }

    private EntitatUsuariResource buildResource(Long entitatId, String usuariCodi) {
        EntitatUsuariResource resource = new EntitatUsuariResource();
        resource.setEntitat(ResourceReference.toResourceReference(entitatId));
        resource.setUsuari(ResourceReference.toResourceReference(usuariCodi));
        resource.setUsuariCodi(usuariCodi);
        resource.setDepartament("Departament");
        resource.setRepresentant(true);
        resource.setActiu(true);
        return resource;
    }

    @Test
    public void create_delegaAActualitzarDadesAdminAmbAfegirFals() throws Exception {
        when(entitatUsuariResourceRepository.findByEntitatIdAndUsuariCodi(5L, "USUARI1"))
                .thenReturn(buildEntity(1L, 5L, "USUARI1"));

        EntitatUsuariResource created = service.create(buildResource(5L, "USUARI1"), null);

        assertEquals(1L, created.getId());
        assertEquals("USUARI1", created.getUsuariCodi());
        verify(usuariService).actualitzarDadesAdmin(
                eq(5L), eq("USUARI1"), isNull(), eq("Departament"),
                eq(true), eq(false), eq(false), eq(false), eq(false), eq(true));
    }

    @Test
    public void create_usuariNoExisteixAlSistemaExtern_llancaResourceNotFound() throws Exception {
        doThrow(new es.caib.pinbal.logic.intf.service.exception.UsuariExternNotFoundException())
                .when(usuariService).actualitzarDadesAdmin(any(), any(), any(), any(), anyBoolean(), anyBoolean(), anyBoolean(), anyBoolean(), anyBoolean(), anyBoolean());

        assertThrows(ResourceNotFoundException.class, () -> service.create(buildResource(5L, "USUARI1"), null));
    }

    @Test
    public void beforeUpdateEntity_usuariCanviat_llancaUnsupportedOperation() {
        EntitatUsuariResourceEntity entity = buildEntity(1L, 5L, "USUARI1");

        assertThrows(UnsupportedOperationException.class,
                () -> service.beforeUpdateEntity(entity, buildResource(5L, "ALTRE_USUARI"), null));
    }

    @Test
    public void beforeUpdateEntity_usuariNoCanviat_noLlancaRes() {
        EntitatUsuariResourceEntity entity = buildEntity(1L, 5L, "USUARI1");

        assertDoesNotThrow(() -> service.beforeUpdateEntity(entity, buildResource(5L, "USUARI1"), null));
    }

    @Test
    public void afterUpdateSave_invalidaCachePermisosPerDelegat() {
        EntitatUsuariResourceEntity entity = buildEntity(1L, 5L, "USUARI1");

        service.afterUpdateSave(entity, null, null, false);

        verify(cacheHelper).evictPermisosPerDelegat("USUARI1");
    }

    @Test
    public void delete_llancaUnsupportedOperation() {
        assertThrows(UnsupportedOperationException.class, () -> service.delete(1L, null));
    }
}
