package es.caib.pinbal.logic.resourceservice;

import es.caib.pinbal.logic.base.helper.AuthenticationHelper;
import es.caib.pinbal.logic.intf.base.config.BaseConfig;
import es.caib.pinbal.logic.intf.service.EntitatService;
import es.caib.pinbal.persist.entity.Entitat;
import es.caib.pinbal.persist.entity.EntitatUsuari;
import es.caib.pinbal.persist.repository.EntitatUsuariRepository;
import es.caib.pinbal.persist.resourceentity.EntitatResourceEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Prova el mapeig genèric per reflexió (via {@link EntitatResourceEntity}) i, sobretot, els
 * hooks que repliquen els efectes secundaris de negoci (sincronització SCSP, abast de
 * findPage segons rol) que no formen part de la persistència del recurs.
 */
@MockitoSettings(strictness = Strictness.LENIENT)
@ExtendWith(MockitoExtension.class)
public class EntitatResourceServiceImplTest {

    @Mock private EntitatService entitatService;
    @Mock private AuthenticationHelper authenticationHelper;
    @Mock private EntitatUsuariRepository entitatUsuariRepository;

    @InjectMocks
    private EntitatResourceServiceImpl service;

    private EntitatResourceEntity buildEntity(Long id, String codi, String nom, String cif, boolean activa) {
        EntitatResourceEntity entity = new EntitatResourceEntity();
        entity.setId(id);
        entity.setCodi(codi);
        entity.setNom(nom);
        entity.setCif(cif);
        entity.setActiva(activa);
        return entity;
    }

    @Test
    public void additionalSpecification_admin_retornaNull() {
        when(authenticationHelper.isCurrentUserInRole(BaseConfig.ROLE_ADMIN)).thenReturn(true);

        Specification<EntitatResourceEntity> specification = service.additionalSpecification(null);

        assertNull(specification);
        verify(entitatUsuariRepository, never()).findByUsuariCodi(anyString());
    }

    @Test
    public void additionalSpecification_noAdmin_restringeixPerUsuari() {
        when(authenticationHelper.isCurrentUserInRole(BaseConfig.ROLE_ADMIN)).thenReturn(false);
        when(authenticationHelper.getCurrentUserName()).thenReturn("usuari1");
        Entitat entitat = Entitat.getBuilder("ENT01", "Entitat U", "Q0700001", Entitat.EntitatTipus.AJUNTAMENT).build();
        EntitatUsuari entitatUsuari = EntitatUsuari.getBuilder(entitat, null, null, false, false, false, false, false).build();
        when(entitatUsuariRepository.findByUsuariCodi("usuari1")).thenReturn(List.of(entitatUsuari));

        Specification<EntitatResourceEntity> specification = service.additionalSpecification(null);

        assertNotNull(specification);
        verify(entitatUsuariRepository).findByUsuariCodi("usuari1");
    }

    @Test
    public void beforeCreateSave_sincronitzaAltaScsp() {
        EntitatResourceEntity entity = buildEntity(null, "ENT01", "Entitat U", "Q0700001", true);

        service.beforeCreateSave(entity, null, null);

        verify(entitatService).scspOrganismeCessionariAlta("Q0700001", "Entitat U", true);
    }

    @Test
    public void afterUpdateSave_sincronitzaActualitzacioScspIServeisActius() {
        EntitatResourceEntity entity = buildEntity(1L, "ENT01", "Entitat Modificada", "Q0700001", false);

        service.afterUpdateSave(entity, null, null, false);

        verify(entitatService).scspOrganismeCessionariActualitzacio("Q0700001", "Entitat Modificada", false);
        verify(entitatService).scspSincronitzarServeisActius(1L);
    }

    @Test
    public void beforeDelete_sincronitzaBaixaScsp() {
        EntitatResourceEntity entity = buildEntity(1L, "ENT01", "Entitat U", "Q0700001", true);

        service.beforeDelete(entity, null);

        verify(entitatService).scspOrganismeCessionariBaixa("Q0700001");
    }
}
