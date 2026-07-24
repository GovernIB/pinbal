package es.caib.pinbal.logic.resourceservice;

import es.caib.pinbal.logic.base.helper.AuthenticationHelper;
import es.caib.pinbal.logic.intf.base.config.BaseConfig;
import es.caib.pinbal.logic.intf.base.exception.ResourceNotFoundException;
import es.caib.pinbal.logic.intf.dto.ConsultaDto;
import es.caib.pinbal.logic.intf.model.ConsultaResource;
import es.caib.pinbal.logic.intf.service.ConsultaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@MockitoSettings(strictness = Strictness.LENIENT)
@ExtendWith(MockitoExtension.class)
public class ConsultaResourceServiceImplTest {

    @Mock private ConsultaService consultaService;
    @Mock private AuthenticationHelper authenticationHelper;

    @InjectMocks
    private ConsultaResourceServiceImpl service;

    private ConsultaDto buildDto(Long id) {
        ConsultaDto dto = new ConsultaDto();
        dto.setId(id);
        dto.setScspPeticionId("PET-" + id);
        dto.setEntitatId(10L);
        dto.setEntitatNom("Entitat Test");
        dto.setJustificantEstat(ConsultaDto.JustificantEstat.OK);
        return dto;
    }

    @Test
    public void getOne_admin_usaFindOneAdmin() throws Exception {
        when(authenticationHelper.isCurrentUserInRole(BaseConfig.ROLE_ADMIN)).thenReturn(true);
        when(consultaService.findOneAdmin(1L)).thenReturn(buildDto(1L));

        ConsultaResource resource = service.getOne(1L, null);

        assertNotNull(resource);
        assertEquals(1L, resource.getId());
        assertEquals("PET-1", resource.getScspPeticionId());
        verify(consultaService).findOneAdmin(1L);
        verify(consultaService, never()).findOneDelegat(anyLong());
    }

    @Test
    public void getOne_superauditor_usaFindOneSuperauditor() throws Exception {
        when(authenticationHelper.isCurrentUserInRole(BaseConfig.ROLE_ADMIN)).thenReturn(false);
        when(authenticationHelper.isCurrentUserInRole(BaseConfig.ROLE_SUPERAUDIT)).thenReturn(true);
        when(consultaService.findOneSuperauditor(2L)).thenReturn(buildDto(2L));

        ConsultaResource resource = service.getOne(2L, null);

        assertEquals(2L, resource.getId());
        verify(consultaService).findOneSuperauditor(2L);
    }

    @Test
    public void getOne_auditor_usaFindOneAuditor() throws Exception {
        when(authenticationHelper.isCurrentUserInRole(BaseConfig.ROLE_ADMIN)).thenReturn(false);
        when(authenticationHelper.isCurrentUserInRole(BaseConfig.ROLE_SUPERAUDIT)).thenReturn(false);
        when(authenticationHelper.isCurrentUserInRole(BaseConfig.ROLE_AUDIT)).thenReturn(true);
        when(consultaService.findOneAuditor(3L)).thenReturn(buildDto(3L));

        ConsultaResource resource = service.getOne(3L, null);

        assertEquals(3L, resource.getId());
        verify(consultaService).findOneAuditor(3L);
    }

    @Test
    public void getOne_delegat_usaFindOneDelegatPerDefecte() throws Exception {
        when(authenticationHelper.isCurrentUserInRole(anyString())).thenReturn(false);
        when(consultaService.findOneDelegat(4L)).thenReturn(buildDto(4L));

        ConsultaResource resource = service.getOne(4L, null);

        assertEquals(4L, resource.getId());
        verify(consultaService).findOneDelegat(4L);
    }

    @Test
    public void getOne_serveiLlancaExcepcio_esConverteixEnResourceNotFound() throws Exception {
        when(authenticationHelper.isCurrentUserInRole(anyString())).thenReturn(false);
        when(consultaService.findOneDelegat(99L)).thenThrow(new RuntimeException("no trobat"));

        assertThrows(ResourceNotFoundException.class, () -> service.getOne(99L, null));
    }

    @Test
    public void findPage_admin_delegaAFindByFiltrePaginatPerAdmin() throws Exception {
        when(authenticationHelper.isCurrentUserInRole(BaseConfig.ROLE_ADMIN)).thenReturn(true);
        Pageable pageable = PageRequest.of(0, 10);
        Page<ConsultaDto> dtoPage = new PageImpl<>(List.of(buildDto(1L), buildDto(2L)), pageable, 2);
        when(consultaService.findByFiltrePaginatPerAdmin(any(), any())).thenReturn(dtoPage);

        Page<ConsultaResource> result = service.findPage(null, null, null, null, pageable);

        assertEquals(2, result.getTotalElements());
        assertEquals("PET-1", result.getContent().get(0).getScspPeticionId());
        verify(consultaService).findByFiltrePaginatPerAdmin(any(), any());
    }

    @Test
    public void findPage_delegatMultiple_delegaAFindMultiplesByFiltrePaginatPerDelegat() throws Exception {
        when(authenticationHelper.isCurrentUserInRole(anyString())).thenReturn(false);
        Pageable pageable = PageRequest.of(0, 10);
        when(consultaService.findMultiplesByFiltrePaginatPerDelegat(any(), any(), any()))
                .thenReturn(new PageImpl<>(Collections.singletonList(buildDto(5L)), pageable, 1));

        Page<ConsultaResource> result = service.findPage(null, "multiple:'true'", null, null, pageable);

        assertEquals(1, result.getTotalElements());
        verify(consultaService).findMultiplesByFiltrePaginatPerDelegat(any(), any(), any());
        verify(consultaService, never()).findSimplesByFiltrePaginatPerDelegat(any(), any(), any());
    }

    @Test
    public void findPage_delegatSimple_delegaAFindSimplesByFiltrePaginatPerDelegat() throws Exception {
        when(authenticationHelper.isCurrentUserInRole(anyString())).thenReturn(false);
        Pageable pageable = PageRequest.of(0, 10);
        when(consultaService.findSimplesByFiltrePaginatPerDelegat(any(), any(), any()))
                .thenReturn(new PageImpl<>(Collections.singletonList(buildDto(6L)), pageable, 1));

        Page<ConsultaResource> result = service.findPage(null, null, null, null, pageable);

        assertEquals(1, result.getTotalElements());
        verify(consultaService).findSimplesByFiltrePaginatPerDelegat(any(), any(), any());
    }

    @Test
    public void findPage_serveiLlancaExcepcio_retornaPaginaBuida() throws Exception {
        when(authenticationHelper.isCurrentUserInRole(BaseConfig.ROLE_ADMIN)).thenReturn(true);
        Pageable pageable = PageRequest.of(0, 10);
        when(consultaService.findByFiltrePaginatPerAdmin(any(), any())).thenThrow(new RuntimeException("error"));

        Page<ConsultaResource> result = service.findPage(null, null, null, null, pageable);

        assertTrue(result.isEmpty());
    }
}
